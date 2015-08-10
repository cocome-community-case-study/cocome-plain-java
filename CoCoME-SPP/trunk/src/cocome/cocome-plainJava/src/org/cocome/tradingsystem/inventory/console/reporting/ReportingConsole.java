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

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.cocome.tradingsystem.inventory.application.reporting.IReporting;
import org.cocome.tradingsystem.util.RemoteComponent;

/**
 * Implements the graphical reporting console.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
final class ReportingConsole extends JPanel {

	public ReportingConsole(final RemoteComponent<IReporting> remoteReporting) {
		super(new BorderLayout());

		final JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		// add the different components
		tabbedPane.addTab(
				"Mean Time to Delivery", null /* no icon */,
				new MTDeliveryReport(remoteReporting),
				""
				);

		tabbedPane.setToolTipTextAt(0, "Use Case 6");

		//

		tabbedPane.addTab(
				"Store Stock Report", null /* no icon */,
				new StoreStockReport(remoteReporting),
				""
				);

		tabbedPane.setToolTipTextAt(1, "Use Case 5");

		//

		tabbedPane.addTab(
				"Enterprise Stock Report", null /* no icon */,
				new EnterpriseStockReport(remoteReporting),
				""
				);

		tabbedPane.setToolTipTextAt(2, "Use Case 5");

		// Add the tabbed pane to this panel.
		add(tabbedPane);
	}

	/**
	 * Create the user interface and show it. For thread safety, this method
	 * should be invoked from the event dispatch thread.
	 */
	static void createAndShowGUI(
			final String reportingName,
			final RemoteComponent<IReporting> remoteReporting
			) {
		// Create and set up the window.
		final JFrame frame = new JFrame("ReportingConsole/" + reportingName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		frame.getContentPane().add(
				new ReportingConsole(remoteReporting), BorderLayout.CENTER
				);

		// Display the window.
		frame.pack();
		frame.setSize(640, 480);
		frame.setVisible(true);
	}

}
