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

import java.io.Serializable;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.cocome.tradingsystem.util.java.Reflection;

import org.apache.log4j.Logger;

/**
 * Helper class to simplify JMS usage and keep JMS setup code in one place.
 * 
 * @author Lubomir Bulej
 */
public final class JmsHelper {

	private static final class Naming {
		private final Context __context;

		Naming() throws NamingException {
			__context = new InitialContext();
		}

		<T> T lookup(final String name, final Class<T> type)
				throws NamingException {
			return type.cast(__context.lookup(name));
		}
	}

	//

	/**
	 * Simple message producer associated with a session. This producer is
	 * intended to be used by components that only need to publish serializable
	 * object messages.
	 */
	public static final class SessionBoundProducer {
		private final Session __session;
		private final MessageProducer __producer;

		private SessionBoundProducer(
				final Session session, final MessageProducer producer) {
			__session = session;
			__producer = producer;
		}

		/** Sends a serializable object message. */
		public void send(final Serializable message) throws JMSException {
			__producer.send(__session.createObjectMessage(message));
		}

		/**
		 * Sends a serializable object message without caring about potential
		 * JMS exception. If such an exception occurs, it is logged but
		 * otherwise ignored.
		 */
		public void sendAsync(final Serializable message) {
			try {
				send(message);

			} catch (final JMSException jmse) {
				final Logger log = Logger.getLogger(Reflection.getCallerClass());
				final String typeName = message.getClass().getSimpleName();
				log.error("Failed to emit " + typeName, jmse);
			}
		}
	}

	//

	/** Connection factory name to look for. */
	private static final String __CONNECTION_FACTORY__ = "ConnectionFactory";

	//

	/** Naming singleton. */
	private static Naming __naming__;

	/** JMS connection factory singleton. */
	private static ConnectionFactory __factory__;

	//

	private JmsHelper() {
		// utility class, not to be instantiated
	}

	//

	public static Connection createConnection() throws JMSException, NamingException {
		__initFactory();
		return __factory__.createConnection();
	}

	/**
	 * Looks up a topic of given name in the initial context.
	 * 
	 * @param name
	 *            name of the topic to look up
	 * @return
	 *         topic corresponding to the given name
	 * @throws NamingException
	 */
	public static Topic lookupTopic(final String name) throws NamingException {
		__initNaming();
		return __naming__.lookup(name, Topic.class);
	}

	public static Session createSession(final Connection connection) throws JMSException {
		return connection.createSession(
				false, // normal session
				Session.AUTO_ACKNOWLEDGE
				);
	}

	public static Session createTransactedSession(final Connection connection) throws JMSException {
		return connection.createSession(
				true, // transacted session
				Session.AUTO_ACKNOWLEDGE
				);
	}

	/**
	 * Creates a {@link SessionBoundProducer} for the given topic, which allows the
	 * client to send {@link Serializable} messages to the topic.
	 * 
	 * @param topicName
	 *            name of the topic
	 * @return
	 *         simple message producer for the topic
	 */
	public static SessionBoundProducer createSessionBoundProducer(
			final Session session, final String topicName
			) throws NamingException, JMSException {
		final Topic topic = lookupTopic(topicName);
		return new SessionBoundProducer(session, session.createProducer(topic));
	}

	/**
	 * Subscribes the given listener to the given topic. The listener will
	 * receive messages from the topic when the underlying connection starts
	 * receiving messages.
	 * 
	 * @param session
	 *            session to create a message consumer in
	 * @param topicName
	 *            name of the topic
	 * @param listener
	 *            listener to receive topic messages
	 */
	public static void registerConsumer(
			final Session session, final String topicName,
			final MessageListener listener
			) throws NamingException, JMSException {
		final Topic topic = lookupTopic(topicName);
		final MessageConsumer consumer = session.createConsumer(topic);
		consumer.setMessageListener(listener);
	}

	//

	synchronized private static void __initNaming() throws NamingException {
		if (__naming__ == null) {
			__naming__ = new Naming();
		}
	}

	synchronized private static void __initFactory() throws NamingException {
		if (__factory__ == null) {
			__initNaming();
			__factory__ = __naming__.lookup(
					JmsHelper.__CONNECTION_FACTORY__,
					ConnectionFactory.class
					);
		}
	}

}
