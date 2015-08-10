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

import org.apache.log4j.Logger;
import org.cocome.tradingsystem.util.event.ObjectMessageListener.IEventDispatcher;

/**
 * Base class for event handler implementations. By default, it dispatches
 * events to event handler methods according to event type. This class is
 * intended to be used by event handler implementations, which do not need to
 * know anything about the dispatching mechanism.
 * <p>
 * To hook into the dispatching process, subclasses need to override the {@link #_dispatch(Serializable)} method and either take care of the dispatching themselves,
 * or reuse the default implementation and perform various task before and after an event handler has been called.
 * 
 * @author Lubomir Bulej
 */
public abstract class AbstractSerializableEventDispatcher implements IEventDispatcher {

	private final Logger log = Logger.getLogger(this.getClass());

	private final String handlerName;

	private final ReflectionDispatcher dispatcher;

	/**
	 * Create a serializable event dispatcher.
	 * 
	 * @param handlerName
	 *            name of the handler
	 */
	public AbstractSerializableEventDispatcher(final String handlerName) {
		this.handlerName = handlerName;
		this.dispatcher = ReflectionDispatcher.newInstance(this);
	}

	/**
	 * Dispatches the given {@link Serializable} event to appropriate event
	 * handler. This is a template method that first logs details about the
	 * received event and then call a protected method {@link #_dispatch(Serializable)}. The protected method by default uses a
	 * reflection-based event dispatcher to invoke the appropriate method of the
	 * event handler.
	 * 
	 * @param eventObject
	 *            event object to dispatch
	 * @throws JMSException
	 *             when event communication fails
	 */
	@Override
	public void dispatch(final Serializable eventObject) throws JMSException {
		if (this.log.isDebugEnabled()) {
			final String eventName = eventObject.getClass().getSimpleName();
			this.log.debug(String.format("%s: received %s", this.handlerName, eventName));
		}

		this.dispatcher.dispatch(eventObject);
	}

}
