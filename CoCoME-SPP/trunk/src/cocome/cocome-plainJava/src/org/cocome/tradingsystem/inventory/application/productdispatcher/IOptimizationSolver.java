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

import java.util.Collection;
import java.util.Map;

import org.cocome.tradingsystem.inventory.application.store.ProductAmountTO;
import org.cocome.tradingsystem.inventory.application.store.StoreTO;
import org.cocome.tradingsystem.inventory.data.store.StockItem;
import org.cocome.tradingsystem.inventory.data.store.Store;

/**
 * For description see method solveOptimization()
 * <p>
 * Required for UC8.
 * 
 * @author kelsaka
 */
interface IOptimizationSolver {

	/**
	 * Solves the problem of optimal transportation costs with respect to:
	 * <ol>
	 * <li>the required products and the required amount</li>
	 * <li>the available products and their provided amount per providing store</li>
	 * <li>The distances between the requiring and the providing store</li>
	 * </ol>
	 * 
	 * @param requiredProductAmounts
	 *            products/amounts required by the calling store
	 * @param storeStockItems
	 *            stock item availability per store
	 * @param storeDistances
	 *            distances between requiring store and each providing stores
	 * @return
	 *         a map: for each Store the product/amount tuple is given;
	 *         those products/amounts have to be delivered by the store
	 */
	Map<StoreTO, Collection<ProductAmountTO>> solveOptimization(
			Collection<ProductAmountTO> requiredProductAmounts,
			Map<Store, Collection<StockItem>> storeStockItems,
			Map<Store, Integer> storeDistances
			);

}
