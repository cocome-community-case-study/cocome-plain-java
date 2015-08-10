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

package org.cocome.tradingsystem.cashdeskline.cashdesk.cashbox;

import org.cocome.tradingsystem.cashdeskline.events.CashBoxClosedEvent;
import org.cocome.tradingsystem.cashdeskline.events.ChangeAmountCalculatedEvent;
import org.cocome.tradingsystem.util.event.EventConsumer;

/**
 * Specifies events consumed by the cash box component. Each event has to have a
 * handler method with a single parameter of the same type as the consumed
 * event. To ensure implementation of event handlers for all relevant event
 * types, the cash box component has to implement this interface.
 * 
 * @author Holger Klus
 * @author Lubomir Bulej
 */
@EventConsumer
interface ICashBoxEventConsumer {

	void onEvent(ChangeAmountCalculatedEvent event);

	/* XXX Only needed because of the testing code. */
	void onEvent(CashBoxClosedEvent event);

}
