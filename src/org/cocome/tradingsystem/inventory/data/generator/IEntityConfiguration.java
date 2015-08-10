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

import java.util.Date;

/**
 * Represents configuration of database entity attribute values. The interface
 * separates entity configuration from (structural) database configuration,
 * which allows combining different entity configurations with different
 * database configurations. The implementor of this interface are free (and
 * supposed) to randomize values of entity attributes related to pricing, stock
 * levels, etc. On the other hand, values representing entity names should
 * mostly depend on the value of the index parameter.
 * 
 * @author Lubomir Bulej
 */
interface IEntityConfiguration {

	String getEnterpriseName(int index);

	String getStoreLocation(int index);

	String getStoreName(int index);

	String getSupplierName(String prefix, int index);

	//

	String getProductName(String prefix, int index);

	long getProductBarcode(long barcodeOffset, int index);

	double getProductPurchasePrice();

	//

	/**
	 * Adjusts the product purchase price and returns the stock item sales price.
	 * 
	 * @param purchasePrice
	 *            the original product purchase price
	 * 
	 * @return stock item sales price
	 */
	double getStockItemSalesPrice(double purchasePrice);

	long getStockItemAmount();

	long getStockItemMinStock();

	long getStockItemMaxStock();

	//

	Date getProductOrderOrderingDate();

	Date getProductOrderDeliveryDate();

	//

	/**
	 * Returns the amount of product items to insert into a product order entry.
	 * 
	 * @param currentAmount
	 *            current stock amount
	 * 
	 * @return number of product items to order
	 */
	long getOrderEntryAmount(long currentAmount);

}
