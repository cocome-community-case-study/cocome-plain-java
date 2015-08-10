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

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import javax.persistence.EntityNotFoundException;

/**
 * Remote interface to the management facet of the store inventory application.
 * The store console uses this interface to obtain information about the store
 * and its stock as well as to change product sales prices, while the product
 * dispatcher uses it to manage movement of products among stores.
 * <p>
 * TODO Consider splitting the interface so that both the store console and product dispatcher have their own. --LB
 * 
 * @author Sebastian Herold
 * @author Lubomir Bulej
 */
public interface IStoreInventoryManager extends Remote {

	/**
	 * Returns information on the store in which the component is running. This
	 * information is retrieved by the component during configuration and
	 * initialization.
	 * 
	 * @return Store and enterprise information about the local store.
	 */
	StoreWithEnterpriseTO getStore() throws RemoteException;

	/**
	 * Determines products and stock items which are nearly out of stock,
	 * meaning amount is lower than 10% of maximal stock. Used for realization
	 * of UC 3.
	 * 
	 * @return List of products and their stock item in the given store.
	 */
	List<ProductWithStockItemTO> getProductsWithLowStock()
			throws RemoteException;

	/**
	 * Returns all products offered by a given store (a stock item exists for a
	 * product) and the supplier for each of them. Used for realization of UC 3.
	 * <p>
	 * TODO Consider returning all enterprise products, so that the manager can also new products sold by other stores within the enterprise. -- LB
	 * 
	 * @return List of products and their suppliers
	 */
	List<ProductWithSupplierTO> getAllProducts() throws RemoteException;

	/**
	 * Returns all products offered by this store (a stock item exists for a
	 * product) and the supplier and corresponding stock item for each of them.
	 * 
	 * @return
	 *         List of products, their suppliers and the corresponding stock item.
	 */
	List<ProductWithSupplierAndStockItemTO> getProductsWithStockItems()
			throws RemoteException;

	/**
	 * Orders products for a store. A separate product order is created for
	 * each supplier, with the ordering date set to the date of method
	 * execution.
	 * <p>
	 * Used for realization of UC 3.
	 * 
	 * @param complexOrder
	 *            order for a store which contains all products to be ordered
	 * @return
	 *         List of product orders, one for each supplier that is affected.
	 */
	List<ComplexOrderTO> orderProducts(ComplexOrderTO complexOrder)
			throws RemoteException;

	/**
	 * Returns order information for a given order id. Used for realization of
	 * UC 4.
	 * 
	 * @param orderId
	 *            the order entity identifier
	 * @return Detailed order information of the desired order.
	 * @throws EntityNotFoundException
	 *             if there is no order with the given id
	 */
	ComplexOrderTO getOrder(long orderId) throws RemoteException;

	/**
	 * Returns all outstanding product orders in the store.
	 * 
	 * @return
	 *         List ofDetailed order information of the desired order. NULL, if there
	 *         is no order with the given id.
	 */
	List<ComplexOrderTO> getOutstandingOrders() throws RemoteException;

	/**
	 * Updates stocks after order delivery. Adds amount of ordered items to the
	 * stock items of the store. Sets delivery date to date of method execution.
	 * Used for realization of UC 4.
	 * 
	 * @param orderId
	 *            the entity identifier of the order to roll in
	 * @throws InvalidRollInRequestException
	 */
	void rollInReceivedOrder(long orderId)
			throws InvalidRollInRequestException, RemoteException;

	/**
	 * Updates sales price of a stock item. Used for realization of UC 7.
	 * 
	 * @param stockItemTO
	 *            Stock item with new price.
	 * @return
	 *         Instance of ProductWithStockItemTO which holds product
	 *         information and updated price information for stock item
	 *         identified by <code>stockItemTO</code>.
	 */
	ProductWithStockItemTO changePrice(StockItemTO stockItemTO)
			throws RemoteException;

	/**
	 * Initiates the delivery of products that ran out at another store. The
	 * products to be moved are marked as unavailable at this store afterwards.
	 * <p>
	 * Method required for UC 8 (product exchange (on low stock) among stores).
	 * <p>
	 * TODO Consider returning information on products that could be moved instead aborting when one product is out of stock. -- LB
	 * 
	 * @param movedProductAmounts
	 *            products and their amounts to be moved to another store
	 * 
	 * @throws ProductNotAvailableException
	 *             if the local stock of any of the required products is less than
	 *             the required amount
	 * 
	 * @author SDQ
	 */
	void markProductsUnavailableInStock(
			ProductMovementTO movedProductAmounts
			) throws RemoteException, ProductNotAvailableException;

	/**
	 * Returns the stock for the given products.
	 * <p>
	 * Required for UC 8
	 * <p>
	 * <b>NOTE:</b> This method is currently unused (and unimplemented), because there is a single database for the entire enterprise. The product dispatcher
	 * therefore does not have to ask individual stores for products but can just look into the shared database.
	 * 
	 * @param requiredProductTOs
	 *            The products to look up in the stock
	 * @return The products including amounts from the stock
	 * @author SDQ
	 */
	ComplexOrderEntryTO[] getStockItems(ProductTO[] requiredProductTOs)
			throws RemoteException;

}
