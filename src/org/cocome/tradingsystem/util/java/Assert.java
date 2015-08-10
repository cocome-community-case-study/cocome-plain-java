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

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * Utility class providing static methods for common assertions on objects from
 * the Java runtime, i.e. the java.* packages. These methods are intended for
 * use at internal interface boundaries to document and check contract
 * assumptions, but not enforce them.
 * 
 * @author Lubomir Bulej
 */
public final class Assert {

	/**
	 * Prevents creating instances of this utility class.
	 */
	private Assert() {
		// pure static class - not to be instantiated
	}

	/* ***********************************************************************
	 * PUBLIC STATIC FIELDS
	 * **********************************************************************
	 */

	/**
	 * Represents the state of runtime assertions.
	 */
	public static final boolean ENABLED;

	static {
		//
		// Determine the state of runtime assertions. Try to force an
		// assertion error and if it succeeds, we know that assertions are
		// enabled.
		//
		boolean enabled = false;
		try {
			assert false;

		} catch (final AssertionError ae) {
			enabled = true;
		}

		ENABLED = enabled;
	}

	/* ***********************************************************************
	 * PUBLIC STATIC METHODS
	 * **********************************************************************
	 */

	public static void valueNotNegative(final long value, final String name) {
		assert value >= 0 : name + " is negative (" + value + ")";
	}

	public static void valueIsPositive(final long value, final String name) {
		assert value > 0 : name + " is not positive (" + value + ")";
	}

	public static void objectNotNull(final Object object, final String name) {
		assert object != null : name + " is null";
	}

	public static void objectIsArray(final Object object, final String name) {
		Assert.objectNotNull(object, name);
		assert object.getClass().isArray() : name + " is not an array";
	}

	public static void arrayNotEmpty(final int[] array, final String name) {
		Assert.objectNotNull(array, name);
		__arrayNotEmpty(array.length, name);
	}

	public static void arrayNotEmpty(final Object array, final String name) {
		Assert.objectIsArray(array, name);
		__arrayNotEmpty(Array.getLength(array), name);

	}

	private static void __arrayNotEmpty(final int length, final String name) {
		assert length > 0 : name + " array is empty";
	}

	public static void collectionNotEmpty(final Collection<?> collection, final String name) {
		Assert.objectNotNull(collection, name);
		assert collection.size() > 0 : name + " collection is empty";
	}

	public static void stringNotEmpty(final String string, final String name) {
		Assert.objectNotNull(string, name);
		assert !string.isEmpty() : name + " is empty";
	}

	public static void unreachable() {
		throw new AssertionError("unreachable code reached");
	}

	public static void unreachable(final String message) {
		throw new AssertionError(message);
	}

}
