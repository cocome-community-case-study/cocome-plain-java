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

package org.cocome.tradingsystem.util.event;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.apache.log4j.Logger;

/**
 * Implements a {@link MessageListener} for object messages and uses an {@link IEventDispatcher} to handle various event objects.
 * 
 * @author Lubomir Bulej
 */
public final class ObjectMessageListener implements MessageListener {

	private static final Logger LOG = Logger.getLogger(ObjectMessageListener.class);

	//

	/**
	 * An event dispatcher interface for the message listener.
	 */
	public interface IEventDispatcher {

		/**
		 * Dispatches an event object to the appropriate event handler.
		 * 
		 * @param eventObject
		 *            event object to dispatch
		 * @throws JMSException
		 */
		void dispatch(final Serializable eventObject) throws JMSException;

	}

	//

	private final IEventDispatcher dispatcher;

	//

	public ObjectMessageListener(final IEventDispatcher dispatcher) {
		this.dispatcher = dispatcher;
	}

	/**
	 * Handles object messages delivered via the message channel. Extracts event
	 * object from {@link ObjectMessage} and delegates the dispatching to
	 * specific event handlers to given {@link IEventDispatcher}.
	 */
	// @Override
	@Override
	public final void onMessage(final Message message) {
		if (message instanceof ObjectMessage) {
			final ObjectMessage objectMessage = (ObjectMessage) message;

			try {
				this.dispatcher.dispatch(objectMessage.getObject());

			} catch (final JMSException jmse) {
				LOG.warn("Failed to extract message object, message ignored!", jmse);
			}
		}
	}

}
