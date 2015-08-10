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

package org.cocome.tradingsystem.inventory.application.store;

import java.rmi.RemoteException;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.NamingException;

import org.apache.log4j.Logger;
import org.cocome.tradingsystem.inventory.application.productdispatcher.IProductDispatcher;
import org.cocome.tradingsystem.util.ApplicationHelper;
import org.cocome.tradingsystem.util.JmsHelper;
import org.cocome.tradingsystem.util.Names;
import org.cocome.tradingsystem.util.RemoteComponent;
import org.cocome.tradingsystem.util.event.ObjectMessageListener;

/**
 * Launcher for the store inventory application.
 * 
 * @author Lubomir Bulej
 */
public final class StoreApplicationLauncherMain {

	private static final Logger LOG =
			Logger.getLogger(StoreApplicationLauncherMain.class);

	/**
	 * Used by Ant target to start an inventory store component, subscribe it to
	 * event channel to receive events from the store cash desk line and finally
	 * register it in RMI registry. The following arguments are expected on the
	 * command line:
	 * <ul>
	 * {@code registryHost registryPort storeName storeId}
	 * </ul>
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(final String[] args) throws Exception {
		if (args.length != 4) {
			System.err.println("Expected 4 arguments: registryHost registryPort storeName dispatcherName");
			System.exit(1);
		}

		StoreApplicationLauncherMain.startStoreApplication(args[0], args[1], args[2], args[3]);

		ApplicationHelper.sleepForever();
	}

	/**
	 * 
	 * @param registryHost
	 * @param registryPort
	 * @param storeName
	 * @param dispatcherName
	 * @throws Exception
	 */
	private static void startStoreApplication(final String registryHost, final String registryPort, final String storeName, final String dispatcherName)
			throws Exception {
		final String description = "application " + storeName;
		LOG.debug("Starting store " + description);

		final String remoteName = Names.getStoreRemoteName(storeName);
		ApplicationHelper.serviceStarting(remoteName);

		//

		try {
			ApplicationHelper.initNaming(registryHost, registryPort);
			final Connection connection = JmsHelper.createConnection();

			final StoreServer store = StoreApplicationLauncherMain.buildStoreApplication(storeName, dispatcherName, connection);

			ApplicationHelper.registerComponent(remoteName, store);
			connection.start();

		} catch (final Exception e) {
			LOG.fatal("Failed to start store " + description);
			LOG.fatal(e.getClass().getName() + ": " + e.getMessage());
			System.exit(1);
		}

		//

		ApplicationHelper.serviceStarted(remoteName);
		LOG.info("Store " + description + " started");
	}

	/**
	 * 
	 * @param storeName
	 * @param dispatcherName
	 * @param connection
	 * @return
	 * @throws RemoteException
	 * @throws JMSException
	 * @throws NamingException
	 */
	private static StoreServer buildStoreApplication(final String storeName, final String dispatcherName, final Connection connection) throws RemoteException,
			JMSException, NamingException {

		final RemoteComponent<IProductDispatcher> remoteDispatcher = ApplicationHelper.getRemoteComponent(Names.getProductDispatcherRemoteName(dispatcherName),
				IProductDispatcher.class);

		final StoreServer store = StoreServer.newInstance(storeName, remoteDispatcher);

		//
		// Create a session for messages originating in the context of the JMS
		// dispatcher thread. Create the store application server and register a
		// separate event handler as consumer in the session.
		//
		final Session session = JmsHelper.createSession(connection);
		final String storeTopicName = Names.getStoreTopicName(storeName);

		JmsHelper.registerConsumer(session, storeTopicName, new ObjectMessageListener(new StoreEventHandler(store)));

		return store;
	}

}
