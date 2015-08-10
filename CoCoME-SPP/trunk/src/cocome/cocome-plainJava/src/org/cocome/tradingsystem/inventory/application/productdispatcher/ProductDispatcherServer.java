/***************************************************************************
 * Copyright 2013 DFG SPP 1593 (http://dfg-spp1593.de)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package org.cocome.tradingsystem.inventory.application.productdispatcher;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.cocome.tradingsystem.inventory.application.store.FillTransferObjects;
import org.cocome.tradingsystem.inventory.application.store.IStoreInventoryManager;
import org.cocome.tradingsystem.inventory.application.store.ProductAmountTO;
import org.cocome.tradingsystem.inventory.application.store.ProductMovementTO;
import org.cocome.tradingsystem.inventory.application.store.StoreTO;
import org.cocome.tradingsystem.inventory.data.DataFactory;
import org.cocome.tradingsystem.inventory.data.enterprise.TradingEnterprise;
import org.cocome.tradingsystem.inventory.data.persistence.IPersistence;
import org.cocome.tradingsystem.inventory.data.persistence.IPersistenceContext;
import org.cocome.tradingsystem.inventory.data.store.IStoreQuery;
import org.cocome.tradingsystem.inventory.data.store.StockItem;
import org.cocome.tradingsystem.inventory.data.store.Store;
import org.cocome.tradingsystem.util.ApplicationHelper;
import org.cocome.tradingsystem.util.Names;
import org.cocome.tradingsystem.util.java.Maps;

/**
 * Implements the {@link IProductDispatcher} interface used by stores
 * to initiate transfer of low-stock products from other stores.
 * <p>
 * TODO Associate a single product dispatcher with a single enterprise. --LB
 * 
 * @author unknown
 * @author Lubomir Bulej
 */
final class ProductDispatcherServer
		extends UnicastRemoteObject implements IProductDispatcher {

	private static final long serialVersionUID = -4570575471689265815L;

	private static final Logger LOG =
			Logger.getLogger(ProductDispatcherServer.class);

	//

	private final IPersistence persistence =
			DataFactory.getInstance().getPersistenceManager();

	private final IStoreQuery storeQuery =
			DataFactory.getInstance().getStoreQuery();

	//

	public ProductDispatcherServer() throws RemoteException {
		super();
	}

	/*
	 * @see ProductDispatcherIf#isProductAvailableAtOtherStores(EnterpriseTO, StoreTO, ProductTO[])
	 */
	@Override
	public ProductAmountTO[] dispatchProductsFromOtherStores(
			final long callingStoreId, final Collection<ProductAmountTO> requiredProducts
			) throws RemoteException {
		final IPersistenceContext pctx = this.persistence.getPersistenceContext();

		try {
			//
			// Get store and enterprise database entities.
			//
			final Store callingStore = this.storeQuery.queryStoreById(callingStoreId, pctx);
			final TradingEnterprise enterprise = callingStore.getEnterprise();

			//
			// Distributed action: flush databases in all stores.
			//
			final Collection<Store> stores = enterprise.getStores();
			this.__flushStoreDatabases(stores, pctx);

			//
			// Exclude the calling store from the set of stores and solve
			// the optimization problem using ampl/cplex.
			//
			stores.remove(callingStore);
			final Map<StoreTO, Collection<ProductAmountTO>> storeProductAmounts =
					this.__solveStoreProductOrders(requiredProducts, stores, callingStore, pctx);

			//
			// Distributed action: issue product movement in stores determined
			// by the solver.
			//
			final Map<StoreTO, Collection<ProductAmountTO>> incomingProducts =
					this.__issueProductMovements(storeProductAmounts, callingStore);

			return this.__sumUpIncomingProducts(incomingProducts);

		} finally {
			pctx.close();
		}
	}

	/**
	 * Solves the optimization problem of finding from which store to order
	 * required products. Returns a map of product amounts per store, which
	 * represents the amount of products to deliver from a store to the calling
	 * store.
	 */
	private Map<StoreTO, Collection<ProductAmountTO>> __solveStoreProductOrders(
			final Collection<ProductAmountTO> requiredProductAmounts,
			final Collection<Store> stores, final Store callingStore,
			final IPersistenceContext pctx
			) {
		final IOptimizationSolver solver = new AmplCplexSolver(pctx);
		final long[] productIds = this.__getProductIds(requiredProductAmounts);

		return solver.solveOptimization(
				requiredProductAmounts,
				this.__getOfferedStockItemsPerStore(stores, productIds, pctx),
				this.__getStoreDistances(stores, callingStore.getLocation())
				);
	}

	/**
	 * Advises all store servers from the given list to flush their local data.
	 * The data is written to the enterprise server database.
	 * 
	 * @param stores
	 * @param pctx
	 *            TODO: parameters need to be fixed.
	 */
	private void __flushStoreDatabases(final Collection<Store> stores, final IPersistenceContext pctx) {
		for (final Store store : stores) {
			// TODO: multi-thread
			// TODO: The calls should be done at the store servers via RMI (currently not supported by architecture)
			// TODO: Introduce time-out for multi-threaded RMI connections
		}
	}

	/**
	 * Returns an array of product identifiers (from the given ProductAmounts).
	 */
	private long[] __getProductIds(
			final Collection<ProductAmountTO> productAmounts
			) {
		final long[] result = new long[productAmounts.size()];

		final Iterator<ProductAmountTO> iterator = productAmounts.iterator();
		for (int index = 0; iterator.hasNext(); index++) {
			result[index] = iterator.next().getProduct().getId();
		}

		return result;
	}

	/**
	 * Issues orders for the given products to be transferred to the calling
	 * store. This makes the products unavailable in stock at the supplying
	 * stores. Makes distributed calls at all store servers whose stores have to
	 * deliver products.
	 * 
	 * @param storeProductAmounts
	 *            products/amounts that have to be delivered
	 * @param destinationStore
	 *            the store to deliver the products to (the calling store)
	 * @return
	 *         list of entries for stores where product movement failed
	 */
	private Map<StoreTO, Collection<ProductAmountTO>> __issueProductMovements(
			final Map<StoreTO, Collection<ProductAmountTO>> storeProductAmounts,
			final Store destinationStore
			) {
		final Map<StoreTO, Collection<ProductAmountTO>> result = Maps.newHashMap();
		final StoreTO destinationStoreTO = FillTransferObjects.fillStoreTO(destinationStore);

		for (final Entry<StoreTO, Collection<ProductAmountTO>> storeEntry : storeProductAmounts.entrySet()) {
			final StoreTO originStoreTO = storeEntry.getKey();
			final Collection<ProductAmountTO> productAmounts = storeEntry.getValue();

			final boolean movementIssued = this.__issueProductMovement(
					productAmounts, originStoreTO, destinationStoreTO
					);

			if (movementIssued) {
				result.put(originStoreTO, productAmounts);
			}
		}

		return result;
	}

	/**
	 * Issues product movement order for products in a single store.
	 * 
	 * @returns
	 *          {@code true} if the movement was successfully issued, {@code false} otherwise
	 */
	private boolean __issueProductMovement(
			final Collection<ProductAmountTO> productAmounts,
			final StoreTO originStore, final StoreTO destinationStore
			) {
		try {
			//
			// Connect to the store, create a description of the product
			// movement and mark the required products as unavailable.
			//
			final IStoreInventoryManager sourceStore = ApplicationHelper.getComponent(
					Names.getStoreRemoteName(originStore.getName()), IStoreInventoryManager.class
					);

			final ProductMovementTO movedProducts = this.__createProductMovement(
					productAmounts, originStore, destinationStore
					);

			sourceStore.markProductsUnavailableInStock(movedProducts);
			return true;

		} catch (final Exception e) {
			LOG.warn(String.format(
					"Error marking products for transfer from store %s to store %s: %s",
					originStore.getName(), destinationStore.getName(), e.getMessage()
					));
			return false;
		}
	}

	/**
	 * Helper method to build a ProductMovement for a given combination of
	 * deliveringStore and targetStore. Returns new {@link ProductMovementTO} based on createProductMovements.
	 */
	private ProductMovementTO __createProductMovement(
			final Collection<ProductAmountTO> productAmounts,
			final StoreTO originStore, final StoreTO destinationStore
			) {
		final ProductMovementTO result = new ProductMovementTO();
		result.setOriginStore(originStore);
		result.setDestinationStore(destinationStore);
		result.setProductAmounts(productAmounts);

		return result;
	}

	/**
	 * Sums up the incoming productAmounts of all stores
	 * 
	 * @param storeProductAmounts
	 *            a map of stores and their respective amounts of available products
	 * @return
	 *         An array of ProductAmounts. Might contain duplicate product entries.
	 *         Those duplicates have to be respected.
	 */
	private ProductAmountTO[] __sumUpIncomingProducts(
			final Map<StoreTO, Collection<ProductAmountTO>> storeProductAmounts
			) {
		final List<ProductAmountTO> result = new ArrayList<ProductAmountTO>();

		for (final Collection<ProductAmountTO> productAmounts : storeProductAmounts.values()) {
			result.addAll(productAmounts);
		}

		return result.toArray(new ProductAmountTO[result.size()]);
	}

	/**
	 * Determines the available products for every store from the list of stores.
	 * s
	 * 
	 * @param stores
	 *            the stores to look up
	 * @param productIds
	 *            the products ids to look for
	 * @param pctx
	 *            the lookup is done locally within this PersistenceContext
	 * @return
	 *         A {@link Map} containing the available StockItems per Store.
	 */
	private Map<Store, Collection<StockItem>> __getOfferedStockItemsPerStore(
			final Collection<Store> stores, final long[] productIds,
			final IPersistenceContext pctx
			) {
		final Map<Store, Collection<StockItem>> result =
				new HashMap<Store, Collection<StockItem>>();

		for (final Store store : stores) {
			final Collection<StockItem> stockItems = this.storeQuery.queryStockItemsByProductId(
					store.getId(), productIds, pctx
					);

			result.put(store, stockItems);
		}

		return result;
	}

	/**
	 * Simple test for geographical adjacency: the location strings of each store
	 * are compared to the calling stores location using "compareTo".
	 * 
	 * @param stores
	 *            all stores possibly in the region
	 * @param destinationLocation
	 *            the location of the store to check adjacency for
	 * @return
	 *         a map of stores and their distance to the calling store
	 */
	private Map<Store, Integer> __getStoreDistances(
			final Collection<Store> stores, final String destinationLocation
			) {
		final Map<Store, Integer> result = new HashMap<Store, Integer>();

		for (final Store store : stores) {
			final String location = store.getLocation();
			result.put(store, this.__getStoreDistance(location, destinationLocation));
		}

		return result;
	}

	/**
	 * Returns the distance between two stores.
	 */
	private int __getStoreDistance(
			final String originLocation, final String destinationLocation
			) {
		// currently simply abs (compareTo):
		return Math.abs(destinationLocation.compareTo(originLocation));
	}

}
