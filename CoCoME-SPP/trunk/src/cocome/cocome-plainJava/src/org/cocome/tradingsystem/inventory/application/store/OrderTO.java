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
import java.util.Date;

/**
 * A transfer object class for exchanging basic order information between client
 * and the service-oriented application layer. It contains either copies of
 * persisted data which is transferred to the client, or data which is
 * transferred from the client to the application layer to be processed and
 * persisted.
 * 
 * @author Sebastian Herold
 * @author Lubomir Bulej
 */
class OrderTO implements Serializable {

	private static final long serialVersionUID = 2560032621725754893L;

	//

	private long __id;

	private Date __deliveryDate;

	private Date __orderingDate;

	/**
	 * Returns the unique entity identifier of this order.
	 * 
	 * @return
	 *         Entity identifier of this order.
	 */
	public long getId() {
		return __id;
	}

	/**
	 * Sets the unique entity identifier of this order.
	 * 
	 * @param id
	 *            the new entity identifier
	 */
	public void setId(final long id) {
		__id = id;
	}

	/**
	 * Returns the saved delivery date of this order.
	 * 
	 * @return
	 *         Order delivery date.
	 * 
	 * @see java.util.Date
	 */
	public Date getDeliveryDate() {
		return __deliveryDate;
	}

	/**
	 * Sets order delivery date.
	 * 
	 * @param deliveryDate
	 *            the new delivery date
	 */
	public void setDeliveryDate(final Date deliveryDate) {
		__deliveryDate = deliveryDate;
	}

	/**
	 * Returns the saved ordering date of this order.
	 * 
	 * @return
	 *         Order ordering date.
	 * 
	 * @see java.util.Date
	 */
	public Date getOrderingDate() {
		return __orderingDate;
	}

	/**
	 * Sets order ordering date.
	 * 
	 * @param orderingDate
	 *            the new ordering date
	 */
	public void setOrderingDate(final Date orderingDate) {
		__orderingDate = orderingDate;
	}

}
