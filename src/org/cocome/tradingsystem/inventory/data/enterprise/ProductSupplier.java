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

import java.util.Collection;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

/**
 * This class represents a ProductSupplier in the database.
 * 
 * @author Yannick Welsch
 */
@Entity
public class ProductSupplier {

	private long id;

	private String name;

	private Collection<Product> products;

	//

	/**
	 * @return A unique identifier for ProductSupplier objects
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	/**
	 * @param id
	 *            A unique identifier for ProductSupplier objects
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return The name of the ProductSupplier
	 */
	@Basic
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name of the ProductSupplier
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The list of Products provided by the ProductSupplier
	 */
	@OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	public Collection<Product> getProducts() {
		return products;
	}

	/**
	 * @param products
	 *            The list of Products provided by the ProductSupplier
	 */
	public void setProducts(Collection<Product> products) {
		this.products = products;
	}

}
