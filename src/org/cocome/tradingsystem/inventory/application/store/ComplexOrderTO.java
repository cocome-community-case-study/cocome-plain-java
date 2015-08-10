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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.cocome.tradingsystem.util.java.Lists;

/**
 * A transfer object class for exchanging full order information between client
 * and the service-oriented application layer. It contains either copies of
 * persisted data which is transferred to the client, or data which is
 * transferred from the client to the application layer to be processed and
 * persisted.
 * 
 * @author Sebastian Herold
 * @author Lubomir Bulej
 */
public class ComplexOrderTO extends OrderTO {

	private static final long serialVersionUID = 7826213625996223229L;

	//

	private List<ComplexOrderEntryTO> __orderEntries = Collections.emptyList();

	//

	/**
	 * Gets list of order entry transfer objects which the order consists of.
	 * 
	 * @return
	 *         List of order entries.
	 */
	public List<ComplexOrderEntryTO> getOrderEntryTOs() {
		return new ArrayList<ComplexOrderEntryTO>(__orderEntries);
	}

	/**
	 * Sets list of order entry transfer objects.
	 * 
	 * @param entries
	 *            the list of order entries
	 */
	public void setOrderEntryTOs(final List<ComplexOrderEntryTO> entries) {
		__orderEntries = Lists.newArrayList(entries);
	}

}
