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

package org.cocome.tradingsystem.cashdeskline.cashdesk.userdisplay;

import org.cocome.tradingsystem.cashdeskline.events.CashAmountEnteredEvent;
import org.cocome.tradingsystem.cashdeskline.events.CashBoxNumPadKeypressEvent;
import org.cocome.tradingsystem.cashdeskline.events.ChangeAmountCalculatedEvent;
import org.cocome.tradingsystem.cashdeskline.events.CreditCardScanFailedEvent;
import org.cocome.tradingsystem.cashdeskline.events.CreditCardScannedEvent;
import org.cocome.tradingsystem.cashdeskline.events.ExpressModeDisabledEvent;
import org.cocome.tradingsystem.cashdeskline.events.ExpressModeEnabledEvent;
import org.cocome.tradingsystem.cashdeskline.events.InvalidCreditCardEvent;
import org.cocome.tradingsystem.cashdeskline.events.InvalidProductBarcodeEvent;
import org.cocome.tradingsystem.cashdeskline.events.PaymentModeRejectedEvent;
import org.cocome.tradingsystem.cashdeskline.events.PaymentModeSelectedEvent;
import org.cocome.tradingsystem.cashdeskline.events.RunningTotalChangedEvent;
import org.cocome.tradingsystem.cashdeskline.events.SaleStartedEvent;
import org.cocome.tradingsystem.cashdeskline.events.SaleSuccessEvent;
import org.cocome.tradingsystem.util.event.EventConsumer;

/**
 * Specifies events consumed by the user display component. Each event has to
 * have a handler method with a single parameter of the same type as the
 * consumed event. To ensure implementation of event handlers for all relevant
 * event types, the user display component has to implement this interface.
 * 
 * @author Holger Klus
 * @author Lubomir Bulej
 */
@EventConsumer
interface IUserDisplayEventConsumer {

	void onEvent(SaleStartedEvent event);

	void onEvent(RunningTotalChangedEvent event);

	void onEvent(InvalidProductBarcodeEvent event);

	void onEvent(PaymentModeSelectedEvent event);

	void onEvent(PaymentModeRejectedEvent event);

	void onEvent(CashBoxNumPadKeypressEvent event);

	void onEvent(CashAmountEnteredEvent event);

	void onEvent(ChangeAmountCalculatedEvent event);

	void onEvent(CreditCardScannedEvent event);

	void onEvent(CreditCardScanFailedEvent event);

	void onEvent(InvalidCreditCardEvent event);

	void onEvent(SaleSuccessEvent event);

	//

	void onEvent(ExpressModeDisabledEvent event);

	void onEvent(ExpressModeEnabledEvent event);

}
