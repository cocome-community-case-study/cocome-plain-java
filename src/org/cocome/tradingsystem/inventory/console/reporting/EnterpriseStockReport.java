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

package org.cocome.tradingsystem.inventory.console.reporting;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.persistence.EntityNotFoundException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.text.html.HTMLEditorKit;

import org.cocome.tradingsystem.inventory.application.reporting.ReportTO;
import org.cocome.tradingsystem.inventory.application.reporting.IReporting;
import org.cocome.tradingsystem.util.RemoteComponent;
import org.cocome.tradingsystem.util.java.Swing;

/**
 * TODO Extract template class from report types. --LB
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
final class EnterpriseStockReport extends JPanel {

	public EnterpriseStockReport(final RemoteComponent<IReporting> remoteReporting) {
		super(new BorderLayout());

		//

		final JLabel hintLabel = new JLabel("Enter enterprise id and press 'Create Report'");
		final JTextField inputField = new JTextField();
		final JLabel statusLabel = new JLabel();
		statusLabel.setForeground(Color.RED);

		final JPanel inputPanel = new JPanel(new BorderLayout());
		inputPanel.add(hintLabel, BorderLayout.NORTH);
		inputPanel.add(inputField, BorderLayout.CENTER);
		inputPanel.add(statusLabel, BorderLayout.SOUTH);

		//

		final JTextPane outputPane = new JTextPane();
		outputPane.setEditorKit(new HTMLEditorKit());

		//

		final JButton createReportButton = Swing.createButton(
				"Create Report", new ActionListener() {
					@Override
					public void actionPerformed(@SuppressWarnings("unused") final ActionEvent event) {
						try {
							outputPane.setText("");

							final long enterpriseId = Long.parseLong(inputField.getText());
							final IReporting reporting = remoteReporting.get();
							final ReportTO report = reporting.getEnterpriseStockReport(enterpriseId);

							outputPane.setText(report.getReportText());
							statusLabel.setText("");
							inputField.setText("");

						} catch (final NumberFormatException nfe) {
							statusLabel.setText("Invalid enterprise id: " + nfe.getMessage());

						} catch (final EntityNotFoundException enfe) {
							statusLabel.setText("There is no such enterprise!");

						} catch (final Exception e) {
							statusLabel.setText(
									"Failed to get enterprise stock report: " + e.getMessage()
									);
						}
					}
				}
				);

		//

		add(inputPanel, BorderLayout.NORTH);
		add(new JScrollPane(outputPane), BorderLayout.CENTER);
		add(createReportButton, BorderLayout.SOUTH);
	}
}
