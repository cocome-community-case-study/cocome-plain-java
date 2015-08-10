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

package org.cocome.tradingsystem.util;

/**
 * Utility class for deriving RMI and JNDI names from component names.
 * 
 * @author Lubomir Bulej
 */
public final class Names {

	public static String getStoreName(final int storeIndex) {
		return String.format("Store%d", storeIndex);
	}

	public static String getCashDeskName(final int cashDeskIndex) {
		return String.format("CashDesk%d", cashDeskIndex);
	}

	//

	public static String getStoreTopicName(final String storeName) {
		return storeName;
	}

	public static String getCashDeskTopicName(final String cashDeskName, final String storeName) {
		return String.format("%s/%s", storeName, cashDeskName);
	}

	public static String getCashDeskTopicName(final int cashDeskIndex, final String storeName) {
		return getCashDeskTopicName(getCashDeskName(cashDeskIndex), storeName);
	}

	public static String getCashDeskTopicName(final int cashDeskIndex, final int storeIndex) {
		return getCashDeskTopicName(getCashDeskName(cashDeskIndex), getStoreName(storeIndex));
	}

	//

	public static String getBankRemoteName(final String bankName) {
		return String.format("Bank.%s", bankName);
	}

	public static String getStoreRemoteName(final String storeName) {
		return String.format("StoreApplication.%s", storeName);
	}

	public static String getReportingRemoteName(final String reportingName) {
		return String.format("ReportingApplication.%s", reportingName);
	}

	public static String getProductDispatcherRemoteName(final String dispatcherName) {
		return String.format("ProductDispatcher.%s", dispatcherName);
	}

}
