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

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * This class represents a Product in the database
 * 
 * @author Yannick Welsch
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "barcode" }))
public class Product {

	private long id;

	private long barcode;

	private double purchasePrice;

	private String name;

	private ProductSupplier supplier;

	//

	/**
	 * Gets identifier value
	 * 
	 * @return The id.
	 */
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	public long getId() {
		return id;
	}

	/**
	 * Sets identifier.
	 * 
	 * @param id
	 *            Identifier value.
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @return The barcode of the product
	 */
	@Basic
	public long getBarcode() {
		return barcode;
	}

	/**
	 * @param barcode
	 *            The barcode of the product
	 */
	public void setBarcode(long barcode) {
		this.barcode = barcode;
	}

	/**
	 * @return The name of the product
	 */
	@Basic
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            The name of the product
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return The ProductSupplier of this product
	 */
	@ManyToOne
	public ProductSupplier getSupplier() {
		return supplier;
	}

	/**
	 * @param supplier
	 *            The ProductSupplier of this product
	 */
	public void setSupplier(ProductSupplier supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return The purchase price of this product
	 */
	@Basic
	public double getPurchasePrice() {
		return purchasePrice;
	}

	/**
	 * @param purchasePrice
	 *            The purchase price of this product
	 */
	public void setPurchasePrice(double purchasePrice) {
		this.purchasePrice = purchasePrice;
	}

}
