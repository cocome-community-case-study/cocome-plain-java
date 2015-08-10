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

import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.table.AbstractTableModel;

import org.apache.log4j.Logger;

/**
 * Represents an abstract handler based table model. This class provides a
 * generic overlay over the {@link AbstractHandlerTableModel} class that allows
 * creating table models using {@link ColumnHandler} callback objects which
 * encapsulate cell behavior logic.
 * 
 * @author Lubomir Bulej
 * @author Yannick Welsch
 */
abstract class AbstractHandlerTableModel<E>
		extends AbstractTableModel implements IRefreshable {

	/**
	 * Handles tasks associated with a table column. Responds to queries for
	 * column name and provides access to the appropriate value of a table row.
	 * Determines whether a column is editable or immutable and for editable
	 * columns, it allows setting a cell value.
	 */
	abstract static class ColumnHandler<T> {
		private final String __name;

		ColumnHandler(final String name) {
			__name = name;
		}

		final String name() {
			return __name;
		}

		abstract boolean isEditable();

		abstract Object getValue(T row);

		abstract boolean setValue(final T row, final String value);
	};

	/**
	 * Represents an immutable table column. The cells in such a column cannot
	 * be edited and the subclasses of this class only need to provide read-only
	 * access to cell values.
	 */
	abstract static class ImmutableColumnHandler<T> extends ColumnHandler<T> {

		ImmutableColumnHandler(final String name) {
			super(name);
		}

		@Override
		final boolean isEditable() {
			return false;
		}

		@Override
		@SuppressWarnings("unused")
		final boolean setValue(final T row, final String value) {
			throw new UnsupportedOperationException(
					"Editing not supported for column " + name());
		}

	};

	/**
	 * Represents an editable table column. The cells in such a column can be
	 * edited and the subclasses of this class need to provide read-write access
	 * to cell values.
	 */
	abstract static class EditableColumnHandler<T> extends ColumnHandler<T> {

		EditableColumnHandler(final String name) {
			super(name);
		}

		@Override
		final boolean isEditable() {
			return true;
		}

	};

	//

	private static final Logger __log__ = Logger.getLogger(StoreConsole.class);

	/**
	 * Holds the rows of this table.
	 * <p>
	 * This attribute <strong>must be never accessed directly</strong>, only through the {@link #getRows()} method.
	 */
	private List<E> __rows;

	/**
	 * Synchronizes access to the {@link #__rows} field during lazy
	 * initialization.
	 */
	private ReentrantLock __rowsLock = new ReentrantLock();

	/**
	 * Handlers for table columns.
	 */
	private final ColumnHandler<E>[] __columnHandlers;

	//

	public AbstractHandlerTableModel(final ColumnHandler<E>[] columnHandlers) {
		__columnHandlers = columnHandlers;
	}

	//
	// AbstractTableModel methods
	//

	@Override
	public final int getRowCount() {
		return getRows().size();
	}

	@Override
	public final int getColumnCount() {
		return __columnHandlers.length;
	}

	@Override
	public final String getColumnName(final int columnIndex) {
		return __columnHandlers[columnIndex].name();
	}

	@Override
	public final Object getValueAt(final int rowIndex, final int columnIndex) {
		final E row = getRows().get(rowIndex);
		return __columnHandlers[columnIndex].getValue(row);
	}

	@Override
	public final boolean isCellEditable(
			@SuppressWarnings("unused") final int rowIndex,
			final int columnIndex
			) {
		// we only distinguish between editable columns, not rows
		return __columnHandlers[columnIndex].isEditable();
	}

	@Override
	public final void setValueAt(
			final Object value, final int rowIndex, final int columnIndex
			) {
		//
		// Ignore other than String values and notify the listeners of a
		// changed cell if the update succeeds.
		//
		if (value instanceof String) {
			final String stringValue = (String) value;

			final E row = getRows().get(rowIndex);
			final boolean changed = __columnHandlers[columnIndex].setValue(row, stringValue);
			if (changed) {
				fireTableCellUpdated(rowIndex, columnIndex);
			}
		}
	}

	//
	// Refreshable methods
	//

	/**
	 * Forces refresh of data backing the table.
	 * 
	 * @see IRefreshable#refresh()
	 */
	@Override
	public void refresh() {
		// Invalidate the row data and the table, forcing a new fetch.
		_invalidateRows();
		fireTableDataChanged();
	}

	//

	/**
	 * Invalidates the row data.
	 */
	protected final void _invalidateRows() {
		__rowsLock.lock();
		__rows = null;
		__rowsLock.unlock();
	}

	/**
	 * Returns the rows of this table. The rows are fetched lazily, meaning that
	 * if we have no products yet, we ask the subclass to fetch them for us.
	 * <p>
	 * Users should use this method to access the row data if necessary.
	 * 
	 * @return
	 *         List of row data items.
	 */
	final List<E> getRows() {
		__rowsLock.lock();
		try {
			if (__rows == null) {
				__fetchRows();
			}

			return __rows;

		} finally {
			__rowsLock.unlock();
		}
	}

	/**
	 * Unconditionally initializes the {@link #__rows} field with a
	 * list of rows provided a by subclass of this class. If the subclass
	 * throws an exception while fetching the rows, and empty list is used to
	 * initialize the {@link #__rows} field.
	 * <p>
	 * The {@link #__rowsLock} lock must be held when calling this function.
	 */
	private void __fetchRows() {
		assert __rowsLock.isHeldByCurrentThread();

		try {
			__rows = _fetchRows();

		} catch (final Exception e) {
			__log__.error("Failed to fetch rows", e);

			// Return an empty list when fetching the rows failed
			__rows = Collections.emptyList();

		}
	}

	/**
	 * Fetches row elements for the table.
	 * <p>
	 * <strong>This method must be implemented by subclasses.</strong>
	 * 
	 * @return
	 *         List of row element for the table, must not return {@code null}.
	 * 
	 * @throws Exception
	 *             if fetching the rows failed
	 */
	protected abstract List<E> _fetchRows() throws Exception;

}
