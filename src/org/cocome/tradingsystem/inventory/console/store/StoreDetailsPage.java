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

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.cocome.tradingsystem.inventory.application.store.IStoreInventoryManager;
import org.cocome.tradingsystem.inventory.application.store.StoreWithEnterpriseTO;
import org.cocome.tradingsystem.util.RemoteComponent;

/**
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
final class StoreDetailsPage extends RefreshablePage<IStoreInventoryManager> {

	private final JLabel __storeIdLabel;
	private final JLabel __storeNameLabel;
	private final JLabel __storeLocationLabel;
	private final JLabel __enterpriseIdLabel;
	private final JLabel __enterpriseNameLabel;

	//

	private StoreWithEnterpriseTO __storeInfo;

	//

	public StoreDetailsPage(final RemoteComponent<IStoreInventoryManager> remoteStore) {
		super(remoteStore);

		//

		__storeIdLabel = new JLabel();
		__storeNameLabel = new JLabel();
		__storeLocationLabel = new JLabel();
		__enterpriseIdLabel = new JLabel();
		__enterpriseNameLabel = new JLabel();

		//

		_pageView = __createPageView();

		//

		refresh();
	}

	private JPanel __createPageView() {
		final GridLayout layout = new GridLayout(5, 2);
		final JPanel result = new JPanel(layout);

		result.add(new JLabel("Store ID: "));
		result.add(__storeIdLabel);

		result.add(new JLabel("Store Name: "));
		result.add(__storeNameLabel);

		result.add(new JLabel("Store Location: "));
		result.add(__storeLocationLabel);

		result.add(new JLabel("Enterprise ID: "));
		result.add(__enterpriseIdLabel);

		result.add(new JLabel("Enterprise Name: "));
		result.add(__enterpriseNameLabel);

		return result;
	}

	//

	@Override
	public void refresh() {
		//
		// Refresh the store description. Indicate outdated information if
		// the communications with the store fails.
		//
		try {
			final IStoreInventoryManager store = _remote.get();
			__storeInfo = store.getStore();

			__storeIdLabel.setText(Long.toString(__storeInfo.getId()));
			__storeNameLabel.setText(__storeInfo.getName());
			__storeLocationLabel.setText(__storeInfo.getLocation());
			__enterpriseIdLabel.setText(Long.toString(__storeInfo.getEnterpriseTO().getId()));
			__enterpriseNameLabel.setText(__storeInfo.getEnterpriseTO().getName());

		} catch (final Exception e) {
			__storeNameLabel.setText(__storeInfo.getName() + " (outdated)");
		}
	}

}
