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

package org.cocome.tradingsystem.cashdeskline.cashdesk.expresslight;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.cocome.tradingsystem.util.mvc.ActiveVisualController;

/**
 * Implements the visual view/controller for the express light component of the
 * cash desk.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
public final class VisualExpressLight
		extends ActiveVisualController<ExpressLightModel> {

	private final JLabel statusLabel;

	//

	public VisualExpressLight() {
		final JLabel statusLabel = this.createStatusLabel(); // NOCS

		// construct the view and lay out the components

		final JPanel panel = new JPanel(new BorderLayout());
		panel.add(statusLabel, BorderLayout.CENTER);

		//

		this.statusLabel = statusLabel;
		this.viewComponent = panel;
	}

	private JLabel createStatusLabel() {
		final JLabel result = new JLabel();
		result.setFont(new Font(Font.DIALOG, Font.BOLD, 24));
		result.setHorizontalAlignment(SwingConstants.CENTER);
		return result;
	}

	//

	@Override
	protected void updateContent(final ExpressLightModel expressLight) {
		if (expressLight.isOn()) {
			this.viewComponent.setBackground(Color.GREEN);
			this.statusLabel.setForeground(Color.YELLOW);
			this.statusLabel.setText("EXPRESS");

		} else {
			this.viewComponent.setBackground(Color.DARK_GRAY);
			this.statusLabel.setForeground(Color.BLACK);
			this.statusLabel.setText("NORMAL");
		}
	}

}
