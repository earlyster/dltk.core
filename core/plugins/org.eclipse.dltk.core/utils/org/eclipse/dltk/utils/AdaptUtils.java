/*******************************************************************************
 * Copyright (c) 2003, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.utils;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;

/**
 * Provides utilities for working with adaptable and non-adaptable objects.
 */
public class AdaptUtils {

	/**
	 * <p>
	 * Returns an adapter of the requested type (anAdapterType)
	 * 
	 * @param anElement
	 *            The element to adapt, which may or may not implement
	 *            {@link IAdaptable}, or null
	 * @param anAdapterType
	 *            The class type to return
	 * @return An adapter of the requested type or null
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getAdapter(Object anElement, Class<T> anAdapterType) {
		Assert.isNotNull(anAdapterType);
		if (anElement == null) {
			return null;
		}
		if (anAdapterType.isInstance(anElement)) {
			return (T) anElement;
		}
		if (anElement instanceof IAdaptable) {
			final IAdaptable adaptable = (IAdaptable) anElement;
			Object result = adaptable.getAdapter(anAdapterType);
			if (result != null) {
				// Sanity-check
				Assert.isTrue(anAdapterType.isInstance(result));
				return (T) result;
			}
		}
		if (!(anElement instanceof PlatformObject)) {
			Object result = Platform.getAdapterManager().getAdapter(anElement,
					anAdapterType);
			if (result != null) {
				return (T) result;
			}
		}
		return null;
	}

}
