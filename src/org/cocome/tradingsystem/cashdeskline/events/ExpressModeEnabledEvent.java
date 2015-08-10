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
 * Event emitted by the cash desk line coordinator on the store topic to switch
 * a particular cash desk into express mode. If a cash desk changes its mode,
 * the event is reposted by the cash desk on the cash desk topic.
 * 
 * @author Sebastian Herold
 * @author Lubomir Bulej
 */
public final class ExpressModeEnabledEvent implements Serializable {

	private static final long serialVersionUID = -6894844300001277997L;

	//

	private final String __cashDesk;

	//

	public ExpressModeEnabledEvent(final String cashdesk) {
		__cashDesk = cashdesk;
	}

	/**
	 * Returns the name of the cash desk to be switched into express mode.
	 * 
	 * @return
	 *         cash desk (channel) name
	 */
	public String getCashDesk() {
		return __cashDesk;
	}

}
