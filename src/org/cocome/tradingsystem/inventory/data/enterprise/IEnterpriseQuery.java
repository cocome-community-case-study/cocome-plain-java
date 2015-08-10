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

package org.cocome.tradingsystem.inventory.data.enterprise;

import javax.persistence.EntityNotFoundException;

import org.cocome.tradingsystem.inventory.application.reporting.IReporting;
import org.cocome.tradingsystem.inventory.data.persistence.IPersistenceContext;

/**
 * This interface provides methods for querying the database. It is used by the
 * inventory application. The methods are derived from methods defined in the {@link IReporting} interface.
 * 
 * @author Yannick Welsch
 */
public interface IEnterpriseQuery {

	/**
	 * @param enterpriseId
	 *            the unique identifier of a TradingEnterprise entity
	 * @param pctx
	 *            the persistence context
	 * @return A TradingEnterprise object with the specified id.
	 * 
	 * @throws EntityNotFoundException
	 *             if a trading enterprise with the given id could not be found
	 */
	TradingEnterprise queryEnterpriseById(
			long enterpriseId, IPersistenceContext pctx);

	/**
	 * @param supplier
	 *            The supplier which delivers the products
	 * @param enterprise
	 *            The enterprise for which the products are delivered
	 * @param pctx
	 *            The persistence context
	 * @return The mean time to delivery in milliseconds
	 */
	long getMeanTimeToDelivery(ProductSupplier supplier,
			TradingEnterprise enterprise, IPersistenceContext pctx);

}
