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

package org.cocome.tradingsystem.inventory.data.generator;

import java.util.Random;

/**
 * Helper class for generating various random numbers and ranges.
 * 
 * @author Lubomir Bulej
 */
final class RandomHelper {

	private final Random __random;

	//

	private RandomHelper(final Random random) {
		__random = random;
	}

	public RandomHelper() {
		this(new Random());
	}

	public RandomHelper(final long seed) {
		this(new Random(seed));
	}

	//

	public int uniformInt(final int range) {
		return __random.nextInt(range);
	}

	public double uniformDouble(final double range) {
		return __random.nextDouble() * range;
	}

	//

	public int uniformIntRange(final int min, final int max) {
		return Math.min(min, max) + uniformInt(Math.abs(max - min));
	}

	public double uniformDoubleRange(final double min, final double max) {
		return Math.min(min, max) + uniformDouble(Math.abs(max - min));
	}

	//

	public int gaussianInt(final double mean, final double sd) {
		return (int) Math.round(mean + sd * __random.nextGaussian());
	}

	public double gaussianDouble(final double mean, final double sd) {
		return mean + sd * __random.nextGaussian();
	}

	//

	public int gaussianIntRange(final int min, final int max) {
		final double range = Math.abs(max - min);
		final double base = Math.min(min, max);

		return gaussianInt(base + range / 2, __sigma(range));
	}

	public double gaussianDoubleRange(final double min, final double max) {
		final double range = Math.abs(max - min);
		final double base = Math.min(min, max);

		return gaussianDouble(base + range / 2, __sigma(range));
	}

	private double __sigma(final double range) {
		//
		// Consider half of the range to be 3-sigma, which will cause
		// little percentage of values to fall slightly outside the range.
		//
		return range / (2 * 3);
	}

}
