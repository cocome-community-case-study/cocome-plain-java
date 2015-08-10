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

import java.io.Serializable;

import org.cocome.tradingsystem.inventory.data.store.StockItem;

/**
 * A transfer object class for exchanging basic stock item information between
 * client and the service-oriented application layer. It contains either copies
 * of persisted data which is transferred to the client, or data which is
 * transferred from the client to the application layer to be processed and
 * persisted.
 * 
 * @see StockItem
 * 
 * @author Sebastian Herold
 * @author Lubomir Bulej
 */
public final class StockItemTO implements Serializable {

	private static final long serialVersionUID = 5874806761123366899L;

	//

	private long __id;

	private double __salesPrice;

	private long __amount;

	private long __minStock;

	private long __maxStock;

	/**
	 * Returns the unique identifier of the {@link StockItem} entity.
	 * 
	 * @return
	 *         {@link StockItem} entity identifier.
	 */
	public long getId() {
		return __id;
	}

	/**
	 * Sets the unique identifier of the {@link StockItem} entity.
	 * 
	 * @param id
	 *            new {@link StockItem} entity identifier
	 */
	public void setId(final long id) {
		__id = id;
	}

	/**
	 * Returns the stock amount.
	 * 
	 * @return
	 *         Stock amount.
	 */
	public long getAmount() {
		return __amount;
	}

	/**
	 * Sets the stock amount.
	 * 
	 * @param amount
	 *            new stock amount
	 */
	public void setAmount(final long amount) {
		__amount = amount;
	}

	/**
	 * Returns the minimal stock amount for this stock item.
	 * 
	 * @return
	 *         Minimum stock amount.
	 */
	public long getMinStock() {
		return __minStock;
	}

	/**
	 * Sets the minimal stock amount for this stock item.
	 * 
	 * @param minStock
	 *            new minimal stock amount
	 */
	public void setMinStock(final long minStock) {
		__minStock = minStock;
	}

	/**
	 * Returns the maximal stock amount for this stock item.
	 * 
	 * @return
	 *         Maximum stock amount.
	 */
	public long getMaxStock() {
		return __maxStock;
	}

	/**
	 * Sets the maximal stock amount for this stock item.
	 * 
	 * @param maxStock
	 *            new maximal stock amount
	 */
	public void setMaxStock(final long maxStock) {
		__maxStock = maxStock;
	}

	/**
	 * Returns the sales price of a stock item product.
	 * 
	 * @return
	 *         Stock item product sales price.
	 */
	public double getSalesPrice() {
		return __salesPrice;
	}

	/**
	 * Sets the sales price of a stock item product.
	 * 
	 * @param salesPrice
	 *            new stock item product sales price
	 */
	public void setSalesPrice(final double salesPrice) {
		__salesPrice = salesPrice;
	}

}
