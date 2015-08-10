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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.cocome.tradingsystem.inventory.application.store.ComplexOrderTO;
import org.cocome.tradingsystem.inventory.application.store.IStoreInventoryManager;
import org.cocome.tradingsystem.inventory.console.store.AbstractHandlerTableModel.ColumnHandler;
import org.cocome.tradingsystem.util.RemoteComponent;
import org.cocome.tradingsystem.util.java.Swing;

/**
 * @author Lubomir Bulej
 */
final class ReceiveOrdersPage
		extends RefreshableTablePage<IStoreInventoryManager, ComplexOrderTO> {

	ReceiveOrdersPage(
			final RemoteComponent<IStoreInventoryManager> remoteStore) {
		super(remoteStore);

		_tableModel = __createTableModel();
		_pageView = __createPageView(_tableModel);
	}

	//

	private AbstractHandlerTableModel<ComplexOrderTO> __createTableModel() {
		@SuppressWarnings("unchecked")
		final ColumnHandler<ComplexOrderTO>[] handlers = new ColumnHandler[] {
			ColumnHandlerFactory.immutableOrderId(),
			ColumnHandlerFactory.immutableOrderEntryCount(),
			ColumnHandlerFactory.immutableOrderDateOrdered(),
			ColumnHandlerFactory.immutableOrderDateDelivered(),
		};

		return new AbstractHandlerTableModel<ComplexOrderTO>(handlers) {
			@Override
			protected List<ComplexOrderTO> _fetchRows() throws RemoteException {
				final IStoreInventoryManager store = _remote.get();
				return store.getOutstandingOrders();
			}
		};
	}

	private JPanel __createPageView(final AbstractHandlerTableModel<ComplexOrderTO> tableModel) {
		final JLabel hintLabel = new JLabel("Select received product order and press the 'Roll In' button");
		final JTable orderTable = new JTable(tableModel);
		final JButton rollInButton = __createRollInButton(orderTable);

		//

		final JPanel result = new JPanel(new BorderLayout());
		result.add(hintLabel, BorderLayout.PAGE_START);
		result.add(new JScrollPane(orderTable), BorderLayout.CENTER);
		result.add(rollInButton, BorderLayout.PAGE_END);

		return result;
	}

	private JButton __createRollInButton(final JTable orderTable) {
		return Swing.createButton("Roll In Order", new ActionListener() {
			@Override
			public void actionPerformed(
					@SuppressWarnings("unused") final ActionEvent ae
					) {
				final int selectedRowIndex = orderTable.getSelectedRow();
				if (selectedRowIndex >= 0) {
					__rollInOrder(selectedRowIndex);
				}
			}
		});
	}

	private void __rollInOrder(final int rowIndex) {
		final JFrame parent = _getParentFrame();

		try {
			final long orderId = (Long) _tableModel.getValueAt(rowIndex, 0);
			final IStoreInventoryManager store = _remote.get();
			store.rollInReceivedOrder(orderId);

			JOptionPane.showMessageDialog(
					parent, "Order " + orderId + " successfully rolled in"
					);

			refresh();

		} catch (final Exception e) {
			JOptionPane.showMessageDialog(
					parent, "Failed to roll in order: " + e.getMessage()
					);
		}
	}

}
