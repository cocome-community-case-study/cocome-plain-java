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

package org.cocome.tradingsystem.inventory.data.enterprise;

import java.util.List;

import javax.persistence.EntityManager;

import org.cocome.tradingsystem.inventory.data.persistence.EntityPersistenceContext;
import org.cocome.tradingsystem.inventory.data.persistence.IPersistenceContext;
import org.cocome.tradingsystem.inventory.data.store.ProductOrder;

/**
 * @author Yannick Welsch
 */
public final class EnterpriseQueryProvider implements IEnterpriseQuery {

	@Override
	public TradingEnterprise queryEnterpriseById(
			final long enterpriseId, final IPersistenceContext pctx
			) {
		return __getEntityById(TradingEnterprise.class, enterpriseId, pctx);
	}

	@Override
	public long getMeanTimeToDelivery(
			final ProductSupplier supplier, final TradingEnterprise enterprise,
			final IPersistenceContext pctx
			) {
		final EntityManager em = __getEntityManager(pctx);

		// XXX It should be possible to simplify the MTTD query.
		@SuppressWarnings("unchecked")
		List<ProductOrder> pos = em.createQuery(
				"SELECT productorder FROM ProductOrder AS productorder " +
						"WHERE productorder.deliveryDate IS NOT NULL " +
						"AND EXISTS (" +
						"SELECT orderentry FROM OrderEntry AS orderentry " +
						"WHERE orderentry.order = productorder " +
						"AND EXISTS (" +
						"SELECT product FROM Product AS product " +
						"WHERE product.supplier = ?1 AND orderentry.product = product" +
						")" +
						") AND EXISTS (" +
						"SELECT store FROM Store AS store " +
						"WHERE productorder.store = store AND store.enterprise = ?2" +
						")"
				).setParameter(1, supplier).setParameter(2, enterprise).getResultList();

		long result = 0;
		for (ProductOrder po : pos) {
			result += po.getDeliveryDate().getTime() - po.getOrderingDate().getTime();
		}

		return (pos.size() > 0) ? result / pos.size() : 0;
	}

	//

	private <E> E __getEntityById(
			final Class<E> entityClass, final long entityId,
			final IPersistenceContext pctx
			) {
		// throws EntityNotFound exception if the entity could not be found
		final EntityManager em = __getEntityManager(pctx);
		return em.getReference(entityClass, entityId);
	}

	private EntityManager __getEntityManager(final IPersistenceContext pctx) {
		// XXX There should be no need to escape the PersistenceContext interface
		return ((EntityPersistenceContext) pctx).getEntityManager();
	}

}
