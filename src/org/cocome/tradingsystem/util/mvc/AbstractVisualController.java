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

import javax.swing.JComponent;

import org.apache.log4j.Logger;

/**
 * Represents a base class for implementing a visual controller depending on
 * some model. The visual controller is responsible for providing a user
 * interface for interaction with a model (which typically includes a view of
 * the model).
 * 
 * @author Lubomir Bulej
 */
public abstract class AbstractVisualController<T extends AbstractModel<T>>
		implements IObserver<T> {

	private final Logger __log = Logger.getLogger(this.getClass());

	//

	/**
	 * Model visualized and managed by the visual controller. The model can only
	 * be set from outside using the {@link #setModel(AbstractModel)
	 * setModel()} methods.
	 */
	private T __model;

	protected JComponent viewComponent;

	//

	/**
	 * Sets the model to be visualized and managed by this controller. The
	 * controller registers itself with the model as an observer. If a model has
	 * been already set, the controller unregisters itself from the previous
	 * model and replaces the model with the specified one.
	 * 
	 * @param model
	 *            model to visualize and manage
	 */
	public final void setModel(final T model) {
		if (__model != null) {
			__log.debug(__model.getModelName() + " model changed!");
			__model.removeObserver(this);
		}

		__model = model;

		if (model != null) {
			model.addObserver(this);
			update(model);
		}
	}

	/**
	 * Returns a Swing component representing the visual controller's
	 * user interface. This should not be a Swing top-level component.
	 * 
	 * @return component representing the user interface
	 */
	public final JComponent getView() {
		return viewComponent;
	}

	//
	// Observer methods
	//

	@Override
	public void update(@SuppressWarnings("unused") T source) {
		// do nothing by default
	}

	//
	// Subclass interface
	//

	/**
	 * Returns the model associated with the visual controller.
	 * 
	 * @return model
	 */
	protected final T _model() {
		return __model;
	}

}
