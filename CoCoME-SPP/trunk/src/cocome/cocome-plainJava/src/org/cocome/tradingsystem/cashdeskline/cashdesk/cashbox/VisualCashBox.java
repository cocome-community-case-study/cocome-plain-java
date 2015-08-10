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

package org.cocome.tradingsystem.cashdeskline.cashdesk.cashbox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cocome.tradingsystem.cashdeskline.datatypes.ControlKeyStroke;
import org.cocome.tradingsystem.cashdeskline.datatypes.NumPadKeyStroke;
import org.cocome.tradingsystem.util.java.Swing;
import org.cocome.tradingsystem.util.mvc.ActiveVisualController;

/*
 import static org.cocome.tradingsystem.cashdeskline.datatypes.NumPadKeyStroke.COMMA;
 import static org.cocome.tradingsystem.cashdeskline.datatypes.NumPadKeyStroke.EIGHT;
 import static org.cocome.tradingsystem.cashdeskline.datatypes.NumPadKeyStroke.ENTER;
 import static org.cocome.tradingsystem.cashdeskline.datatypes.NumPadKeyStroke.FIVE;
 import static org.cocome.tradingsystem.cashdeskline.datatypes.NumPadKeyStroke.FOUR;
 import static org.cocome.tradingsystem.cashdeskline.datatypes.NumPadKeyStroke.NINE;
 import static org.cocome.tradingsystem.cashdeskline.datatypes.NumPadKeyStroke.ONE;
 import static org.cocome.tradingsystem.cashdeskline.datatypes.NumPadKeyStroke.SEVEN;
 import static org.cocome.tradingsystem.cashdeskline.datatypes.NumPadKeyStroke.SIX;
 import static org.cocome.tradingsystem.cashdeskline.datatypes.NumPadKeyStroke.THREE;
 import static org.cocome.tradingsystem.cashdeskline.datatypes.NumPadKeyStroke.TWO;
 import static org.cocome.tradingsystem.cashdeskline.datatypes.NumPadKeyStroke.ZERO;
 */

/**
 * Implements the visual view/controller for the cash box component of the cash
 * desk.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
public final class VisualCashBox
		extends ActiveVisualController<CashBoxModel> {

	private final JLabel statusLabel;

	//

	public VisualCashBox() {

		// cash box buttons

		final JPanel buttonsPanel = this.createVerticalBox(
				this.createHorizontalGrid(
						// sale control buttons
						this.createStartNewSaleButton(), this.createSaleFinishedButton()
						),

				this.createNumpad(),

				this.createHorizontalGrid(
						// payment mode buttons
						this.createBarPaymentButton(), this.createCardPaymentButton()
						),

				this.createHorizontalGrid(
						// cash box control buttons
						this.createExpressModeButton(), this.createCloseCashBoxButton()
						)
				);

		// cash box status

		final JLabel statusLabel = this.createStatusLabel(); // NOCS

		// construct the view and lay out the components

		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(buttonsPanel, BorderLayout.CENTER);
		panel.add(statusLabel, BorderLayout.SOUTH);

		//

		this.statusLabel = statusLabel;
		this.viewComponent = panel;
	}

	private JButton createExpressModeButton() {
		return Swing.createButton(
				"Disable Express Mode",

				new ActionListener() {
					@Override
					@SuppressWarnings("unused")
					public void actionPerformed(final ActionEvent event) {
						VisualCashBox.this._model().pressControlKey(ControlKeyStroke.DISABLE_EXPRESS_MODE);
					}
				}
				);
	}

	private JButton createCardPaymentButton() {
		return Swing.createButton(
				"Card Payment",

				new ActionListener() {
					@Override
					@SuppressWarnings("unused")
					public void actionPerformed(final ActionEvent event) {
						VisualCashBox.this._model().pressControlKey(ControlKeyStroke.CREDIT_CARD_PAYMENT);
					}
				}
				);
	}

	private JButton createBarPaymentButton() {
		return Swing.createButton(
				"Bar Payment",

				new ActionListener() {
					@Override
					@SuppressWarnings("unused")
					public void actionPerformed(final ActionEvent event) {
						VisualCashBox.this._model().pressControlKey(ControlKeyStroke.CASH_PAYMENT);
					}
				}
				);
	}

	private JButton createSaleFinishedButton() {
		return Swing.createButton(
				"Sale Finished",

				new ActionListener() {
					@Override
					@SuppressWarnings("unused")
					public void actionPerformed(final ActionEvent event) {
						VisualCashBox.this._model().pressControlKey(ControlKeyStroke.FINISH_SALE);
					}
				}
				);
	}

	private JButton createStartNewSaleButton() {
		return Swing.createButton(
				"Start New Sale",

				new ActionListener() {
					@Override
					@SuppressWarnings("unused")
					public void actionPerformed(final ActionEvent event) {
						VisualCashBox.this._model().pressControlKey(ControlKeyStroke.START_SALE);
					}
				}
				);
	}

	private JButton createCloseCashBoxButton() {
		return Swing.createButton(
				"Close Cash Box",

				new ActionListener() {
					@Override
					@SuppressWarnings("unused")
					public void actionPerformed(final ActionEvent event) {
						VisualCashBox.this._model().close();
					}
				}
				);
	}

	private JPanel createNumpad() {
		final JPanel result = new JPanel(new GridLayout(4, 3));

		for (final NumPadKeyStroke keyStroke : new NumPadKeyStroke[] {
			NumPadKeyStroke.SEVEN, NumPadKeyStroke.EIGHT, NumPadKeyStroke.NINE,
			NumPadKeyStroke.FOUR, NumPadKeyStroke.FIVE, NumPadKeyStroke.SIX,
			NumPadKeyStroke.ONE, NumPadKeyStroke.TWO, NumPadKeyStroke.THREE,
			NumPadKeyStroke.ZERO, NumPadKeyStroke.COMMA, NumPadKeyStroke.ENTER,
		}) {
			final JButton button = this.createNumpadButton(keyStroke);
			button.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
			result.add(button);
		}

		return result;
	}

	private JButton createNumpadButton(final NumPadKeyStroke key) {
		return Swing.createButton(
				key.label(),

				new ActionListener() {
					@Override
					@SuppressWarnings("unused")
					public void actionPerformed(final ActionEvent event) {
						//
						// The listener is tied to a button instance created for
						// a particular key stroke, so we don't need to derive
						// the key stroke from the button text.
						//
						VisualCashBox.this._model().pressNumpadKey(key);
					}
				}
				);
	}

	//

	private JPanel createVerticalBox(final JPanel... panels) {
		final JPanel result = new JPanel();
		result.setLayout(new BoxLayout(result, BoxLayout.Y_AXIS));

		final Dimension spacerDimension = new Dimension(0, 5);
		for (final JPanel panel : panels) {
			result.add(Box.createRigidArea(spacerDimension));
			result.add(panel);
		}

		result.add(Box.createRigidArea(spacerDimension));
		return result;
	}

	private JPanel createHorizontalGrid(final JButton... buttons) {
		final GridLayout gridLayout = new GridLayout(1, buttons.length);
		gridLayout.setHgap(5);

		final JPanel result = new JPanel(gridLayout);
		for (final JButton button : buttons) {
			result.add(button);
		}

		return result;
	}

	//

	private JLabel createStatusLabel() {
		final JLabel result = new JLabel();
		result.setBorder(BorderFactory.createLoweredBevelBorder());
		return result;
	}

	//

	@Override
	protected void updateContent(final CashBoxModel cashBox) {
		if (cashBox.isOpen()) {
			this.statusLabel.setForeground(Color.RED);
			this.statusLabel.setText("Cash box is open.");
		} else {
			this.statusLabel.setForeground(Color.BLACK);
			this.statusLabel.setText("Cash box is closed.");
		}
	}

}
