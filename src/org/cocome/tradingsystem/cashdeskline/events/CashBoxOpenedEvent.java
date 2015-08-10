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

/**
 * Event emitted by the cash box after opening the cash box.
 * 
 * @see CashBoxModel
 * 
 * @author Lubomir Bulej
 */
public final class CashBoxOpenedEvent implements Serializable {

	private static final long serialVersionUID = -5029546123444960504L;

	//

	public CashBoxOpenedEvent() { /* empty */}

}
