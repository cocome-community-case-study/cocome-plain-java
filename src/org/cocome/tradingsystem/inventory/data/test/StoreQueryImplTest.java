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

package org.cocome.tradingsystem.inventory.data.test;

import static org.junit.Assert.assertTrue;

import java.util.Collection;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.cocome.tradingsystem.inventory.data.DataFactory;
import org.cocome.tradingsystem.inventory.data.enterprise.Product;
import org.cocome.tradingsystem.inventory.data.persistence.EntityPersistenceContext;
import org.cocome.tradingsystem.inventory.data.store.ProductOrder;
import org.cocome.tradingsystem.inventory.data.store.StockItem;
import org.cocome.tradingsystem.inventory.data.store.Store;
import org.cocome.tradingsystem.inventory.data.store.IStoreQuery;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the {@link IStoreQuery} interface.
 * 
 * @author Yannick Welsch
 */
public final class StoreQueryImplTest {

	private static EntityManagerFactory emf;

	private static IStoreQuery sq;

	//

	private EntityManager em = null;

	private Store s1;

	private StockItem si1, si2;

	private ProductOrder o1;

	private Product p1;

	//

	@BeforeClass
	public static void setUpClass() {
		emf = Persistence.createEntityManagerFactory("inventory-manager");
		sq = DataFactory.getInstance().getStoreQuery();
	}

	@Before
	public void setUp()
			throws Exception {
		em = emf.createEntityManager();
		em.getTransaction().begin();

		s1 = new Store();
		em.persist(s1);

		si1 = new StockItem();
		si1.setAmount(100);
		si1.setMinStock(50);
		si1.setStore(s1);

		si2 = new StockItem();
		si2.setAmount(100);
		si2.setMinStock(200);
		si2.setStore(s1);

		o1 = new ProductOrder();
		o1.setStore(s1);

		p1 = new Product();
		si2.setProduct(p1);

		em.persist(s1);
		em.persist(si1);
		em.persist(si2);
		em.persist(p1);
		em.persist(o1);

		em.getTransaction().commit();

	}

	@After
	public void tearDown()
			throws Exception {
		em.close();
	}

	@Test
	public void queryLowStockItems() {
		em.getTransaction().begin();
		em.refresh(s1);
		em.refresh(si2);

		final Collection<StockItem> result = sq.queryLowStockItems(
				s1.getId(), new EntityPersistenceContext(em)
				);

		assertTrue(result.size() == 1
				&& result.iterator().next().getId() == si2.getId());

		em.getTransaction().commit();
	}

	@Test
	public void queryProducts() {
		em.getTransaction().begin();
		em.refresh(s1);
		em.refresh(p1);

		final Collection<Product> result = sq.queryProducts(
				s1.getId(), new EntityPersistenceContext(em)
				);

		assertTrue(result.size() == 1
				&& result.iterator().next().getId() == p1.getId());

		em.getTransaction().commit();
	}

	@Test
	public void queryOrderById() {
		em.getTransaction().begin();
		em.refresh(o1);

		final ProductOrder result = sq.queryOrderById(
				o1.getId(), new EntityPersistenceContext(em)
				);

		// TODO: Compare real equality
		assertTrue(result.getId() == o1.getId());
		em.getTransaction().commit();
	}

	@Test
	public void queryStoreById() {
		em.getTransaction().begin();
		em.refresh(s1);

		final Store result = sq.queryStoreById(
				s1.getId(), new EntityPersistenceContext(em)
				);

		// TODO: Compare real equality
		assertTrue(result.getId() == s1.getId());
		em.getTransaction().commit();
	}

}
