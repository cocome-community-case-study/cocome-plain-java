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

package org.cocome.tradingsystem.inventory.data.store;

import java.util.Collection;

import javax.persistence.EntityNotFoundException;
import javax.persistence.NonUniqueResultException;

import org.cocome.tradingsystem.inventory.application.store.IStoreInventory;
import org.cocome.tradingsystem.inventory.application.store.IStoreInventoryManager;
import org.cocome.tradingsystem.inventory.data.enterprise.Product;
import org.cocome.tradingsystem.inventory.data.persistence.IPersistenceContext;

/**
 * This interface provides methods for querying the database. The interface will
 * be used by the InventoryApplication. The methods are derived from methods
 * defined in {@link IStoreInventoryManager} and {@link IStoreInventory}.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
public interface IStoreQuery {

	//
	// Global queries.
	//

	/**
	 * Queries the database for a {@link Store} entity with given name and location.
	 * 
	 * @param name
	 *            store name
	 * @param location
	 *            store location, may contain wildcard characters
	 * @param pctx
	 *            the persistence context
	 * @return
	 *         {@link Store} entity or {@code null} if there is no store with
	 *         the given name and location.
	 * 
	 * @throws NonUniqueResultException
	 *             if more than one store matches the given name and location
	 */
	Store queryStore(String name, String location, IPersistenceContext pctx);

	/**
	 * Returns a {@link Store} entity corresponding to the given unique identifier.
	 * 
	 * @param storeId
	 *            unique identifier of a {@link Store} entity
	 * @param pctx
	 *            the persistence context
	 * @return
	 *         {@link Store} entity
	 * 
	 * @throws EntityNotFoundException
	 *             if a store with the given id could not be found
	 */
	Store queryStoreById(long storeId, IPersistenceContext pctx);

	/**
	 * Returns a {@link StockItem} entity corresponding to the given unique identifier.
	 * 
	 * @param stockItemId
	 *            unique identifier of a {@link StockItem} entity
	 * @param pctx
	 *            the persistence context
	 * @return
	 *         {@link StockItem} entity
	 * 
	 * @throws EntityNotFoundException
	 *             if a stock item with the given id could not be found
	 */
	StockItem queryStockItemById(long stockItemId, IPersistenceContext pctx);

	/**
	 * Returns a {@link Product} entity corresponding to the given unique identifier.
	 * 
	 * @param productId
	 *            unique identifier of a {@link Product} entity
	 * @param pctx
	 *            the persistence context
	 * @return
	 *         {@link Product} entity
	 * 
	 * @throws EntityNotFoundException
	 *             if a product with the given id could not be found
	 */
	Product queryProductById(long productId, IPersistenceContext pctx);

	/**
	 * Queries the database for a {@link Product} entity with given barcode.
	 * 
	 * @param barcode
	 *            barcode of the product to find
	 * @param pctx
	 *            the persistence context
	 * @return
	 *         {@link Product} entity with the specified barcode, or {@code null} if there is no such product.
	 */
	Product queryProduct(long barcode, IPersistenceContext pctx);

	/**
	 * Queries the database for a {@link ProductOrder} with given identifier.
	 * <p>
	 * The following methods from StoreIf use this method: List<ComplexOrderTO> orderProducts(ComplexOrderTO complexOrder, StoreTO storeTO); ComplexOrderTO
	 * getOrder(int orderId); void rollInReceivedOrder(ComplexOrderTO complexOrder, StoreTO store);
	 * 
	 * @param orderId
	 *            unique identifier of a {@link ProductOrder} entity
	 * @param pctx
	 *            the persistence context
	 * @return
	 *         An instance of {@link ProductOrder} with the specified id.
	 * 
	 * @throws EntityNotFoundException
	 *             if a product order with the given id could not be found
	 */
	ProductOrder queryOrderById(long orderId, IPersistenceContext pctx);

	//
	// Queries limited to a given store.
	//

	/**
	 * Queries the specified store for all the products it carries, i.e.
	 * products for which there is a stock item.
	 * 
	 * @param storeId
	 *            unique identifier of a {@link Store} entity
	 * @param pctx
	 *            the persistence context
	 * @return
	 *         A collection of products carried by the given store.
	 */
	Collection<Product> queryProducts(long storeId, IPersistenceContext pctx);

	/**
	 * Queries the specified store for all outstanding product orders, i.e.
	 * product orders which do not have their delivery date set.
	 * 
	 * @param storeId
	 *            unique identifier of a {@link Store} entity
	 * @param pctx
	 *            the persistence context
	 * @return
	 *         A collection of outstanding product orders in the specified store.
	 */
	Collection<ProductOrder> queryOutstandingOrders(long storeId, IPersistenceContext pctx);

	/**
	 * Queries a store for all its stock items.
	 * 
	 * @param storeId
	 *            unique identifier of a {@link Store} entity
	 * @param pctx
	 *            the persistence context
	 * @return
	 *         A collection of {@link StockItem} entities.
	 */
	Collection<StockItem> queryAllStockItems(long storeId, IPersistenceContext pctx);

	/**
	 * Queries a given store for all stock items with low stock.
	 * 
	 * @param storeId
	 *            unique identifier of a {@link Store} entity
	 * @param pctx
	 *            the persistence context
	 * @return
	 *         A collection of {@link StockItem} entities objects representing
	 *         low stock products in the given store.
	 */
	Collection<StockItem> queryLowStockItems(long storeId, IPersistenceContext pctx);

	/**
	 * Queries the specified store for a stock item associated with product
	 * identified by specified barcode.
	 * 
	 * @param storeId
	 *            unique identifier of a {@link Store} entity
	 * @param productBarcode
	 *            product barcode for which to find the stock item
	 * @param pctx
	 *            the persistence context
	 * @return
	 *         The StockItem from the given store for a product with the given
	 *         barcode, or {@code null} if the stock item could not be found.
	 */
	StockItem queryStockItem(
			long storeId, long productBarcode, IPersistenceContext pctx
			);

	/**
	 * @author SDQ Returns the stock for the given productIds.
	 * @param storeId
	 *            unique identifier of a {@link Store} entity
	 * @param productIds
	 *            {@link Product} entity identifiers to look up in the stock
	 * @param pctx
	 *            the persistence context
	 * @return
	 *         The products as StockItems (including amounts)
	 */
	Collection<StockItem> queryStockItemsByProductId(
			long storeId, long[] productIds, IPersistenceContext pctx
			);

}
