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
 * A transfer object class for transferring basic product and additional
 * supplier information between client and the service-oriented application
 * layer. It contains either copies of persisted data which are transferred to
 * the client, or data which is transferred from the client to the application
 * layer to be processed and persisted.
 * 
 * @author Sebastian Herold
 * @author Lubomir Bulej
 */
public class ProductWithSupplierTO extends ProductTO {

	private static final long serialVersionUID = 5315366349773650L;

	//

	private SupplierTO __supplierTO;

	/**
	 * Gets transfer object for supplier which offers this product.
	 * 
	 * @return
	 *         Transfer object for supplier.
	 */
	public SupplierTO getSupplierTO() {
		return __supplierTO;
	}

	/**
	 * Sets transfer object for supplier.
	 * 
	 * @param supplierTO
	 *            new supplier transfer object
	 */
	public void setSupplierTO(final SupplierTO supplierTO) {
		__supplierTO = supplierTO;
	}

}
