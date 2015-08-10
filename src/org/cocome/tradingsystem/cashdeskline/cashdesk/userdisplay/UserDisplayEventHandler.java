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

import org.apache.log4j.Logger;
import org.cocome.tradingsystem.cashdeskline.datatypes.NumPadKeyStroke;
import org.cocome.tradingsystem.cashdeskline.datatypes.PaymentMode;
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
import org.cocome.tradingsystem.util.event.AbstractSerializableEventDispatcher;

/**
 * Implements the cash desk event handler for the user display model. The event
 * handler is similar to a controller in that it converts incoming cash desk
 * messages to actions on the user display model. However, there is no view
 * associated with the controller.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
final class UserDisplayEventHandler
		extends AbstractSerializableEventDispatcher implements IUserDisplayEventConsumer {

	private static final Logger LOG =
			Logger.getLogger(UserDisplayEventHandler.class);

	//

	private final UserDisplayModel display;

	private final StringBuilder cashAmountInput = new StringBuilder();

	private boolean enteringCashAmount;

	private double cashAmount;

	//

	public UserDisplayEventHandler(final UserDisplayModel display) {
		super(display.getModelName());

		this.display = display;
	}

	//
	// Event handler methods
	//

	@Override
	public void onEvent(final SaleStartedEvent event) {
		this.display.setContent(MessageKind.SPECIAL, "New sale");
	}

	@Override
	public void onEvent(final RunningTotalChangedEvent event) {
		final double runningTotal = event.getRunningTotal();
		LOG.debug("\trunningTotal: " + runningTotal);

		//

		this.display.setContent(
				MessageKind.NORMAL, String.format(
						"%s: %f\nRunning total: %f",
						event.getProductName(), event.getProductPrice(), runningTotal
						)
				);
	}

	@Override
	public void onEvent(final InvalidProductBarcodeEvent event) {
		final long barcode = event.getBarcode();
		LOG.debug("\tbarcode: " + barcode);

		//

		this.display.setContent(
				MessageKind.WARNING, "No product for barcode " + barcode + "!"
				);
	}

	@Override
	public void onEvent(final PaymentModeSelectedEvent event) {
		final PaymentMode mode = event.getMode();
		LOG.debug("\tpaymentMode: " + mode);

		//

		if (mode == PaymentMode.CASH) {
			this.clearAmountInput();
			this.display.setContent(MessageKind.SPECIAL, "Paying by cash\n");
			this.enteringCashAmount = true;
		} else {
			this.display.setContent(MessageKind.SPECIAL, "Paying by credit card\n");
			this.enteringCashAmount = false;
		}
	}

	@Override
	public void onEvent(final PaymentModeRejectedEvent event) {
		final PaymentMode mode = event.getMode();
		LOG.debug("\tpaymentMode: " + mode);

		final String reason = event.getReason();
		LOG.debug("\treason: " + reason);

		this.display.setContent(MessageKind.WARNING, reason);
	}

	@Override
	public void onEvent(final CashBoxNumPadKeypressEvent event) {
		final NumPadKeyStroke keyStroke = event.getKeyStroke();
		LOG.debug("\tkeyStroke: " + keyStroke);

		//
		// When in cash payment mode, collect key strokes into a buffer and
		// display the cash amount being entered.
		//
		if (this.enteringCashAmount && (keyStroke != NumPadKeyStroke.ENTER)) {
			this.cashAmountInput.append(keyStroke.label());
			this.display.setContent(
					MessageKind.SPECIAL,
					"Paying by cash\nAmount received: " + this.cashAmountInput.toString()
					);
		} else {
			this.clearAmountInput();
		}
	}

	private void clearAmountInput() {
		this.cashAmountInput.setLength(0);
	}

	@Override
	public void onEvent(final CashAmountEnteredEvent event) {
		final double cashAmount = event.getCashAmount(); // NOCS
		LOG.debug("\tcashAmount: " + cashAmount);

		//

		this.cashAmount = cashAmount;
		this.enteringCashAmount = false;
	}

	@Override
	public void onEvent(final ChangeAmountCalculatedEvent event) {
		final double changeAmount = event.getChangeAmount();
		LOG.debug("\tchangeAmount: " + changeAmount);

		//

		this.display.setContent(
				MessageKind.NORMAL,
				"Cash received: " + this.cashAmount + "\n"
						+ "Change amount: " + changeAmount
				);
	}

	@Override
	public void onEvent(final CreditCardScannedEvent event) {
		this.display.setContent(
				MessageKind.SPECIAL, "Credit card scanned.\nPlease enter your PIN..."
				);
	}

	@Override
	public void onEvent(final CreditCardScanFailedEvent event) {
		this.display.setContent(
				MessageKind.WARNING, "Failed to scan card.\nPlease try again..."
				);
	}

	@Override
	public void onEvent(final InvalidCreditCardEvent event) {
		this.display.setContent(
				MessageKind.WARNING, "Invalid credit card information.\nPlease try again..."
				);
	}

	@Override
	public void onEvent(final SaleSuccessEvent event) {
		this.display.setContent(
				MessageKind.SPECIAL,
				"Thank you for shopping!\nHave a nice day."
				);
	}

	//

	@Override
	public void onEvent(final ExpressModeDisabledEvent event) {
		this.display.setContent(
				MessageKind.SPECIAL, "Express mode disabled."
				);
	}

	@Override
	public void onEvent(final ExpressModeEnabledEvent event) {
		this.display.setContent(
				MessageKind.SPECIAL, "Express mode enabled."
				);
	}

}
