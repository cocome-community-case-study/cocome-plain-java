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

import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;

/**
 * Utility class providing utility methods for iterables such as collections and
 * arrays. The factory methods rely on type inference to determine the type
 * parameters needed for constructing a specific instance.
 * 
 * @author Lubomir Bulej
 */
public final class Iterables {

	/**
	 * Prevents creating instances of this class.
	 */
	private Iterables() {
		// pure static class - not to be instantiated
	}

	/* ***********************************************************************
	 * PUBLIC STATIC FIELDS
	 * **********************************************************************
	 */

	static abstract class ImmutableIterator<T> implements Iterator<T> {
		@Override
		public final void remove() {
			throw new UnsupportedOperationException();
		}
	}

	public static final Iterator<?> EMPTY_ITERATOR = new ImmutableIterator() {
		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public Object next() {
			throw new NoSuchElementException();
		}
	};

	/* ***********************************************************************
	 * PUBLIC INTERFACEs
	 * **********************************************************************
	 */

	/**
	 * Implementing this interface allows an object to be used as filter
	 * callback in filtering iterator.
	 */
	public interface Filter<E> {
		/**
		 * Determines whether the given element should be
		 * accepted by the calling filter.
		 * 
		 * @param element
		 *            element to check, may be {@code null}
		 * 
		 * @return
		 *         {@code true} if the element should be accepted, {@code false} otherwise
		 */
		boolean accept(E element);
	}

	/**
	 * Implementing this interface allows an object to be used as an
	 * lambda function applied to items in an iterable.
	 */
	public interface Lambda<E> {
		/**
		 * Applies the function to the given element.
		 * 
		 * @param element
		 *            element to apply the function on, may be {@code null}
		 */
		void apply(E element);
	}

	/* ***********************************************************************
	 * PUBLIC METHODS
	 * **********************************************************************
	 */

	/**
	 * Creates an iterable from the given array.
	 * 
	 * @param <E>
	 *            element type
	 * @param array
	 *            the array to create iterable for
	 * @return
	 *         {@link Iterable} encapsulating the given array.
	 */
	public static <E> Iterable<E> iterable(final E[] array) {
		return new Iterable<E>() {
			@Override
			public Iterator<E> iterator() {
				return new ArrayIterator<E>(array);
			}
		};
	}

	/**
	 * Creates a random iterable from the given array. The iterable will
	 * provide iterators that will return elements of the array in random
	 * order, without repeating.
	 * 
	 * @param <E>
	 *            element type
	 * @param array
	 *            the array to create iterable for
	 * @return
	 *         {@link Iterable} encapsulating the given array in random fashion.
	 */
	public static <E> Iterable<E> randomIterable(final E[] array) {
		return Iterables.__randomIterable(Iterables.__randomAccessible(array));
	}

	/**
	 * Creates an infinite sampling iterable from the given array. The iterable
	 * will provide iterators that will return random elements from the array,
	 * indefinitely.
	 * 
	 * @param <E>
	 *            element type
	 * @param array
	 *            the array to create iterable for
	 * @return
	 *         Sampling {@link Iterable} encapsulating the given array.
	 */
	public static <E> Iterable<E> samplingIterable(final E[] array) {
		return Iterables.__samplingIterable(Iterables.__randomAccessible(array));
	}

	/**
	 * Creates a random iterable from the given collection. The iterable will
	 * provide iterators that will return elements of the collection in random
	 * order, without repeating.
	 * 
	 * @param <E>
	 *            element type
	 * @param list
	 *            the collection to create the iterable for
	 * @return
	 *         Random {@link Iterable} encapsulating the given collection.
	 */
	public static <E> Iterable<E> randomIterable(final Collection<E> collection) {
		return Iterables.__randomIterable(Iterables.__randomAccessible(Lists.newArrayList(collection)));
	}

	/**
	 * Creates an infinite sampling iterable from the given collection. The
	 * iterable will provide iterators that will return random elements from
	 * the collection, indefinitely.
	 * 
	 * @param <E>
	 *            element type
	 * @param list
	 *            the collection to create the iterable for
	 * @return
	 *         Sampling {@link Iterable} encapsulating the given collection.
	 */
	public static <E> Iterable<E> samplingIterable(final Collection<E> collection) {
		return Iterables.__samplingIterable(Iterables.__randomAccessible(Lists.newArrayList(collection)));
	}

	/**
	 * Creates an iterator for the given array.
	 * 
	 * @param <E>
	 *            element type
	 * @param array
	 *            the source array to create the iterator for
	 * @return
	 *         {@link Iterator} encapsulating the given array
	 */
	public static <E> Iterator<E> iterator(final E[] array) {
		return new ArrayIterator<E>(array);
	}

	/**
	 * Creates a random iterator for the given array. The iterator will
	 * return elements of the array in random order, without repeating.
	 * 
	 * @param <E>
	 *            element type
	 * @param array
	 *            the source array to create the iterator for
	 * @return
	 *         {@link Iterator} encapsulating the given array.
	 */
	public static <E> Iterator<E> randomIterator(final E[] array) {
		return new RandomIterator<E>(Iterables.__randomAccessible(array));
	}

	/**
	 * Creates an infinite sampling iterator for the given array. The iterator
	 * will return random elements from the array, indefinitely.
	 * 
	 * @param <E>
	 *            element type
	 * @param array
	 *            the source array to create the iterator for
	 * @return
	 *         Sampling {@link Iterator} encapsulating the given array.
	 */
	public static <E> Iterator<E> samplingIterator(final E[] array) {
		return new RandomSampler<E>(Iterables.__randomAccessible(array));
	}

	/**
	 * Creates a random iterator for the given collection. The iterator will
	 * return elements of the collection in random order, without repeating.
	 * 
	 * @param <E>
	 *            element type
	 * @param array
	 *            the source collection to create the iterator for
	 * @return
	 *         {@link Iterator} encapsulating the given collection.
	 */
	public static <E> Iterator<E> randomIterator(final Collection<E> collection) {
		return new RandomIterator<E>(Iterables.__randomAccessible(Lists.newArrayList(collection)));
	}

	/**
	 * Creates an infinite sampling iterator for the given collection. The
	 * iterator will return random elements from the collection, indefinitely.
	 * 
	 * @param <E>
	 *            element type
	 * @param array
	 *            the source collection to create the iterator for
	 * @return
	 *         Sampling {@link Iterator} encapsulating the given collection.
	 */
	public static <E> Iterator<E> samplingIterator(final Collection<E> collection) {
		return new RandomSampler<E>(Iterables.__randomAccessible(Lists.newArrayList(collection)));
	}

	/**
	 * Creates a random iterator for the given iterable. The iterator will
	 * return elements of the iterable in random order, without repeating.
	 * 
	 * @param <E>
	 *            element type
	 * @param array
	 *            the source iterable to create the iterator for
	 * @return
	 *         {@link Iterator} encapsulating the given iterable.
	 */
	public static <E> Iterator<E> randomIterator(final Iterable<E> iterable) {
		return new RandomIterator<E>(Iterables.__randomAccessible(Lists.newArrayList(iterable)));
	}

	/**
	 * Creates an infinite sampling iterator for the given iterable. The
	 * iterator will return random elements from the iterable, indefinitely.
	 * 
	 * @param <E>
	 *            element type
	 * @param array
	 *            the source iterable to create the iterator for
	 * @return
	 *         {@link Iterator} encapsulating the given iterable.
	 */
	public static <E> Iterator<E> samplingIterator(final Iterable<E> iterable) {
		return new RandomSampler<E>(Iterables.__randomAccessible(Lists.newArrayList(iterable)));
	}

	/**
	 * Returns an empty iterator. This is an equivalent of an empty array or
	 * an empty collection.
	 * 
	 * @param <E>
	 *            element type
	 * @return Empty iterator.
	 */
	public static <E> Iterator<E> emptyIterator() {
		return (Iterator<E>) EMPTY_ITERATOR;
	}

	/**
	 * Creates a filtering iterable from the given source array
	 * and filter. The filtering iterable will only return elements
	 * that are accepted by the filter.
	 * 
	 * @param <E>
	 *            element type
	 * @param array
	 *            the source array to filter
	 * @param filter
	 *            object encapsulating the filtering method
	 * @return
	 *         filtering {@link Iterable} returning elements from the source array
	 */
	public static <E> Iterable<E> filter(
			final E[] array, final Filter<E> filter
			) {
		return new Iterable<E>() {
			@Override
			public Iterator<E> iterator() {
				return new FilteringIterator<E>(new ArrayIterator<E>(array), filter);
			}
		};
	}

	/**
	 * Creates a filtering iterable from the given source iterable
	 * and filter. The filtering iterable will only return elements
	 * that are accepted by the filter.
	 * 
	 * @param <E>
	 *            element type
	 * @param array
	 *            the source iterable to filter
	 * @param filter
	 *            object encapsulating the filtering method
	 * @return
	 *         filtering {@link Iterable} returning elements from the source iterable
	 */
	public static <E> Iterable<E> filter(
			final Iterable<E> iterable, final Filter<E> filter
			) {
		return new Iterable<E>() {
			@Override
			public Iterator<E> iterator() {
				return new FilteringIterator<E>(iterable.iterator(), filter);
			}
		};
	}

	/**
	 * Applies a lambda function encapsulated in an object implementing the {@link Lambda} interface on each element of the given array.
	 * 
	 * @param <E>
	 *            element type
	 * @param array
	 *            array of elements to apply the lambda on
	 * @param lambda
	 *            object encapsulating the lambda method
	 * 
	 * @see Lambda
	 */
	public static <E> void apply(
			final E[] array, final Lambda<E> lambda
			) {
		for (final E element : array) {
			lambda.apply(element);
		}
	}

	/**
	 * Applies a lambda function encapsulated in an object implementing the {@link Lambda} interface on each element of the given iterable.
	 * 
	 * @param <E>
	 *            element type
	 * @param array
	 *            iterable containing elements to apply the lambda on
	 * @param lambda
	 *            object encapsulating the lambda method
	 * 
	 * @see Lambda
	 */
	public static <E> void apply(
			final Iterable<E> iterable, final Lambda<E> lambda
			) {
		for (final E element : iterable) {
			lambda.apply(element);
		}
	}

	/* ***********************************************************************
	 * PRIVATE STATIC CLASS: FilteringIterator
	 * **********************************************************************
	 */

	private static final class FilteringIterator<E> implements Iterator<E> {
		private final Iterator<E> __source;
		private final Filter<E> __filter;
		private E __next;

		private FilteringIterator(
				final Iterator<E> source, final Filter<E> filter) {
			this.__filter = filter;
			this.__source = source;
		}

		/*
		 * @see Iterator#remove()
		 */
		@Override
		public final void remove() {
			throw new UnsupportedOperationException(
					"element removal not supported in iterable filter");
		}

		/*
		 * @see Iterator#hasNext()
		 */
		@Override
		public final boolean hasNext() {
			final Iterator<E> source = this.__source;
			final Filter<E> filter = this.__filter;

			while ((this.__next == null) && (source.hasNext())) {
				final E element = source.next();

				if (filter.accept(element)) {
					this.__next = element;
					return true;
				}
			}

			return (this.__next != null);
		}

		/*
		 * @see Iterator#next()
		 */
		@Override
		public final E next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}

			final E result = this.__next;
			this.__next = null;

			return result;
		}
	}

	/* *******************************************************************
	 * PRIVATE STATIC CLASS: ArrayIterator
	 * ******************************************************************
	 */

	private static final class ArrayIterator<E> implements Iterator<E> {
		private final E[] __array;
		private final int __length;
		private int __position;

		private ArrayIterator(final E[] array) {
			this.__array = array;
			this.__length = array.length;
			this.__position = 0;
		}

		/*
		 * @see Iterator#remove()
		 */
		@Override
		public void remove() {
			throw new UnsupportedOperationException(
					"cannot remove elements from array");
		}

		/*
		 * @see Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.__position < this.__length;
		}

		/*
		 * @see Iterator#next()
		 */
		@Override
		public E next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}

			return this.__array[this.__position++];
		}
	}

	/* *******************************************************************
	 * PRIVATE STATIC CLASS: RandomAccessible
	 * ******************************************************************
	 */

	private interface RandomAccessible<E> {
		public abstract E get(int index);

		public abstract void set(int index, E element);

		public abstract int size();
	}

	//

	public static <E> RandomAccessible<E> __randomAccessible(final E[] array) {
		return new RandomAccessible<E>() {
			@Override
			public E get(final int index) {
				return array[index];
			}

			@Override
			public void set(final int index, final E element) {
				array[index] = element;
			}

			@Override
			public int size() {
				return array.length;
			}
		};
	}

	//

	public static <E, T extends List<E>, RandomAccess>
			RandomAccessible<E> __randomAccessible(final T list) {
		return new RandomAccessible<E>() {
			@Override
			public E get(final int index) {
				return list.get(index);
			}

			@Override
			public void set(final int index, final E element) {
				list.set(index, element);
			}

			@Override
			public int size() {
				return list.size();
			}
		};
	}

	/* *******************************************************************
	 * PRIVATE STATIC CLASS: RandomSampler
	 * ******************************************************************
	 */

	private static <E> Iterable<E> __samplingIterable(
			final RandomAccessible<E> elements
			) {
		return new Iterable<E>() {
			@Override
			public Iterator<E> iterator() {
				return new RandomSampler<E>(elements);
			}
		};
	}

	private static class RandomSampler<E> extends ImmutableIterator<E> {

		protected final Random _random;
		protected final RandomAccessible<E> _elements;
		protected final int _count;

		//

		private RandomSampler(final RandomAccessible<E> elements, final Random random) {
			this._elements = elements;
			this._count = elements.size();
			this._random = random;
		}

		private RandomSampler(final RandomAccessible<E> elements) {
			this(elements, new Random());
		}

		private RandomSampler(final RandomAccessible<E> elements, final long seed) {
			this(elements, new Random(seed));
		}

		//

		/*
		 * @see Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return true;
		}

		/*
		 * @see Iterator#next()
		 */
		@Override
		public E next() {
			return this._elements.get(this._random.nextInt(this._count));
		}

	}

	/* *******************************************************************
	 * PRIVATE STATIC CLASS: RandomIterator
	 * ******************************************************************
	 */

	private static <E> Iterable<E> __randomIterable(
			final RandomAccessible<E> elements
			) {
		return new Iterable<E>() {
			@Override
			public Iterator<E> iterator() {
				return new RandomIterator<E>(elements);
			}
		};
	}

	private static final class RandomIterator<E> extends RandomSampler<E> {

		private int __lower;
		private int __upper;
		private final BitSet __used;

		//

		private RandomIterator(final RandomAccessible<E> elements, final Random random) {
			super(elements, random);

			this.__lower = 0;
			this.__upper = elements.size();
			this.__used = new BitSet(elements.size());
		}

		private RandomIterator(final RandomAccessible<E> elements) {
			this(elements, new Random());
		}

		private RandomIterator(final RandomAccessible<E> elements, final long seed) {
			this(elements, new Random(seed));
		}

		/*
		 * @see Iterator#hasNext()
		 */
		@Override
		public boolean hasNext() {
			return this.__lower < this.__upper;
		}

		/*
		 * @see Iterator#next()
		 */
		@Override
		public E next() {
			if (!this.hasNext()) {
				throw new NoSuchElementException();
			}

			// TODO Make this more work-conserving - this gets ineffective. --LB
			final int range = this.__upper - this.__lower;
			while (true) {
				final int index = this.__lower + this._random.nextInt(range);
				if (!this.__used.get(index)) {
					this.__used.set(index);
					this.__updateLowerBoundary(index);
					this.__updateUpperBoundary(index);
					return this._elements.get(index);
				}
			}
		}

		private void __updateLowerBoundary(final int index) {
			if (this.__lower == index) {
				this.__lower = this.__used.nextClearBit(this.__lower);
				if (this.__lower < 0) {
					this.__lower = this.__upper;
				}
			}
		}

		private void __updateUpperBoundary(final int index) {
			if ((index + 1) == this.__upper) {
				while ((this.__lower < this.__upper) && this.__used.get(this.__upper - 1)) {
					this.__upper--;
				}
				;
			}
		}

	}

}
