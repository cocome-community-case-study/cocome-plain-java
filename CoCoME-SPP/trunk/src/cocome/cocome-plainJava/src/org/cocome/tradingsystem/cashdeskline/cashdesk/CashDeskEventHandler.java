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

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.log4j.Logger;
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
import org.cocome.tradingsystem.util.event.AbstractSerializableEventDispatcher;

//import org.cocome.tradingsystem.cashdeskline.events.CreditCardPinEnteredEvent;

/**
 * Implements the business logic of the cash desk. The controller handles events
 * received by the cash desk and translates them to invocations on the cash desk
 * model. The cash desk model is stateful, so most methods can only be called in
 * certain state. If an event is received that results in illegal method
 * invocation on the model, the event is logged and ignored.
 * <p>
 * The state is kept in the model to enforce sequencing, because the model will emit events in response to method invocations. This allows to control the model from
 * outside.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
final class CashDeskEventHandler
		extends AbstractSerializableEventDispatcher implements ICashDeskEventConsumer {

	private static final Logger LOG =
			Logger.getLogger(CashDeskEventHandler.class);

	private final CashDeskModel cashDesk;

	private final Session session;

	/**
	 * Creates a new instance of cash desk controller. The application naming
	 * must be initialized prior to creating a new instance.
	 */
	public CashDeskEventHandler(final CashDeskModel cashDesk, final Session session) {
		super(cashDesk.getModelName());

		this.cashDesk = cashDesk;
		this.session = session;
	}

	@Override
	public void dispatch(final Serializable eventObject) throws JMSException {
		try {
			//
			// This being a transacted session, we dispatch the event using the
			// default mechanism and commit the session afterwards,
			// acknowledging the received messages as well as any messages sent
			// by the cash desk.
			//
			super.dispatch(eventObject);
			this.session.commit();

		} catch (final IllegalCashDeskStateException icse) {
			//
			// If an illegal state exception occurs, it means that the message
			// was not appropriate for the cash desk and that it was ignored.
			// We still commit the session to acknowledge the received messages.
			//
			this.logUnexpectedState(icse.getState());
			this.session.commit();

		}

		// TODO catching all is not acceptable, see checkstyle
		// catch (final Exception e) {
		//
		// Any other exception thrown by an event handler will cause session
		// rollback. If even the rollback fails, it is up to the message
		// listener to handle it.
		//
		// this.logHandlerFailure(eventObject, e);
		// this.session.rollback();
		// }
	}

	private void logUnexpectedState(final CashDeskState state) {
		LOG.debug("\tevent ignored - not expected in state " + state);
	}

	private void logHandlerFailure(final Serializable eventObject, final Exception exception) {
		LOG.error(
				String.format(
						"Failed to handle %s, rolling back session",
						eventObject.getClass().getName()
						),
				exception
				);
	}

	//
	// Event handler methods
	//

	@Override
	public void onEvent(final SaleStartedEvent event) throws JMSException {
		this.cashDesk.startSale();
	}

	@Override
	public void onEvent(final SaleFinishedEvent event) throws JMSException {
		this.cashDesk.finishSale();
	}

	@Override
	public void onEvent(final PaymentModeSelectedEvent event) throws JMSException {
		this.cashDesk.selectPaymentMode(event.getMode());
	}

	@Override
	public void onEvent(final ExpressModeDisabledEvent event) throws JMSException {
		this.cashDesk.disableExpressMode();
	}

	@Override
	public void onEvent(final ProductBarcodeScannedEvent event) throws JMSException {
		final long barcode = event.getBarcode();
		LOG.debug("\tbarcode: " + barcode);

		//

		this.cashDesk.addItemToSale(barcode);
	}

	@Override
	public void onEvent(final CashAmountEnteredEvent event) throws JMSException {
		final double cashAmount = event.getCashAmount();
		LOG.debug("\tcashAmount: " + cashAmount);

		//

		this.cashDesk.startCashPayment(cashAmount);
	}

	@Override
	public void onEvent(final CashBoxClosedEvent event) throws JMSException {
		this.cashDesk.finishCashPayment();
	}

	@Override
	public void onEvent(final CreditCardScannedEvent event) {
		final String cardInfo = event.getCreditCardInformation();
		LOG.debug("\tcreditCardInformation: " + cardInfo);

		//

		this.cashDesk.startCreditCardPayment(cardInfo);
	}

	@Override
	public void onEvent(final CreditCardPinEnteredEvent event) throws JMSException {
		final int pin = event.getPIN();
		LOG.debug("\tPIN: " + pin);

		//

		this.cashDesk.finishCreditCardPayment(pin);
	}

	/**
	 * If the event targets this cash desk, switch the cash desk to express
	 * mode. If the cash desk state actually changed, republish the event on the
	 * cash-desk local channel.
	 */
	@Override
	public void onEvent(final ExpressModeEnabledEvent event) throws JMSException {
		final String cashDeskName = event.getCashDesk();
		LOG.debug("\tcashDesk: " + cashDeskName);

		//

		if (cashDeskName.equals(this.cashDesk.getName())) {
			this.cashDesk.enableExpressMode();
		}
	}

}
