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

package org.cocome.tradingsystem.inventory.data.persistence;

import javax.persistence.EntityManager;

/**
 * TODO The EntityPersistenceContext class should not need to be public.
 * 
 * @author Yannick Welsch
 */
public final class EntityPersistenceContext implements IPersistenceContext {

	private final EntityManager __em;

	//

	public EntityPersistenceContext(final EntityManager em) {
		__em = em;
	}

	public EntityManager getEntityManager() {
		return __em;
	}

	//

	@Override
	public ITransactionContext getTransactionContext() {
		return new EntityTransactionContext(__em.getTransaction());
	}

	@Override
	public void makePersistent(final Object object) {
		__em.persist(object);
	}

	@Override
	public void refresh(final Object object) {
		__em.refresh(object);
	}

	@Override
	public void close() {
		__em.close();
	}

}
