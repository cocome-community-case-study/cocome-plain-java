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

package org.cocome.tradingsystem.inventory.application.reporting;

import java.io.Serializable;

/**
 * Transfer object class for encapsulating report information in simple text
 * format.
 * 
 * @author Sebastian Herold
 * @author Lubomir Bulej
 */
public final class ReportTO implements Serializable {

	private static final long serialVersionUID = -2388667179057402191L;

	//

	private String reportText;

	/**
	 * empty constructor.
	 */
	public ReportTO() {}

	/**
	 * Returns the text of the report in HTML.
	 * 
	 * @return HTML report.
	 */
	public String getReportText() {
		return this.reportText;
	}

	/**
	 * Sets the text of the report.
	 * 
	 * @param reportText
	 *            the HTML report
	 */
	public void setReportText(final String reportText) {
		this.reportText = reportText;
	}

}
