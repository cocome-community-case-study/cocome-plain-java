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

package org.cocome.tradingsystem.inventory.console.reporting;

import javax.swing.SwingUtilities;

import org.cocome.tradingsystem.inventory.application.reporting.IReporting;
import org.cocome.tradingsystem.util.ApplicationHelper;
import org.cocome.tradingsystem.util.Names;
import org.cocome.tradingsystem.util.RemoteComponent;

/**
 * Launcher for the inventory application reporting component graphical
 * management console.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
public final class ReportingConsoleLauncherMain {

	/**
	 * Starts the graphical management console for the inventory application
	 * reporting component. The following arguments are required on the command
	 * line:
	 * <ul>
	 * {@code registryHost registryPort reportingName}
	 * </ul>
	 * For example:
	 * <p>
	 * <ul>
	 * {@code localhost 1099 Reporting0}
	 * </ul>
	 */
	public static void main(final String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("Expecting 3 arguments: registryHost registryPort reportingName");
			System.exit(1);
		}

		//

		__startReportingConsole(args[0], args[1], args[2]);
	}

	private static void __startReportingConsole(
			final String registryHost, final String registryPort,
			final String reportingName
			) {
		ApplicationHelper.initNaming(registryHost, registryPort);

		final RemoteComponent<IReporting> remoteReporting =
				ApplicationHelper.getRemoteComponent(
						Names.getReportingRemoteName(reportingName), IReporting.class
						);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ReportingConsole.createAndShowGUI(reportingName, remoteReporting);
			}
		});
	}

}
