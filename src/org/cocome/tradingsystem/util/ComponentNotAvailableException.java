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

/**
 * Exception thrown by the {@link RemoteComponent} when a component is not
 * available. This can be caused by errors during communication with the RMI
 * registry, or when a component cannot be found in the RMI registry.
 * 
 * @author Lubomir Bulej
 */
public final class ComponentNotAvailableException extends RuntimeException {

	ComponentNotAvailableException(
			final String name, final Class<?> type, final Throwable cause) {
		super(String.format(
				"%s named %s is not available: %s",
				type.getSimpleName(), name, cause.getMessage()
				));
	}

}
