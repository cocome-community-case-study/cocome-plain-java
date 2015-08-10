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

package org.cocome.tradingsystem.cashdeskline.cashdesk.cardreader;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.cocome.tradingsystem.util.java.Swing;
import org.cocome.tradingsystem.util.mvc.AbstractVisualController;

/**
 * Implements the visual view/controller for the credit card reader component of
 * the cash desk.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
public final class VisualCardReader
		extends AbstractVisualController<CardReaderModel> {

	private static final Logger LOG =
			Logger.getLogger(VisualCardReader.class);

	//

	public VisualCardReader() {
		final JTextField entryField = this.createEntryField();
		final JButton scanCardButton = this.createScanCardButton(entryField);
		final JButton enterPinButton = this.createEnterPinButton(entryField);

		// construct the view and lay out the components

		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(scanCardButton, BorderLayout.NORTH);
		panel.add(entryField, BorderLayout.CENTER);
		panel.add(enterPinButton, BorderLayout.SOUTH);

		//

		this.viewComponent = panel;
	}

	private JButton createEnterPinButton(final JTextField pinEntryField) {
		return Swing.createButton(
				"Enter PIN",

				new ActionListener() {
					@Override
					@SuppressWarnings("unused")
					public void actionPerformed(final ActionEvent actionEvent) {
						final String input = pinEntryField.getText();
						try {
							final int pin = Integer.parseInt(input);
							pinEntryField.setBackground(Color.WHITE);

							VisualCardReader.this._model().sendCreditCardPin(pin);

						} catch (final NumberFormatException nfe) {
							LOG.error("Invalid PIN entered: " + nfe.getMessage());
							pinEntryField.setBackground(Color.RED);
						}
					}
				}
				);
	}

	private JTextField createEntryField() {
		final JTextField result = new JTextField();
		result.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
		return result;
	}

	private JButton createScanCardButton(final JTextField cardInfoEntryField) {
		return Swing.createButton(
				"Scan Card",

				new ActionListener() {
					@Override
					@SuppressWarnings("unused")
					public void actionPerformed(final ActionEvent event) {
						final String cardInfo = cardInfoEntryField.getText();
						VisualCardReader.this._model().sendCreditCardInfo(cardInfo);
					}
				}
				);
	}

}
