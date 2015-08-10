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

package org.cocome.tradingsystem.inventory.data.persistence;

/**
 * @author Yannick Welsch
 */
public interface IPersistenceContext {

	/**
	 * @return the TransactionContext
	 */
	ITransactionContext getTransactionContext();

	/**
	 * Persists the given entity object.
	 * 
	 * @param object
	 *            the entity object to persist
	 */
	void makePersistent(Object object);

	/**
	 * Updates the given entity object under this persistence context.
	 * 
	 * @param object
	 *            the entity object to refresh
	 */
	void refresh(Object object);

	/**
	 * Closes this persistence context. No methods should be called on this
	 * context after calling this method.
	 */
	void close();

}
