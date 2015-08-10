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

import java.lang.reflect.InvocationTargetException;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.cocome.tradingsystem.util.ApplicationHelper;
import org.cocome.tradingsystem.util.JmsHelper;

/**
 * Launcher for the cash desk application.
 * 
 * @author Lubomir Bulej
 */
public final class CashDeskLauncherMain {

	private static final Logger LOG =
			Logger.getLogger(CashDeskLauncherMain.class);

	/**
	 * private constructor for utility class.
	 */
	private CashDeskLauncherMain() {}

	/**
	 * Used by ant target to start a cash desk application. The following
	 * arguments are expected on the command line:
	 * <p>
	 * <ul>
	 * {@code registryHost registryPort cashDeskName storeName bankName}
	 * </ul>
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(final String[] args) throws Exception {
		// check arguments
		final int argsCount = args.length;
		if ((argsCount < 5) || (argsCount > 6)) {
			System.err.println(
					"Expecting 5 arguments: registryHost registryPort cashDeskName storeName bankName [nogui]"
					);
			System.exit(1);
		}

		// start launcher
		CashDeskLauncherMain.startCashDesk(
				args[0], args[1],
				args[2], args[3], args[4],
				argsCount == 6 ? args[5] : ""); // NOCS

		ApplicationHelper.sleepForever();
	}

	/**
	 * 
	 * @param registryHost
	 * @param registryPort
	 * @param cashDeskName
	 * @param storeName
	 * @param bankName
	 * @param mode
	 * @throws InterruptedException
	 */
	static void startCashDesk(
			final String registryHost, final String registryPort,
			final String cashDeskName, final String storeName,
			final String bankName, final String mode) throws InterruptedException {
		final String description = "desk application " + cashDeskName + " for " + storeName;
		LOG.debug("Starting cash " + description);

		final String serviceName = "CashDesk." + storeName + "." + cashDeskName;
		ApplicationHelper.serviceStarting(serviceName);

		//

		try {
			//
			// Initialize the naming, create a JMS connection and build
			// the cash desk from other components. When the cash desk is
			// ready, start the JMS connection to receive messages.
			//
			ApplicationHelper.initNaming(registryHost, registryPort);
			final Connection connection = JmsHelper.createConnection();

			CashDeskFactory.createCashDesk(cashDeskName, storeName, bankName, mode, connection);

			connection.start();

		} catch (final JMSException e) {
			LOG.fatal("Failed to start cash " + description);
			LOG.fatal(e.getClass().getName() + ": " + e.getMessage());
			System.exit(1);
		} catch (final NamingException e) {
			LOG.fatal("Failed to start cash " + description);
			LOG.fatal(e.getClass().getName() + ": " + e.getMessage());
			System.exit(1);
		} catch (final InvocationTargetException e) {
			LOG.fatal("Failed to start cash " + description);
			LOG.fatal(e.getClass().getName() + ": " + e.getMessage());
			System.exit(1);
		}

		ApplicationHelper.serviceStarted(serviceName);
		LOG.info("XCash " + description + " started");
	}
}
