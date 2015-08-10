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

package org.cocome.tradingsystem.cashdeskline.cashdesk.barcodescanner;

import javax.jms.Connection;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.cocome.tradingsystem.cashdeskline.events.ProductBarcodeScannedEvent;
import org.cocome.tradingsystem.util.JmsHelper;
import org.cocome.tradingsystem.util.JmsHelper.SessionBoundProducer;
import org.cocome.tradingsystem.util.Names;
import org.cocome.tradingsystem.util.mvc.AbstractModel;

/**
 * Implements the cash desk barcode scanner model. The scanner does not consume
 * any events, instead it only produces {@link ProductBarcodeScannedEvent} events whenever a barcode is scanned.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
public final class BarcodeScannerModel
		extends AbstractModel<BarcodeScannerModel> implements IBarcodeScanner {

	private static final Logger LOG =
			Logger.getLogger(BarcodeScannerModel.class);

	private static final String COMPONENT_NAME = "Barcode Scanner";

	//

	private final SessionBoundProducer cashDeskProducer;

	//

	private BarcodeScannerModel(final SessionBoundProducer cashDeskProducer) {
		super(COMPONENT_NAME);

		this.cashDeskProducer = cashDeskProducer;
	}

	//
	// Barcode scanner model methods
	//

	// @Override
	public void sendProductBarcode(final long barcode) {
		this.cashDeskProducer.sendAsync(new ProductBarcodeScannedEvent(barcode));
	}

	//

	public static BarcodeScannerModel newInstance(
			final String cashDeskName, final String storeName,
			final Connection connection
			) {
		try {
			final String cashDeskTopicName =
					Names.getCashDeskTopicName(cashDeskName, storeName);

			//
			// Create a session for messages originating from the Swing event
			// dispatcher thread and create the barcode scanner model.
			//
			final Session producerSession = JmsHelper.createSession(connection);
			final BarcodeScannerModel scanner = new BarcodeScannerModel(
					JmsHelper.createSessionBoundProducer(producerSession, cashDeskTopicName)
					);

			return scanner;

			// TODO catch all violates style
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
