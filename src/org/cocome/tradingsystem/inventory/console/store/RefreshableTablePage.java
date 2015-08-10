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

package org.cocome.tradingsystem.inventory.console.store;

import java.rmi.Remote;

import org.cocome.tradingsystem.util.RemoteComponent;

/**
 * Represents a refreshable page for the store console user interface. This
 * class expects a table data model for the displayed data.
 * 
 * @author Lubomir Bulej
 */
abstract class RefreshableTablePage<C extends Remote, T> extends RefreshablePage<C> {

	protected AbstractHandlerTableModel<T> _tableModel;

	//

	public RefreshableTablePage(final RemoteComponent<C> remote) {
		super(remote);
	}

	//

	@Override
	public void refresh() {
		_tableModel.refresh();
	}

}
