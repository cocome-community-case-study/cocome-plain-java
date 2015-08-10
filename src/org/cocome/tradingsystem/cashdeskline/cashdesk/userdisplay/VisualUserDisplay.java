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

package org.cocome.tradingsystem.cashdeskline.cashdesk.userdisplay;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Map;

import javax.swing.Box;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.cocome.tradingsystem.util.java.Maps;
import org.cocome.tradingsystem.util.mvc.ActiveVisualController;

/**
 * Implements the visual view/controller for the cash desk user display
 * component.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
public final class VisualUserDisplay
		extends ActiveVisualController<UserDisplayModel> {

	private final JTextArea displayArea;

	private final Map<MessageKind, Color> colorMap = this.createColorMap();

	//

	/**
	 * 
	 */
	public VisualUserDisplay() {

		// construct the view and lay out the components

		final JPanel panel = new JPanel(new BorderLayout());

		this.displayArea = this.createDisplayTextArea(panel);
		panel.add(this.displayArea, BorderLayout.CENTER);

		final Dimension spacer = new Dimension(10, 10);
		for (final String region : new String[] { BorderLayout.WEST, BorderLayout.EAST, BorderLayout.NORTH, BorderLayout.SOUTH }) {
			panel.add(Box.createRigidArea(spacer), region);
		}

		this.viewComponent = panel;
	}

	private JTextArea createDisplayTextArea(final JPanel panel) {
		final JTextArea result = new JTextArea();
		result.setFont(new Font(Font.DIALOG, Font.BOLD, 14));
		result.setLineWrap(true);
		result.setWrapStyleWord(true);
		result.setBackground(panel.getBackground());
		result.setEditable(false);
		return result;
	}

	//

	/**
	 * 
	 * @return
	 */
	private Map<MessageKind, Color> createColorMap() {
		final Map<MessageKind, Color> result = Maps.newHashMap();
		result.put(MessageKind.SPECIAL, Color.BLUE);
		result.put(MessageKind.NORMAL, Color.BLACK);
		result.put(MessageKind.WARNING, Color.RED);
		return result;
	}

	//

	@Override
	protected final void updateContent(final UserDisplayModel display) {
		this.displayArea.setText(display.getMessage());
		this.displayArea.setForeground(this.colorMap.get(display.getMessageKind()));
	}

}
