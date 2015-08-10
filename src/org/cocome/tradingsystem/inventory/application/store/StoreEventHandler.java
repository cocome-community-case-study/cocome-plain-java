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

import org.cocome.tradingsystem.cashdeskline.events.AccountSaleEvent;
import org.cocome.tradingsystem.util.event.AbstractSerializableEventDispatcher;

/**
 * Store server event handler. Handles events supported by the store inventory
 * application.
 * 
 * @author Lubomir Bulej
 */
final class StoreEventHandler extends AbstractSerializableEventDispatcher
		implements IStoreEventConsumer {

	final StoreServer __store;

	//

	public StoreEventHandler(final StoreServer store) {
		super("Store Server");
		__store = store;
	}

	//
	// Event handler methods
	//

	@Override
	public void onEvent(final AccountSaleEvent event) {
		__store.accountSale(event.getSale());
	}

}
