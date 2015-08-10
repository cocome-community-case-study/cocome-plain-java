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

import java.awt.Dimension;
import java.lang.reflect.InvocationTargetException;

import javax.jms.Connection;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.cocome.tradingsystem.cashdeskline.cashdesk.barcodescanner.BarcodeScannerModel;
import org.cocome.tradingsystem.cashdeskline.cashdesk.barcodescanner.VisualBarcodeScanner;
import org.cocome.tradingsystem.cashdeskline.cashdesk.cardreader.CardReaderModel;
import org.cocome.tradingsystem.cashdeskline.cashdesk.cardreader.VisualCardReader;
import org.cocome.tradingsystem.cashdeskline.cashdesk.cashbox.CashBoxModel;
import org.cocome.tradingsystem.cashdeskline.cashdesk.cashbox.VisualCashBox;
import org.cocome.tradingsystem.cashdeskline.cashdesk.expresslight.ExpressLightModel;
import org.cocome.tradingsystem.cashdeskline.cashdesk.expresslight.VisualExpressLight;
import org.cocome.tradingsystem.cashdeskline.cashdesk.printer.PrinterModel;
import org.cocome.tradingsystem.cashdeskline.cashdesk.printer.VisualPrinter;
import org.cocome.tradingsystem.cashdeskline.cashdesk.userdisplay.UserDisplayModel;
import org.cocome.tradingsystem.cashdeskline.cashdesk.userdisplay.VisualUserDisplay;

/**
 * Represents a factory for the cash desk application. The factory creates
 * and connects the individual cash desk components to create a cash desk
 * application. The cash desk can be either controlled by a user interface,
 * or it can be run in headless mode (without user interface) and controlled
 * (potentially) through remote interface or messages sent to the cash desk
 * topic.
 * 
 * @author Lubomir Bulej
 */
final class CashDeskFactory {

	private static final Dimension DESKTOP_SIZE = new Dimension(800, 600);

	/** private utility class constructor. */
	private CashDeskFactory() {}

	/**
	 * 
	 * @param cashDeskName
	 * @param storeName
	 * @param bankName
	 * @param mode
	 * @param connection
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 */
	public static void createCashDesk(final String cashDeskName, final String storeName, final String bankName, final String mode, final Connection connection)
			throws InterruptedException, InvocationTargetException {
		//
		// Create the models and if no user interface is required, we are done.
		//
		final UserDisplayModel userDisplay = UserDisplayModel.newInstance(cashDeskName, storeName, connection);

		final BarcodeScannerModel barcodeScanner = BarcodeScannerModel.newInstance(cashDeskName, storeName, connection);

		final ExpressLightModel expressLight = ExpressLightModel.newInstance(cashDeskName, storeName, connection);

		final CardReaderModel cardReader = CardReaderModel.newInstance(cashDeskName, storeName, connection);

		final PrinterModel printer = PrinterModel.newInstance(cashDeskName, storeName, connection);

		final CashBoxModel cashBox = CashBoxModel.newInstance(cashDeskName, storeName, connection);

		final CashDeskModel cashDesk = CashDeskModel.newInstance(cashDeskName, storeName, bankName, connection);

		//
		// If user interface is required, create visual controllers.
		//
		if (!"no-gui".equalsIgnoreCase(mode)) {
			final VisualUserDisplay visualUserDisplay = new VisualUserDisplay();
			visualUserDisplay.setModel(userDisplay);

			final VisualBarcodeScanner visualBarcodeScanner = new VisualBarcodeScanner();
			visualBarcodeScanner.setModel(barcodeScanner);

			final VisualExpressLight visualExpressLight = new VisualExpressLight();
			visualExpressLight.setModel(expressLight);

			final VisualCardReader visualCardReader = new VisualCardReader();
			visualCardReader.setModel(cardReader);

			final VisualPrinter visualPrinter = new VisualPrinter();
			visualPrinter.setModel(printer);

			final VisualCashBox visualCashBox = new VisualCashBox();
			visualCashBox.setModel(cashBox);

			final VisualCashDesk visualCashDesk = new VisualCashDesk(
					visualUserDisplay, visualBarcodeScanner,
					visualExpressLight, visualCardReader,
					visualPrinter, visualCashBox);
			visualCashDesk.setModel(cashDesk);

			//
			// Show the user interface.
			//
			SwingUtilities.invokeAndWait(new Runnable() {
				// @Override
				public void run() {
					CashDeskFactory.showUserInterface(cashDeskName, storeName, bankName, visualCashDesk);
				}
			});
		}
	}

	/**
	 * 
	 * @param cashDeskName
	 * @param storeName
	 * @param bankName
	 * @param visualCashDesk
	 */
	protected static void showUserInterface(final String cashDeskName, final String storeName, final String bankName, final VisualCashDesk visualCashDesk) {
		// Create and set up the window.
		final JFrame frame = new JFrame(String.format("Cash desk %s for %s using %s", cashDeskName, storeName, bankName));

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(visualCashDesk.getView());

		// Display the window.
		frame.pack();
		frame.setSize(DESKTOP_SIZE);
		frame.setVisible(true);
	}

}
