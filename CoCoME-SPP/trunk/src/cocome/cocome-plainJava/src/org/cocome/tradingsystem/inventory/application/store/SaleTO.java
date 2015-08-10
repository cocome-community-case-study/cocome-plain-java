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

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * A transfer object class for exchanging sale information between client and
 * the service-oriented application layer. It has no persistent counterpart.
 * 
 * @author Sebastian Herold
 * @author Lubomir Bulej
 */
public final class SaleTO implements Serializable {

	private static final long serialVersionUID = 5164217707863683479L;

	//

	private Date __date;

	private List<ProductWithStockItemTO> __productTOs;

	//

	/**
	 * Gets date of sale.
	 * 
	 * @return
	 *         Date of sale.
	 */
	public Date getDate() {
		return __date;
	}

	/**
	 * Sets date of sale.
	 * 
	 * @param date
	 *            Date to be set.
	 */
	public void setDate(final Date date) {
		__date = date;
	}

	/**
	 * Gets list of products and corresponding item in stock which the sale
	 * consists of.
	 * 
	 * @return
	 *         List of contained products.
	 */
	public List<ProductWithStockItemTO> getProductTOs() {
		return __productTOs;
	}

	/**
	 * Sets list of products for the sale.
	 * 
	 * @param productTOs
	 *            List of products the sale should contain.
	 */
	public void setProductTOs(final List<ProductWithStockItemTO> productTOs) {
		__productTOs = productTOs;
	}

}
