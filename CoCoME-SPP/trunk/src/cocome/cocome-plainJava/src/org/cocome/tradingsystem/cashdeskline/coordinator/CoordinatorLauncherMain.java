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

package org.cocome.tradingsystem.cashdeskline.coordinator;

import javax.jms.Connection;
import javax.jms.JMSException;
import javax.jms.Session;
import javax.naming.NamingException;

import org.cocome.tradingsystem.util.ApplicationHelper;
import org.cocome.tradingsystem.util.JmsHelper;
import org.cocome.tradingsystem.util.Names;
import org.cocome.tradingsystem.util.event.ObjectMessageListener;

import org.apache.log4j.Logger;

/**
 * Launcher for the cash desk line coordinator component.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
public final class CoordinatorLauncherMain {

	private static final Logger LOG =
			Logger.getLogger(CoordinatorLauncherMain.class);

	//

	/**
	 * Used by Ant target to start a cash desk line coordinator. The following
	 * arguments are expected on the command line:
	 * <ul>
	 * {@code storeName}
	 * </ul>
	 * 
	 * @param args
	 *            command line arguments
	 */
	public static void main(final String[] args) throws Exception {
		if (args.length != 1) {
			System.err.println(
					"Expecting 1 argument: storeName"
					);
			System.exit(1);
		}

		//

		startCashDeskLineCoordinator(args[0]);

		ApplicationHelper.sleepForever();
	}

	/**
	 * Starts the cash desk line coordinator.
	 * 
	 * @param storeName
	 *            name of the store for which to start the coordinator
	 */
	private static void startCashDeskLineCoordinator(
			final String storeName
			) throws InterruptedException {
		final String description = "desk coordinator for " + storeName;
		LOG.debug("Starting cash " + description);

		final String serviceName = "Coordinator." + storeName;
		ApplicationHelper.serviceStarting(serviceName);

		//
		// Create a JMS connection and the coordinator. When the coordinator
		// is ready, start the JMS connection to receive messages.
		//

		try {
			final Connection connection = JmsHelper.createConnection();
			buildCoordinator(storeName, connection);
			connection.start();

		} catch (final Exception e) {
			LOG.fatal("Failed to start cash " + description);
			LOG.fatal(e.getMessage());
			System.exit(1);
		}

		//

		ApplicationHelper.serviceStarted(serviceName);
		LOG.info("Cash " + description + " started");
	}

	private static void buildCoordinator(
			final String storeName, final Connection connection
			) throws JMSException, NamingException {
		//
		// Create a session for messages originating and produced in the context
		// of the JMS dispatcher thread. Create the coordinator model and
		// register a separate event handler as consumer in the session.
		//
		final Session session = JmsHelper.createSession(connection);
		final String storeTopicName = Names.getStoreTopicName(storeName);

		//

		final CoordinatorModel coordinator = new CoordinatorModel(
				JmsHelper.createSessionBoundProducer(session, storeTopicName)
				);

		JmsHelper.registerConsumer(
				session, storeTopicName,
				new ObjectMessageListener(new CoordinatorEventHandler(coordinator))
				);
	}

}
