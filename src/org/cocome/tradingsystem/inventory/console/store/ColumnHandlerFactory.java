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

package org.cocome.tradingsystem.inventory.console.store;

import org.cocome.tradingsystem.inventory.application.store.ComplexOrderTO;
import org.cocome.tradingsystem.inventory.application.store.ProductTO;
import org.cocome.tradingsystem.inventory.application.store.ProductWithSupplierTO;
import org.cocome.tradingsystem.inventory.application.store.StockItemTO;
import org.cocome.tradingsystem.inventory.application.store.IStockItemTOAccessor;
import org.cocome.tradingsystem.inventory.application.store.IStoreInventoryManager;
import org.cocome.tradingsystem.inventory.console.store.AbstractHandlerTableModel.ColumnHandler;
import org.cocome.tradingsystem.inventory.console.store.AbstractHandlerTableModel.EditableColumnHandler;
import org.cocome.tradingsystem.inventory.console.store.AbstractHandlerTableModel.ImmutableColumnHandler;
import org.cocome.tradingsystem.util.RemoteComponent;

import org.apache.log4j.Logger;

/**
 * Provide a factory for column handlers used in the store user interface table
 * models. This is a utility class intended to avoid code duplication when
 * creating column handlers for various transfer objects in the table models.
 * 
 * @author Lubomir Bulej
 */
final class ColumnHandlerFactory {

	private ColumnHandlerFactory() {
		// utility class, not to be instantiated
	}

	//

	static <T extends ProductTO> ColumnHandler<T> immutableProductId() {
		return new ImmutableColumnHandler<T>("Product ID") {
			@Override
			Object getValue(final T productTO) {
				return productTO.getId();
			}
		};
	}

	static <T extends ProductTO> ColumnHandler<T> immutableProductName() {
		return new ImmutableColumnHandler<T>("Product Name") {
			@Override
			Object getValue(final T productTO) {
				return productTO.getName();
			}
		};
	}

	static <T extends ProductTO> ColumnHandler<T> immutableProductBarcode() {
		return new ImmutableColumnHandler<T>("Product Barcode") {
			@Override
			Object getValue(final T productTO) {
				return productTO.getBarcode();
			}
		};
	}

	static <T extends ProductTO> ColumnHandler<T> immutableProductPurchasePrice() {
		return new ImmutableColumnHandler<T>("Product Purchase Price") {
			@Override
			Object getValue(final T productTO) {
				return productTO.getPurchasePrice();
			}
		};
	}

	//

	static <T extends ComplexOrderTO> ColumnHandler<T> immutableOrderId() {
		return new ImmutableColumnHandler<T>("Order ID") {
			@Override
			Object getValue(final T complexOrderTO) {
				return complexOrderTO.getId();
			}
		};
	}

	static <T extends ComplexOrderTO> ColumnHandler<T> immutableOrderEntryCount() {
		return new ImmutableColumnHandler<T>("Entry Count") {
			@Override
			Object getValue(final T complexOrderTO) {
				return complexOrderTO.getOrderEntryTOs().size();
			}
		};
	}

	static <T extends ComplexOrderTO> ColumnHandler<T> immutableOrderDateOrdered() {
		return new ImmutableColumnHandler<T>("Date Ordered") {
			@Override
			Object getValue(final T complexOrderTO) {
				return complexOrderTO.getOrderingDate();
			}
		};
	}

	static <T extends ComplexOrderTO> ColumnHandler<T> immutableOrderDateDelivered() {
		return new ImmutableColumnHandler<T>("Date Delivered") {
			@Override
			Object getValue(final T complexOrderTO) {
				return complexOrderTO.getDeliveryDate();
			}
		};
	}

	//

	static <T extends ProductWithSupplierTO> ColumnHandler<T> immutableSupplierId() {
		return new ImmutableColumnHandler<T>("Supplier ID") {
			@Override
			Object getValue(final T productWithSupplierTO) {
				return productWithSupplierTO.getSupplierTO().getId();
			}
		};
	}

	static <T extends ProductWithSupplierTO> ColumnHandler<T> immutableSupplierName() {
		return new ImmutableColumnHandler<T>("Supplier Name") {
			@Override
			Object getValue(final T productWithSupplierTO) {
				return productWithSupplierTO.getSupplierTO().getName();
			}
		};
	}

	//

	static <T extends IStockItemTOAccessor> ColumnHandler<T> immutableStockItemId() {
		return new ImmutableColumnHandler<T>("Stock Item ID") {
			@Override
			Object getValue(final T stockItemTOAccessor) {
				return stockItemTOAccessor.getStockItemTO().getId();
			}
		};
	}

	static <T extends IStockItemTOAccessor> ColumnHandler<T> immutableStockItemAmount() {
		return new ImmutableColumnHandler<T>("StockItem Amount") {
			@Override
			Object getValue(final T stockItemTOAccessor) {
				return stockItemTOAccessor.getStockItemTO().getAmount();
			}
		};
	}

	static <T extends IStockItemTOAccessor> ColumnHandler<T> immutableStockItemMinStock() {
		return new ImmutableColumnHandler<T>("Stock Item Min Stock") {
			@Override
			Object getValue(final T stockItemTOAccessor) {
				return stockItemTOAccessor.getStockItemTO().getMinStock();
			}
		};
	}

	static <T extends IStockItemTOAccessor> ColumnHandler<T> immutableStockItemMaxStock() {
		return new ImmutableColumnHandler<T>("StockItem Max Stock") {
			@Override
			Object getValue(final T stockItemTOAccessor) {
				return stockItemTOAccessor.getStockItemTO().getMaxStock();
			}
		};
	}

	static <T extends IStockItemTOAccessor> ColumnHandler<T> editableStockItemSalesPrice(
			final RemoteComponent<IStoreInventoryManager> remoteStore, final Logger log
			) {
		return new EditableColumnHandler<T>("Stock Item Sales Price") {
			@Override
			Object getValue(final T accessor) {
				return accessor.getStockItemTO().getSalesPrice();
			}

			@Override
			boolean setValue(final T accessor, final String value) {
				try {
					final double price = Double.parseDouble(value);
					final StockItemTO stockItemTO = accessor.getStockItemTO();
					if (stockItemTO != null) {
						return __changeStockItemSalesPrice(stockItemTO, price);
					}

				} catch (final NumberFormatException e) {
					log.debug("Invalid new stock item price", e);
					// do nothing
				}

				return false;
			}

			private boolean __changeStockItemSalesPrice(
					final StockItemTO stockItemTO, final double newPrice
					) {
				final double oldPrice = stockItemTO.getSalesPrice();

				try {
					if (newPrice != oldPrice) {
						stockItemTO.setSalesPrice(newPrice);
						remoteStore.get().changePrice(stockItemTO);
						return true;
					}

				} catch (final Exception e) {
					log.error("Failed to change stock item price", e);

					// Revert to old price if changing price failed
					stockItemTO.setSalesPrice(oldPrice);
				}

				return false;
			}
		};
	}

}
