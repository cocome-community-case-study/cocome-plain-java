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

package org.cocome.tradingsystem.inventory.data.generator;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.cocome.tradingsystem.inventory.data.enterprise.Product;
import org.cocome.tradingsystem.inventory.data.enterprise.ProductSupplier;
import org.cocome.tradingsystem.inventory.data.enterprise.TradingEnterprise;
import org.cocome.tradingsystem.inventory.data.store.OrderEntry;
import org.cocome.tradingsystem.inventory.data.store.ProductOrder;
import org.cocome.tradingsystem.inventory.data.store.StockItem;
import org.cocome.tradingsystem.inventory.data.store.Store;
import org.cocome.tradingsystem.util.java.Iterables;
import org.cocome.tradingsystem.util.java.Lists;
import org.cocome.tradingsystem.util.java.Sets;

/**
 * Generates database contents. This class is responsible for creating
 * associations between primitive trading system entities produced by {@link EntityGenerator}.
 * 
 * @author Lubomir Bulej
 */
final class DatabaseGenerator {

	private static final long __BARCODE_BASE__ = 777;

	//

	private final DatabaseConfiguration __config;

	private final IEntityConfiguration __entityConfig;

	private final EntityGenerator __entityGenerator;

	//

	private DatabaseGenerator(
			final DatabaseConfiguration config,
			final IEntityConfiguration entityConfig) {
		__config = config;
		__entityConfig = entityConfig;
		__entityGenerator = new EntityGenerator(entityConfig);
	}

	public static DatabaseContent generate(
			final DatabaseConfiguration databaseConfig,
			final IEntityConfiguration entityConfig
			) {
		return new DatabaseGenerator(databaseConfig, entityConfig).__generate();
	}

	//

	private DatabaseContent __generate() {
		//
		// Create stores to carry the products.
		//
		final List<Store> stores = __entityGenerator.createStores(__config.getStoreCount());

		//
		// Create shared products and their suppliers and assign stock items for
		// all shared products to every store, so that each store carries the
		// shared products.
		//
		final List<Product> sharedProducts = __entityGenerator.createProducts(
				"Shared ", __BARCODE_BASE__,
				__config.getSharedProductCount()
				);

		final List<ProductSupplier> sharedSuppliers = __entityGenerator.createSuppliers(
				"Shared ", __config.getSharedSupplierCount()
				);
		__distributeProductsAmongSuppliers(sharedProducts, sharedSuppliers);

		final List<StockItem> sharedStockItems = __assignProductsToStores(sharedProducts, stores);

		//
		// Create unique products and their suppliers and distribute stock items
		// for the products evenly among all stores, so that no two stores carry
		// the same product.
		//
		final List<Product> uniqueProducts = __entityGenerator.createProducts(
				"Unique ", __BARCODE_BASE__ + __config.getSharedProductCount(),
				__config.getUniqueProductCount()
				);

		final List<ProductSupplier> uniqueSuppliers = __entityGenerator.createSuppliers(
				"Unique ", __config.getUniqueSupplierCount()
				);
		__distributeProductsAmongSuppliers(uniqueProducts, uniqueSuppliers);

		final List<StockItem> uniqueStockItems = __distributeProductsAmongStores(uniqueProducts, stores);

		//
		// Create enterprises and assign the stores to the enterprises. Then
		// collect suppliers from individual stores in each enterprise and
		// assign the suppliers to the respective enterprise.
		//
		final List<TradingEnterprise> enterprises = __entityGenerator.createEnterprises(
				__config.getEnterpriseCount()
				);
		__assignStoresToEnterprises(stores, enterprises);
		__assignSuppliersToEnterprises(enterprises);

		//
		// Create product orders and distribute them among stores.
		// Create order entries and distribute them among product stores.
		//
		final List<ProductOrder> productOrders = __entityGenerator.createProductOrders(
				__config.getProductOrderCount()
				);
		__distributeProductOrdersAmongStores(productOrders, stores);

		final List<OrderEntry> orderEntries = __entityGenerator.createOrderEntries(
				__config.getOrderEntryCount()
				);
		__distributeOrderEntriesAmongProductOrders(orderEntries, productOrders);
		__assignProductsToOrderEntries(stores);

		//
		// Create the result.
		//
		final DatabaseContent result = new DatabaseContent();
		result.enterprises.addAll(enterprises);
		result.stores.addAll(stores);
		result.products.addAll(sharedProducts);
		result.products.addAll(uniqueProducts);
		result.suppliers.addAll(sharedSuppliers);
		result.suppliers.addAll(uniqueSuppliers);
		result.sharedStockItems.addAll(sharedStockItems);
		result.uniqueStockItems.addAll(uniqueStockItems);
		result.productOrders.addAll(productOrders);
		result.orderEntries.addAll(orderEntries);

		return result;
	}

	private void __assignProductsToOrderEntries(final List<Store> stores) {
		//
		// For each store, get product orders and populate their order entries
		// with random products.
		//
		for (final Store store : stores) {
			final Collection<StockItem> items = store.getStockItems();

			//
			// For each product order, fill the order entries with randomly
			// selected products. There should be no duplicate products in
			// one product order.
			//
			for (final ProductOrder order : store.getProductOrders()) {
				final Iterator<StockItem> randomItems = Iterables.randomIterator(items);

				for (final OrderEntry entry : order.getOrderEntries()) {
					final StockItem item = randomItems.next();
					entry.setProduct(item.getProduct());

					long amount = __entityConfig.getOrderEntryAmount(
							item.getAmount()
							);

					entry.setAmount(amount);

					//
					// Keep track of incoming items. This will be used to
					// adjust product stock amount and max stock attributes.
					//
					item.setIncomingAmount(item.getIncomingAmount() + amount);
				}
			}

			//
			// Ensure that the sum of amounts in order entries does not exceed
			// the amount of items available in the store. Clear the incoming
			// amount afterwards.
			//
			for (final StockItem item : items) {
				item.setAmount(Math.max(item.getIncomingAmount(), item.getAmount()));
				item.setMaxStock(Math.max(item.getAmount(), item.getMaxStock()));
				item.setIncomingAmount(0);
			}
		}
	}

	private void __distributeOrderEntriesAmongProductOrders(
			final List<OrderEntry> entries, final List<ProductOrder> orders
			) {
		final Iterator<ProductOrder> randomOrders = Iterables.samplingIterator(orders);
		for (final OrderEntry entry : entries) {
			final ProductOrder order = randomOrders.next();
			entry.setOrder(order);
			order.getOrderEntries().add(entry);
		}
	}

	private void __distributeProductOrdersAmongStores(
			final List<ProductOrder> orders, final List<Store> stores
			) {
		final Iterator<Store> randomStores = Iterables.samplingIterator(stores);
		for (final ProductOrder order : orders) {
			final Store store = randomStores.next();
			order.setStore(store);
			store.getProductOrders().add(order);
		}
	}

	private static void __assignStoresToEnterprises(
			final List<Store> stores, final List<TradingEnterprise> enterprises
			) {
		final Iterator<TradingEnterprise> randomEnterprises = Iterables.samplingIterator(enterprises);

		for (final Store store : stores) {
			final TradingEnterprise enterprise = randomEnterprises.next();
			store.setEnterprise(enterprise);
			enterprise.getStores().add(store);
		}
	}

	private static void __assignSuppliersToEnterprises(
			final List<TradingEnterprise> enterprises
			) {
		for (final TradingEnterprise enterprise : enterprises) {
			final Set<ProductSupplier> suppliers = __collectEntepriseSuppliers(enterprise);
			enterprise.setSuppliers(suppliers);
		}
	}

	private static Set<ProductSupplier> __collectEntepriseSuppliers(
			final TradingEnterprise enterprise
			) {
		final Set<ProductSupplier> suppliers = Sets.newHashSet();
		for (final Store store : enterprise.getStores()) {
			for (final StockItem stockItem : store.getStockItems()) {
				suppliers.add(stockItem.getProduct().getSupplier());
			}
		}
		return suppliers;
	}

	private static void __distributeProductsAmongSuppliers(
			final List<Product> products, final List<ProductSupplier> suppliers
			) {
		//
		// Pick a random supplier for each product. This ensures that each
		// product has only a single supplier, while each supplier can provide
		// multiple products.
		//
		final Iterator<ProductSupplier> randomSuppliers = Iterables.samplingIterator(suppliers);

		for (final Product product : products) {
			product.setSupplier(randomSuppliers.next());
		}
	}

	private List<StockItem> __assignProductsToStores(
			final List<Product> sharedProducts, final List<Store> stores
			) {
		//
		// For each store, generate stock items for the given products
		// and assign the stock items to the store.
		//
		final List<StockItem> result = Lists.newArrayList();
		for (final Store store : stores) {
			final List<StockItem> stockItems = __createStockItems(sharedProducts);
			for (final StockItem stockItem : stockItems) {
				__assignStockItemToStore(stockItem, store);
			}

			result.addAll(stockItems);
		}

		return result;
	}

	private List<StockItem> __distributeProductsAmongStores(
			final List<Product> uniqueProducts, final List<Store> stores
			) {
		//
		// Generate stock items for the given products and distribute them
		// randomly among the stores.
		//
		final List<StockItem> result = __createStockItems(uniqueProducts);
		final Iterator<Store> randomStores = Iterables.samplingIterator(stores);

		for (final StockItem stockItem : result) {
			final Store store = randomStores.next();
			__assignStockItemToStore(stockItem, store);
		}

		return result;
	}

	private static void __assignStockItemToStore(
			final StockItem stockItem, final Store store
			) {
		stockItem.setStore(store);
		store.getStockItems().add(stockItem);
	}

	//

	private List<StockItem> __createStockItems(
			final List<Product> products
			) {
		// create stock items
		final int count = products.size();
		final List<StockItem> result = __entityGenerator.createStockItems(count);

		// map stock items to products
		for (int index = 0; index < count; index++) {
			final Product product = products.get(index);
			final StockItem stockItem = result.get(index);

			stockItem.setProduct(product);
			stockItem.setSalesPrice(
					__entityConfig.getStockItemSalesPrice(product.getPurchasePrice())
					);
		}

		return result;
	}

}
