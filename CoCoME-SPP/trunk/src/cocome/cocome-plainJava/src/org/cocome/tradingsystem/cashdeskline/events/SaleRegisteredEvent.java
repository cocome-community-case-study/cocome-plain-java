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

package org.cocome.tradingsystem.cashdeskline.events;

import java.io.Serializable;

import org.cocome.tradingsystem.cashdeskline.datatypes.PaymentMode;

/**
 * Event emitted by the cash desk on the store topic when a successful sale has
 * been registered in the inventory. It contains statistical information about
 * the sale (number of items, mode of payment) intended for the cash desk line
 * coordinator.
 */
public final class SaleRegisteredEvent implements Serializable {

	private static final long serialVersionUID = 6202472706841986582L;

	//

	private final int __itemCount;

	private final PaymentMode __paymentMode;

	private final String __cashDesk;

	//

	public SaleRegisteredEvent(
			final String cashDesk,
			final int saleItemCount, final PaymentMode paymentMode) {
		__itemCount = saleItemCount;
		__paymentMode = paymentMode;
		__cashDesk = cashDesk;
	}

	public int getItemCount() {
		return __itemCount;
	}

	public PaymentMode getPaymentMode() {
		return __paymentMode;
	}

	public String getCashDesk() {
		return __cashDesk;
	}

}
