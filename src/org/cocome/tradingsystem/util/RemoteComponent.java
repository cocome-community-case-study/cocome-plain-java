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

import java.rmi.Remote;

/**
 * Encapsulates remote component name and type to provide on-demand lookup when
 * a component is needed. This enables certain failure tolerance in RMI-based
 * communication with remote components, because they are looked up only when
 * needed.
 * 
 * @author Lubomir Bulej
 */
public final class RemoteComponent<T extends Remote> {
	private final String __name;
	private final Class<T> __type;

	//

	RemoteComponent(final String name, final Class<T> type) {
		__name = name;
		__type = type;
	}

	public T get() {
		try {
			return ApplicationHelper.getComponent(__name, __type);

		} catch (final Exception e) {
			throw new ComponentNotAvailableException(
					__name, __type, e);
		}
	}

}
