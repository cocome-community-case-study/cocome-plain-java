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

package org.cocome.tradingsystem.inventory.data.store;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.cocome.tradingsystem.inventory.data.enterprise.Product;

/**
 * Represents a concrete product in the, store including sales price,
 * amount, ...
 * 
 * @author Yannick Welsch
 */
@Entity
public class StockItem {

	private long id;

	private double salesPrice;

	private long amount;

	private long minStock;

	private long maxStock;

	private long incomingAmount;

	private Store store;

	private Product product;

	//

	/**
	 * @return A unique identifier of this StockItem.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            a unique identifier of this StockItem
	 */
	public void setId(final long id) {
		this.id = id;
	}

	/**
	 * @return The currently available amount of items of a product
	 */
	@Basic
	public long getAmount() {
		return amount;
	}

	/**
	 * @param amount
	 *            the currently available amount of items of a product
	 */
	public void setAmount(final long amount) {
		this.amount = amount;
	}

	/**
	 * This method will be used while computing the low stock item list.
	 * 
	 * @return The maximum capacity of a product in a store.
	 */
	@Basic
	public long getMaxStock() {
		return maxStock;
	}

	/**
	 * This method enables the definition of the maximum capacity of a product
	 * in a store.
	 * 
	 * @param maxStock
	 *            the maximum capacity of a product in a store
	 */
	public void setMaxStock(final long maxStock) {
		this.maxStock = maxStock;
	}

	/**
	 * @return
	 *         The minimum amount of products which has to be available in a
	 *         store.
	 */
	@Basic
	public long getMinStock() {
		return minStock;
	}

	/**
	 * @param minStock
	 *            the minimum amount of products which has to be available in a
	 *            store
	 */
	public void setMinStock(final long minStock) {
		this.minStock = minStock;
	}

	/**
	 * @return The Product of a StockItem.
	 */
	@ManyToOne
	public Product getProduct() {
		return product;
	}

	/**
	 * @param product
	 *            the Product of a StockItem
	 */
	public void setProduct(final Product product) {
		this.product = product;
	}

	/**
	 * @return The sales price of the StockItem
	 */
	@Basic
	public double getSalesPrice() {
		return salesPrice;
	}

	/**
	 * @param salesPrice
	 *            the sales price of the StockItem
	 */
	public void setSalesPrice(final double salesPrice) {
		this.salesPrice = salesPrice;
	}

	/**
	 * Required for UC 8
	 * 
	 * @return incomingAmount
	 *         the amount of products that will be delivered in the near future
	 */
	@Basic
	public long getIncomingAmount() {
		return this.incomingAmount;
	}

	/**
	 * Set the amount of products that will be delivered in the near future.
	 * <p>
	 * Required for UC 8
	 * 
	 * @param incomingAmount
	 *            the absolute amount (no delta) of incoming products
	 */
	public void setIncomingAmount(final long incomingAmount) {
		this.incomingAmount = incomingAmount;
	}

	/**
	 * @return The store where the StockItem belongs to
	 */
	@ManyToOne
	public Store getStore() {
		return store;
	}

	/**
	 * @param store
	 *            The store where the StockItem belongs to
	 */
	public void setStore(final Store store) {
		this.store = store;
	}

}
