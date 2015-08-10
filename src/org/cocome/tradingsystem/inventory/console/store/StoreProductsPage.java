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

import java.rmi.RemoteException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.cocome.tradingsystem.inventory.application.store.ProductWithSupplierTO;
import org.cocome.tradingsystem.inventory.application.store.IStoreInventoryManager;
import org.cocome.tradingsystem.inventory.console.store.AbstractHandlerTableModel.ColumnHandler;
import org.cocome.tradingsystem.util.RemoteComponent;

/**
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
final class StoreProductsPage
		extends RefreshableTablePage<IStoreInventoryManager, ProductWithSupplierTO> {

	StoreProductsPage(
			final RemoteComponent<IStoreInventoryManager> remoteStore) {
		super(remoteStore);

		_tableModel = __createTableModel();
		_pageView = __createPageView(_tableModel);
	}

	//

	private AbstractHandlerTableModel<ProductWithSupplierTO> __createTableModel() {
		@SuppressWarnings("unchecked")
		final ColumnHandler<ProductWithSupplierTO>[] handlers = new ColumnHandler[] {
			ColumnHandlerFactory.immutableProductId(),
			ColumnHandlerFactory.immutableProductName(),
			ColumnHandlerFactory.immutableProductBarcode(),
			ColumnHandlerFactory.immutableProductPurchasePrice(),

			ColumnHandlerFactory.immutableSupplierId(),
			ColumnHandlerFactory.immutableSupplierName(),
		};

		return new AbstractHandlerTableModel<ProductWithSupplierTO>(handlers) {
			@Override
			protected List<ProductWithSupplierTO> _fetchRows() throws RemoteException {
				final IStoreInventoryManager store = _remote.get();
				return store.getAllProducts();
			}
		};
	}

	private JComponent __createPageView(final AbstractHandlerTableModel<ProductWithSupplierTO> tableModel) {
		final JTable productTable = new JTable(tableModel);
		return new JScrollPane(productTable);
	}

}
