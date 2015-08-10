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

package org.cocome.tradingsystem.cashdeskline.cashdesk.cardreader;

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.cocome.tradingsystem.cashdeskline.events.CreditCardPinEnteredEvent;
import org.cocome.tradingsystem.cashdeskline.events.CreditCardScannedEvent;
import org.cocome.tradingsystem.util.JmsHelper;
import org.cocome.tradingsystem.util.JmsHelper.SessionBoundProducer;
import org.cocome.tradingsystem.util.Names;
import org.cocome.tradingsystem.util.mvc.AbstractModel;

/**
 * Implements the cash desk credit card reader model.
 * 
 * TODO The card reader should handle communication with the bank, not the cashdesk?
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
public final class CardReaderModel
		extends AbstractModel<CardReaderModel> implements ICardReader {

	private static final Logger LOG =
			Logger.getLogger(CardReaderModel.class);

	private static final String COMPONENT_NAME = "Card Reader";

	//

	private final SessionBoundProducer cashDeskProducer;

	//

	private CardReaderModel(final SessionBoundProducer cashDeskProducer) {
		super(COMPONENT_NAME);

		this.cashDeskProducer = cashDeskProducer;
	}

	//
	// Credit card reader model methods
	//

	@Override
	public void sendCreditCardInfo(final String creditInfo) {
		this.sendCashDeskEvent(new CreditCardScannedEvent(creditInfo));
	}

	@Override
	public void sendCreditCardPin(final int pin) {
		this.sendCashDeskEvent(new CreditCardPinEnteredEvent(pin));
	}

	//

	private void sendCashDeskEvent(final Serializable eventObject) {
		this.cashDeskProducer.sendAsync(eventObject);
	}

	//

	public static CardReaderModel newInstance(
			final String cashDeskName, final String storeName,
			final Connection connection
			) {
		try {
			final String cashDeskTopicName =
					Names.getCashDeskTopicName(cashDeskName, storeName);

			//
			// Create a session for messages originating from the Swing event
			// dispatcher thread and create the card reader model.
			//
			final Session producerSession = JmsHelper.createSession(connection);
			final CardReaderModel cardReader = new CardReaderModel(
					JmsHelper.createSessionBoundProducer(producerSession, cashDeskTopicName)
					);

			return cardReader;

			// TODO fix catch all
		} catch (final Exception e) { // NOCS
			final String message = String.format(
					"Failed to initialize %s (%s, %s)",
					COMPONENT_NAME, cashDeskName, storeName
					);

			LOG.fatal(message, e);
			throw new RuntimeException(message, e);
		}
	}

}
