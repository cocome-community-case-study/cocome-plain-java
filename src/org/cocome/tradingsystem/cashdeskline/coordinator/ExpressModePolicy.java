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

package org.cocome.tradingsystem.cashdeskline.coordinator;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.cocome.tradingsystem.cashdeskline.datatypes.PaymentMode;
import org.cocome.tradingsystem.util.java.TimeSpan;

/**
 * Represents policy for deciding whether an express mode should be enabled for
 * a single cash desk. The policy determines the length of the sliding window in
 * which sales should be considered and the conditions that need to be satisfied
 * to switch a cash desk into express mode.
 * 
 * @author Lubomir Bulej
 */
public abstract class ExpressModePolicy {

	/**
	 * Maximal number of items eligible for express mode.
	 */
	private static final int __EXPRESS_ITEMS_LIMIT__ = 8;

	/**
	 * The period with which to evaluate the need for express mode.
	 */
	private static final TimeSpan __EVALUATION_PERIOD__ = new TimeSpan(60, SECONDS);

	/**
	 * The duration of the sliding window in which sales contribute to the
	 * decision to switch a cash desk into express mode.
	 */
	private static final TimeSpan __EXPRESS_SALES_WINDOW__ = new TimeSpan(1, HOURS);

	/**
	 * The ratio of express sales over total sales that needs to be reached
	 * to switch the cash desk into express mode.
	 */
	private static final double __EXPRESS_SALES_THRESHOLD__ = 0.5;

	/**
	 * Determines which exress mode policy to use.
	 */
	private static final boolean __USE_TESTING_POLICY__ = "testing".equalsIgnoreCase(
			System.getProperty("org.cocome.tradingsystem.cashdeskline.coordinator.ExpressModePolicy", ""
					));

	//

	public static ExpressModePolicy getInstance() {
		return __USE_TESTING_POLICY__ ? __testingPolicy__ : __defaultPolicy__;
	}

	//

	public abstract TimeSpan getEvaluationPeriod();

	abstract TimeSpan getEvaluationWindow();

	abstract double getExpressSalesThreshold();

	abstract boolean isExpressSale(Sale sale);

	public abstract int getExpressItemLimit();

	//

	private static final ExpressModePolicy __defaultPolicy__ = new ExpressModePolicy() {
		@Override
		TimeSpan getEvaluationWindow() {
			return __EXPRESS_SALES_WINDOW__;
		}

		@Override
		double getExpressSalesThreshold() {
			return __EXPRESS_SALES_THRESHOLD__;
		}

		@Override
		boolean isExpressSale(final Sale sale) {
			return sale.getPaymentMode() == PaymentMode.CASH
					&& sale.getItemCount() <= __EXPRESS_ITEMS_LIMIT__;
		}

		@Override
		public int getExpressItemLimit() {
			return __EXPRESS_ITEMS_LIMIT__;
		}

		@Override
		public TimeSpan getEvaluationPeriod() {
			return __EVALUATION_PERIOD__;
		}
	};

	private static final ExpressModePolicy __testingPolicy__ = new ExpressModePolicy() {
		private final TimeSpan __period = new TimeSpan(1, SECONDS);
		private final TimeSpan __window = new TimeSpan(5, SECONDS);

		@Override
		TimeSpan getEvaluationWindow() {
			return __window;
		}

		@Override
		double getExpressSalesThreshold() {
			return Double.MIN_NORMAL;
		}

		@Override
		boolean isExpressSale(final Sale sale) {
			return sale.getPaymentMode() == PaymentMode.CASH
					&& sale.getItemCount() <= __EXPRESS_ITEMS_LIMIT__;
		}

		@Override
		public int getExpressItemLimit() {
			return __EXPRESS_ITEMS_LIMIT__;
		}

		@Override
		public TimeSpan getEvaluationPeriod() {
			return __period;
		}
	};

}
