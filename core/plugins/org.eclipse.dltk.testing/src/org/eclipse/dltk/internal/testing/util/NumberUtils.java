/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.testing.util;

public class NumberUtils {

	public static int toInt(final String strPriority) {
		if (strPriority != null && strPriority.length() != 0) {
			try {
				return Integer.parseInt(strPriority);
			} catch (NumberFormatException e) {
				// ignore
			}
		}
		return 0;
	}

}
