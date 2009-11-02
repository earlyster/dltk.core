/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.core;

import java.util.HashMap;

import org.eclipse.core.runtime.Assert;

/**
 * @since 2.0
 */
public class DuplicateResolver {

	public interface Resolver {
		void resolveDuplicates(SourceRefElement handle);
	}

	private static class Counter {
		int value;
	}

	/**
	 * The cache contains the maximum occurrence index per reference element
	 */
	@SuppressWarnings("serial")
	private static class DuplicateResolverImpl extends
			HashMap<SourceRefElement, Counter> implements Resolver {

		/**
		 * Resolves duplicate handles by incrementing the occurrence count of
		 * the handle being created until there is no conflict.
		 */
		public void resolveDuplicates(SourceRefElement handle) {
			Assert.isTrue(handle.occurrenceCount == 1);
			Counter counter = get(handle);
			if (counter == null) {
				counter = new Counter();
				counter.value = handle.occurrenceCount;
				put(handle, counter);
			} else {
				++counter.value;
				handle.occurrenceCount = counter.value;
			}
		}
	}

	public static Resolver create() {
		return new DuplicateResolverImpl();
	}

}
