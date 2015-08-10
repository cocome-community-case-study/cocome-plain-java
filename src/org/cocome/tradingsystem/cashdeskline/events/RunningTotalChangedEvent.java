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

/**
 * Event emitted by the cash desk after an item has been added to the current
 * sale in response to a barcode scan. It contains information about the current
 * item, its price and the running total.
 */
public final class RunningTotalChangedEvent implements Serializable {

	private static final long serialVersionUID = -300914931510566066L;

	//

	private final String __productName;

	private final double __productPrice;

	private final double __runningTotal;

	//

	public RunningTotalChangedEvent(
			final String productName, final double productPrice,
			final double runningTotal) {
		__productName = productName;
		__productPrice = productPrice;
		__runningTotal = runningTotal;
	}

	public String getProductName() {
		return __productName;
	}

	public double getProductPrice() {
		return __productPrice;
	}

	public double getRunningTotal() {
		return __runningTotal;
	}

	//

	@Override
	public String toString() {
		return String.format(
				"RunningTotalChangedEvent(%s, %s, %s)",
				__productName, __productPrice, __runningTotal
				);
	}

}
