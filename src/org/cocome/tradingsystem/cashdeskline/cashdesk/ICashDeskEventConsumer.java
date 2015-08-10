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

package org.cocome.tradingsystem.cashdeskline.cashdesk;

import javax.jms.JMSException;

import org.cocome.tradingsystem.cashdeskline.events.CashAmountEnteredEvent;
import org.cocome.tradingsystem.cashdeskline.events.CashBoxClosedEvent;
import org.cocome.tradingsystem.cashdeskline.events.CreditCardPinEnteredEvent;
import org.cocome.tradingsystem.cashdeskline.events.CreditCardScannedEvent;
import org.cocome.tradingsystem.cashdeskline.events.ExpressModeDisabledEvent;
import org.cocome.tradingsystem.cashdeskline.events.ExpressModeEnabledEvent;
import org.cocome.tradingsystem.cashdeskline.events.PaymentModeSelectedEvent;
import org.cocome.tradingsystem.cashdeskline.events.ProductBarcodeScannedEvent;
import org.cocome.tradingsystem.cashdeskline.events.SaleFinishedEvent;
import org.cocome.tradingsystem.cashdeskline.events.SaleStartedEvent;
import org.cocome.tradingsystem.util.event.EventConsumer;

/**
 * Specifies events consumed by the cash desk component. Each event has to have
 * a handler method with a single parameter of the same type as the consumed
 * event. To ensure implementation of event handlers for all relevant event
 * types, the cash desk component has to implement this interface.
 * 
 * @author Holger Klus
 * @author Lubomir Bulej
 */
@EventConsumer
interface ICashDeskEventConsumer {

	void onEvent(SaleStartedEvent event) throws JMSException;

	void onEvent(SaleFinishedEvent event) throws JMSException;

	void onEvent(PaymentModeSelectedEvent event) throws JMSException;

	void onEvent(ExpressModeDisabledEvent event) throws JMSException;

	void onEvent(ProductBarcodeScannedEvent event) throws JMSException;

	void onEvent(CashAmountEnteredEvent event) throws JMSException;

	void onEvent(CashBoxClosedEvent event) throws JMSException;

	void onEvent(CreditCardScannedEvent event);

	void onEvent(CreditCardPinEnteredEvent event) throws JMSException;

	void onEvent(ExpressModeEnabledEvent event) throws JMSException;

}
