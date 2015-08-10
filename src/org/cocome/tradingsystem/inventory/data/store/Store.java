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

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.cocome.tradingsystem.inventory.data.enterprise.TradingEnterprise;

/**
 * Represents a store in the database.
 * 
 * @author Yannick Welsch
 */
@Entity
public class Store {

	private long id;

	private String name;

	private String location;

	private TradingEnterprise enterprise;

	private Collection<ProductOrder> productOrders;

	private Collection<StockItem> stockItems;

	//

	/**
	 * @return A unique identifier for Store objects
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return this.id;
	}

	/**
	 * @param id
	 *            A unique identifier for Store objects
	 */
	public void setId(final long id) {
		this.id = id;
	}

	/**
	 * Returns the name of the store.
	 * 
	 * @return Store name.
	 */
	@Basic
	public String getName() {
		return this.name;
	}

	/**
	 * @param name
	 *            the name of the Store
	 */
	public void setName(final String name) {
		this.name = name;
	}

	/**
	 * Returns the location of the store.
	 * 
	 * @return Store location.
	 */
	@Basic
	public String getLocation() {
		return this.location;
	}

	/**
	 * Sets the location of the store.
	 * 
	 * @param location
	 *            store location
	 */
	public void setLocation(final String location) {
		this.location = location;
	}

	/**
	 * @return The enterprise which the Store belongs to
	 */
	@ManyToOne
	public TradingEnterprise getEnterprise() {
		return this.enterprise;
	}

	/**
	 * @param enterprise
	 *            The enterprise which the Store belongs to
	 */
	public void setEnterprise(final TradingEnterprise enterprise) {
		this.enterprise = enterprise;
	}

	/**
	 * @return All product orders of the Store.
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Collection<ProductOrder> getProductOrders() {
		return this.productOrders;
	}

	/**
	 * @param productOrders
	 *            all product orders of the Store
	 */
	public void setProductOrders(final Collection<ProductOrder> productOrders) {
		this.productOrders = productOrders;
	}

	/**
	 * @return
	 *         A list of StockItem objects. A StockItem represents a concrete
	 *         product in the store including sales price, ...
	 */
	@OneToMany(mappedBy = "store", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Collection<StockItem> getStockItems() {
		return this.stockItems;
	}

	/**
	 * @param stockItems
	 *            A list of StockItem objects. A StockItem represents a concrete
	 *            product in the store including sales price, ...
	 */
	public void setStockItems(final Collection<StockItem> stockItems) {
		this.stockItems = stockItems;
	}

}
