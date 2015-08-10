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

package org.cocome.tradingsystem.inventory.application.productdispatcher;

import org.apache.log4j.Logger;
import org.cocome.tradingsystem.util.ApplicationHelper;
import org.cocome.tradingsystem.util.Names;

/**
 * Launcher for the enterprise product dispatcher.
 * 
 * @author Lubomir Bulej
 */
public final class ProductDispatcherLauncherMain {

	private static final Logger LOG =
			Logger.getLogger(ProductDispatcherLauncherMain.class);

	/** utility class constructor */
	private ProductDispatcherLauncherMain() {}

	/**
	 * Used by Ant target to start the product dispatcher. The following
	 * arguments are expected on the command line:
	 * <ul>
	 * {@code enterpriseName}
	 * </ul>
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(final String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("Expecting 3 arguments: registryHost registryPort dispatcherName");
			System.exit(1);
		}

		//

		ProductDispatcherLauncherMain.__startProductDispatcher(args[0], args[1], args[2]);

		ApplicationHelper.sleepForever();
	}

	/**
	 * Starts the enterprise product dispatcher.
	 * 
	 * @param dispatcherName
	 *            local name of the product dispatcher
	 */
	private static void __startProductDispatcher(
			final String registryHost, final String registryPort,
			final String dispatcherName
			) throws InterruptedException {
		final String description = "dispatcher " + dispatcherName;
		LOG.debug("Starting product " + description);

		final String remoteName = Names.getProductDispatcherRemoteName(dispatcherName);
		ApplicationHelper.serviceStarting(remoteName);

		//

		try {
			ApplicationHelper.initNaming(registryHost, registryPort);
			ApplicationHelper.registerComponent(remoteName, new ProductDispatcherServer());

		} catch (final Exception e) {
			LOG.fatal("Failed to start product " + description);
			LOG.fatal(e.getClass().getName() + ": " + e.getMessage());
			System.exit(1);
		}

		//

		ApplicationHelper.serviceStarted(remoteName);
		LOG.info("Product " + description + " started");
	}

}
