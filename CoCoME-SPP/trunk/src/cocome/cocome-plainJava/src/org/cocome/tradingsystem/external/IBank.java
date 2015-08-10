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

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface to the Bank
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
public interface IBank extends Remote {

	/**
	 * Validates and authenticates credit card information using the specified
	 * PIN and returns a transaction ID to use for operations on the card.
	 * 
	 * @param cardInfo
	 * @param pin
	 * @return
	 *         transaction ID associated with the (valid and authenticated)
	 *         creit card
	 */
	TransactionID validateCard(String cardInfo, int pin)
			throws RemoteException;

	/**
	 * Charges the credit card associated with the given transaction ID.
	 * <p>
	 * TODO It would be nice to make money participate in the operation :-)
	 * 
	 * @param tid
	 *            the transaction ID
	 * @return
	 *         result of the debit operation
	 * 
	 * @throws RemoteException
	 */
	DebitResult debitCard(TransactionID tid) throws RemoteException;

}
