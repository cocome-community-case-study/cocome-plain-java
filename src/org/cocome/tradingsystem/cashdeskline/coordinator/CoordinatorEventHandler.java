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

package org.cocome.tradingsystem.cashdeskline.coordinator;

import java.util.Date;

import org.cocome.tradingsystem.cashdeskline.datatypes.PaymentMode;
import org.cocome.tradingsystem.cashdeskline.events.SaleRegisteredEvent;
import org.cocome.tradingsystem.util.event.AbstractSerializableEventDispatcher;

import org.apache.log4j.Logger;

/**
 * Implements the event handler for the cash desk line coordinator model. The
 * event handler is similar to a controller in that it converts incoming store
 * events to actions on the coordinator model. However, there is no view
 * associated with the controller.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
final class CoordinatorEventHandler
		extends AbstractSerializableEventDispatcher implements ICoordinatorEventConsumer {

	private static final Logger __log__ =
			Logger.getLogger(CoordinatorEventHandler.class);

	//

	private final CoordinatorModel __coordinator;

	//

	CoordinatorEventHandler(final CoordinatorModel coordinator) {
		super(coordinator.getModelName());

		__coordinator = coordinator;
	}

	//
	// Event handler methods
	//

	@Override
	public void onEvent(final SaleRegisteredEvent event) {
		final String cashDesk = event.getCashDesk();
		final int itemCount = event.getItemCount();
		final PaymentMode paymentMode = event.getPaymentMode();

		if (__log__.isDebugEnabled()) {
			__log__.debug(String.format(
					"\tcashDesk: %s, itemCount: %d, paymentMode: %s",
					cashDesk, itemCount, paymentMode
					));
		}

		//

		__coordinator.updateStatistics(
				cashDesk, new Sale(new Date(), itemCount, paymentMode)
				);
	}

}
