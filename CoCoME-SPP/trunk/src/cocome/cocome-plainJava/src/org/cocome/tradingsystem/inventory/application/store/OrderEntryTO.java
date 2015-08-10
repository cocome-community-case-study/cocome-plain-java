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
 * A transfer object class for exchanging basic order entry information between
 * client and the service-oriented application layer. It contains either copies
 * of persisted data which is transferred to the client, or data which is
 * transferred from the client to the application layer to be processed and
 * persisted.
 * 
 * @author Sebastian Herold
 * @author Lubomir Bulej
 */
class OrderEntryTO implements Serializable {

	private static final long serialVersionUID = 4131269057379126014L;

	// TODO Why this only contains amount? -- LB

	private long __amount;

	/**
	 * Gets value of amount attribute of an order entry.
	 * 
	 * @return Amount.
	 */
	public long getAmount() {
		return __amount;
	}

	/**
	 * Sets value of amount attribute of an order entry.
	 * 
	 * @param amount
	 */
	public void setAmount(long amount) {
		__amount = amount;
	}

}
