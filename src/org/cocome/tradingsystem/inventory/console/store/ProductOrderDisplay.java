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

import java.awt.GridLayout;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import org.cocome.tradingsystem.inventory.application.store.ComplexOrderEntryTO;
import org.cocome.tradingsystem.inventory.application.store.ComplexOrderTO;

/**
 * Displays newly ordered products.
 * 
 * @author Yannick Welsch
 */
final class ProductOrderDisplay extends JPanel {

	private String[] columnNames = { "Amount", "Product ID", "Product Name",
		"Supplier ID", "Supplier Name" };

	public ProductOrderDisplay(List<ComplexOrderTO> orders) {

		for (ComplexOrderTO cto : orders) {
			JPanel descr = new JPanel();
			descr.setLayout(new GridLayout(2, 2));
			JLabel l1 = new JLabel("Order ID: ");
			JLabel l2 = new JLabel(cto.getId() + "");
			JLabel l3 = new JLabel("Ordering Date: ");
			JLabel l4 = new JLabel(cto.getOrderingDate().toString());
			descr.add(l1);
			descr.add(l2);
			descr.add(l3);
			descr.add(l4);
			add(descr);

			Object[][] data = new Object[cto.getOrderEntryTOs().size()][columnNames.length];
			int i = 0;
			for (ComplexOrderEntryTO coeto : cto.getOrderEntryTOs()) {
				data[i][0] = coeto.getAmount();
				data[i][1] = coeto.getProductTO().getId();
				data[i][2] = coeto.getProductTO().getName();
				data[i][3] = coeto.getProductTO().getSupplierTO().getId();
				data[i][4] = coeto.getProductTO().getSupplierTO()
						.getName();
				i++;
			}
			DefaultTableModel dtm = new DefaultTableModel(data, columnNames) {
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			};

			JTable entries = new JTable(dtm);
			add(new JScrollPane(entries));
		}
	}

}
