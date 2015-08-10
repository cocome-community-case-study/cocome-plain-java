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

import org.cocome.tradingsystem.cashdeskline.events.ExpressModeDisabledEvent;
import org.cocome.tradingsystem.cashdeskline.events.ExpressModeEnabledEvent;
import org.cocome.tradingsystem.util.event.AbstractSerializableEventDispatcher;

/**
 * Implements the cash desk event handler for the express light model. The event
 * handler is similar to a controller in that it converts incoming cash desk
 * messages to actions on the express light model. However, there is no view
 * associated with the controller.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
final class ExpressLightEventHandler
		extends AbstractSerializableEventDispatcher implements IExpressLightEventConsumer {

	private final ExpressLightModel expressLight;

	//

	public ExpressLightEventHandler(
			final ExpressLightModel expressLight) {
		super(expressLight.getModelName());

		this.expressLight = expressLight;
	}

	//
	// Event handler methods
	//

	@Override
	public void onEvent(
			@SuppressWarnings("unused") final ExpressModeEnabledEvent event
			) {
		this.expressLight.turnExpressLightOn();
	}

	@Override
	public void onEvent(
			@SuppressWarnings("unused") final ExpressModeDisabledEvent event
			) {
		this.expressLight.turnExpressLightOff();
	}

}
