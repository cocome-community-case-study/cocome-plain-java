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

package org.cocome.tradingsystem.external;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * Extremely trivial bank implementation.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
class TrivialBankServer extends UnicastRemoteObject implements IBank {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3110800105669449581L;

	// TODO Consider making the credit card details externally configurable
	private static final String CREDIT_CARD_INFO = "1234";

	private static final int CREDIT_CARD_PIN = 7777;

	private static final TransactionID CREDIT_CARD_TXID = new TransactionID(1234L);

	/**
	 * Construct a new bank server.
	 * 
	 * @throws RemoteException
	 */
	public TrivialBankServer() throws RemoteException {
		super();
	}

	// @Override
	public TransactionID validateCard(final String cardInfo, final int cardPin) {
		// final IMonitoringRecord record = new OperationExecutionRecord("validateCard", "session", 0L, 0L, 0L, "string", 0, 0);
		if (cardInfo.equals(CREDIT_CARD_INFO) && (cardPin == CREDIT_CARD_PIN)) {
			// BankLauncherMain.KIEKER.newMonitoringRecord(record);
			return CREDIT_CARD_TXID;
		} else {
			// BankLauncherMain.KIEKER.newMonitoringRecord(record);
			return null;
		}
	}

	// @Override
	public DebitResult debitCard(final TransactionID id) {
		// final IMonitoringRecord record = new OperationExecutionRecord("debitCard", "session", 0L, 0L, 0L, "string", 0, 0);
		if (!id.equals(CREDIT_CARD_TXID)) {
			// BankLauncherMain.KIEKER.newMonitoringRecord(record);
			return DebitResult.INVALID_TRANSACTION_ID;
		} else if (Math.random() > 0.5) {
			// BankLauncherMain.KIEKER.newMonitoringRecord(record);
			return DebitResult.INSUFFICIENT_BALANCE;
		} else {
			// BankLauncherMain.KIEKER.newMonitoringRecord(record);
			return DebitResult.OK;
		}
	}

}
