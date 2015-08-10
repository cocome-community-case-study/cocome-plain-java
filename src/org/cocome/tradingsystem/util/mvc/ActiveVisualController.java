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

package org.cocome.tradingsystem.util.mvc;

import javax.swing.SwingUtilities;

/**
 * Implements a visual controller intended for use with models that change their
 * state which is visible in the view.
 * 
 * @author Lubomir Bulej
 */
public abstract class ActiveVisualController<T extends AbstractModel<T>>
		extends AbstractVisualController<T> {

	@Override
	public final void update(final T source) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				ActiveVisualController.this.update(source);
			}
		});
	}

	/**
	 * Provides a subclass interface for executing code that manipulates
	 * with user interface components in the context of the Swing event
	 * dispatcher thread. Subclasses must implement this method.
	 * 
	 * @param source
	 *            the model which triggered the update
	 */
	protected abstract void updateContent(final T source);

}
