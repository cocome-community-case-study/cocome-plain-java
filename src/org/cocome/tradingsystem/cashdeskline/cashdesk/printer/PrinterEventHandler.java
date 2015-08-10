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

package org.cocome.tradingsystem.cashdeskline.cashdesk.printer;

import java.util.Date;

import org.apache.log4j.Logger;
import org.cocome.tradingsystem.cashdeskline.events.CashAmountEnteredEvent;
import org.cocome.tradingsystem.cashdeskline.events.ChangeAmountCalculatedEvent;
import org.cocome.tradingsystem.cashdeskline.events.RunningTotalChangedEvent;
import org.cocome.tradingsystem.cashdeskline.events.SaleFinishedEvent;
import org.cocome.tradingsystem.cashdeskline.events.SaleStartedEvent;
import org.cocome.tradingsystem.cashdeskline.events.SaleSuccessEvent;
import org.cocome.tradingsystem.util.event.AbstractSerializableEventDispatcher;

/**
 * Implements the cash desk event handler for the printer model. The event
 * handler is similar to a controller in that it converts incoming cash desk
 * messages to actions on the printer model. However, there is no view
 * associated with the controller.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
final class PrinterEventHandler
		extends AbstractSerializableEventDispatcher implements IPrinterEventConsumer {

	private static final Logger LOG =
			Logger.getLogger(PrinterEventHandler.class);

	private static final String DELIMITER = "----------------------------\n";

	//

	private final PrinterModel printer;

	//
	// Controller state
	//

	/**
	 * Enumerates the possible states of the printer controller.
	 */
	enum ControllerState {
		STOPPED,
		EXPECTING_ITEMS,
		EXPECTING_CASH_AMOUNT,
		EXPECTING_CHANGE_AMOUNT,
		SALE_COMPLETED,
	}

	private ControllerState controllerState;

	private double runningTotal;

	//

	PrinterEventHandler(final PrinterModel printer) {
		super(printer.getModelName());

		this.printer = printer;
		this.resetControllerState();
	}

	//
	// Event handler methods
	//

	@Override
	public void onEvent(
			@SuppressWarnings("unused") final SaleStartedEvent event
			) {
		if (this.controllerState == ControllerState.STOPPED) {
			this.controllerState = ControllerState.EXPECTING_ITEMS;
			this.startPrintout();

		} else {
			this.logUnexpectedState();
		}
	}

	private void startPrintout() {
		this.printer.tearOffPrintout();
		this.printer.printText(new Date().toString());
		this.printer.printText("\n");
		this.printer.printText(DELIMITER);
	}

	@Override
	public void onEvent(final RunningTotalChangedEvent event) {
		final double runningTotal = event.getRunningTotal(); // NOCS
		LOG.debug("\trunningTotal: " + runningTotal);

		if (this.controllerState == ControllerState.EXPECTING_ITEMS) {
			this.runningTotal = runningTotal;
			this.printProductInfo(
					event.getProductName(), event.getProductPrice());

		} else {
			this.logUnexpectedState();
		}
	}

	/**
	 * 
	 * @param productName
	 * @param productPrice
	 */
	private void printProductInfo(
			final String productName, final double productPrice
			) {
		this.printer.printText(productName + ": " + productPrice + "\n");
	}

	@Override
	public void onEvent(final SaleFinishedEvent event) {
		if (this.controllerState == ControllerState.EXPECTING_ITEMS) {
			this.controllerState = ControllerState.EXPECTING_CASH_AMOUNT;
			this.printSaleTotal(this.runningTotal);

		} else {
			this.logUnexpectedState();
		}
	}

	private void printSaleTotal(final double total) {
		this.printer.printText(DELIMITER);
		this.printer.printText("Total: " + this.round(total) + "\n");
	}

	private double round(final double value) {
		return Math.rint(100 * value) / 100;
	}

	@Override
	public void onEvent(final CashAmountEnteredEvent event) {
		final double cashAmount = event.getCashAmount();
		LOG.debug("\tamount: " + cashAmount);

		if ((this.controllerState == ControllerState.EXPECTING_CASH_AMOUNT)
				|| (this.controllerState == ControllerState.EXPECTING_CHANGE_AMOUNT)) {
			this.printCashAmount(cashAmount);
			this.controllerState = ControllerState.EXPECTING_CHANGE_AMOUNT;
		} else {
			this.logUnexpectedState();
		}
	}

	private void printCashAmount(final double cashAmount) {
		this.printer.printText("Cash received: " + cashAmount + "\n");
	}

	@Override
	public void onEvent(final ChangeAmountCalculatedEvent event) {
		final double changeAmount = event.getChangeAmount();
		LOG.debug("\tchangeAmount: " + changeAmount);

		if (this.controllerState == ControllerState.EXPECTING_CHANGE_AMOUNT) {
			this.printChangeAmount(changeAmount);
			this.controllerState = ControllerState.SALE_COMPLETED;

		} else {
			this.logUnexpectedState();
		}
	}

	private void printChangeAmount(final double changeAmount) {
		this.printer.printText("Change amount: " + changeAmount + "\n");
	}

	@Override
	public void onEvent(final SaleSuccessEvent event) {
		this.finishPrintout();
		this.resetControllerState();
	}

	private void finishPrintout() {
		this.printer.printText(DELIMITER);
		this.printer.printText("Thank you for your purchase!\n");
	}

	//

	private void resetControllerState() {
		this.controllerState = ControllerState.STOPPED;
		this.runningTotal = 0.0;
	}

	private void logUnexpectedState() {
		LOG.debug("\tevent ignored - not expected in state " + this.controllerState);
	}

}
