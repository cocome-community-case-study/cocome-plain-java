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

/**
 * Enumerates the states the {@link CashDeskEventHandler} can be in.
 * 
 * @author Yannick Welsch
 */
enum CashDeskState {

	/** Initial state. */
	EXPECTING_SALE,

	/** After a sale has started ("New Sale" button pushed). */
	EXPECTING_ITEMS,

	/**
	 * After a sale has finished (all products have been scanned) and
	 * "Finish Sale" button pushed.
	 */
	EXPECTING_PAYMENT,

	/** After the choice of cash payment was made. */
	PAYING_BY_CASH,

	/** After the cash payment. */
	PAID_BY_CASH,

	/** After the choice of credit card payment was made. */
	EXPECTING_CARD_INFO,

	/** After the credit card was scanned. */
	PAYING_BY_CREDIT_CARD;

}
