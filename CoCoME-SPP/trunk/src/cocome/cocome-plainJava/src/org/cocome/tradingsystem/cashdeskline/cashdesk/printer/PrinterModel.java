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

import javax.jms.Connection;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.cocome.tradingsystem.util.JmsHelper;
import org.cocome.tradingsystem.util.Names;
import org.cocome.tradingsystem.util.event.ObjectMessageListener;
import org.cocome.tradingsystem.util.mvc.AbstractModel;

/**
 * Implements the cash desk printer model.
 * 
 * @author Lubomir Bulej
 */
public final class PrinterModel
		extends AbstractModel<PrinterModel> implements IPrinter {

	private static final Logger LOG =
			Logger.getLogger(PrinterModel.class);

	private static final String COMPONENT_NAME = "Printer";

	//

	private final StringBuilder output = new StringBuilder();

	//

	PrinterModel() {
		super(COMPONENT_NAME);
	}

	//
	// Printer model methods
	//

	@Override
	public final void tearOffPrintout() {
		this.clearOutput();
	}

	@Override
	public final void printText(final String text) {
		this.appendOutput(text);
	}

	@Override
	public String getCurrentPrintout() {
		return this.output.toString();
	}

	//

	private void clearOutput() {
		this.output.setLength(0);
		this.changedContent();
	}

	private void appendOutput(final String text) {
		this.output.append(text);
		this.changedContent();
	}

	//

	/**
	 * Create new printer model.
	 * 
	 * @param cashDeskName
	 * @param storeName
	 * @param connection
	 * @return
	 */
	public static PrinterModel newInstance(
			final String cashDeskName, final String storeName,
			final Connection connection
			) {
		try {
			final String cashDeskTopicName =
					Names.getCashDeskTopicName(cashDeskName, storeName);

			//
			// Create a session for messages consumed by the printer (these will
			// originate in the JMS dispatcher thread) and register a separate
			// printer event handler as consumer in the session.
			//
			final PrinterModel printer = new PrinterModel();
			final Session consumerSession = JmsHelper.createSession(connection);
			JmsHelper.registerConsumer(
					consumerSession, cashDeskTopicName,
					new ObjectMessageListener(new PrinterEventHandler(printer))
					);

			return printer;

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
