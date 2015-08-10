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
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.cocome.tradingsystem.inventory.application.store.ComplexOrderEntryTO;
import org.cocome.tradingsystem.inventory.application.store.ComplexOrderTO;
import org.cocome.tradingsystem.inventory.application.store.ProductWithSupplierTO;
import org.cocome.tradingsystem.inventory.application.store.IStoreInventoryManager;
import org.cocome.tradingsystem.inventory.console.store.AbstractHandlerTableModel.ColumnHandler;
import org.cocome.tradingsystem.inventory.console.store.AbstractHandlerTableModel.EditableColumnHandler;
import org.cocome.tradingsystem.util.RemoteComponent;
import org.cocome.tradingsystem.util.java.Lists;
import org.cocome.tradingsystem.util.java.Swing;

import org.apache.log4j.Logger;

/**
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
final class IssueOrdersPage
		extends RefreshableTablePage<IStoreInventoryManager, ProductSupplierAmountHolder> {

	private static final Logger __log__ = Logger.getLogger(StoreConsole.class);

	//

	IssueOrdersPage(
			final RemoteComponent<IStoreInventoryManager> remoteStore) {
		super(remoteStore);

		_tableModel = __createTableModel();
		_pageView = __createPageView(_tableModel);
	}

	//

	private AbstractHandlerTableModel<ProductSupplierAmountHolder> __createTableModel() {
		@SuppressWarnings("unchecked")
		final ColumnHandler<ProductSupplierAmountHolder>[] handlers = new ColumnHandler[] {
			__holderColumnHandler(ColumnHandlerFactory.immutableProductId()),
			__holderColumnHandler(ColumnHandlerFactory.immutableProductName()),
			__holderColumnHandler(ColumnHandlerFactory.immutableProductBarcode()),
			__holderColumnHandler(ColumnHandlerFactory.immutableProductPurchasePrice()),

			__holderColumnHandler(ColumnHandlerFactory.immutableSupplierId()),
			__holderColumnHandler(ColumnHandlerFactory.immutableSupplierName()),

			__editableProductAmount(),
		};

		return new AbstractHandlerTableModel<ProductSupplierAmountHolder>(handlers) {
			@Override
			protected List<ProductSupplierAmountHolder> _fetchRows() throws RemoteException {
				final IStoreInventoryManager store = _remote.get();

				final List<ProductWithSupplierTO> products = store.getAllProducts();

				final List<ProductSupplierAmountHolder> result = Lists.newArrayList(products.size());

				for (final ProductWithSupplierTO psto : products) {
					result.add(new ProductSupplierAmountHolder(psto));
				}

				return result;
			}
		};
	}

	private static ColumnHandler<ProductSupplierAmountHolder> __holderColumnHandler(
			final ColumnHandler<? super ProductWithSupplierTO> target
			) {
		return new ColumnHandler<ProductSupplierAmountHolder>(target.name()) {
			@Override
			Object getValue(final ProductSupplierAmountHolder holder) {
				return target.getValue(holder.pwsto);
			}

			@Override
			boolean isEditable() {
				return target.isEditable();
			}

			@Override
			boolean setValue(final ProductSupplierAmountHolder holder, final String value) {
				return target.setValue(holder.pwsto, value);
			}
		};
	}

	private EditableColumnHandler<ProductSupplierAmountHolder> __editableProductAmount() {
		return new EditableColumnHandler<ProductSupplierAmountHolder>("Amount to Order") {
			@Override
			Object getValue(final ProductSupplierAmountHolder p) {
				return p.amount;
			}

			@Override
			boolean setValue(
					final ProductSupplierAmountHolder p, final String value
					) {
				try {
					final long amount = Long.parseLong(value);
					return __changeAmountToOrder(p, amount);

				} catch (final NumberFormatException e) {
					__log__.debug("Invalid product amount", e);

					// do nothing
					return false;
				}
			}

			private boolean __changeAmountToOrder(
					final ProductSupplierAmountHolder p, final long newAmount
					) {
				if (newAmount != p.amount) {
					p.amount = newAmount;
					return true;

				} else {
					return false;
				}
			}
		};
	}

	//

	private JComponent __createPageView(
			final AbstractHandlerTableModel<ProductSupplierAmountHolder> tableModel
			) {
		final JLabel hintLabel = new JLabel("Enter amount for specific product and press Order");
		final JTable productTable = new JTable(tableModel);
		final JButton orderButton = __createOrderButton();

		//

		final JPanel result = new JPanel(new BorderLayout());
		result.add(hintLabel, BorderLayout.PAGE_START);
		result.add(new JScrollPane(productTable), BorderLayout.CENTER);
		result.add(orderButton, BorderLayout.PAGE_END);

		return result;
	}

	private JButton __createOrderButton() {
		return Swing.createButton("Order", new ActionListener() {
			@Override
			public void actionPerformed(
					@SuppressWarnings("unused") final ActionEvent ae
					) {
				__orderProducts();
			}
		});
	}

	//

	private List<ComplexOrderTO> __orderProducts() {
		final ComplexOrderTO order = __collectProductOrders(_tableModel.getRows());
		final int orderEntryCount = order.getOrderEntryTOs().size();
		__log__.debug("Number of order entries: " + orderEntryCount);

		if (orderEntryCount > 0) {
			final List<ComplexOrderTO> issuedOrders = __orderProducts(order);
			final int issuedOrderCount = issuedOrders.size();
			__log__.debug("Number of issued orders: " + issuedOrderCount);

			if (issuedOrderCount > 0) {
				__showIssuedOrders(issuedOrders);
				refresh();
			}

			return issuedOrders;

		} else {
			return Collections.emptyList();
		}
	}

	private ComplexOrderTO __collectProductOrders(
			final List<ProductSupplierAmountHolder> holders
			) {
		final ComplexOrderTO result = new ComplexOrderTO();

		final List<ComplexOrderEntryTO> entries = Lists.newArrayList();
		for (final ProductSupplierAmountHolder holder : holders) {
			if (holder.amount > 0) {
				final ComplexOrderEntryTO oe = new ComplexOrderEntryTO();
				oe.setAmount(holder.amount);
				oe.setProductTO(holder.pwsto);

				entries.add(oe);
			}
		}

		result.setOrderEntryTOs(entries);
		return result;
	}

	private List<ComplexOrderTO> __orderProducts(final ComplexOrderTO order) {
		try {
			final IStoreInventoryManager store = _remote.get();
			return store.orderProducts(order);

		} catch (final RemoteException re) {
			__log__.error("Failed to order products", re);

			// Indicate that no orders were issued
			return Collections.emptyList();
		}
	}

	private void __showIssuedOrders(final List<ComplexOrderTO> issuedOrders) {
		final JPanel panel = new ProductOrderDisplay(issuedOrders);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

		final JFrame frame = new JFrame("Issued Product Orders");
		frame.getContentPane().add(new JScrollPane(panel));

		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}

}
