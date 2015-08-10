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

package org.cocome.tradingsystem.cashdeskline.cashdesk.printer;

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.cocome.tradingsystem.util.mvc.ActiveVisualController;

/**
 * Implements the visual view/controller for the cash desk printer component.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
public final class VisualPrinter
		extends ActiveVisualController<PrinterModel> {

	private final JTextArea outputArea;

	//

	public VisualPrinter() {
		this.outputArea = this.createOutputTextArea();
		final JScrollPane outputPane = new JScrollPane(
				this.outputArea,
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

		// construct the view and lay out the components

		this.viewComponent = new JPanel(new BorderLayout());
		this.viewComponent.add(outputPane);
	}

	private JTextArea createOutputTextArea() {
		final JTextArea result = new JTextArea(5, 20);
		result.setFont(new Font(Font.MONOSPACED, Font.BOLD, 16));
		result.setLineWrap(true);
		result.setEditable(false);
		return result;
	}

	//

	@Override
	protected final void updateContent(final PrinterModel printer) {
		this.outputArea.setText(printer.getCurrentPrintout());
	}

}
