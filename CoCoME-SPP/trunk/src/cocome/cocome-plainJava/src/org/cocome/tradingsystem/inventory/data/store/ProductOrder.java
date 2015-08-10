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
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * The ProductOrder class represents an ProductOrder of a Store in the database.
 * 
 * @author Yannick Welsch
 */
@Entity
public class ProductOrder {

	private long id;

	private Date deliveryDate;

	private Date orderingDate;

	private Collection<OrderEntry> orderEntries;

	private Store store;

	//

	/**
	 * @return A unique identifier for ProductOrder objects
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            A unique identifier for ProductOrder objects
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return A list of OrderEntry objects (pairs of Product-Amount-pairs)
	 */
	@OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Collection<OrderEntry> getOrderEntries() {
		return orderEntries;
	}

	/**
	 * @param orderEntries
	 *            A list of OrderEntry objects (pairs of Product-Amount-pairs)
	 */
	public void setOrderEntries(final Collection<OrderEntry> orderEntries) {
		this.orderEntries = orderEntries;
	}

	/**
	 * @return The date of ordering.
	 */
	@Basic
	public Date getOrderingDate() {
		return orderingDate;
	}

	/**
	 * @param orderingDate
	 *            the date of ordering
	 */
	public void setOrderingDate(final Date orderingDate) {
		this.orderingDate = orderingDate;
	}

	/**
	 * The delivery date is used for computing the mean time to delivery
	 * 
	 * @return The date of order fulfillment.
	 */
	@Basic
	public Date getDeliveryDate() {
		return deliveryDate;
	}

	/**
	 * The delivery date is used for computing the mean time to delivery
	 * 
	 * @param deliveryDate
	 *            the date of order fulfillment
	 */
	public void setDeliveryDate(final Date deliveryDate) {
		this.deliveryDate = deliveryDate;
	}

	/**
	 * @return The store where the order is placed.
	 */
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Store getStore() {
		return store;
	}

	/**
	 * @param store
	 *            the store where the order is placed
	 */
	public void setStore(final Store store) {
		this.store = store;
	}

}
