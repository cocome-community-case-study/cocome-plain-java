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

package org.cocome.tradingsystem.inventory.application.store;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.cocome.tradingsystem.inventory.application.productdispatcher.IProductDispatcher;
import org.cocome.tradingsystem.inventory.data.DataFactory;
import org.cocome.tradingsystem.inventory.data.enterprise.Product;
import org.cocome.tradingsystem.inventory.data.persistence.IPersistenceContext;
import org.cocome.tradingsystem.inventory.data.persistence.TransactionWrapper;
import org.cocome.tradingsystem.inventory.data.persistence.TransactionWrapper.CheckedOperation;
import org.cocome.tradingsystem.inventory.data.persistence.TransactionWrapper.CheckedSimpleOperation;
import org.cocome.tradingsystem.inventory.data.persistence.TransactionWrapper.Operation;
import org.cocome.tradingsystem.inventory.data.persistence.TransactionWrapper.SimpleOperation;
import org.cocome.tradingsystem.inventory.data.store.OrderEntry;
import org.cocome.tradingsystem.inventory.data.store.ProductOrder;
import org.cocome.tradingsystem.inventory.data.store.StockItem;
import org.cocome.tradingsystem.inventory.data.store.Store;
import org.cocome.tradingsystem.inventory.data.store.IStoreQuery;
import org.cocome.tradingsystem.util.ComponentNotAvailableException;
import org.cocome.tradingsystem.util.RemoteComponent;
import org.cocome.tradingsystem.util.java.Lists;
import org.cocome.tradingsystem.util.java.Maps;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Implements the server part of the store application.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
final class StoreServer extends UnicastRemoteObject
		implements IStoreInventoryManager, IStoreInventory {

	private static final long serialVersionUID = -529765757261183369L;

	private static final Logger __log__ = Logger.getLogger(StoreServer.class);

	//

	private final IStoreQuery __storeQuery =
			DataFactory.getInstance().getStoreQuery();

	//

	/** Identifier of the corresponding store entity. */
	private final long __storeId;

	/** Remote reference to the product dispatcher. */
	private final RemoteComponent<IProductDispatcher> __remoteDispatcher;

	//

	private StoreServer(
			final long storeId,
			final RemoteComponent<IProductDispatcher> remoteDispatcher) throws RemoteException {
		__storeId = storeId;
		__remoteDispatcher = remoteDispatcher;
	}

	//

	public ProductWithStockItemTO changePrice(final StockItemTO stockItemTO) {
		return TransactionWrapper.execute(new Operation<ProductWithStockItemTO>() {
			@Override
			public ProductWithStockItemTO execute(final IPersistenceContext pctx) {
				final StockItem si = __storeQuery.queryStockItemById(
						stockItemTO.getId(), pctx
						);

				si.setSalesPrice(stockItemTO.getSalesPrice());

				return FillTransferObjects.fillProductWithStockItemTO(si);
			}
		});
	}

	public List<ProductWithSupplierTO> getAllProducts() {
		return TransactionWrapper.execute(new Operation<List<ProductWithSupplierTO>>() {
			@Override
			public List<ProductWithSupplierTO> execute(final IPersistenceContext pctx) {
				final Collection<Product> products =
						__storeQuery.queryProducts(__storeId, pctx);

				final List<ProductWithSupplierTO> result = Lists.newArrayList();
				for (final Product product : products) {
					result.add(
							FillTransferObjects.fillProductWithSupplierTO(product)
							);
				}

				return result;
			}
		});
	}

	@Override
	public List<ProductWithSupplierAndStockItemTO> getProductsWithStockItems() {
		return TransactionWrapper.execute(new Operation<List<ProductWithSupplierAndStockItemTO>>() {
			@Override
			public List<ProductWithSupplierAndStockItemTO> execute(final IPersistenceContext pctx) {
				final Collection<StockItem> stockItems =
						__storeQuery.queryAllStockItems(__storeId, pctx);

				final List<ProductWithSupplierAndStockItemTO> result = Lists.newArrayList();
				for (final StockItem stockItem : stockItems) {
					result.add(
							FillTransferObjects.fillProductWithSupplierAndStockItemTO(stockItem)
							);
				}

				return result;
			}
		});
	}

	@Override
	public ComplexOrderTO getOrder(final long orderId) {
		return TransactionWrapper.execute(new Operation<ComplexOrderTO>() {
			@Override
			public ComplexOrderTO execute(final IPersistenceContext pctx) {
				return FillTransferObjects.fillComplexOrderTO(
						__storeQuery.queryOrderById(orderId, pctx)
						);
			}
		});
	}

	@Override
	public List<ComplexOrderTO> getOutstandingOrders() {
		return TransactionWrapper.execute(new Operation<List<ComplexOrderTO>>() {
			@Override
			public List<ComplexOrderTO> execute(final IPersistenceContext pctx) {
				final Collection<ProductOrder> orders =
						__storeQuery.queryOutstandingOrders(__storeId, pctx);

				final List<ComplexOrderTO> result = Lists.newArrayList();
				for (final ProductOrder order : orders) {
					result.add(FillTransferObjects.fillComplexOrderTO(order));
				}

				return result;
			}
		});
	}

	@Override
	public List<ProductWithStockItemTO> getProductsWithLowStock() {
		return TransactionWrapper.execute(new Operation<List<ProductWithStockItemTO>>() {
			@Override
			public List<ProductWithStockItemTO> execute(final IPersistenceContext pctx) {
				final Collection<StockItem> stockItems =
						__storeQuery.queryLowStockItems(__storeId, pctx);

				final List<ProductWithStockItemTO> result = Lists.newArrayList();
				for (final StockItem si : stockItems) {
					result.add(FillTransferObjects.fillProductWithStockItemTO(si));
				}

				return result;
			}
		});
	}

	@Override
	public StoreWithEnterpriseTO getStore() {
		return TransactionWrapper.execute(new Operation<StoreWithEnterpriseTO>() {
			@Override
			public StoreWithEnterpriseTO execute(final IPersistenceContext pctx) {
				return FillTransferObjects.fillStoreWithEnterpriseTO(
						__storeQuery.queryStoreById(__storeId, pctx)
						);
			}
		});
	}

	@Override
	public List<ComplexOrderTO> orderProducts(final ComplexOrderTO complexOrder) {
		return TransactionWrapper.execute(new Operation<List<ComplexOrderTO>>() {
			@Override
			public List<ComplexOrderTO> execute(final IPersistenceContext pctx) {
				final IStoreQuery sq = __storeQuery;

				final HashMap<Long, ArrayList<OrderEntry>> ordersBySupplier = Maps.newHashMap();
				for (final ComplexOrderEntryTO coeto : complexOrder.getOrderEntryTOs()) {
					final Product product = sq.queryProductById(
							coeto.getProductTO().getId(), pctx
							);

					__debug("Found product %d", coeto.getProductTO().getId());

					final OrderEntry oe = new OrderEntry();
					oe.setProduct(product);
					oe.setAmount(coeto.getAmount());

					pctx.makePersistent(oe);

					//

					final long supplierId = product.getSupplier().getId();
					ArrayList<OrderEntry> entries = ordersBySupplier.get(supplierId);
					if (entries == null) {
						entries = Lists.newArrayList();
						ordersBySupplier.put(supplierId, entries);
					}
					entries.add(oe);
				}

				//

				System.out.println(ordersBySupplier);
				final Store store = sq.queryStoreById(__storeId, pctx);
				final List<ProductOrder> orders = Lists.newArrayList();
				for (final List<OrderEntry> orderEntries : ordersBySupplier.values()) {
					final ProductOrder po = new ProductOrder();
					po.setOrderEntries(orderEntries);
					po.setStore(store);
					// set OrderingDate to NOW
					po.setOrderingDate(new Date());
					pctx.makePersistent(po);

					orders.add(po);
				}

				//

				final List<ComplexOrderTO> result = Lists.newArrayList();
				for (final ProductOrder order : orders) {
					result.add(FillTransferObjects.fillComplexOrderTO(order));
				}

				return result;
			}
		});
	}

	@Override
	public void rollInReceivedOrder(final long orderId) throws InvalidRollInRequestException {
		TransactionWrapper.execute(new CheckedSimpleOperation<InvalidRollInRequestException>() {
			@Override
			public void execute(final IPersistenceContext pctx) throws InvalidRollInRequestException {
				final ProductOrder order = __storeQuery.queryOrderById(orderId, pctx);

				//
				// Ignore the roll in if the order has been already rolled in.
				//
				if (order.getDeliveryDate() != null) {
					final String message = String.format(
							"Product order %d already rolled in.", order.getId()
							);

					__warn(message);
					throw new InvalidRollInRequestException(message);
				}

				//
				// Ignore the roll in if the order is for different store.
				//
				if (order.getStore().getId() != __storeId) {
					final String message = String.format(
							"Order in store %d cannot be rolled-in by store %d",
							order.getStore().getId(), __storeId
							);

					__error(message);
					throw new InvalidRollInRequestException(message);
				}

				// set DeliveryDate to NOW
				order.setDeliveryDate(new Date());

				for (final OrderEntry oe : order.getOrderEntries()) {
					final StockItem si = __storeQuery.queryStockItem(
							__storeId, oe.getProduct().getBarcode(), pctx
							);

					//
					// Create a new stock item for completely new products.
					//
					if (si == null) {
						// TODO Create a new stock item if it does not exist
					}

					final Product product = si.getProduct();
					final long oldAmount = si.getAmount();
					final long newAmount = oldAmount + oe.getAmount();

					si.setAmount(newAmount);

					__debug(
							"%s (%d) stock increased from %d to %d.",
							product.getName(), product.getBarcode(),
							oldAmount, newAmount
					);
				}
			}

		});
	}

	@Override
	public ProductWithStockItemTO getProductWithStockItem(
			final long productBarCode
			) throws NoSuchProductException {
		return TransactionWrapper.execute(new CheckedOperation<ProductWithStockItemTO, NoSuchProductException>() {
			@Override
			public ProductWithStockItemTO execute(final IPersistenceContext pctx) throws NoSuchProductException {
				final StockItem stockItem =
						__storeQuery.queryStockItem(__storeId, productBarCode, pctx);

				if (stockItem == null) {
					throw new NoSuchProductException(
							"There is no stock item for product with barcode " +
									productBarCode
					);
				}

				return FillTransferObjects.fillProductWithStockItemTO(stockItem);
			}
		});
	}

	public void accountSale(final SaleTO sale) {
		__bookSale(sale);
	}

	/**
	 * Registers the sale of products in the store inventory. Updates the amount
	 * of stock items and checks for items that are running low on stock so that
	 * they can be transported from nearby stores with sufficient stock. Used
	 * for realization of UC 1 and UC 8.
	 * 
	 * @param saleTO
	 *            the sale to be registered in stock
	 */
	private void __bookSale(final SaleTO saleTO) {
		TransactionWrapper.execute(new SimpleOperation() {
			@Override
			public void execute(final IPersistenceContext pctx) {
				for (final ProductWithStockItemTO pwsto : saleTO.getProductTOs()) {
					final StockItem si = __storeQuery.queryStockItemById(
							pwsto.getStockItemTO().getId(), pctx
							);

					si.setAmount(si.getAmount() - 1);
				}
			}
		});

		//
		// Check for items running low on stock. Required for UC 8.
		// Alternative (and probably better) design would be to check
		// once in a while from separate thread, not on every sale.
		//
		try {
			__checkForLowRunningGoods();

		} catch (final Exception e) {
			__warn(
					"Failed UC8! Could not transport low-stock items from other stores: %s",
					e.getMessage());
		}
	}

	@Override
	@SuppressWarnings("unused")
	public ComplexOrderEntryTO[] getStockItems(final ProductTO[] requiredProductTOs) {
		// TODO Auto-generated method stub
		throw new RuntimeException("TODO: SDQ implement");
	}

	@Override
	public void markProductsUnavailableInStock(
			final ProductMovementTO movedProducts
			) throws ProductNotAvailableException {
		TransactionWrapper.execute(new CheckedSimpleOperation<ProductNotAvailableException>() {
			@Override
			public void execute(final IPersistenceContext pctx) throws ProductNotAvailableException {
				for (final ProductAmountTO movedProduct : movedProducts.getProductAmounts()) {
					final ProductTO productTO = movedProduct.getProduct();
					final long barcode = productTO.getBarcode();
					final StockItem stockItem =
							__storeQuery.queryStockItem(__storeId, barcode, pctx);

					if (stockItem == null) {
						throw new ProductNotAvailableException(String.format(
								"Store %d has no product with barcode %d",
								__storeId, barcode
								));
					}

					//

					final long availableAmount = stockItem.getAmount();
					final long movedAmount = movedProduct.getAmount();

					if (availableAmount < movedAmount) {
						throw new ProductNotAvailableException(String.format(
								"Store %d only has %d product(s) with barcode %d, but %d required",
								__storeId, availableAmount, barcode, movedAmount
								));
					}

					// set new remaining stock amount
					stockItem.setAmount(availableAmount - movedAmount);

					//
					// TODO: virtual printout is missing
					// A list of all products that need to be delivered should be printed out.
					//
					final StoreTO originStore = movedProducts.getOriginStore();
					final StoreTO destinationStore = movedProducts.getDestinationStore();
					System.out.printf(
							"[%s at %s] Ship %s, barcode %d to %s at %s, amount %d\n",
							originStore.getName(), originStore.getLocation(),
							productTO.getName(), barcode,
							destinationStore.getName(), destinationStore.getLocation(),
							movedAmount
							);
				}
			}
		});
	}

	/**
	 * Checks for goods that run low. If there are goods running low they
	 * transported from nearby stores in the enterprise.
	 * <p>
	 * Technically, the operation is performed by the product dispatcher. The store only needs to provide it with products that are low on stock. If there is any
	 * problem communicating with the product dispatcher, the operation will not be performed. In case of transient errors, the operation may succeed during next
	 * check for low-stock products.
	 * <p>
	 * Required for UC 8
	 */
	private void __checkForLowRunningGoods() throws Exception {
		TransactionWrapper.execute(new CheckedSimpleOperation<Exception>() {
			@Override
			public void execute(final IPersistenceContext pctx) throws Exception {
				//
				// Determine the products and amounts of items that are
				// actually required, i.e. items that are really low on
				// stock, including their current incoming amount.
				//
				final Collection<ProductAmountTO>
				requiredProducts = __findRequiredProducts(pctx);

				if (requiredProducts.size() < 1) {
					return;
				}

				//
				// Order required products from stores determined by the
				// product dispatcher.
				//
				final ProductAmountTO[] incomingProducts =
						__orderRequiredProducts(requiredProducts, pctx);

				if (incomingProducts.length < 1) {
					return;
				}

				//
				// Mark the products coming from other stores as incoming.
				//
				__registerIncomingProducts(incomingProducts, pctx);
			}
		});
	}

	private Collection<ProductAmountTO> __findRequiredProducts(
			final IPersistenceContext pctx
			) {
		//
		// Query the store inventory for apparently low stock items,
		// without consider items coming from other stores.
		//
		final Collection<StockItem> lowStockItems = __storeQuery.queryLowStockItems(__storeId, pctx);
		if (lowStockItems.size() < 1) {
			return Collections.emptyList();
		}

		//
		// Filter the low-stock items to determine items that are really
		// low on stock and should be transported from other stores.
		//
		final Collection<StockItem> itemsToOrder = __selectItemsToOrder(lowStockItems);
		if (itemsToOrder.size() < 1) {
			return Collections.emptyList();
		}

		//
		// Finally determine the product amounts that need ot be transported
		// from nearby stores.
		//
		return __calculateRequiredAmounts(itemsToOrder);
	}

	/**
	 * Selects and returns stock items that are really low on stock and will be
	 * ordered from other stores. Many items can be low on stock, but have more
	 * stock incoming that along with the current stock satisfies the minimal
	 * stock condition. Such items are filtered out and only those really low on
	 * stock are left.
	 */
	private Collection<StockItem> __selectItemsToOrder(
			final Collection<StockItem> stockItems
			) {
		final Collection<StockItem> result = new LinkedList<StockItem>();
		SCAN: for (final StockItem stockItem : stockItems) {
			final Product product = stockItem.getProduct();
			__debug("\t%s, barcode %d, amount %d, incoming %d, min stock %d",
					product.getName(), product.getBarcode(),
					stockItem.getAmount(), stockItem.getIncomingAmount(),
					stockItem.getMinStock());

			final long virtualAmount = stockItem.getAmount() + stockItem.getIncomingAmount();
			if (virtualAmount >= stockItem.getMinStock()) {
				__debug("\t\tvirtual stock %d => not low stock", virtualAmount);
				continue SCAN;
			}

			result.add(stockItem);
		}

		__debug("%d really low-stock items in store %d", result.size(), __storeId);
		return result;
	}

	/**
	 * Orders by default the minimum stock items for each low running
	 * product/good.
	 * <p>
	 * Required for UC 8
	 * 
	 * @param stockItems
	 *            collection of product stock items that run low
	 * @return
	 *         Collection of Product/Amount tuples for each product, which
	 *         represents the required amount of each product.
	 */
	private Collection<ProductAmountTO> __calculateRequiredAmounts(
			final Collection<StockItem> stockItems
			) {
		final Collection<ProductAmountTO> result = Lists.newArrayList();

		//
		// Order at least minimum stock for each item, but do not exceed stock
		// limits. The stock of each item in the collections is guaranteed lower
		// than the minimum (including the incoming amount), so we will never
		// exceed the maximum level.
		//
		for (final StockItem stockItem : stockItems) {
			long orderAmount = stockItem.getMinStock();
			if (2 * stockItem.getMinStock() >= stockItem.getMaxStock()) {
				orderAmount = stockItem.getMaxStock() - stockItem.getMinStock();
			}

			final ProductAmountTO pa = new ProductAmountTO();
			pa.setProduct(FillTransferObjects.fillProductTO(
					stockItem.getProduct())
					);
			pa.setAmount(orderAmount);

			result.add(pa);
		}

		__debug("%d products to be ordered by store %d", result.size(), __storeId);
		return result;
	}

	/**
	 * Requests the product dispatcher to determine the stores to transfer
	 * goods from and to issue the product movement orders. Returns the
	 * amounts of items incoming from other stores.
	 * 
	 * @throws ComponentNotAvailableException
	 *             if the product dispatcher cannot be found
	 * @throws RemoteException
	 *             if there is a problem communicating with the dispatcher
	 */
	private ProductAmountTO[] __orderRequiredProducts(
			final Collection<ProductAmountTO> requiredProducts,
			final IPersistenceContext pctx
			) throws RemoteException {
		//
		// Connect to the product dispatcher and order the required products
		// from other stores in the enterprise. Do nothing if the connection
		// cannot be established.
		//
		final IProductDispatcher dispatcher = __remoteDispatcher.get();
		final Store store = __storeQuery.queryStoreById(__storeId, pctx);

		final ProductAmountTO[] result = dispatcher.dispatchProductsFromOtherStores(
				store.getId(), requiredProducts
				);

		__debug("%d products incoming to store %d", result.length, __storeId);
		return result;
	}

	/**
	 * Registers the products coming from other stores by increasing the
	 * incoming amount of stock items corresponding to the incoming products.
	 */
	private void __registerIncomingProducts(
			final ProductAmountTO[] incomingProducts,
			final IPersistenceContext pctx
			) {
		for (final ProductAmountTO incomingProductTO : incomingProducts) {
			final ProductTO incomingProduct = incomingProductTO.getProduct();
			final StockItem stockItem = __storeQuery.queryStockItem(
					__storeId, incomingProduct.getBarcode(), pctx
					);

			final long incomingAmount = incomingProductTO.getAmount();
			stockItem.setIncomingAmount(
					stockItem.getIncomingAmount() + incomingAmount
					);

			__debug("\t%s, barcode %d, incoming amount %d",
					incomingProduct.getName(), incomingProduct.getBarcode(),
					incomingAmount);
		}
	}

	//

	/**
	 * Creates an instance of a store component. The store component MUST
	 * have a database counterpart with the given name.
	 * 
	 * @param storeName
	 *            name of the store database entity
	 * @return
	 *         store server instance
	 */
	static StoreServer newInstance(
			final String storeName, final RemoteComponent<IProductDispatcher> remoteDispatcher
			) throws RemoteException {
		return TransactionWrapper.execute(new CheckedOperation<StoreServer, RemoteException>() {
			@Override
			public StoreServer execute(final IPersistenceContext pctx) throws RemoteException {
				final IStoreQuery sq = DataFactory.getInstance().getStoreQuery();
				final Store storeEntity = sq.queryStore(storeName, "%", pctx);
				if (storeEntity != null) {
					return new StoreServer(storeEntity.getId(), remoteDispatcher);

				} else {
					final String message = String.format(
							"Could not find store named '%s' in the database",
							storeName
							);

					__error(message);
					throw new RuntimeException(message);
				}
			}
		});
	}

	//

	private static void __debug(final String format, final Object... args) {
		__log(Level.DEBUG, format, args);
	}

	private static void __warn(final String format, final Object... args) {
		__log(Level.WARN, format, args);
	}

	private static void __error(final String format, final Object... args) {
		__log(Level.ERROR, format, args);
	}

	private static void __log(final Level level, final String format, final Object... args) {
		if (__log__.isEnabledFor(level)) {
			__log__.log(level, String.format(format, args));
		}
	}

}
