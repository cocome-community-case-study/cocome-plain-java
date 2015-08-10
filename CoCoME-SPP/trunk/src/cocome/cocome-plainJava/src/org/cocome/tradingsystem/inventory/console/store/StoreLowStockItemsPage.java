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

import java.awt.BorderLayout;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.cocome.tradingsystem.inventory.application.store.ProductWithStockItemTO;
import org.cocome.tradingsystem.inventory.application.store.IStoreInventoryManager;
import org.cocome.tradingsystem.inventory.console.store.AbstractHandlerTableModel.ColumnHandler;
import org.cocome.tradingsystem.util.RemoteComponent;

import org.apache.log4j.Logger;

/**
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
final class StoreLowStockItemsPage
		extends RefreshableTablePage<IStoreInventoryManager, ProductWithStockItemTO> {

	//

	StoreLowStockItemsPage(
			final RemoteComponent<IStoreInventoryManager> remoteStore) {
		super(remoteStore);

		_tableModel = __createTableModel(remoteStore);
		_pageView = __createPageView(_tableModel);
	}

	//

	private AbstractHandlerTableModel<ProductWithStockItemTO> __createTableModel(
			final RemoteComponent<IStoreInventoryManager> remoteStore
			) {
		final Logger log = Logger.getLogger(StoreConsole.class);

		@SuppressWarnings("unchecked")
		final ColumnHandler<ProductWithStockItemTO>[] handlers = new ColumnHandler[] {
			ColumnHandlerFactory.immutableProductId(),
			ColumnHandlerFactory.immutableProductName(),
			ColumnHandlerFactory.immutableProductBarcode(),
			ColumnHandlerFactory.immutableProductPurchasePrice(),

			ColumnHandlerFactory.immutableStockItemId(),
			ColumnHandlerFactory.immutableStockItemAmount(),
			ColumnHandlerFactory.immutableStockItemMinStock(),
			ColumnHandlerFactory.immutableStockItemMaxStock(),
			ColumnHandlerFactory.editableStockItemSalesPrice(remoteStore, log),
		};

		return new AbstractHandlerTableModel<ProductWithStockItemTO>(handlers) {
			@Override
			protected List<ProductWithStockItemTO> _fetchRows() throws RemoteException {
				final IStoreInventoryManager store = _remote.get();
				return store.getProductsWithLowStock();
			}
		};
	}

	private JComponent __createPageView(
			final AbstractHandlerTableModel<ProductWithStockItemTO> tableModel
			) {
		final JLabel hintLabel = new JLabel("The stock item sales price can be edited in-place");
		final JTable orderTable = new JTable(tableModel);

		//

		final JPanel result = new JPanel(new BorderLayout());
		result.add(hintLabel, BorderLayout.PAGE_START);
		result.add(new JScrollPane(orderTable), BorderLayout.CENTER);

		return result;
	}

}
