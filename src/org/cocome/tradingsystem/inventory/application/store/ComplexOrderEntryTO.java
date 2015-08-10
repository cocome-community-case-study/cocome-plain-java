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
 * A transfer object class for partial order information between client and the
 * service-oriented application layer. It contains either copies of persisted
 * data which is transferred to the client, or data which is transferred from
 * the client to the application layer to be processed and persisted.
 * 
 * @author Sebastian Herold
 * @author Lubomir Bulej
 */
public final class ComplexOrderEntryTO extends OrderEntryTO {

	private static final long serialVersionUID = 6508124170856718383L;

	//

	private ProductWithSupplierTO __productTO;

	//

	/**
	 * Gets transfer object for product information from an order entry.
	 * 
	 * @return Product transfer object with supplier information.
	 */
	public ProductWithSupplierTO getProductTO() {
		return __productTO;
	}

	/**
	 * Sets product transfer object with supplier information for order entry.
	 * 
	 * @param product
	 *            Product transfer object to be set.
	 */
	public void setProductTO(final ProductWithSupplierTO product) {
		__productTO = product;
	}

}
