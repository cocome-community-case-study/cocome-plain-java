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

import static java.util.concurrent.TimeUnit.MILLISECONDS;

import java.util.Queue;

import org.cocome.tradingsystem.util.java.Lists;
import org.cocome.tradingsystem.util.java.TimeSpan;

/**
 * Manages history of cash desk sales and evaluates the cash desk eligibility
 * for express mode. uses {@link ExpressModePolicy} to decide what sale record
 * to retain and to distinguish between normal and express sales.
 * 
 * @author Lubomir Bulej
 */
final class ExpressModeEvaluator {

	private final ExpressModePolicy __policy = ExpressModePolicy.getInstance();

	private final Queue<Sale> __salesHistory = Lists.newLinkedList();

	private long __lastEvaluated;

	//

	private boolean __isExpressModeNeeded;

	//

	public void updateSalesHistory(final Sale sale) {
		__salesHistory.add(sale);
		__trimSalesHistory(__salesHistory, __policy.getEvaluationWindow());
	}

	private void __trimSalesHistory(
			final Queue<Sale> sales, final TimeSpan timeSpan
			) {
		//
		// Scan the contents of the queue and throw out sale records that are
		// not relevant any more (too old). Since the sale records are naturally
		// ordered, we can stop scanning after encountering the first relevant
		// record.
		//
		final long now = System.currentTimeMillis();
		TRIM: while (!sales.isEmpty()) {
			final Sale sale = sales.peek();
			if (__saleIsRelevant(sale, now, timeSpan)) {
				break TRIM;
			}

			sales.remove();
		}
	}

	private boolean __saleIsRelevant(
			final Sale sale, final long now, final TimeSpan timeSpan
			) {
		final long saleAgeInMillis = __difference(now, sale.getWhen().getTime());
		return saleAgeInMillis <= timeSpan.getDuration(MILLISECONDS);
	}

	/**
	 * Checks the condition for UC 2:ManageExpressCheckout:
	 * 
	 * 50% of all sales during the last 60 minutes meet the requirements of an
	 * express checkout (up to 8 products per sale and customer pays cash).
	 */
	public boolean isExpressModeNeeded() {
		final long now = System.currentTimeMillis();
		if (__updateIsNeeded(now)) {
			__trimSalesHistory(__salesHistory, __policy.getEvaluationWindow());
			__isExpressModeNeeded = __isExpressModeNeeded(__salesHistory, __policy);
			__lastEvaluated = now;
		}

		return __isExpressModeNeeded;
	}

	private boolean __updateIsNeeded(final long now) {
		final long millisSinceUpdate = __difference(now, __lastEvaluated);
		return millisSinceUpdate >= __policy.getEvaluationPeriod().getDuration(MILLISECONDS);
	}

	private long __difference(final long now, final long then) {
		return Math.abs(now - then);
	}

	private boolean __isExpressModeNeeded(
			final Queue<Sale> sales, final ExpressModePolicy policy
			) {
		//
		// Determine the ratio of express sales. Express mode is needed if the
		// ratio reaches or exceeds a threshold defined by the given policy.
		//
		final int expressCount = __countExpressSales(sales, policy);
		final int salesCount = sales.size();
		final double expressRatio = (double) expressCount / salesCount;

		return Double.compare(expressRatio, policy.getExpressSalesThreshold()) >= 0;
	}

	private int __countExpressSales(
			final Queue<Sale> sales, final ExpressModePolicy policy
			) {
		int result = 0;
		for (final Sale sale : sales) {
			result += policy.isExpressSale(sale) ? 1 : 0;
		}

		return result;
	}

}
