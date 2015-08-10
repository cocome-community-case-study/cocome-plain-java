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

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Represents a time span, i.e. certain amount of various time units.
 * 
 * @author Lubomir Bulej
 */
public final class TimeSpan implements Comparable<TimeSpan> {

	private final long __value;

	private final TimeUnit __unit;

	//

	public TimeSpan(final long value, final TimeUnit unit) {
		__value = value;
		__unit = unit;
	}

	public TimeSpan(final Date lower, final Date upper) {
		this(__differenceInMillis(lower, upper), MILLISECONDS);
	}

	public TimeSpan(final Date then) {
		this(__differenceInMillisSince(then), MILLISECONDS);
	}

	//

	public long getDuration(final TimeUnit unit) {
		return unit.convert(Math.abs(__value), __unit);
	}

	//

	@Override
	public boolean equals(final Object object) {
		if (object == this) {
			return true;
		}

		if (object instanceof TimeSpan) {
			final TimeSpan that = (TimeSpan) object;
			return this.compareTo(that) == 0;

		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return __longHashCode(__value) ^ __unit.hashCode();
	}

	private int __longHashCode(final long value) {
		return (int) (value ^ (value >>> 32));
	}

	//

	@Override
	public int compareTo(final TimeSpan that) {
		if (this.__unit.compareTo(that.__unit) <= 0) {
			return __compareTo(this.__unit, that);
		} else {
			return __compareTo(that.__unit, that);
		}
	}

	private int __compareTo(final TimeUnit base, final TimeSpan that) {
		final long thisDuration = base.convert(this.__value, this.__unit);
		final long thatDuration = base.convert(that.__value, that.__unit);

		return Long.signum(thisDuration - thatDuration);
	}

	//

	private static long __differenceInMillis(final Date lower, final Date upper) {
		return Math.abs(upper.getTime() - lower.getTime());
	}

	private static long __differenceInMillisSince(final Date then) {
		return Math.abs(System.currentTimeMillis() - then.getTime());
	}

}
