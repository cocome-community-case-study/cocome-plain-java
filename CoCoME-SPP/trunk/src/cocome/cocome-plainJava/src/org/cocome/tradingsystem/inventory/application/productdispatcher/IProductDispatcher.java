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

package org.cocome.tradingsystem.inventory.application.productdispatcher;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;

import org.cocome.tradingsystem.inventory.application.store.ProductAmountTO;

/**
 * This interface provides enterprise specific business logic (business logic
 * that is not available for stores).
 * <p>
 * TODO Rename to reflect the one-per-enterprise scope. --LB
 * 
 * @author kelsaka
 * @author Lubomir Bulej
 */
public interface IProductDispatcher extends Remote {

	/**
	 * Executes a query to search for products (that ran out of stock at one
	 * store) in other stores in the region and dispatch the products to
	 * the calling store. Called by the store application.
	 * <p>
	 * Required for UC 8 (low-stock product exchange among stores).
	 * <p>
	 * 
	 * @param callingStoreId
	 *            the store running out of stock
	 * @param requiredProducts
	 *            the products running out at the calling store and the required
	 *            amount of those products
	 * 
	 * @return
	 *         A list of the required products that will be made available by the
	 *         enterprise. The included amount of products might be "0" to indicate
	 *         that a product is not available in the enterprise.
	 *         <p>
	 *         Products that are indicated to be available (amount > 0) are prepared for delivery by the delivering store (markProductsUnavailableInStock).
	 */
	ProductAmountTO[] dispatchProductsFromOtherStores(
			long callingStoreId, Collection<ProductAmountTO> requiredProducts
			) throws RemoteException;

}
