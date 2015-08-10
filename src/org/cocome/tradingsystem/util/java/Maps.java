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

package org.cocome.tradingsystem.util.java;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Utility class providing generic static factory methods for creating instances
 * of {@link Map} collection types. The factory methods rely on type inference
 * to determine the type parameters needed for constructing a specific instance.
 * 
 * @author Lubomir Bulej
 */
public class Maps {

	/**
	 * Prevents creating instances of this class.
	 */
	private Maps() {
		// pure static class - not to be instantiated
	}

	/* ***********************************************************************
	 * PUBLIC METHODS
	 * **********************************************************************
	 */

	/**
	 * Creates a new instance of a generic {@link HashMap}.
	 * 
	 * @see HashMap#HashMap()
	 * 
	 * @param <K>
	 *            the type of the key
	 * @param <V>
	 *            the type of the value
	 * @return
	 *         new instance of HashMap <K, V>
	 */
	public static <K, V> HashMap<K, V> newHashMap() {
		return new HashMap<K, V>();
	}

	/**
	 * Creates a new instance of a generic {@link HashMap} using
	 * mappings provided by source {@link Map}.
	 * 
	 * @see HashMap#HashMap(Map)
	 * 
	 * @param <K>
	 *            the type of the key
	 * @param <V>
	 *            the type of the value
	 * @return
	 *         new instance of HashMap <K, V>
	 */
	public static <K, V> Map<K, V> newHashMap(final Map<K, V> source) {
		return new HashMap<K, V>(source);
	}

	/**
	 * Creates a new instance of a generic {@link LinkedHashMap}.
	 * 
	 * @see LinkedHashMap#LinkedHashMap()
	 * 
	 * @param <K>
	 *            the type of the key
	 * @param <V>
	 *            the type of the value
	 * @return
	 *         new instance of LinkedHashMap <K, V>
	 */
	public static <K, V> LinkedHashMap<K, V> newLinkedHashMap() {
		return new LinkedHashMap<K, V>();
	}

	/**
	 * Creates a new instance of a generic {@link ConcurrentHashMap}.
	 * 
	 * @see ConcurrentHashMap#ConcurrentHashMap()
	 * 
	 * @param <K>
	 *            the type of the key
	 * @param <V>
	 *            the type of the value
	 * @return
	 *         new instance of ConcurrentHashMap <K, V>
	 */
	public static <K, V> ConcurrentHashMap<K, V> newConcurrentHashMap() {
		return new ConcurrentHashMap<K, V>();
	}

}
