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
import java.util.Collection;
import java.util.Collections;

/**
 * A transfer object class representing transported from one store to another.
 * Stores the origin and destination stores along with a list of product and
 * amounts being transported.
 * <p>
 * Required for UC 8.
 * 
 * @author kelsaka
 * @author Lubomir Bulej
 */
public final class ProductMovementTO implements Serializable {

	private static final long serialVersionUID = 6086062214137352097L;

	//

	private StoreTO __originStore;

	private StoreTO __destinationStore;

	private Collection<ProductAmountTO> __productAmounts;

	/**
	 * Initializes an empty product movement.
	 */
	public ProductMovementTO() {
		__productAmounts = Collections.emptyList();
	}

	/**
	 * Returns products that are subject of this product movement.
	 * 
	 * @return
	 *         A list of product transfer objects.
	 */
	public Collection<ProductAmountTO> getProductAmounts() {
		return __productAmounts;
	}

	/**
	 * Sets the products that are subject of this product movement.
	 * 
	 * @param productAmounts
	 *            list of products to be moved
	 */

	public void setProductAmounts(final Collection<ProductAmountTO> productAmounts) {
		__productAmounts = productAmounts;
	}

	/**
	 * Returns the store that delivers the products.
	 * 
	 * @return
	 *         The delivering store transfer object.
	 */
	public StoreTO getOriginStore() {
		return __originStore;
	}

	/**
	 * Sets the store that delivers the products.
	 * 
	 * @param originStore
	 *            the delivering store transfer object
	 */
	public void setOriginStore(final StoreTO originStore) {
		__originStore = originStore;
	}

	/**
	 * Returns the store that receives the products.
	 * 
	 * @return
	 *         The receiving store transfer object.
	 */
	public StoreTO getDestinationStore() {
		return __destinationStore;
	}

	/**
	 * Sets the store that receives the products.
	 * 
	 * @param destinationStore
	 *            the receiving store transfer object
	 */
	public void setDestinationStore(final StoreTO destinationStore) {
		__destinationStore = destinationStore;
	}

}
