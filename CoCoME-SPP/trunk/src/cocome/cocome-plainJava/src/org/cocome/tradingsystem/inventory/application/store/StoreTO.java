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

/**
 * Transfer object class for exchanging basic store information between client
 * and the service-oriented application layer. It contains either copies of
 * persisted data which is transferred to the client, or data which is
 * transferred from the client to the application layer to be processed and
 * persisted.
 * 
 * @author Sebastian Herold
 * @author Lubomir Bulej
 */
public class StoreTO implements Serializable {

	private static final long serialVersionUID = 1635539555697050823L;

	//

	private long __id;

	private String __name;

	private String __location;

	//

	/**
	 * Gets the entity identifier of this store.
	 * 
	 * @return Store entity identifier.
	 */
	public final long getId() {
		return __id;
	}

	/**
	 * Sets the entity identifier of this store.
	 * 
	 * @param id
	 *            store entity identifier
	 */
	public final void setId(final long id) {
		__id = id;
	}

	/**
	 * Gets the name of this store.
	 * 
	 * @return Store name.
	 */
	public final String getName() {
		return __name;
	}

	/**
	 * Sets the name of this store.
	 * 
	 * @param name
	 *            store name
	 */
	public final void setName(final String name) {
		__name = name;
	}

	/**
	 * Gets the location of this store.
	 * 
	 * @return Store location.
	 */
	public final String getLocation() {
		return __location;
	}

	/**
	 * Sets the location of this store.
	 * 
	 * @param location
	 *            store location
	 */
	public final void setLocation(final String location) {
		__location = location;
	}

}
