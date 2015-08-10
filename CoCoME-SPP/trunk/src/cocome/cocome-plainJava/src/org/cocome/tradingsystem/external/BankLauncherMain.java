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

package org.cocome.tradingsystem.external;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.cocome.tradingsystem.util.ApplicationHelper;
import org.cocome.tradingsystem.util.Names;

// import kieker.monitoring.core.controller.IMonitoringController;
// import kieker.monitoring.core.controller.MonitoringController;

/**
 * Launcher for the Bank application.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
public final class BankLauncherMain {

	// public static final IMonitoringController KIEKER = MonitoringController.getInstance();

	private static final Logger LOG = Logger.getLogger(BankLauncherMain.class);

	/** private constructor. */
	private BankLauncherMain() {}

	/**
	 * Creeate a bank service.
	 * 
	 * @param args
	 *            input arguments
	 */
	public static void main(final String[] args) {
		if (args.length != 3) {
			System.err.println("Expecting 3 arguments: registryHost registryPort bankName");
			System.exit(1);
		}

		//

		BankLauncherMain.startBank(args[0], args[1], args[2]);

		try {
			ApplicationHelper.sleepForever();
		} catch (final InterruptedException e) {
			LOG.info("Bank service terminated");
		}
	}

	/**
	 * Start bank application.
	 * 
	 * @param registryHost
	 * @param registryPort
	 * @param bankName
	 * @throws RemoteException
	 */
	private static void startBank(final String registryHost, final String registryPort, final String bankName) {
		final String description = "application " + bankName;
		LOG.debug("Starting bank " + description);

		final String serviceName = Names.getBankRemoteName(bankName);
		ApplicationHelper.serviceStarting(serviceName);

		ApplicationHelper.initNaming(registryHost, registryPort);

		try {
			ApplicationHelper.initNaming(registryHost, registryPort);
			ApplicationHelper.registerComponent(serviceName, new TrivialBankServer());

			ApplicationHelper.serviceStarted(serviceName);
			LOG.info("Bank " + description + " started");
		} catch (final IllegalArgumentException e) {
			LOG.fatal("Failed to start bank " + description);
			LOG.fatal(e.getClass().getName() + ": " + e.getMessage());
			System.exit(1);
			// TODO fix catch all for RuntimeException
		} catch (final RuntimeException e) { // NOCS
			LOG.fatal("Failed to start bank " + description);
			LOG.fatal(e.getClass().getName() + ": " + e.getMessage());
			System.exit(1);
		} catch (final RemoteException e) {
			LOG.fatal("Failed to start bank " + description);
			LOG.fatal(e.getClass().getName() + ": " + e.getMessage());
			System.exit(1);
		}

	}

}
