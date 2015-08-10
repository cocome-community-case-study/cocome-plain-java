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

import java.awt.Container;
import java.rmi.Remote;

import javax.swing.JComponent;
import javax.swing.JFrame;

import org.cocome.tradingsystem.util.RemoteComponent;

/**
 * Represents a refreshable page for the store console user interface. This
 * class does not expect any particular data model.
 * 
 * @author Lubomir Bulej
 */
abstract class RefreshablePage<T extends Remote> implements IRefreshable {

	protected RemoteComponent<T> _remote;
	protected JComponent _pageView;

	//

	public RefreshablePage(final RemoteComponent<T> remote) {
		_remote = remote;
	}

	//

	public final JComponent getView() {
		return _pageView;
	}

	//

	protected final JFrame _getParentFrame() {
		Container parent = _pageView.getParent();
		while (parent != null) {
			if (parent instanceof JFrame) {
				return (JFrame) parent;
			}

			parent = parent.getParent();
		}

		return null;
	}

}
