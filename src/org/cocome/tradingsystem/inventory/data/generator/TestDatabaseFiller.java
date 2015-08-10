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
 * Fills the database with entities representing a single enterprise with a
 * configurable number of stores. The counts of other entities are derived from
 * the number of stores and are rather small, for testing purposes. Each store
 * only has two suppliers, one for shared items and the other for unique items.
 * 
 * @author Lubomir Bulej
 */
public final class TestDatabaseFiller {

	/**
	 * Provides parameters for a store-centric configuration of the database.
	 */
	private static final class Configuration extends DatabaseConfiguration {

		private static final int __ENTERPRISE_COUNT__ = 1;
		private static final int __SHARED_PRODUCT_COUNT__ = 50;
		private static final int __UNIQUE_PRODUCTS_PER_STORE__ = 50;

		//

		/** Total number of stores in all enterprises. */
		private final int __storeCount;

		//

		Configuration(final int storeCount) {
			__storeCount = storeCount;
		}

		//

		@Override
		public int getEnterpriseCount() {
			return __ENTERPRISE_COUNT__;
		}

		@Override
		public int getStoreCount() {
			return __storeCount;
		}

		@Override
		public int getStockItemCount() {
			return __storeCount * getSharedProductCount() + getUniqueProductCount();
		}

		//

		@Override
		public int getSharedProductCount() {
			return __SHARED_PRODUCT_COUNT__;
		}

		@Override
		public int getUniqueProductCount() {
			return __storeCount * __UNIQUE_PRODUCTS_PER_STORE__;
		}

		@Override
		public int getSharedSupplierCount() {
			return __storeCount;
		}

		@Override
		public int getUniqueSupplierCount() {
			return __storeCount;
		}

	}

	//

	public static void main(final String[] args) throws Exception {
		if (args.length < 1 || args.length > 3) {
			DatabaseFiller.die("expected arguments: storeCount [cashDesksPerStore [outputDirectory]]\n");
		}

		//

		final int storeCount = Integer.parseInt(args[0]);
		final int cashDesksPerStore = DatabaseFiller.getCashDesksPerStore(args, 1);
		final File outputDir = DatabaseFiller.getOutputDir(args, 2);

		//

		DatabaseFiller.fillDatabase(
				new Configuration(storeCount), new DefaultEntityConfiguration(),
				cashDesksPerStore,
				outputDir
				);
	}

}
