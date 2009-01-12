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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.internal.testing.launcher.NullTestRunnerUI;
import org.eclipse.dltk.testing.model.ITestCaseElement;
import org.eclipse.dltk.testing.model.ITestElement;
import org.eclipse.dltk.testing.model.ITestRunSession;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;

/**
 * UI part of the testing engine implementation. New instances of this interface
 * are supposed to be created for each test session (=launch).
 * 
 * Instances of this interface are acquire via the call to the
 * {@link ITestingEngine#getTestRunnerUI(org.eclipse.dltk.core.IScriptProject, org.eclipse.debug.core.ILaunchConfiguration)}
 * 
 * The implementations should support adapting to {@link ITestElementResolver}
 */
public interface ITestRunnerUI extends IAdaptable {

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

	/**
	 * @param caseElement
	 * @return
	 */
	String getTestCaseLabel(ITestCaseElement caseElement, boolean full);

	/**
	 * @param caseElement
	 */
	String getTestStartedMessage(ITestCaseElement caseElement);

	/**
	 * @param testElement
	 * @return
	 */
	boolean canRerun(ITestElement testElement);

	/**
	 * Tests that this testing engine can filter the stack.
	 * 
	 * @return
	 */
	boolean canFilterStack();

	/**
	 * Filters the stack trace. This method is called only if filtering is
	 * enabled for this engine.
	 * 
	 * @param trace
	 * @return
	 */
	String filterStackTrace(String trace);

	/**
	 * Tests that stack filtering is enabled for this engine.
	 * 
	 * @return
	 */
	boolean isFilterStack();

	/**
	 * Sets the filtering value for this engine.
	 * 
	 * @param value
	 */
	void setFilterStack(boolean value);

	/**
	 * Returns the engine this UI is acquired from. Should not be
	 * <code>null</code> (at the moment only {@link NullTestRunnerUI} designed
	 * for compatibility issues returns <code>null</code> here).
	 */
	ITestingEngine getTestingEngine();

	/**
	 * Returns the project of the current launch. Could return <code>null</code>
	 * if the session was loaded from XML and there is no such project now.
	 */
	IScriptProject getProject();

	/**
	 * Tests that this testing engine can return failed tests.
	 */
	boolean canRerunFailures();

	/**
	 * @param allFailedTestElements
	 * @return
	 * @throws CoreException
	 */
	String collectFailures(ITestRunSession testRunSession) throws CoreException;
}
