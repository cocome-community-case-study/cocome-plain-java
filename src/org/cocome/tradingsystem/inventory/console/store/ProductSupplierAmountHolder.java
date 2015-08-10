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

package org.cocome.tradingsystem.inventory.console.store;

import org.cocome.tradingsystem.inventory.application.store.ProductWithSupplierTO;

/**
 * A primitive holder for a {@link ProductWithSupplierTO} instance associated
 * with the amount of products to be ordered from the supplier.
 * <p>
 * <strong>This class is not to be used outside this package.</strong>
 * 
 * @author Lubomir Bulej
 */
final class ProductSupplierAmountHolder {

	final ProductWithSupplierTO pwsto;

	long amount;

	//

	ProductSupplierAmountHolder(final ProductWithSupplierTO psto) {
		this.pwsto = psto;
	}

}
