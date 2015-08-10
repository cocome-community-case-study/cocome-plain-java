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

import java.util.Date;

import org.cocome.tradingsystem.cashdeskline.datatypes.PaymentMode;

/**
 * Helper class to keep track of the sales.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
final class Sale {

	private final Date __when;

	private final int __itemCount;

	private final PaymentMode __paymentMode;

	//

	protected Sale(
			final Date when,
			final int itemCount,
			final PaymentMode paymentMode) {
		__when = when;
		__itemCount = itemCount;
		__paymentMode = paymentMode;
	}

	//

	public Date getWhen() {
		return __when;
	}

	public int getItemCount() {
		return __itemCount;
	}

	public PaymentMode getPaymentMode() {
		return __paymentMode;
	}

}
