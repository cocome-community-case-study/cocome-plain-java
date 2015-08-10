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

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collection;
import java.util.Formatter;

import org.cocome.tradingsystem.inventory.data.DataFactory;
import org.cocome.tradingsystem.inventory.data.enterprise.IEnterpriseQuery;
import org.cocome.tradingsystem.inventory.data.enterprise.ProductSupplier;
import org.cocome.tradingsystem.inventory.data.enterprise.TradingEnterprise;
import org.cocome.tradingsystem.inventory.data.persistence.IPersistenceContext;
import org.cocome.tradingsystem.inventory.data.persistence.TransactionWrapper;
import org.cocome.tradingsystem.inventory.data.persistence.TransactionWrapper.Operation;
import org.cocome.tradingsystem.inventory.data.store.IStoreQuery;
import org.cocome.tradingsystem.inventory.data.store.StockItem;
import org.cocome.tradingsystem.inventory.data.store.Store;

/**
 * Implements the {@link IReporting} interface used by the reporting console to
 * realize UC 5 and UC 6.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
final class ReportingServer extends UnicastRemoteObject implements IReporting {

	private static final long serialVersionUID = -1393595223298557552L;

	//

	private final IStoreQuery storeQuery =
			DataFactory.getInstance().getStoreQuery();

	private final IEnterpriseQuery enterpriseQuery =
			DataFactory.getInstance().getEnterpriseQuery();

	//

	public ReportingServer() throws RemoteException {
		super();
	}

	/**
	 * Used for implementation of UC 6:ShowDeliveryReports.
	 * 
	 * @param enterpriseId
	 *            enterpise id for the report
	 * @returns a report
	 */
	@Override
	public ReportTO getEnterpriseDeliveryReport(final long enterpriseId) {
		return TransactionWrapper.execute(new Operation<ReportTO>() {
			@Override
			public ReportTO execute(final IPersistenceContext pctx) {
				final TradingEnterprise enterprise = ReportingServer.this.enterpriseQuery.
						queryEnterpriseById(enterpriseId, pctx);

				//

				final Formatter report = new Formatter();
				ReportingServer.this.appendReportHeader(report);
				ReportingServer.this.appendDeliveryReport(enterprise, pctx, report);
				ReportingServer.this.appendReportFooter(report);

				//

				return ReportingServer.this.createReportTO(report);
			}
		});
	}

	/**
	 * Used for implementation of UC 5:ShowStockReports.
	 * 
	 * @param storeId
	 *            id of the store
	 * @return retruns a store report
	 */
	@Override
	public ReportTO getStoreStockReport(final long storeId) {
		return TransactionWrapper.execute(new Operation<ReportTO>() {
			@Override
			public ReportTO execute(final IPersistenceContext pctx) {
				final Store store = ReportingServer.this.storeQuery.queryStoreById(storeId, pctx);

				//

				final Formatter report = new Formatter();
				ReportingServer.this.appendReportHeader(report);
				ReportingServer.this.appendStoreReport(store, pctx, report);
				ReportingServer.this.appendReportFooter(report);

				//

				return ReportingServer.this.createReportTO(report);
			}
		});
	}

	@Override
	public ReportTO getEnterpriseStockReport(final long enterpriseId) {
		return TransactionWrapper.execute(new Operation<ReportTO>() {
			@Override
			public ReportTO execute(final IPersistenceContext pctx) {
				final TradingEnterprise enterprise = ReportingServer.this.enterpriseQuery.
						queryEnterpriseById(enterpriseId, pctx);

				//

				final Formatter report = new Formatter();
				ReportingServer.this.appendReportHeader(report);
				ReportingServer.this.appendEnterpriseReport(enterprise, pctx, report);
				ReportingServer.this.appendReportFooter(report);

				//

				return ReportingServer.this.createReportTO(report);
			}
		});
	}

	//

	private void appendDeliveryReport(
			final TradingEnterprise enterprise,
			final IPersistenceContext pctx, final Formatter output
			) {
		this.appendTableHeader(output,
				"Supplier ID", "Supplier Name", "Mean Time To Delivery");

		for (final ProductSupplier supplier : enterprise.getSuppliers()) {
			final long mtd = this.enterpriseQuery.
					getMeanTimeToDelivery(supplier, enterprise, pctx);

			this.appendTableRow(output,
					supplier.getId(), supplier.getName(), (mtd != 0) ? mtd : "N/A"); // NOCS
		}

		this.appendTableFooter(output);
	}

	private void appendStoreReport(
			final Store store, final IPersistenceContext pctx, final Formatter output
			) {
		output.format(
				"<h3>Report for %s at %s, id %d</h3>\n",
				store.getName(), store.getLocation(), store.getId()
				);

		this.appendTableHeader(output,
				"StockItem ID", "Product Name",
				"Amount", "Min Stock", "Max Stock");

		//

		final Collection<StockItem> stockItems = this.storeQuery.
				queryAllStockItems(store.getId(), pctx);

		for (final StockItem si : stockItems) {
			this.appendTableRow(output,
					si.getId(), si.getProduct().getName(),
					si.getAmount(), si.getMinStock(), si.getMaxStock());
		}

		this.appendTableFooter(output);
	}

	private void appendEnterpriseReport(
			final TradingEnterprise enterprise, final IPersistenceContext pctx,
			final Formatter output
			) {
		output.format(
				"<h2>Stock report for %s</h2>\n", enterprise.getName()
				);

		for (final Store store : enterprise.getStores()) {
			this.appendStoreReport(store, pctx, output);
		}
	}

	//

	private ReportTO createReportTO(final Formatter report) {
		final ReportTO result = new ReportTO();
		result.setReportText(report.toString());
		return result;
	}

	private Formatter appendReportFooter(final Formatter output) {
		return output.format("</body></html>\n");
	}

	private Formatter appendReportHeader(final Formatter output) {
		return output.format("<html><body>\n");
	}

	private void appendTableHeader(
			final Formatter output, final String... names
			) {
		output.format("<table>\n<tr>");
		for (final String name : names) {
			output.format("<th>%s</th>", name);
		}
		output.format("</tr>\n");
	}

	private void appendTableRow(
			final Formatter output, final Object... values
			) {
		output.format("<tr>");
		for (final Object value : values) {
			output.format("<td>%s</td>", value);
		}
		output.format("</tr>\n");
	}

	private void appendTableFooter(final Formatter output) {
		output.format("</table><br/>\n");
	}

}
