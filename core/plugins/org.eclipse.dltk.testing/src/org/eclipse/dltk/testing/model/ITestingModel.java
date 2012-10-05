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
	 * @return a list of active {@link TestRunSession}s. The list is a copy of
	 *         the internal data structure and modifications do not affect the
	 *         global list of active sessions. The list is sorted by age,
	 *         youngest first.
	 */
	List<TestRunSession> getTestRunSessions();

	/**
	 * @param launch
	 * @return
	 */
	ITestRunSession getTestRunSession(ILaunch launch);

	/**
	 * Adds the given {@link TestRunSession} and notifies all registered
	 * {@link ITestRunSessionListener}s.
	 * <p>
	 * <b>To be called in the UI thread only!</b>
	 * </p>
	 * 
	 * @param testRunSession
	 *            the session to add
	 */
	void addTestRunSession(TestRunSession session);

	/**
	 * Removes the given {@link TestRunSession} and notifies all registered
	 * {@link ITestRunSessionListener}s.
	 * <p>
	 * <b>To be called in the UI thread only!</b>
	 * </p>
	 * 
	 * @param testRunSession
	 *            the session to remove
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
