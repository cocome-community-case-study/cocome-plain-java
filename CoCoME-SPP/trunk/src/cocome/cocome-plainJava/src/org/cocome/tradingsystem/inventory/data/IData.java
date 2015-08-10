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

package org.cocome.tradingsystem.inventory.data;

import org.cocome.tradingsystem.inventory.data.enterprise.IEnterpriseQuery;
import org.cocome.tradingsystem.inventory.data.persistence.IPersistence;
import org.cocome.tradingsystem.inventory.data.store.IStoreQuery;

/**
 * Interface for the Data component
 * 
 * @author Yannick Welsch
 */
public interface IData {

	/**
	 * Creates a new EnterpriseQuery component
	 * 
	 * @return the new EnterpriseQuery component
	 */
	IEnterpriseQuery getEnterpriseQuery();

	/**
	 * Creates a new StoreQuery component
	 * 
	 * @return the new StoreQuery component
	 */
	IStoreQuery getStoreQuery();

	/**
	 * Creates a new PersistenceIf component
	 * 
	 * @return the new PersistenceIf component
	 */
	IPersistence getPersistenceManager();

}
