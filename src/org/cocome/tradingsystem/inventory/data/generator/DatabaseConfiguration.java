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

/**
 * Provides entity counts to the database generator. Different subclasses of
 * should be used to provide different configuration to the database generator.
 * Separating the configuration from the generator allows sharing the code for
 * generating the database content.
 * 
 * @author Lubomir Bulej
 */
class DatabaseConfiguration {

	public DatabaseConfiguration() {
		super();
	}

	//

	/**
	 * @return number of enterprises in the database
	 */
	public int getEnterpriseCount() {
		return 0;
	}

	/**
	 * @return number of stores in the database
	 */
	public int getStoreCount() {
		return 0;
	}

	/**
	 * @return total number of products in the database
	 */
	public int getProductCount() {
		return getSharedProductCount() + getUniqueProductCount();
	}

	/**
	 * @return number of products shared by all stores
	 */
	public int getSharedProductCount() {
		return 0;
	}

	/**
	 * @return number of products that not shared by any two stores
	 */
	public int getUniqueProductCount() {
		return 0;
	}

	/**
	 * @return number of all suppliers
	 */
	public int getSupplierCount() {
		return getSharedSupplierCount() + getUniqueSupplierCount();
	}

	/**
	 * @return number of suppliers for shared products
	 */
	public int getSharedSupplierCount() {
		return 0;
	}

	/**
	 * @return number of suppliers for unique products
	 */
	public int getUniqueSupplierCount() {
		return 0;
	}

	/**
	 * @return number of stock items in the database
	 */
	public int getStockItemCount() {
		return 0;
	}

	/**
	 * @return number of product orders in the database
	 */
	public int getProductOrderCount() {
		return 0;
	}

	/**
	 * @return number of order entries in the database
	 */
	public int getOrderEntryCount() {
		return 0;
	}

}
