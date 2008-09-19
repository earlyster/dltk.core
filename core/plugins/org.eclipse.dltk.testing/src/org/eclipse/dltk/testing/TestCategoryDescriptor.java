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
 * Test category descriptor.
 * 
 * Instances of these objects are acquired via calls to the
 * {@link ITestCategoryEngine#getCategory(String, String, boolean)}
 */
public class TestCategoryDescriptor {

	private final String id;
	private final String name;

	/**
	 * @param id
	 * @param name
	 */
	public TestCategoryDescriptor(String id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

}
