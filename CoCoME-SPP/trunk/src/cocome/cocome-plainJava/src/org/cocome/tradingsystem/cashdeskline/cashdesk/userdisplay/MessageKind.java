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

/**
 * Enumerates the kinds of messages that can be displayed by the cash desk user
 * display.
 * 
 * @author Lubomir Bulej
 */
public enum MessageKind {
	/**
	 * Special messages are meant for greetings, farewells, and other non-sale
	 * (status) messages.
	 */
	SPECIAL,

	/**
	 * Normal messages are meant for sale information.
	 */
	NORMAL,

	/**
	 * Warning messages are meant for abnormal conditions, such as invalid
	 * barcodes, failed credit card payments, etc.
	 */
	WARNING
}
