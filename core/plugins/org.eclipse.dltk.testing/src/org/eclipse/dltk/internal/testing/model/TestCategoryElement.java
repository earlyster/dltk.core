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

import org.eclipse.dltk.testing.model.ITestCategoryElement;

public class TestCategoryElement extends TestContainerElement implements
		ITestCategoryElement {

	/**
	 * @param parent
	 * @param id
	 * @param testName
	 */
	public TestCategoryElement(TestContainerElement parent, String id,
			String testName) {
		super(parent, id, testName);
	}

	public String getCategoryName() {
		return getTestName();
	}

}
