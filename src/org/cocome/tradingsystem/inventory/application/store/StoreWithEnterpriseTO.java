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

package org.cocome.tradingsystem.inventory.application.store;

/**
 * Transfer object class for exchanging basic store information and additional
 * enterprise information between client and the service-oriented application
 * layer. It contains either copies of persisted data which is transferred to
 * the client, or data which is transferred from the client to the application
 * layer to be processed and persisted.
 * 
 * @author Sebastian Herold
 * @author Lubomir Bulej
 */
public final class StoreWithEnterpriseTO extends StoreTO {

	private static final long serialVersionUID = 5339232518833582866L;

	//

	private EnterpriseTO __enterpriseTO;

	/**
	 * Gets the enterprise the store belongs to.
	 * 
	 * @return Enterprise TO this store belongs to.
	 */
	public EnterpriseTO getEnterpriseTO() {
		return __enterpriseTO;
	}

	/**
	 * Sets the enterprise the store belongs to.
	 * 
	 * @param enterpriseTO
	 */
	public void setEnterpriseTO(final EnterpriseTO enterpriseTO) {
		__enterpriseTO = enterpriseTO;
	}

}
