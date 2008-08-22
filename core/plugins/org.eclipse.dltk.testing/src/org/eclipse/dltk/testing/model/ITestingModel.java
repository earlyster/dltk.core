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
package org.eclipse.dltk.testing.model;

import java.util.List;

import org.eclipse.debug.core.ILaunch;
import org.eclipse.dltk.internal.testing.model.ITestRunSessionListener;
import org.eclipse.dltk.internal.testing.model.TestRunSession;

public interface ITestingModel {

	/**
	 * @return
	 */
	List getTestRunSessions();

	/**
	 * @param launch
	 * @return
	 */
	ITestRunSession getTestRunSession(ILaunch launch);

	/**
	 * @param session
	 */
	void addTestRunSession(TestRunSession session);

	/**
	 * @param next
	 */
	void removeTestRunSession(TestRunSession session);

	/**
	 * 
	 */
	void start();

	/**
	 * @param listener
	 */
	void addTestRunSessionListener(ITestRunSessionListener listener);

	/**
	 * @param listener
	 */
	void removeTestRunSessionListener(ITestRunSessionListener listener);

}
