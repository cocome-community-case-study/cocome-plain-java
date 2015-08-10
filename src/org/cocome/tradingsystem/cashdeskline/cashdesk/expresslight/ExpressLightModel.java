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

import javax.jms.Connection;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.cocome.tradingsystem.util.JmsHelper;
import org.cocome.tradingsystem.util.Names;
import org.cocome.tradingsystem.util.event.ObjectMessageListener;
import org.cocome.tradingsystem.util.mvc.AbstractModel;

/**
 * Implements the cash desk express light model.
 * 
 * @author Lubomir Bulej
 */
public final class ExpressLightModel
		extends AbstractModel<ExpressLightModel> implements IExpressLight {

	private static final Logger LOG =
			Logger.getLogger(ExpressLightModel.class);

	private static final String COMPONENT_NAME = "Express Light";

	//

	/** Enumerates the possible states of an express light. */
	private enum ExpressLightState {
		ON, OFF;
	}

	private ExpressLightState state;

	//

	public ExpressLightModel() {
		super(COMPONENT_NAME);

		this.turnExpressLightOff();
	}

	//
	// Express light model methods
	//

	@Override
	public void turnExpressLightOn() {
		this.setState(ExpressLightState.ON);
	}

	@Override
	public void turnExpressLightOff() {
		this.setState(ExpressLightState.OFF);
	}

	@Override
	public boolean isOn() {
		return this.getState() == ExpressLightState.ON;
	}

	//

	private synchronized void setState(final ExpressLightState newState) {
		if (this.state != newState) {
			this.state = newState;
			this.changedContent();
		}
	}

	private synchronized ExpressLightState getState() {
		return this.state;
	}

	//

	public static ExpressLightModel newInstance(
			final String cashDeskName, final String storeName,
			final Connection connection
			) {
		try {
			final String cashDeskTopicName =
					Names.getCashDeskTopicName(cashDeskName, storeName);

			//
			// Create a session for messages consumed by the express light
			// (these will originate in the JMS dispatcher thread) and register
			// the express light event handler as consumer in the session.
			//
			final ExpressLightModel expressLight = new ExpressLightModel();
			final Session consumerSession = JmsHelper.createSession(connection);
			JmsHelper.registerConsumer(
					consumerSession, cashDeskTopicName,
					new ObjectMessageListener(new ExpressLightEventHandler(expressLight))
					);

			return expressLight;

			// TODO fix catch all
		} catch (final Exception e) { // NOCS
			final String message = String.format(
					"Failed to initialize %s (%s, %s)",
					COMPONENT_NAME, cashDeskName, storeName
					);

			LOG.fatal(message, e);
			throw new RuntimeException(message, e);
		}
	}

}
