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

import java.util.List;

import org.cocome.tradingsystem.inventory.data.enterprise.Product;
import org.cocome.tradingsystem.inventory.data.enterprise.ProductSupplier;
import org.cocome.tradingsystem.inventory.data.enterprise.TradingEnterprise;
import org.cocome.tradingsystem.inventory.data.store.OrderEntry;
import org.cocome.tradingsystem.inventory.data.store.ProductOrder;
import org.cocome.tradingsystem.inventory.data.store.StockItem;
import org.cocome.tradingsystem.inventory.data.store.Store;
import org.cocome.tradingsystem.util.java.Lists;

/**
 * Generates primitive the trading system entities. This generator only handles
 * creation of [randomized] primitive entities -- the relations between the
 * entities are handled by the {@link DatabaseGenerator}.
 * 
 * @author Lubomir Bulej
 */
final class EntityGenerator {

	interface Factory<T> {
		T newEntity(int index);
	}

	//

	private final IEntityConfiguration __config;

	//

	EntityGenerator(final IEntityConfiguration config) {
		__config = config;
	}

	//

	private static <T> List<T> _generateEntities(
			final int count, final Factory<T> factory
			) {
		final List<T> result = Lists.newArrayList();
		for (int index = 0; index < count; index++) {
			result.add(factory.newEntity(index));
		}

		return result;
	}

	//

	List<TradingEnterprise> createEnterprises(final int count) {
		return _generateEntities(count, new Factory<TradingEnterprise>() {
			@Override
			public TradingEnterprise newEntity(final int index) {
				final TradingEnterprise enterprise = new TradingEnterprise();
				enterprise.setName(__config.getEnterpriseName(index));
				enterprise.setStores(Lists.<Store> newArrayList());
				enterprise.setSuppliers(Lists.<ProductSupplier> newArrayList());

				return enterprise;
			}
		});
	}

	List<Store> createStores(final int count) {
		return _generateEntities(count, new Factory<Store>() {
			@Override
			public Store newEntity(final int index) {
				final Store store = new Store();
				store.setName(__config.getStoreName(index));
				store.setLocation(__config.getStoreLocation(index));
				store.setStockItems(Lists.<StockItem> newArrayList());
				store.setProductOrders(Lists.<ProductOrder> newArrayList());

				return store;
			}
		});
	}

	//

	List<Product> createProducts(
			final String namePrefix, final long barcodeOffset, final int count
			) {
		return _generateEntities(count, new Factory<Product>() {
			@Override
			public Product newEntity(final int index) {
				final Product product = new Product();
				product.setName(__config.getProductName(namePrefix, index));
				product.setBarcode(__config.getProductBarcode(barcodeOffset, index));
				product.setPurchasePrice(__config.getProductPurchasePrice());

				return product;
			}
		});
	}

	//

	List<StockItem> createStockItems(final int count) {
		return _generateEntities(count, new Factory<StockItem>() {
			@Override
			@SuppressWarnings("unused")
			public StockItem newEntity(final int index) {
				final StockItem stockitem = new StockItem();
				stockitem.setAmount(__config.getStockItemAmount());
				stockitem.setMinStock(__config.getStockItemMinStock());
				stockitem.setMaxStock(__config.getStockItemMaxStock());

				return stockitem;
			}
		});
	}

	List<ProductSupplier> createSuppliers(final String prefix, final int count) {
		return _generateEntities(count, new Factory<ProductSupplier>() {
			@Override
			public ProductSupplier newEntity(final int index) {
				final ProductSupplier supplier = new ProductSupplier();
				supplier.setName(__config.getSupplierName(prefix, index));

				return supplier;
			}
		});
	}

	List<ProductOrder> createProductOrders(final int count) {
		return _generateEntities(count, new Factory<ProductOrder>() {
			@Override
			@SuppressWarnings("unused")
			public ProductOrder newEntity(final int index) {
				final ProductOrder order = new ProductOrder();
				order.setOrderingDate(__config.getProductOrderOrderingDate());
				order.setDeliveryDate(__config.getProductOrderDeliveryDate());
				order.setOrderEntries(Lists.<OrderEntry> newArrayList());

				return order;
			}
		});
	}

	List<OrderEntry> createOrderEntries(final int count) {
		return _generateEntities(count, new Factory<OrderEntry>() {
			@Override
			public OrderEntry newEntity(@SuppressWarnings("unused") final int index) {
				return new OrderEntry();
			}
		});
	}

}
