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
package org.cocome.tradingsystem.cashdeskline.cashdesk;

import java.util.Set;

/**
 * This exception is thrown by the cash desk when its asked to perform an
 * operation that is illegal in its current state.
 * 
 * @author Lubomir Bulej
 * @author Reiner Jung
 */
/**
 * @author rju
 * 
 */
final class IllegalCashDeskStateException extends IllegalStateException {

	/** serial id */
	private static final long serialVersionUID = -8928408995389286143L;

	/** state of the cash desk */
	private final CashDeskState state;
	/** set of legal states */
	private final Set<CashDeskState> legalStates;

	/**
	 * Create state exception.
	 * 
	 * @param state
	 *            observed state
	 * @param legalStates
	 *            expected states
	 */
	public IllegalCashDeskStateException(final CashDeskState state,
			final Set<CashDeskState> legalStates) {
		this.state = state;
		this.legalStates = legalStates;
	}

	public CashDeskState getState() {
		return this.state;
	}

	public Set<CashDeskState> getLegalStates() {
		return this.legalStates;
	}

}
