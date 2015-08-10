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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Set;

/**
 * Utility class simplifying the usage of Java reflection.
 * 
 * @author Lubomir Bulej
 */
public class Reflection {

	/**
	 * Prevents public from creating instances of this class.
	 */
	private Reflection() {
		// pure static class - not to be instantiated
	}

	/* ***********************************************************************
	 * PRIVATE STATIC CLASS: InheritanceChainIterable
	 * **********************************************************************
	 */

	/**
	 * Provides an iterable interface to the inheritance hierarchy of a class.
	 * The provided iterator allows traversing the inheritance hierarchy from
	 * the given (leaf) class to the (root) base class. The iterator (obviously)
	 * does not support element removal, since it operates on an immutable
	 * structure.
	 */
	private static final class InheritanceChainIterable
			implements Iterable<Class<?>> {
		private final Class<?> __leafClass;

		/**
		 * Creates an iterable for the given leaf class. If the leaf class
		 * is {@code null}, the iterable produces an empty iterator.
		 */
		private InheritanceChainIterable(final Class<?> leafClass) {
			__leafClass = leafClass;
		}

		@Override
		public Iterator<Class<?>> iterator() {
			return newIterator(__leafClass);
		}

		private static Iterator<Class<?>> newIterator(final Class<?> leafClass) {
			return new Iterables.ImmutableIterator<Class<?>>() {
				private Class<?> __nextClass = leafClass;

				@Override
				public final boolean hasNext() {
					return __nextClass != null;
				}

				@Override
				public final Class<?> next() {
					if (hasNext()) {
						final Class<?> next = __nextClass;
						__nextClass = next.getSuperclass();
						return next;

					} else {
						throw new NoSuchElementException();
					}
				}
			};
		}
	}

	/* ***********************************************************************
	 * PRIVATE STATIC CLASS: DeclaredFieldsIterable
	 * **********************************************************************
	 */

	/**
	 * Provides an iterable interface to fields declared in a class hierarchy.
	 * The provided iterator traverses the inheritance hierarchy from the given
	 * (leaf) class to the (root) base class and returns all fields declared in
	 * the classes along the inheritance path. The iterator (obviously) does not
	 * support element removal, since it operates on an immutable structure.
	 */
	private static final class DeclaredFieldsIterable
			implements Iterable<Field> {
		private final Class<?> __leafClass;

		/**
		 * Creates an iterable for the given leaf class. If the leaf class
		 * is {@code null}, the iterable produces an empty iterator.
		 */
		private DeclaredFieldsIterable(final Class<?> leafClass) {
			__leafClass = leafClass;
		}

		@Override
		public Iterator<Field> iterator() {
			return newIterator(__leafClass);
		}

		private static Iterator<Field> newIterator(final Class<?> leafClass) {
			return new Iterables.ImmutableIterator<Field>() {
				private final Iterator<Class<?>> __classes =
						InheritanceChainIterable.newIterator(leafClass);

				private Iterator<Field> __fields = Iterables.emptyIterator();

				@Override
				public boolean hasNext() {
					//
					// If there are no fields, check if there is another class
					// where to look for fields. If we run out of classes, there
					// are no more fields left.
					//
					while (!__fields.hasNext()) {
						if (__classes.hasNext()) {
							final Class<?> nextClass = __classes.next();
							__fields = Iterables.iterator(nextClass.getDeclaredFields());

						} else {
							return false;
						}
					}

					return true;
				}

				@Override
				public Field next() {
					if (!hasNext()) {
						throw new NoSuchElementException();
					}

					return __fields.next();
				}
			};
		}
	}

	/**
	 * An instance of the {@link Comparator} interface used to compare
	 * methods for purpose of ordering and searching.
	 */
	private static final Comparator<Method> __METHOD_COMPARATOR__ =
			new Comparator<Method>() {
				public int compare(final Method m1, final Method m2) {
					return m1.toGenericString().compareTo(m2.toGenericString());
				}
			};

	/* **********************************************************************
	 * PUBLIC METHODS
	 * *********************************************************************
	 */

	/**
	 * Returns the stack trace element corresponding to the caller
	 * of the method from which this static method was invoked.
	 * 
	 * @return
	 *         stack trace element
	 */
	public static StackTraceElement getCallerStackTraceElement() {
		final StackTraceElement[] stackTrace =
				Thread.currentThread().getStackTrace();

		//
		// Stack trace array indices:
		//
		// 0 ... getStackTrace
		// 1 ... this method
		// 2 ... caller of this static method
		// 3 ... caller of the caller of this static method
		//
		return stackTrace[3];
	}

	/**
	 * Returns the class corresponding to the caller of the method
	 * from which this static method was invoked.
	 * 
	 * @return
	 *         caller class
	 */
	public static Class<?> getCallerClass() {
		final StackTraceElement[] stackTrace =
				Thread.currentThread().getStackTrace();

		//
		// Stack trace array indices:
		//
		// 0 ... getStackTrace
		// 1 ... this method
		// 2 ... caller of this static method
		// 3 ... caller of the caller of this static method
		//
		try {
			return Class.forName(stackTrace[3].getClassName());

		} catch (final ClassNotFoundException e) {
			assert false : "cannot obtain caller class";
			return null;
		}
	}

	/**
	 * Returns methods implemented by a particular interface.
	 * 
	 * @param interfaceName
	 * @return
	 */
	public static List<Method> getInterfaceMethods(final String interfaceName) {
		final List<Method> result = Lists.newArrayList();

		try {
			final Class<?> interfaceClass = Class.forName(interfaceName);

			// add interface methods to the list
			for (final Method method : interfaceClass.getMethods()) {
				result.add(method);
			}
		} catch (final ClassNotFoundException cnfe) {
			// just emit a warning and return an empty list
			System.err.println("warning: unable to get class for interface " + interfaceName);
		}

		return result;
	}

	/**
	 * Returns set of all effective interfaces implied by a given interface.
	 * This set is constructed by traversing the interface inheritance hierarchy
	 * from the given interface to the root(s) of the hierarchy, collecting all
	 * interfaces along the way, including the starting interface.
	 * 
	 * @param itfClass
	 *            interface class where the traversal should start
	 * @return
	 *         set of all effective interfaces implied by the given interface
	 */
	public static Set<Class<?>> getEffectiveInterfaces(final Class<?> itfClass) {
		//
		// Traverse the hierarchy in a breadth-first manner. Add newly visited
		// interfaces to the result set and their super interfaces to the queue
		// of interfaces to visit.
		//
		final Set<Class<?>> result = Sets.newHashSet();

		final Queue<Class<?>> queue = Lists.newLinkedList();
		queue.add(itfClass);

		while (!queue.isEmpty()) {
			final Class<?> itf = queue.poll();

			result.add(itf);
			for (final Class<?> superItf : itf.getInterfaces()) {
				if (!result.contains(superItf)) {
					queue.add(superItf);
				}
			}
		}

		return result;
	}

	/**
	 * Returns a set of all effective methods implied by a given interface. This
	 * includes methods of the interface itself and all methods declared in all
	 * super interfaces of the given interface.
	 * 
	 * @param itfClass
	 *            interface to determine effective methods for
	 * @return
	 *         set of all effective methods implied by the given interface
	 */
	public static Set<Method> getEffectiveInterfaceMethods(final Class<?> itfClass) {
		final Set<Method> result = Sets.newHashSet();
		for (final Class<?> itf : getEffectiveInterfaces(itfClass)) {
			result.addAll(Arrays.asList(itf.getMethods()));
		}

		return result;
	}

	/**
	 * Orders a list of {@link Method} instances by their generic name, which
	 * should provide stable method ordering in all executions of the same
	 * code base.
	 * 
	 * @param methods
	 *            list of {@link Method} instances to order.
	 */
	public static void orderMethods(final List<Method> methods) {
		Collections.sort(methods, __METHOD_COMPARATOR__);
	}

	/**
	 * Looks for a field of given name declared in given class or
	 * its super classes.
	 * 
	 * @param targetClass
	 *            {@link Class} where to start looking for field declaration
	 * 
	 * @param fieldName
	 *            name of the field to look for
	 * 
	 * @return
	 *         {@link Field} reflecting the declared field of given name
	 *         or {@code null} if the field was not found in the class
	 *         hierarchy
	 */
	public static Field findClassField(
			final Class<?> targetClass,
			final String fieldName
			) {
		try {
			//
			// Try the given class first -- we may be lucky.
			//
			return targetClass.getDeclaredField(fieldName);

		} catch (final NoSuchFieldException nsfe) {
			//
			// The field was not found, try the superclass.
			// If there is no super class, report failure.
			//
			final Class<?> superClass = targetClass.getSuperclass();
			if (superClass != null) {
				return findClassField(superClass, fieldName);

			} else {
				return null;
			}
		}
	}

	public static Method findClassMethod(
			final Class<?> targetClass,
			final String methodName,
			final Class<?>... paramTypes
			) {
		Method targetMethod;

		//
		// First try an exact match through the entire inheritance chain.
		//
		try {
			targetMethod = targetClass.getMethod(methodName, paramTypes);

		} catch (final Exception e) {
			targetMethod = null;
		}

		//
		// If the exact match failed, search for the method only by its name.
		// This will succeed only if there is at most one method with the
		// specified name in the entire inheritance chain.
		//
		if (targetMethod == null) {
			try {
				// System.err.println ("Searching class: "+ targetClass.getName ());
				targetMethod = searchClassForMethod(targetClass, methodName, 1);

			} catch (final Exception e) {
				// quell the exception
			}
		}

		return targetMethod;
	}

	public static Object getFieldValue(final Object target, final Field field)
			throws IllegalAccessException {
		//
		// Make the field accessible before getting its value and
		// restore the previous accessibility state after that.
		//
		final boolean accessible = field.isAccessible();

		field.setAccessible(true);
		final Object result = field.get(target);
		field.setAccessible(accessible);

		return result;
	}

	public static void setFieldValue(
			final Object target, final Field field, final Object value
			) throws IllegalAccessException {
		//
		// Make the field accessible before setting its value and
		// restore the previous accessibility state after that.
		//
		final boolean accessible = field.isAccessible();

		field.setAccessible(true);
		field.set(target, value);
		field.setAccessible(accessible);
	}

	/**
	 * Returns an iterable representing the collection of all classes along the
	 * path from leaf class (determined from the given {@code object}) to the {@link Object} base class.
	 * 
	 * @param object
	 *            the object used to derive the leaf class from
	 * @return
	 *         iterable representing the classes
	 */
	public static Iterable<Class<?>> inheritanceChain(final Object object) {
		Assert.objectNotNull(object, "object");
		return new InheritanceChainIterable(object.getClass());
	}

	/**
	 * Returns an iterable representing the collection of all classes along the
	 * path from the given leaf class to the {@link Object} base class.
	 * 
	 * @return
	 *         iterable representing the classes
	 */
	public static Iterable<Class<?>> inheritanceChain(final Class<?> leafClass) {
		return new InheritanceChainIterable(leafClass);
	}

	/**
	 * Returns an iterable representing the collection of all fields declared in
	 * classes along the path from the given leaf class to the {@link Object} base class.
	 * 
	 * @return
	 *         iterable representing the declared fields
	 */
	public static Iterable<Field> allDeclaredFields(final Object object) {
		Assert.objectNotNull(object, "object");
		return new DeclaredFieldsIterable(object.getClass());
	}

	/**
	 * Returns an iterable representing the collection of all fields declared in
	 * classes along the path from the given leaf class to the {@link Object} base class.
	 * 
	 * @return
	 *         iterable representing the declared fields
	 */
	public static Iterable<Field> allDeclaredFields(final Class<?> leafClass) {
		return new DeclaredFieldsIterable(leafClass);
	}

	/**
	 * Returns the package part of a fully qualified class name.
	 * 
	 * @param className
	 *            class name to extract the package name from
	 * 
	 * @return
	 *         package name
	 */
	public static String getPackageName(final String className) {
		Assert.objectNotNull(className, "class name");
		final int lastDotStart = className.lastIndexOf('.');
		return (lastDotStart > 0) ? className.substring(0, lastDotStart) : "";
	}

	/* **********************************************************************
	 * PRIVATE CODE
	 * *********************************************************************
	 */

	private static Method searchClassForMethod(
			final Class<?> targetClass,
			final String methodName,
			final int level
			) throws NoSuchMethodException {

		Method method = null;

		//
		// Search this class for the method name.
		//
		for (final Method classMethod : targetClass.getMethods()) {
			if (!methodName.equals(classMethod.getName())) {
				continue;
			}

			//
			// If there has been a match, check that no other
			// method of the same name has been found yet, otherwise
			// throw it away and report failure.
			//
			if (method != null && !method.equals(classMethod)) {
				throw new NoSuchMethodException();
			}

			method = classMethod;
		}

		//
		// Search the superclass (unless this class is an interface).
		//
		if (!targetClass.isInterface()) {
			final Class<?> superClass = targetClass.getSuperclass();

			if (superClass != null) {
				Method superMethod;

				superMethod = searchClassForMethod(superClass, methodName, level + 1);
				if (superMethod != null) {
					if (method != null && !method.equals(superMethod)) {
						throw new NoSuchMethodException();
					}

					method = superMethod;
				}
			}
		}

		//
		// Search the interfaces.
		//
		for (final Class<?> ifaceClass : targetClass.getInterfaces()) {
			Method ifaceMethod;

			ifaceMethod = searchClassForMethod(ifaceClass, methodName, level + 1);
			if (ifaceMethod != null) {
				if (method != null && !method.equals(ifaceMethod)) {
					throw new NoSuchMethodException();
				}

				method = ifaceMethod;
			}
		}

		return method;
	}

	/**
	 * Changes the accessibility of a reflection object and returns the previous value.
	 * This method can be used to set the accessibility of {@link Method} and {@link Field} instances.
	 * 
	 * @param ao
	 *            accessible object to set the accessibility on
	 * @param accessible
	 *            the accessibility statues to set on the object
	 * @return
	 *         the previous value of the accessibility flag
	 */
	public static boolean exchangeAccessible(
			final AccessibleObject ao, final boolean accessible
			) {
		//
		// At least synchronize on the object so that users of this
		// particular method won't step on their toes.
		//
		synchronized (ao) {
			final boolean oldAccessible = ao.isAccessible();
			ao.setAccessible(accessible);
			return oldAccessible;
		}
	}

}
