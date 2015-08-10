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

import java.io.File;

/**
 * Fills the database with entities representing multiple enterprise and stores
 * based on a configurable number of stock items in the database (largest table
 * in the database). The counts of other entities are derived from the number of
 * stock items and preserve the ratios between numbers of items found in the
 * original database filler class (which was configured for 10000 stock items).
 * 
 * @author Lubomir Bulej
 * @author Vlastimil Babka
 */
public final class ItemizedDatabaseFiller {

	/**
	 * Provides parameters for a item-centric configuration of the database.
	 */
	private static final class Configuration extends DatabaseConfiguration {

		private static final int __MIN_ENTERPRISE_COUNT__ = 1;
		private static final int __MIN_SUPPLIER_COUNT__ = 1;
		private static final int __MIN_STORES_PER_ENTERPRISE__ = 1;
		private static final int __MIN_PRODUCTS_PER_STORE__ = 1;
		private static final int __MIN_ENTRIES_PER_ORDER__ = 1;

		//

		private static final double __STOCK_ITEMS_PER_ENTERPRISE__ = 1000;
		private static final double __STOCK_ITEMS_PER_STORE__ = 100;
		private static final double __STOCK_ITEMS_PER_PRODUCT__ = 10;
		private static final double __STOCK_ITEMS_PER_SUPPLIER__ = 500;

		private static final double __STOCK_ITEMS_PER_PRODUCT_ORDER__ = 50;
		private static final double __STOCK_ITEMS_PER_ORDER_ENTRY__ = 40;

		//

		private static final double __STORE_UNIQUE_TO_SHARED_ITEMS_RATIO__ = 1 / 6.0;
		private static final double __ENTERPRISE_UNIQUE_TO_SHARED_SUPPLIERS_RATIO = 1 / 3.0;

		//

		/** Total number of stock items in the database. */
		private final int __stockItemCount;

		/** Total number of enterprises in the database. */
		private final int __enterpriseCount;

		/** Total number of stores in the database. */
		private final int __storeCount;

		/** Total number of products in the database. */
		private final int __productCount;

		/** Total number of suppliers in the database. */
		private final int __supplierCount;

		/** Total number of unique product suppliers. */
		private final int __uniqueSupplierCount;

		/** Total number of product orders. */
		private final int __productOrderCount;

		/** Total number of product order entries. */
		private final int __orderEntryCount;

		//

		Configuration(final int stockItemCount) {
			final int enterpriseCount = Math.max(
					__MIN_ENTERPRISE_COUNT__,
					(int) Math.round(stockItemCount / __STOCK_ITEMS_PER_ENTERPRISE__)
					);

			final int storeCount = Math.max(
					enterpriseCount * __MIN_STORES_PER_ENTERPRISE__,
					(int) Math.round(stockItemCount / __STOCK_ITEMS_PER_STORE__)
					);

			final int productCount = Math.max(
					storeCount * __MIN_PRODUCTS_PER_STORE__,
					(int) Math.round(stockItemCount / __STOCK_ITEMS_PER_PRODUCT__)
					);

			//
			// Determine the number of suppliers. The total number is derived
			// from the number of stock items, with suppliers for unique
			// products representing a portion of all suppliers. The there are
			// any unique products, there must be at least one supplier for
			// them.
			//
			final int supplierCount = Math.max(
					__MIN_SUPPLIER_COUNT__ + Integer.signum(__uniqueProductCount(productCount)),
					(int) Math.round(stockItemCount / __STOCK_ITEMS_PER_SUPPLIER__)
					);

			final int uniqueSupplierCount = Math.max(
					Integer.signum(__uniqueProductCount(productCount)),
					(int) Math.round(
							supplierCount * __ENTERPRISE_UNIQUE_TO_SHARED_SUPPLIERS_RATIO
							)
					);

			//
			// Determine the number of product orders and order entries. If
			// there are no product orders, there can be no order entries.
			//
			final int productOrderCount = (int) Math.round(
					stockItemCount / __STOCK_ITEMS_PER_PRODUCT_ORDER__
					);

			final int orderEntryCount = Math.max(
					productOrderCount * __MIN_ENTRIES_PER_ORDER__,
					Integer.signum(productOrderCount) * (int) Math.round(stockItemCount / __STOCK_ITEMS_PER_ORDER_ENTRY__)
					);

			//

			__enterpriseCount = enterpriseCount;
			__supplierCount = supplierCount;
			__uniqueSupplierCount = uniqueSupplierCount;
			__stockItemCount = stockItemCount;
			__productCount = productCount;
			__storeCount = storeCount;
			__orderEntryCount = orderEntryCount;
			__productOrderCount = productOrderCount;
		}

		//

		@Override
		public int getEnterpriseCount() {
			return __enterpriseCount;
		}

		@Override
		public int getStoreCount() {
			return __storeCount;
		}

		@Override
		public int getStockItemCount() {
			return __stockItemCount;
		}

		@Override
		public int getProductCount() {
			return __productCount;
		}

		@Override
		public int getSharedProductCount() {
			return __productCount - getUniqueProductCount();
		}

		@Override
		public int getUniqueProductCount() {
			return __uniqueProductCount(__productCount);
		}

		private int __uniqueProductCount(final int productCount) {
			return (int) Math.round(
					productCount * __STORE_UNIQUE_TO_SHARED_ITEMS_RATIO__
					);
		}

		@Override
		public int getSupplierCount() {
			return __supplierCount;
		}

		@Override
		public int getSharedSupplierCount() {
			return __supplierCount - getUniqueSupplierCount();
		}

		@Override
		public int getUniqueSupplierCount() {
			return __uniqueSupplierCount;
		}

		@Override
		public int getProductOrderCount() {
			return __productOrderCount;
		}

		@Override
		public int getOrderEntryCount() {
			return __orderEntryCount;
		}

	}

	//

	public static void main(final String[] args) throws Exception {
		if (args.length < 1 || args.length > 3) {
			DatabaseFiller.die("expected arguments: stockItemCount [cashDesksPerStore [outputDirectory]]\n");
		}

		//

		final int stockItemCount = Integer.parseInt(args[0]);
		final int cashDesksPerStore = DatabaseFiller.getCashDesksPerStore(args, 1);
		final File outputDir = DatabaseFiller.getOutputDir(args, 2);

		//

		DatabaseFiller.fillDatabase(
				new Configuration(stockItemCount),
				new DefaultEntityConfiguration(),
				cashDesksPerStore, outputDir
				);
	}

}
