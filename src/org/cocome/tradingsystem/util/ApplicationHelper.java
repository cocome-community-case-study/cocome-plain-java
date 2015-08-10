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

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import org.cocome.tradingsystem.util.java.Sets;

/**
 * Contains various utility methods for application classes.
 * 
 * @author Lubomir Bulej
 */
public final class ApplicationHelper {

	private static final String RUN_DIR_PROPERTY = "application.status.dir";

	private static final File RUN_DIR =
			new File(System.getProperty(RUN_DIR_PROPERTY, "."));

	private static final Set<String> REGISTER_NAMES = Sets.newHashSet();

	//

	static {
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			@Override
			public void run() {
				for (final String name : REGISTER_NAMES) {
					try {
						ApplicationHelper.unbindName(name);

					} catch (final RemoteException e) {
						// ignore any remote exceptions when unbinding the name
					}
				}
			}
		}));
	}

	//

	private ApplicationHelper() {
		// utility class, not to be instantiated
	}

	//

	/**
	 * 
	 * @param registryHost
	 * @param port
	 */
	public static void initNaming(final String registryHost, final String port) throws IllegalArgumentException, RuntimeException {
		ApplicationHelper.initNaming(registryHost, ApplicationHelper.parsePort(port));
	}

	/**
	 * 
	 * @param registryHost
	 * @param registryPort
	 */
	public static void initNaming(final String registryHost, final int registryPort) throws RuntimeException {
		RmiRegistryHelper.init(registryHost, registryPort);
	}

	/**
	 * 
	 * @param name
	 * @param remote
	 */
	public static void registerComponent(final String name, final Remote remote) throws RuntimeException {
		try {
			RmiRegistryHelper.rebind(name, remote);
			REGISTER_NAMES.add(name);

		} catch (final RemoteException re) {
			throw new RuntimeException(
					String.format("Failed to register component %s", name), re);
		}
	}

	/**
	 * 
	 * @param name
	 * @return
	 */
	public static boolean unregisterComponent(final String name) throws RuntimeException {
		try {
			final boolean nameUnregistered = ApplicationHelper.unbindName(name);
			if (nameUnregistered) {
				REGISTER_NAMES.remove(name);
			}

			return nameUnregistered;

		} catch (final RemoteException re) {
			throw new RuntimeException(
					String.format("Failed to unregister component %s", name), re);
		}
	}

	/**
	 * 
	 * @param name
	 * @return
	 * @throws RemoteException
	 */
	private static boolean unbindName(final String name) throws RemoteException {
		return RmiRegistryHelper.unbind(name);
	}

	/**
	 * 
	 * @param name
	 * @param type
	 * @return
	 * @throws RemoteException
	 * @throws NotBoundException
	 */
	public static <T extends Remote> T getComponent(final String name, final Class<T> type) throws RemoteException, NotBoundException {
		return RmiRegistryHelper.lookup(name, type);
	}

	/**
	 * 
	 * @param name
	 * @param type
	 * @return
	 */
	public static <T extends Remote> RemoteComponent<T> getRemoteComponent(final String name, final Class<T> type) {
		return new RemoteComponent<T>(name, type);
	}

	/**
	 * Creates a temporary pid file for a service, which gets auto deleted on exit.
	 * 
	 * @param name
	 *            the service name
	 */
	public static void serviceStarting(final String name) {
		new File(RUN_DIR, name + ".pid").deleteOnExit();
	}

	/**
	 * Delete the old status file of a service.
	 * 
	 * @param name
	 *            service name
	 */
	public static void serviceStarted(final String name) {
		new File(RUN_DIR, name + ".status").delete();
	}

	/**
	 * Parse an string containing a port number.
	 * 
	 * @param portString
	 *            the port number as a string
	 * @return returns the port number
	 * @throws IllegalArgumentException
	 *             if the given string does not contain a valid port number
	 */
	public static int parsePort(final String portString) throws IllegalArgumentException {
		try {
			final int port = Integer.parseInt(portString);
			if ((port < 0) || (port > 65535)) {
				throw new IllegalArgumentException("Port number not in range.");
			}

			return port;

		} catch (final NumberFormatException e) {
			//
			// Handle both NumberFormatException for non-integer ports as
			// well as IllegalArgumentException for invalid ports.
			//
			throw new IllegalArgumentException(String.format("Illegal port number %s (must be a number between 0 and 65535)",
					portString));
		}
	}

	/**
	 * A really bad way to keep a thread waiting.
	 * 
	 * @throws InterruptedException
	 */
	public static void sleepForever() throws InterruptedException {
		while (true) {
			Thread.sleep(10000);
			Thread.yield();
		}

	}

}
