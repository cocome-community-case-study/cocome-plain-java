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

import org.cocome.tradingsystem.cashdeskline.cashdesk.cashbox.CashBoxModel;
import org.cocome.tradingsystem.cashdeskline.datatypes.PaymentMode;

/**
 * Event emitted by cash desk when a payment mode has been rejected.
 * 
 * @see CashBoxModel
 * 
 * @author Lubomir Bulej
 */
public final class PaymentModeRejectedEvent implements Serializable {

	private static final long serialVersionUID = -7349265927547423726L;

	//

	private final PaymentMode __mode;

	private final String __reason;

	//

	public PaymentModeRejectedEvent(final PaymentMode mode, final String reason) {
		__mode = mode;
		__reason = reason;
	}

	public PaymentMode getMode() {
		return __mode;
	}

	public String getReason() {
		return __reason;
	}

}
