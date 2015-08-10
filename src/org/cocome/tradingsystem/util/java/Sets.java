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

import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * Utility class providing generic static factory methods for creating instances
 * of {@link Set} collection types. The factory methods rely on type inference
 * to determine the type parameters needed for constructing a specific instance.
 * 
 * @author Lubomir Bulej
 */
public final class Sets {

	/**
	 * Prevents creating instances of this class.
	 */
	private Sets() {
		// pure static class - not to be instantiated
	}

	/* ***********************************************************************
	 * PUBLIC METHODS
	 * **********************************************************************
	 */

	/**
	 * Creates a new instance of a generic {@link HashSet}.
	 * 
	 * @param <E>
	 *            the type of the set element
	 * @return
	 *         new instance of HashSet <E>
	 */
	public static <E> HashSet<E> newHashSet() {
		return new HashSet<E>();
	}

	/**
	 * Creates a new instance of a generic {@link LinkedHashSet}.
	 * 
	 * @param <E>
	 *            the type of the set element
	 * @return
	 *         new instance of LinkedHashSet <E>
	 */
	public static <E> LinkedHashSet<E> newLinkedHashSet() {
		return new LinkedHashSet<E>();
	}

	/**
	 * Creates a new instance of a generic {@link TreeSet}.
	 * 
	 * @param <E>
	 *            the type of the set element
	 * @return
	 *         new instance of TreeSet <E>
	 */
	public static <E> TreeSet<E> newTreeSet() {
		return new TreeSet<E>();
	}

	/**
	 * Creates a new instance of a generic {@link EnumSet} containing the
	 * specified elements.
	 * 
	 * @param <E>
	 *            the type of the set element
	 * @return
	 *         new instance of EnumSet <E>
	 */
	public static <E extends Enum<E>> Set<E> newEnumSet(final E... elements) {
		//
		// The following cast is OK, because the method will only accept enum
		// elements of the same class.
		//
		@SuppressWarnings("unchecked")
		final Class<E> enumClass = (Class<E>) elements.getClass().getComponentType();

		final EnumSet<E> result = EnumSet.noneOf(enumClass);
		for (final E element : elements) {
			result.add(element);
		}

		return result;
	}

}
