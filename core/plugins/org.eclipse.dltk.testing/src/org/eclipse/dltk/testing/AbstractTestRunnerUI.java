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

import org.eclipse.dltk.testing.model.ITestCaseElement;
import org.eclipse.jface.action.IAction;

public abstract class AbstractTestRunnerUI implements ITestRunnerUI {

	public String filterStackTrace(String trace) {
		return trace;
	}

	public boolean isStackFrame(String line) {
		return false;
	}

	public IAction createOpenEditorAction(String traceLine) {
		return null;
	}

	/*
	 * @see ITestRunnerUI#getTestCaseLabel(ITestCaseElement)
	 */
	public String getTestCaseLabel(ITestCaseElement caseElement) {
		String testName = caseElement.getTestName();
		int index = testName.indexOf('(');
		if (index > 0)
			return testName.substring(0, index);
		index = testName.indexOf('@');
		if (index > 0)
			return testName.substring(0, index);
		return testName;
	}

	/*
	 * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
	 */
	public Object getAdapter(Class adapter) {
		return null;
	}

}
