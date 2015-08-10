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

package org.cocome.tradingsystem.cashdeskline.cashdesk.barcodescanner;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.cocome.tradingsystem.util.java.Swing;
import org.cocome.tradingsystem.util.mvc.AbstractVisualController;

/**
 * Implements the visual view/controller for the cash desk barcode scanner
 * component.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
public final class VisualBarcodeScanner
		extends AbstractVisualController<BarcodeScannerModel> {

	private static final Logger LOG =
			Logger.getLogger(VisualBarcodeScanner.class);

	//

	public VisualBarcodeScanner() {
		final JLabel hintLabel = this.createHintLabel();
		final JTextField barcodeEntryField = this.createBarcodeEntryField();
		final JButton scanItemButton = this.createScanItemButton(barcodeEntryField);

		// construct the view and lay out the components

		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(hintLabel, BorderLayout.NORTH);
		panel.add(barcodeEntryField, BorderLayout.CENTER);
		panel.add(scanItemButton, BorderLayout.SOUTH);

		//

		this.viewComponent = panel;
	}

	private JButton createScanItemButton(final JTextField barcodeEntryField) {
		return Swing.createButton(
				"Scan Item",

				new ActionListener() {
					@Override
					@SuppressWarnings("unused")
					public void actionPerformed(final ActionEvent event) {
						final String input = barcodeEntryField.getText();
						try {
							final long barcode = Long.parseLong(input);
							barcodeEntryField.setBackground(Color.WHITE);

							VisualBarcodeScanner.this._model().sendProductBarcode(barcode);

						} catch (final NumberFormatException nfe) {
							LOG.debug("Invalid bar code: " + nfe.getMessage());
							barcodeEntryField.setBackground(Color.RED);
						}
					}
				}
				);
	}

	private JLabel createHintLabel() {
		return new JLabel("Enter product barcode to scan and press 'Scan Item'");
	}

	private JTextField createBarcodeEntryField() {
		final JTextField result = new JTextField();
		result.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
		return result;
	}

}
