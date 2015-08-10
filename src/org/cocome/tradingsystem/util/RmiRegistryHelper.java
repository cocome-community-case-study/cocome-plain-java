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

import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.NotBoundException;
import java.rmi.RMISecurityManager;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import org.apache.log4j.Logger;

/**
 * Singleton providing centralized access to the RMI registry. Before invoking
 * any (static) methods on this class, the helper needs to be initialized to
 * associate itself with the given RMI registry by calling the {@link #init(String, int) init()} method with the host name and port of the
 * desired RMI registry.
 * 
 * @author Lubomir Bulej
 */
final class RmiRegistryHelper {

	private static final Logger LOG = Logger.getLogger(RmiRegistryHelper.class);

	//

	private static Registry registry;
	private static URI registryUri;

	//

	private RmiRegistryHelper() {
		// utility class, not to be instantiated
	}

	/**
	 * Initializes the RMI helper singleton with application-wide RMI registry.
	 * The helper can be only bound once, any attempts to rebind it to other RMI
	 * registry will be ignored.
	 * 
	 * @param registryHost
	 *            host for the remote registry
	 * @param registryPort
	 *            port on which the registry accepts requests
	 */
	public static synchronized void init(final String registryHost, final int registryPort) throws RuntimeException {
		RmiRegistryHelper.assertStringNotEmpty(registryHost);

		//

		final URI registryUri = RmiRegistryHelper.createRmiUri(registryHost, registryPort); // NOCS

		if (RmiRegistryHelper.registry == null) {
			if (System.getSecurityManager() == null) {
				System.setSecurityManager(new RMISecurityManager());
			}

			try {
				RmiRegistryHelper.registry = LocateRegistry.getRegistry(registryHost, registryPort);
				RmiRegistryHelper.registryUri = registryUri;

			} catch (final RemoteException re) {
				throw new RuntimeException(
						String.format("Failed to initialize RMI registry helper for %s", registryUri),
						re);
			}

		} else {
			if (!registryUri.equals(registryUri)) {
				LOG.warn(String.format(
						"RMI registry helper singleton already bound to %s, ignoring attempt to bind it to %s",
						registryUri, registryUri
						));
			}
		}
	}

	/**
	 * 
	 * @param host
	 * @param port
	 * @return
	 */
	private static URI createRmiUri(final String host, final int port) throws RuntimeException {
		try {
			return new URI(String.format("rmi://%s:%d", host, port));

		} catch (final URISyntaxException use) {
			throw new RuntimeException("Failed to create RMI URI", use);
		}
	}

	/**
	 * Returns the remote reference bound to the specified name in the
	 * application-wide RMI registry, or {@code null} if {@code name} is not
	 * currently bound.
	 * 
	 * @param <T>
	 *            generic type of the remote reference; must extend {@link Remote}
	 * @param name
	 *            the name for the remote reference to look up
	 * @param type
	 *            the type of the remote reference
	 * @return
	 *         remote reference cast to the desired type or {@code null} if {@code name} is not currently bound
	 * @throws RemoteException
	 *             if remote communication with the registry failed
	 */
	public static <T extends Remote> T softLookup(final String name, final Class<T> type) throws RemoteException {
		try {
			return RmiRegistryHelper.lookup(name, type);

		} catch (final NotBoundException nbe) {
			LOG.warn(String.format(
					"Remote object %s (%s) not bound in RMI registry at %s",
					name, type.getName(), registryUri
					));
			return null;
		}
	}

	/**
	 * Replaces the binding for the specified name in the application-wide RMI
	 * registry with the supplied remote reference. If there is an existing
	 * binding for the specified name, it is discarded.
	 * 
	 * @param name
	 *            the name to associate with the remote reference
	 * @param remote
	 *            a reference to a remote object (usually a stub)
	 * @throws RemoteException
	 *             if remote communication with the registry failed
	 */
	public static void rebind(final String name, final Remote remote) throws RemoteException {
		RmiRegistryHelper.ensureHelperInitialized();

		RmiRegistryHelper.assertStringNotEmpty(name);
		RmiRegistryHelper.assertObjectNotNull(remote);

		registry.rebind(name, remote);
		LOG.debug(String.format(
				"Registered %s as %s in RMI registry at %s",
				remote.getClass().getName(), name, registryUri
				));
	}

	/**
	 * Removes the binding for the specified {@code name} from the
	 * application-wide RMI registry.
	 * 
	 * @param name
	 *            the name of the binding to remove
	 * @throws RemoteException
	 *             if remote communication with the registry failed
	 * @return
	 *         {@code true} if the binding has been removed, {@code false} if the name was not bound
	 */
	public static boolean unbind(
			final String name
			) throws RemoteException {
		RmiRegistryHelper.ensureHelperInitialized();

		RmiRegistryHelper.assertStringNotEmpty(name);

		try {
			registry.unbind(name);
			LOG.debug(String.format(
					"Unregistered %s from RMI registry at %s",
					name, registryUri
					));
			return true;

		} catch (final NotBoundException nbe) {
			//
			// Don't make too much fuss about unbound names, just allow to
			// check for it if interested...
			//
			LOG.debug(String.format(
					"Name not bound, cannot unregister %s from RMI registry at %s",
					name, registryUri
					));
			return false;
		}

	}

	/**
	 * Returns the remote reference bound to the specified name in the
	 * application-wide RMI registry.
	 * 
	 * @param <T>
	 *            generic type of the remote reference; must extend {@link Remote}
	 * @param name
	 *            the name for the remote reference to look up
	 * @param type
	 *            the type of the remote reference
	 * @return
	 *         remote reference cast to the desired type
	 * @throws RemoteException
	 *             if remote communication with the registry failed
	 * @throws NotBoundException
	 *             if {@code name} is not currently bound
	 */
	public static <T extends Remote> T lookup(
			final String name, final Class<T> type
			) throws RemoteException, NotBoundException {
		RmiRegistryHelper.ensureHelperInitialized();

		RmiRegistryHelper.assertStringNotEmpty(name);
		RmiRegistryHelper.assertObjectNotNull(type);

		final Remote remote = registry.lookup(name);

		try {
			return type.cast(remote);

		} catch (final ClassCastException cce) {
			LOG.error(String.format(
					"Remote object %s found, but is of type %s instead of %s",
					name, remote.getClass().getName(), type.getName()
					));
			throw cce;
		}
	}

	//

	private static synchronized void ensureHelperInitialized() {
		if (registry == null) {
			throw new IllegalStateException("RmiRegistryHelper not initialized");
		}
	}

	private static void assertObjectNotNull(final Object object) {
		assert object != null;
	}

	private static void assertStringNotEmpty(final String string) {
		RmiRegistryHelper.assertObjectNotNull(string);
		assert !string.isEmpty();
	}

}
