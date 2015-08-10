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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Utility class providing generic static factory methods for creating instances
 * of {@link List} collection types. The factory methods rely on type inference
 * to determine the type parameters needed for constructing a specific instance.
 * 
 * @author Lubomir Bulej
 */
public final class Lists {

	/**
	 * Prevents public from creating instances of this class.
	 */
	private Lists() {
		// pure static class - not to be instantiated
	}

	/* ***********************************************************************
	 * PUBLIC STATIC METHODS
	 * **********************************************************************
	 */

	/**
	 * Creates a new instance of a generic {@link ArrayList}.
	 * 
	 * @param <E>
	 *            the type of the list element
	 * @return
	 *         new {@link ArrayList} instance of appropriate type
	 */
	public static <E> ArrayList<E> newArrayList() {
		return new ArrayList<E>();
	}

	/**
	 * Creates a new instance of a generic {@link ArrayList} with a given
	 * initial capacity.
	 * 
	 * @param <E>
	 *            the type of the list element
	 * @param initialCapacity
	 *            the initial capacity of the list
	 * @return
	 *         new {@link ArrayList} instance of appropriate type
	 * @throws IllegalArgumentException
	 *             if the specified initial capacity is negative
	 */
	public static <E> ArrayList<E> newArrayList(final int initialCapacity) {
		return new ArrayList<E>(initialCapacity);
	}

	/**
	 * Creates a new instance of a generic {@link ArrayList} and fills it with {@code items} from the given collection.
	 * 
	 * @param <E>
	 *            the type of the list element
	 * @param items
	 *            collection of items to put into the list
	 * @return
	 *         new instance of ArrayList <E>
	 */
	public static <E> ArrayList<E> newArrayList(final Collection<E> items) {
		Assert.objectNotNull(items, "items");

		//

		return new ArrayList<E>(items);
	}

	/**
	 * Creates a new instance of a generic {@link ArrayList} and fills it with {@code items} from the given iterable.
	 * 
	 * @param <E>
	 *            the type of the list element
	 * @param items
	 *            iterable of items to put into the list
	 * @return
	 *         new instance of ArrayList <E>
	 */
	public static <E> ArrayList<E> newArrayList(final Iterable<E> items) {
		Assert.objectNotNull(items, "items");

		//

		final ArrayList<E> result = newArrayList();
		for (final E item : items) {
			result.add(item);
		}

		return result;
	}

	/**
	 * Creates a new instance of a generic {@link ArrayList} and fills it with items from a given collections.
	 * 
	 * @param <E>
	 *            the type of the list element
	 * @param collections
	 *            collections of items to put into the list
	 * @return
	 *         new instance of ArrayList <E>
	 */
	public static <E> List<E> newArrayList(
			final Collection<E>... collections
			) {
		Assert.objectNotNull(collections, "collections");

		//

		final ArrayList<E> result = newArrayList();
		for (final Collection<E> collection : collections) {
			result.addAll(collection);
		}

		return result;
	}

	/**
	 * Creates a new instance of a generic {@link LinkedList}.
	 * 
	 * @param <E>
	 *            the type of the list element
	 * @return
	 *         new instance of LinkedList <E>
	 */
	public static <E> LinkedList<E> newLinkedList() {
		return new LinkedList<E>();
	}

	/**
	 * Creates a new instance of a generic {@link LinkedList} and fills it with items from a given collection.
	 * 
	 * @param <E>
	 *            the type of the list element
	 * @param items
	 *            collection of items to put into the list
	 * @return
	 *         new instance of LinkedList <E>
	 */
	public static <E> LinkedList<E> newLinkedList(final Collection<E> items) {
		Assert.objectNotNull(items, "items");

		//

		return new LinkedList<E>(items);
	}

	/**
	 * Creates a new instance of a generic {@link LinkedList} and fills it
	 * with given items.
	 * 
	 * @param <E>
	 *            the type of the list element
	 * @param items
	 *            array of items to put into the list
	 * @return
	 *         new instance of LinkedList <E>
	 */
	public static <E> LinkedList<E> newLinkedList(final E... items) {
		return new LinkedList<E>(Arrays.asList(items));
	}

	/**
	 * Creates a new instance of a {@link LinkedList} and fills it {@link Integer} value objects based on values in a given array of
	 * integer.
	 * 
	 * @param values
	 *            array of integers to create the list for
	 * @return
	 *         new instance of LinkedList <Integer>
	 */
	public static LinkedList<Integer> newLinkedList(final int... values) {
		Assert.objectNotNull(values, "values");

		//

		final LinkedList<Integer> result = new LinkedList<Integer>();

		for (final int value : values) {
			result.add(Integer.valueOf(value));
		}

		return result;
	}

	/**
	 * Creates a new instance of a {@link LinkedList} and fills it {@link String} instances passed in a given array.
	 * 
	 * @param values
	 *            array of strings to create the list for
	 * @return
	 *         new instance of LinkedList <String>
	 */
	public static LinkedList<String> newLinkedList(final String... values) {
		final LinkedList<String> result = new LinkedList<String>();

		for (final String value : values) {
			result.add(value);
		}

		return result;
	}

}
