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

import java.util.Calendar;
import java.util.Date;

/**
 * Default provider of entity attribute values.
 * 
 * @author Lubomir Bulej
 */
final class DefaultEntityConfiguration implements IEntityConfiguration {

	private static final double __PRODUCT_PURCHASE_PRICE_MIN__ = 0.05;
	private static final double __PRODUCT_PURCHASE_PRICE_MAX__ = 100;

	private static final double __STOCK_ITEM_SALES_MARGIN_MIN__ = 1.05;
	private static final double __STOCK_ITEM_SALES_MARGIN_MAX__ = 1.15;

	private static final int __STOCK_ITEM_AMOUNT_MIN__ = 50;
	private static final int __STOCK_ITEM_AMOUNT_MAX__ = 80;

	private static final int __STOCK_ITEM_MIN_STOCK_MIN__ = 10;
	private static final int __STOCK_ITEM_MIN_STOCK_MAX__ = 40;

	private static final int __STOCK_ITEM_MAX_STOCK_MIN__ = 90;
	private static final int __STOCK_ITEM_MAX_STOCK_MAX__ = 120;

	private static final int __ORDER_ENTRY_ORDERING_DATE_MIN__ = 21;
	private static final int __ORDER_ENTRY_ORDERING_DATE_MAX__ = 14;

	private static final int __ORDER_ENTRY_DELIVERY_DATE_MIN__ = 1;
	private static final int __ORDER_ENTRY_DELIVERY_DATE_MAX__ = 7;

	private static final double __ORDER_ENTRY_AMOUNT_PERCENTAGE_MIN__ = 0.05;
	private static final double __ORDER_ENTRY_AMOUNT_PERCENTAGE_MAX__ = 0.10;

	//

	/** Random generator for product purchase price. */
	private final RandomHelper __randomProductPurchasePrice = new RandomHelper();

	/** Random generator for stock item sales margin. */
	private final RandomHelper __randomStockItemSalesMargin = new RandomHelper();

	/** Random generator for stock item amount. */
	private final RandomHelper __randomStockItemAmount = new RandomHelper();

	/** Random generator for stock item minimal stock. */
	private final RandomHelper __randomStockItemMinStock = new RandomHelper();

	/** Random generator for stock item maximal stock. */
	private final RandomHelper __randomStockItemMaxStock = new RandomHelper();

	/** Random generator for order entry product amount. */
	private final RandomHelper __randomOrderEntryAmount = new RandomHelper();

	/** Random generator for order entry ordering date. */
	private final RandomHelper __randomOrderEntryOrderingDate = new RandomHelper();

	/** Random generator for order entry delivery date. */
	private final RandomHelper __randomOrderEntryDeliveryDate = new RandomHelper();

	//

	@Override
	public String getEnterpriseName(final int index) {
		return "Enterprise " + index;
	}

	@Override
	public String getStoreLocation(final int index) {
		return "Location " + index;
	}

	@Override
	public String getStoreName(final int index) {
		return "Store" + index;
	}

	@Override
	public String getSupplierName(final String prefix, final int index) {
		return prefix + "Supplier " + index;
	}

	@Override
	public String getProductName(final String prefix, int index) {
		return prefix + "Product " + index;
	}

	@Override
	public long getProductBarcode(final long base, int index) {
		return base + index;
	}

	//

	@Override
	public double getProductPurchasePrice() {
		return __roundToCents(__randomProductPurchasePrice.uniformDoubleRange(
				__PRODUCT_PURCHASE_PRICE_MIN__, __PRODUCT_PURCHASE_PRICE_MAX__
				));
	}

	@Override
	public double getStockItemSalesPrice(final double purchasePrice) {
		return __roundToCents(purchasePrice * __randomStockItemSalesMargin.gaussianDoubleRange(
				__STOCK_ITEM_SALES_MARGIN_MIN__, __STOCK_ITEM_SALES_MARGIN_MAX__
				));
	}

	@Override
	public long getStockItemAmount() {
		return __randomStockItemAmount.gaussianIntRange(
				__STOCK_ITEM_AMOUNT_MIN__, __STOCK_ITEM_AMOUNT_MAX__
				);
	}

	@Override
	public long getStockItemMinStock() {
		return __randomStockItemMinStock.uniformIntRange(
				__STOCK_ITEM_MIN_STOCK_MIN__, __STOCK_ITEM_MIN_STOCK_MAX__
				);
	}

	@Override
	public long getStockItemMaxStock() {
		return __randomStockItemMaxStock.uniformIntRange(
				__STOCK_ITEM_MAX_STOCK_MIN__, __STOCK_ITEM_MAX_STOCK_MAX__
				);
	}

	@Override
	public Date getProductOrderOrderingDate() {
		final Calendar calendar = Calendar.getInstance();
		final int deliveryDelay = __randomOrderEntryOrderingDate.uniformIntRange(
				__ORDER_ENTRY_ORDERING_DATE_MIN__, __ORDER_ENTRY_ORDERING_DATE_MAX__
				);

		calendar.add(Calendar.DAY_OF_MONTH, -deliveryDelay);
		return calendar.getTime();
	}

	@Override
	public Date getProductOrderDeliveryDate() {
		final Calendar calendar = Calendar.getInstance();
		final int deliveryDelay = __randomOrderEntryDeliveryDate.uniformIntRange(
				__ORDER_ENTRY_DELIVERY_DATE_MIN__, __ORDER_ENTRY_DELIVERY_DATE_MAX__
				);

		calendar.add(Calendar.DAY_OF_MONTH, -deliveryDelay);
		return calendar.getTime();
	}

	@Override
	public long getOrderEntryAmount(final long currentAmount) {
		return Math.round(currentAmount * __randomOrderEntryAmount.gaussianDoubleRange(
				__ORDER_ENTRY_AMOUNT_PERCENTAGE_MIN__, __ORDER_ENTRY_AMOUNT_PERCENTAGE_MAX__
				));
	}

	//

	private static double __roundToCents(final double price) {
		return Math.rint((100 * price)) / 100;
	}

}
