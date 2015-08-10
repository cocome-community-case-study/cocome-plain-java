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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import javax.jms.JMSException;

import org.cocome.tradingsystem.util.java.Iterables;
import org.cocome.tradingsystem.util.java.Lists;
import org.cocome.tradingsystem.util.java.Maps;
import org.cocome.tradingsystem.util.java.Reflection;

/**
 * Dispatches events to event handler methods according to event type. This
 * particular dispatcher uses reflection to call the appropriate handler
 * methods.
 * <p>
 * The dispatcher is always created for a particular target object, which is searched for interfaces marked by the {@link EventConsumer} annotation. Such interfaces
 * may only contain event handler methods, which take a single {@link Serializable} argument and return {@code void}. The dispatcher then looks up the implementation
 * methods in the target object and dispatches messages of matching types to those methods. Messages for which there is no handler are ignored.
 * <p>
 * This dispatcher simplifies implementation of many event handlers and enforces implementation of the required event handlers. To handle a message type, a
 * corresponding handler method signature should be added to the {@link EventConsumer}-marked interface implemented by the target, which in turn ensures
 * implementation of the handler. The dispatching is then performed automatically.
 * <p>
 * This class is not intended to be used directly. Rather, it should be used as an implementation of a particular dispatching mechanism, which should be hidden
 * before the users.
 * 
 * @author Lubomir Bulej
 */
final class ReflectionDispatcher {

	private static final Iterables.Filter<Class<?>> __EVENT_CONSUMER_FILTER__ = new Iterables.Filter<Class<?>>() {
		@Override
		public boolean accept(final Class<?> type) {
			final boolean isInterface = type.isInterface();
			final boolean isAnnotated = type.getAnnotation(EventConsumer.class) != null;

			return isInterface && isAnnotated;
		}
	};

	//

	private final Object __target;

	private final Map<Class<? extends Serializable>, Method> __eventHandlers;

	//

	private ReflectionDispatcher(
			final Object target,
			Map<Class<? extends Serializable>, Method> eventHandlers) {
		__target = target;
		__eventHandlers = eventHandlers;
	}

	//

	public void dispatch(final Serializable eventObject) throws JMSException {
		final Method handler = __eventHandlers.get(eventObject.getClass());
		if (handler != null) {
			try {
				handler.invoke(__target, eventObject);

			} catch (final InvocationTargetException ite) {
				final Throwable cause = ite.getCause();
				if (cause instanceof RuntimeException) {
					throw (RuntimeException) cause;
				} else if (cause instanceof Error) {
					throw (Error) cause;
				} else {
					throw new RuntimeException(cause);
				}

			} catch (final IllegalAccessException iae) {
				// this should not happen
				throw new AssertionError(String.format(
						"Could not access %s in %s",
						handler.getName(), __target.getClass().getName()
						));
			}
		}
	}

	//

	public static ReflectionDispatcher newInstance(
			final Object target
			) {
		final List<Class<?>> consumerInterfaces = Lists.newArrayList(
				Iterables.filter(
						Reflection.getEffectiveInterfaces(target.getClass()),
						__EVENT_CONSUMER_FILTER__
						)
				);

		if (consumerInterfaces.size() < 1) {
			throw new IllegalArgumentException(
					"Target does not implement any @EventConsumer interface!");
		}

		//

		return new ReflectionDispatcher(
				target, __collectEventHandlers(target, consumerInterfaces));
	}

	private static Map<Class<? extends Serializable>, Method> __collectEventHandlers(
			final Object target, List<Class<?>> consumerInterfaces
			) {
		final Map<Class<? extends Serializable>, Method> result = Maps.newHashMap();

		//

		for (final Class<?> consumerInterface : consumerInterfaces) {
			for (final Method interfaceMethod : consumerInterface.getMethods()) {
				__ensureMethodIsEventHandler(consumerInterface, interfaceMethod);
				final Method targetMethod = __getTargetMethod(target, interfaceMethod);

				//
				// The following cast is OK, because to be considered an event
				// handler, the method argument must be Serializable.
				//
				@SuppressWarnings("unchecked")
				final Class<? extends Serializable> eventClass =
						(Class<? extends Serializable>) interfaceMethod.getParameterTypes()[0];

				result.put(eventClass, targetMethod);
			}
		}

		return result;
	}

	private static void __ensureMethodIsEventHandler(
			final Class<?> consumerInterface, final Method method
			) {
		if (!__isEventHandler(method)) {
			throw new IllegalArgumentException(String.format(
					"%s() in %s is not an event handler method",
					method.getName(), consumerInterface.getSimpleName()
					));
		}
	}

	private static boolean __isEventHandler(final Method method) {
		final boolean returnsVoid =
				method.getReturnType() == void.class;

		final boolean hasSingleArgument =
				method.getParameterTypes().length == 1;

		final boolean argumentIsSerializable = hasSingleArgument ?
				Serializable.class.isAssignableFrom(method.getParameterTypes()[0]) : false;

		return returnsVoid && hasSingleArgument && argumentIsSerializable;
	}

	private static Method __getTargetMethod(
			final Object target, final Method interfaceMethod
			) {
		//
		// Get target method implementing the interface method and make
		// sure it is accessible.
		//
		try {
			final Method result = target.getClass().getMethod(
					interfaceMethod.getName(), interfaceMethod.getParameterTypes()
					);

			if (!result.isAccessible()) {
				result.setAccessible(true);
			}

			return result;

		} catch (final Exception e) {
			// Should not happen, the class implements the interface
			throw new AssertionError("expected to be unreachable");
		}
	}

}
