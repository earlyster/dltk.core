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
package org.eclipse.dltk.internal.debug.core.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.debug.core.model.IScriptBreakpoint;

/**
 * @author Alexey
 * 
 */
public class ScriptBreakpointUtils {

	/**
	 * Checks that {@link #getExpression()} is true and {@link #getExpression()}
	 * is not empty
	 * 
	 * @return
	 * @throws CoreException
	 */
	public static boolean isConditional(IScriptBreakpoint bp)
			throws CoreException {
		return bp.getExpressionState() && !StrUtils.isBlank(bp.getExpression());
	}

}
