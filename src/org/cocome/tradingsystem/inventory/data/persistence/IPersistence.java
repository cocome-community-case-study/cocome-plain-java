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
 * This interface is used by the application layer to handle persistence
 * 
 * @author Yannick Welsch
 */
public interface IPersistence {

	/**
	 * Returns a {@link IPersistenceContext} instances that allows to start
	 * transactions and persist objects.
	 * 
	 * @return
	 *         new {@link IPersistenceContext} instance
	 */
	IPersistenceContext getPersistenceContext();

}
