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

import javax.persistence.EntityTransaction;

/**
 * @author Yannick Welsch
 */
final class EntityTransactionContext implements ITransactionContext {

	private final EntityTransaction __et;

	//

	EntityTransactionContext(final EntityTransaction et) {
		__et = et;
	}

	public void beginTransaction() {
		__et.begin();
	}

	public void commit() {
		__et.commit();
	}

	public void rollback() {
		__et.rollback();
	}

	public boolean isActive() {
		return __et.isActive();
	}

}
