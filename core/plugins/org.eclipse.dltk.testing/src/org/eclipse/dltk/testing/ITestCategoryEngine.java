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

/**
 * Test category engine. New instances of this interface are created for each
 * test run. It used only during test launch and building internal data model.
 * After the test launch is completed all internal data structures are built and
 * implementations of this interface are not used anymore.
 */
public interface ITestCategoryEngine {

	/**
	 * Initializes this category engine. Returns <code>true</code> if this
	 * engine should be used for the specified launch or <code>false</code>
	 * otherwise.
	 * 
	 * @param runnerUI
	 * @return
	 */
	boolean initialize(ITestRunnerUI runnerUI);

	/**
	 * Returns the array of categories to be placed in the test tree initially.
	 * This method could return <code>null</code> if it does not require initial
	 * categories to be added to the test tree.
	 * 
	 * @return
	 */
	TestCategoryDescriptor[] getInitialCategories();

	/**
	 * Returns the category this test should be placed in or <code>null</code>.
	 * 
	 * @param id
	 * @param name
	 * @param isSuite
	 * @return
	 */
	TestCategoryDescriptor getCategory(String id, String name, boolean isSuite);

}
