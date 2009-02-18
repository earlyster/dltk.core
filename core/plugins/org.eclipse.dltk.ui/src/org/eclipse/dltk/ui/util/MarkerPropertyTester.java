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
package org.eclipse.dltk.ui.util;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;

public class MarkerPropertyTester extends PropertyTester {

	public boolean test(Object receiver, String property, Object[] args,
			Object expectedValue) {
		if (receiver instanceof IMarker) {
			if ("markerType".equals(property) && expectedValue instanceof String) { //$NON-NLS-1$
				try {
					return expectedValue.equals(((IMarker) receiver).getType());
				} catch (CoreException e) {
					return false;
				}
			}
		}
		return false;
	}
}
