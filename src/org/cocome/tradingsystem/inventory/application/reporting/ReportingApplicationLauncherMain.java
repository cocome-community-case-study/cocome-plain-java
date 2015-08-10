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

package org.cocome.tradingsystem.inventory.application.reporting;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;
import org.cocome.tradingsystem.util.ApplicationHelper;
import org.cocome.tradingsystem.util.Names;

/**
 * Launcher for the enterprise inventory reporting application.
 * 
 * @author Lubomir Bulej
 */
public final class ReportingApplicationLauncherMain {

	private static final Logger LOG =
			Logger.getLogger(ReportingApplicationLauncherMain.class);

	/** Utility class constructor. */
	private ReportingApplicationLauncherMain() {}

	/**
	 * Used by Ant target to start the inventory application reporting component
	 * and register it in RMI registry. The following arguments are expected on
	 * the command line:
	 * <ul>
	 * {@code registryHost registryPort reportingName}
	 * </ul>
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(final String[] args) {
		if (args.length != 3) {
			System.err.println("Expected 4 arguments: registryHost registryPort reportingName");
			System.exit(1);
		}

		ReportingApplicationLauncherMain.startReportingApplication(args[0], args[1], args[2]);

		try {
			ApplicationHelper.sleepForever();
		} catch (final InterruptedException e) {
			LOG.info("Reporting service terminated");
		}
	}

	/**
	 * Start the reporting application.
	 * 
	 * @param registryHost
	 * @param registryPort
	 * @param reportingName
	 */
	private static void startReportingApplication(final String registryHost, final String registryPort, final String reportingName) {
		final String description = "application " + reportingName;
		LOG.debug("Starting reporting " + description);

		final String remoteName = Names.getReportingRemoteName(reportingName);
		ApplicationHelper.serviceStarting(remoteName);

		try {
			ApplicationHelper.initNaming(registryHost, registryPort);
			ApplicationHelper.registerComponent(remoteName, new ReportingServer());

			ApplicationHelper.serviceStarted(remoteName);
			LOG.info("Reporting " + description + " started");
		} catch (final RemoteException e) {
			LOG.fatal("Failed to start reporting " + description);
			LOG.fatal(e.getClass().getName() + ": " + e.getMessage());
			System.exit(1);
			// TODO fix catch all
		} catch (final RuntimeException e) { // NOCS
			LOG.fatal("Failed to start reporting " + description);
			LOG.fatal(e.getClass().getName() + ": " + e.getMessage());
			System.exit(1);
		}

	}

}
