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

/**
 * This exception is thrown if there is no product for a specific barcode in the
 * database
 * 
 * @author Yannick Welsch
 */
public final class NoSuchProductException extends Exception {

	private static final long serialVersionUID = -6026652539932418410L;

	//

	public NoSuchProductException() {
		super();
	}

	public NoSuchProductException(final String message) {
		super(message);
	}

	public NoSuchProductException(final Throwable cause) {
		super(cause);
	}

	public NoSuchProductException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
