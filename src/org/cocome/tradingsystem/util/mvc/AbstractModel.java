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

package org.cocome.tradingsystem.util.mvc;

import java.util.List;

import org.cocome.tradingsystem.util.java.Lists;

/**
 * Represents a base class for implementing named observable models. It supports
 * observer management and change notification.
 * 
 * @author Lubomir Bulej
 */
public abstract class AbstractModel<T> implements IObservable<T> {

	private final List<IObserver<T>> __observers = Lists.newArrayList();

	//

	private final String __name;

	//

	public AbstractModel(final String name) {
		__name = name;
	}

	//

	public final String getModelName() {
		return __name;
	}

	//
	// Observable methods
	//

	public final void addObserver(final IObserver<T> observer) {
		synchronized (__observers) {
			__observers.add(observer);
		}
	}

	public final void removeObserver(final IObserver<T> observer) {
		synchronized (__observers) {
			__observers.remove(observer);
		}
	}

	//

	protected final void changedContent() {
		//
		// Create a copy of the currently registered observers and notify
		// the observers without holding a local lock.
		//
		final List<IObserver<T>> observers;
		synchronized (__observers) {
			observers = Lists.newArrayList(__observers);
		}

		//
		// The following cast is OK, because only observers of this
		// can be added using the addObserver() method.
		//
		@SuppressWarnings("unchecked")
		final T source = (T) this;
		for (final IObserver<T> observer : observers) {
			observer.update(source);
		}
	}

}
