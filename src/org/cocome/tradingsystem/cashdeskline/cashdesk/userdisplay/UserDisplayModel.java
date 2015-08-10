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

import javax.jms.Connection;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.cocome.tradingsystem.util.JmsHelper;
import org.cocome.tradingsystem.util.Names;
import org.cocome.tradingsystem.util.event.ObjectMessageListener;
import org.cocome.tradingsystem.util.mvc.AbstractModel;

/**
 * Implements the cash desk user display model.
 * 
 * @author Lubomir Bulej
 */
public final class UserDisplayModel
		extends AbstractModel<UserDisplayModel> implements IUserDisplay {

	private static final Logger LOG =
			Logger.getLogger(UserDisplayModel.class);

	private static final String COMPONENT_NAME = "User Display";

	//

	private String message = "Welcome!";
	private MessageKind messageKind = MessageKind.SPECIAL;

	//

	UserDisplayModel() {
		super(COMPONENT_NAME);
	}

	//
	// User display model methods
	//

	@Override
	public void setContent(final MessageKind messageKind, final String message) { // NOCS
		this.message = message;
		this.messageKind = messageKind;

		this.changedContent();
	}

	@Override
	public String getMessage() {
		return this.message;
	}

	@Override
	public MessageKind getMessageKind() {
		return this.messageKind;
	}

	//
	/**
	 * 
	 * @param cashDeskName
	 * @param storeName
	 * @param connection
	 * @return
	 */
	public static UserDisplayModel newInstance(
			final String cashDeskName, final String storeName,
			final Connection connection
			) {
		try {
			final String cashDeskTopicName =
					Names.getCashDeskTopicName(cashDeskName, storeName);

			//
			// Create a session for messages consumed by the user display (these
			// will originate in the JMS dispatcher thread) and register a
			// separate user display event handler as consumer in the session.
			//
			final UserDisplayModel display = new UserDisplayModel();
			final Session consumerSession = JmsHelper.createSession(connection);
			JmsHelper.registerConsumer(
					consumerSession, cashDeskTopicName,
					new ObjectMessageListener(new UserDisplayEventHandler(display))
					);

			return display;

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
