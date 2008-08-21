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
package org.eclipse.dltk.internal.testing.model;

public interface ITestRunnerClient {

	/**
	 * @return
	 */
	boolean isRunning();

	/**
	 * 
	 */
	void stopTest();

	/**
	 * 
	 */
	void stopWaiting();

	/**
	 * @param testId
	 * @param className
	 * @param testName
	 */
	void rerunTest(String testId, String className, String testName);

	/**
	 * @param listeners
	 */
	void startListening(ITestRunListener2[] listeners);

}
