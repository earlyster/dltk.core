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
package org.eclipse.dltk.testing;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

public interface ITestRunnerUI {

	/**
	 * Filters the stack trace if filtering is enabled for this engine.
	 * 
	 * @param trace
	 * @return
	 */
	String filterStackTrace(String trace);

	/**
	 * Tests that the specified line looks like a stack frame
	 * 
	 * @param line
	 * @return
	 */
	boolean isStackFrame(String line);

	/**
	 * Creates the action to open the editor by the specified stack frame line.
	 * Returns {@link Action} of <code>null</code>.
	 * 
	 * @param line
	 * @return the action created or <code>null</code> if the specified line
	 *         does not look like a stack frame.
	 */
	IAction createOpenEditorAction(String line);

	/**
	 * @return
	 */
	String getDisplayName();

}
