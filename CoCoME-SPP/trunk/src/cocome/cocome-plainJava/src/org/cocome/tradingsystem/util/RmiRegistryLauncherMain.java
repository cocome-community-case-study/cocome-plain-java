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

package org.cocome.tradingsystem.util;

import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import org.apache.log4j.Logger;

/**
 * Helper class to start RMI registry (not only) from an Ant target. Multiple
 * enterprises are expected to use separate RMI registries.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
public final class RmiRegistryLauncherMain {

	private static final Logger LOG = Logger.getLogger(RmiRegistryLauncherMain.class);

	//

	private RmiRegistryLauncherMain() {
		// utility class, not to be instantiated
	}

	public static void main(final String[] args) throws RemoteException, InterruptedException {
		if (args.length != 1) {
			System.err.println("Expecting 1 argument: portNumber");
			System.exit(1);
		}

		//

		RmiRegistryLauncherMain.startRmiRegistry(args[0]);

		ApplicationHelper.sleepForever();
	}

	public static void startRmiRegistry(final String portNumber) throws RemoteException {
		final String description = "RMI registry on port " + portNumber;
		LOG.debug("Starting " + description);

		final String serviceName = "RmiRegistry";
		ApplicationHelper.serviceStarting(serviceName);

		final int registryPort = ApplicationHelper.parsePort(portNumber);

		if (System.getSecurityManager() == null) {
			System.setSecurityManager(new RMISecurityManager());
		}

		LocateRegistry.createRegistry(registryPort);

		ApplicationHelper.serviceStarted(serviceName);
		LOG.info(description + " started");
	}

}
