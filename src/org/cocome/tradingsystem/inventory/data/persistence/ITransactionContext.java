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
public interface ITransactionContext {

	/**
	 * Starts a new transaction.
	 */
	void beginTransaction();

	/**
	 * Commits the current transaction.
	 */
	void commit();

	/**
	 * Rolls back the current transaction.
	 */
	void rollback();

	/**
	 * Checks whether this transaction is still active.
	 * 
	 * @return
	 *         {@code true} if this transaction is active, {@code false} otherwise
	 */
	boolean isActive();

}
