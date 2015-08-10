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

package org.cocome.tradingsystem.cashdeskline.cashdesk;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import org.cocome.tradingsystem.cashdeskline.cashdesk.barcodescanner.VisualBarcodeScanner;
import org.cocome.tradingsystem.cashdeskline.cashdesk.cardreader.VisualCardReader;
import org.cocome.tradingsystem.cashdeskline.cashdesk.cashbox.VisualCashBox;
import org.cocome.tradingsystem.cashdeskline.cashdesk.expresslight.VisualExpressLight;
import org.cocome.tradingsystem.cashdeskline.cashdesk.printer.VisualPrinter;
import org.cocome.tradingsystem.cashdeskline.cashdesk.userdisplay.VisualUserDisplay;
import org.cocome.tradingsystem.util.mvc.AbstractVisualController;

/**
 * Implements the visual view/controller for the cash desk application.
 * 
 * @author Yannick Welsch
 * @author Lubomir Bulej
 */
final class VisualCashDesk extends AbstractVisualController<CashDeskModel> {

	//

	public VisualCashDesk(
			final VisualUserDisplay userDisplay,
			final VisualBarcodeScanner barcodeScanner,
			final VisualExpressLight expressLight,
			final VisualCardReader cardReader,
			final VisualPrinter printer,
			final VisualCashBox cashBox) {
		final JDesktopPane desktopPane = new JDesktopPane();

		// lay out the components on the desktop

		final int hGap = 10;
		final int vGap = 15;
		final int width = 380;
		final int height = 100;

		// 1st row: user display + barcode scanner (spans 2 rows)

		final int col1x = hGap;
		final int row1y = vGap;

		desktopPane.add(this.createInternalFrame(
				userDisplay, "User Display",
				col1x, row1y, width, height
				));

		//

		final int col2x = col1x + width + hGap;

		desktopPane.add(this.createInternalFrame(
				barcodeScanner, "Barcode Scanner",
				col2x, row1y, width, height
				));

		// 2nd row: express light + barcode scanner (spans 2 rows)

		final int row2y = row1y + height + vGap;

		desktopPane.add(this.createInternalFrame(
				expressLight, "Express Light",
				col1x, row2y, width, height
				));

		// 3rd row: card reader + cash box (spans 2 rows)

		final int row3y = row2y + height + vGap;
		final int readerHeight = 120;

		desktopPane.add(this.createInternalFrame(
				cardReader, "Card Reader",
				col1x, row3y, width, readerHeight
				));

		// 4th row: printer + cash box (spans 2 rows)

		final int row4y = row3y + readerHeight + vGap;
		final int printerHeight = 180;

		desktopPane.add(this.createInternalFrame(
				printer, "Printer",
				col1x, row4y, width, printerHeight
				));

		// cashbox

		final int cashBoxHeight = (row4y + printerHeight) - row2y;

		desktopPane.add(this.createInternalFrame(
				cashBox, "Cash Box",
				col2x, row2y, width, cashBoxHeight
				));

		//

		this.viewComponent = desktopPane;
	}

	/**
	 * Draw an internal frame.
	 * 
	 * @param component
	 * @param title
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	private JInternalFrame createInternalFrame(
			final AbstractVisualController<?> component, final String title,
			final int x, final int y, final int width, final int height
			) {
		final JInternalFrame result = new JInternalFrame(
				title, true, false, true, true
				);

		result.getContentPane().add(component.getView());

		//
		// Internal frames MUST have their location and size set, and must
		// be made visible explicitly. Fortunately, this does not make them
		// realized, because they are not top-level.
		//
		result.setLocation(x, y);
		if ((width != 0) && (height != 0)) {
			result.setSize(width, height);
		} else {
			result.pack();
		}

		result.setVisible(true);
		return result;
	}

}
