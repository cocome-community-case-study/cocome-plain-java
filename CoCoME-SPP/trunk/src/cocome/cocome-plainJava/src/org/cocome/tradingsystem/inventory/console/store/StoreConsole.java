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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.cocome.tradingsystem.inventory.application.store.IStoreInventoryManager;
import org.cocome.tradingsystem.util.RemoteComponent;
import org.cocome.tradingsystem.util.java.Lists;
import org.cocome.tradingsystem.util.java.Swing;

/**
 * Implements the graphical store console.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
final class StoreConsole extends JPanel {

	private final List<IRefreshable> __refreshables = Lists.newArrayList();

	//

	public StoreConsole(final RemoteComponent<IStoreInventoryManager> remoteStore) {
		super(new BorderLayout());

		//
		// Create a tabbed pane in which to display the items
		//
		final JTabbedPane consolePane = new JTabbedPane();
		consolePane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

		//
		// Add tabs to the tab pane and register the underlying models
		// as refreshable entities for the refresh button.
		//
		final StoreDetailsPage storeDetailsPage = new StoreDetailsPage(remoteStore);
		consolePane.addTab(
				"Store Details", null /* no icon */,
				storeDetailsPage.getView(), ""
				);
		__refreshables.add(storeDetailsPage);

		//

		final StoreProductsPage storeProductsPage = new StoreProductsPage(remoteStore);
		consolePane.addTab(
				"Products", null /* no icon */,
				storeProductsPage.getView(), ""
				);
		__refreshables.add(storeProductsPage);

		//

		final StoreStockItemsPage storeStockItemsPage = new StoreStockItemsPage(remoteStore);
		consolePane.addTab(
				"Stock Items", null /* no icon */,
				storeStockItemsPage.getView(), "Use Case 7"
				);
		__refreshables.add(storeStockItemsPage);

		//

		final StoreLowStockItemsPage lowStockItemsPage = new StoreLowStockItemsPage(remoteStore);
		consolePane.addTab(
				"Low-stock Products", null /* no icon */,
				lowStockItemsPage.getView(), "Use Case 3"
				);
		__refreshables.add(lowStockItemsPage);

		//

		final IssueOrdersPage issueOrdersPage = new IssueOrdersPage(remoteStore);
		consolePane.addTab(
				"Issue Orders", null /* no icon */,
				issueOrdersPage.getView(), "Use Case 3"
				);
		__refreshables.add(issueOrdersPage);

		//

		final ReceiveOrdersPage receiveOrdersPage = new ReceiveOrdersPage(remoteStore);
		consolePane.addTab(
				"Receive Orders", null /* no icon */,
				receiveOrdersPage.getView(), "Use Case 4"
				);
		__refreshables.add(receiveOrdersPage);

		// Add Refresh Button
		final JButton refreshButton = __createRefreshButton();
		add(refreshButton, BorderLayout.PAGE_START);

		// Add the tabbed pane to this panel.
		add(consolePane);
	}

	private JButton __createRefreshButton() {
		return Swing.createButton("Refresh", new ActionListener() {
			@Override
			public void actionPerformed(
					@SuppressWarnings("unused") final ActionEvent ae
					) {
				for (final IRefreshable refreshable : __refreshables) {
					refreshable.refresh();
				}
			}
		});
	}

	/**
	 * Create the user interface and show it. For thread safety, this method
	 * should be invoked from the event dispatch thread.
	 */
	static void createAndShowGUI(
			final String storeName,
			final RemoteComponent<IStoreInventoryManager> storeProvider
			) {
		// Create and set up the window.
		final JFrame frame = new JFrame("StoreConsole/" + storeName);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create and set up the content pane.
		frame.getContentPane().add(
				new StoreConsole(storeProvider), BorderLayout.CENTER
				);

		// Display the window.
		frame.pack();
		frame.setSize(640, 480);
		frame.setVisible(true);
	}

}
