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

package org.cocome.tradingsystem.cashdeskline.coordinator;

import java.util.Map;

import org.cocome.tradingsystem.cashdeskline.events.ExpressModeEnabledEvent;
import org.cocome.tradingsystem.util.JmsHelper.SessionBoundProducer;
import org.cocome.tradingsystem.util.java.Maps;
import org.cocome.tradingsystem.util.mvc.AbstractModel;

import org.apache.log4j.Logger;

/**
 * Implements the cash desk line coordinator model. The coordinator is
 * responsible for collecting sales information from all cash desks and
 * deciding in which mode (normal or express) they should operate.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
final class CoordinatorModel extends AbstractModel<CoordinatorModel> {

	private static final Logger __log__ =
			Logger.getLogger(CoordinatorModel.class);

	private static final String __COMPONENT_NAME__ = "Coordinator";

	//

	private final SessionBoundProducer __storeProducer;

	private final Map<String, ExpressModeEvaluator> __cashDeskEvaluators = Maps.newHashMap();

	//

	CoordinatorModel(final SessionBoundProducer storeProducer) {
		super(__COMPONENT_NAME__);

		__storeProducer = storeProducer;
	}

	//
	// Coordinator model methods
	//

	public void updateStatistics(final String cashDeskName, final Sale sale) {
		final ExpressModeEvaluator evaluator = __getCashDeskEvaluator(cashDeskName);

		evaluator.updateSalesHistory(sale);

		if (evaluator.isExpressModeNeeded()) {
			__switchToExpressMode(cashDeskName);
			__log__.info("Express mode activated for cash desk " + cashDeskName);
		}
	}

	private ExpressModeEvaluator __getCashDeskEvaluator(
			final String cashDeskName
			) {
		ExpressModeEvaluator result = __cashDeskEvaluators.get(cashDeskName);
		if (result == null) {
			result = new ExpressModeEvaluator();
			__cashDeskEvaluators.put(cashDeskName, result);
		}

		return result;
	}

	private void __switchToExpressMode(final String cashDeskName) {
		__storeProducer.sendAsync(new ExpressModeEnabledEvent(cashDeskName));
	}

}
