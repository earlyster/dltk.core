/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
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

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.IParameter;

public class SourceMethodUtils {

	public static final IParameter[] NO_PARAMETERS = new IParameter[0];

	/**
	 * @param parameters
	 * @return
	 */
	public static String[] getParameterNames(IParameter[] parameters) {
		if (parameters.length == 0) {
			return CharOperation.NO_STRINGS;
		}
		final String[] names = new String[parameters.length];
		for (int i = 0, len = parameters.length; i < len; ++i) {
			names[i] = parameters[i].getName();
		}
		return names;
	}

}
