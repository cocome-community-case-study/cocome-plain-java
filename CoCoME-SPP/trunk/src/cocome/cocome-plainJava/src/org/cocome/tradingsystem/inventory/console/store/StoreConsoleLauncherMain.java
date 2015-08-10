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

package org.cocome.tradingsystem.inventory.console.store;

import javax.swing.SwingUtilities;

import org.cocome.tradingsystem.inventory.application.store.IStoreInventoryManager;
import org.cocome.tradingsystem.util.ApplicationHelper;
import org.cocome.tradingsystem.util.Names;
import org.cocome.tradingsystem.util.RemoteComponent;

/**
 * Launcher for the inventory application store component graphical
 * management console.
 * 
 * @author Lubomir Bulej
 */
public final class StoreConsoleLauncherMain {

	/**
	 * Starts the graphical management console for the inventory application
	 * store component. The following arguments are required on the command
	 * line:
	 * <ul>
	 * {@code registryHost registryPort storeName}
	 * </ul>
	 * For example:
	 * <p>
	 * <ul>
	 * {@code localhost 1099 Store1}
	 * </ul>
	 */
	public static void main(final String[] args) throws Exception {
		if (args.length != 3) {
			System.err.println("Expecting 3 arguments: registryHost registryPort storeName");
			System.exit(1);
		}

		//

		__startStoreConsole(args[0], args[1], args[2]);
	}

	private static void __startStoreConsole(
			final String registryHost, final String registryPort,
			final String storeName
			) {
		ApplicationHelper.initNaming(registryHost, registryPort);

		final RemoteComponent<IStoreInventoryManager> remoteStore =
				ApplicationHelper.getRemoteComponent(
						Names.getStoreRemoteName(storeName),
						IStoreInventoryManager.class
						);

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				StoreConsole.createAndShowGUI(storeName, remoteStore);
			}
		});
	}

}
