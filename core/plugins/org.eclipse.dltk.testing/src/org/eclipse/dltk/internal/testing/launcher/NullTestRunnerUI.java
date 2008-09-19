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
package org.eclipse.dltk.internal.testing.launcher;

import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.testing.AbstractTestRunnerUI;
import org.eclipse.dltk.testing.ITestRunnerUI;
import org.eclipse.dltk.testing.ITestingEngine;

public class NullTestRunnerUI extends AbstractTestRunnerUI {

	private NullTestRunnerUI() {
		// hidden constructor
	}

	private static ITestRunnerUI instance;

	public static ITestRunnerUI getInstance() {
		if (instance == null) {
			instance = new NullTestRunnerUI();
		}
		return instance;
	}

	/*
	 * @see org.eclipse.dltk.testing.ITestRunnerUI#getDisplayName()
	 */
	public String getDisplayName() {
		return null;
	}

	/*
	 * @see org.eclipse.dltk.testing.ITestRunnerUI#getProject()
	 */
	public IScriptProject getProject() {
		return null;
	}

	/*
	 * @see org.eclipse.dltk.testing.ITestRunnerUI#getTestingEngine()
	 */
	public ITestingEngine getTestingEngine() {
		return null;
	}

}
