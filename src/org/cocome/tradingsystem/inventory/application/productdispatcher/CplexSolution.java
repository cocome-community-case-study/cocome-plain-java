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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents the solution returned by the CPLEX parser. An instance of this
 * class can only be created by parsing the output of the CPLEX solver.
 * <p>
 * Please note that the parser is tightly tied to the {@link AmplCplexSolver} because the output of the parser depends both on variable names in the model and the
 * commands sent to the AMPL backend to display the solution.
 * 
 * @author Lubomir Bulej
 */
final class CplexSolution {

	/**
	 * Primitive holder for product identifier and amount.
	 */
	public static final class Product {
		private final long __id;
		private final int __amount;

		//

		Product(final long id, final int amount) {
			__id = id;
			__amount = amount;
		}

		//

		public long id() {
			return __id;
		}

		public int amount() {
			return __amount;
		}
	}

	//

	/**
	 * Primitive holder for store identifier and a collection
	 * of products the store will be sending to target store.
	 */
	public static final class Store {
		private final Map<Long, Product> __products = new HashMap<Long, Product>();

		private final long __id;

		//

		Store(final long id) {
			__id = id;
		}

		void addProduct(final long productId, final int amount) {
			__products.put(productId, new Product(productId, amount));
		}

		//

		public long id() {
			return __id;
		}

		public Collection<Product> products() {
			return __products.values();
		}
	}

	//

	/** Stores with products to send out. */
	private final Map<Long, Store> __stores = new HashMap<Long, Store>();

	private CplexSolution() {
		// not to be instantiated by public
	}

	//

	void addStoreProduct(
			final long storeId, final long productId, final int amount
			) {
		Store store = __stores.get(storeId);
		if (store == null) {
			store = new Store(storeId);
			__stores.put(storeId, store);
		}

		store.addProduct(productId, amount);
	}

	public boolean isEmpty() {
		return __stores.isEmpty();
	}

	public Collection<Store> stores() {
		return __stores.values();
	}

	//

	private static final Pattern __ROW_PATTERN__ = Pattern.compile(
			"shipping_amount\\['Product(\\d+)','Store(\\d+)'\\]\\s=\\s(\\d+)\n"
			);

	/**
	 * Parses the output of the CPLEX solver and returns the parsed solution.
	 * If the solution is empty, we have either failed to parse the solution
	 * or the solver did not provide any.
	 * 
	 * @param solverOutput
	 *            the output of the CPLEX solver
	 * @return
	 *         solution containing the product amounts that should be
	 *         shipped to the calling store
	 */
	public static CplexSolution parse(final String solverOutput) {
		final CplexSolution solution = new CplexSolution();

		final Matcher matcher = __ROW_PATTERN__.matcher(solverOutput);
		while (matcher.find()) {
			final long productId = Long.parseLong(matcher.group(1));
			final long storeId = Long.parseLong(matcher.group(2));
			final int amount = Integer.parseInt(matcher.group(3));

			if (amount > 0) {
				solution.addStoreProduct(storeId, productId, amount);
			}
		}

		return solution;
	}

}
