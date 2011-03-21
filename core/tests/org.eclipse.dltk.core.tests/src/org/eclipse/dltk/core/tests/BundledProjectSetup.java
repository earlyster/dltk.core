/*******************************************************************************
 * Copyright (c) 2011 NumberFour AG
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.core.tests;

import junit.extensions.TestSetup;
import junit.framework.Test;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.dltk.core.tests.model.AbstractModelTests;

/**
 * A decorator for tests to initialize workspace project before executing the
 * tests. Use as follows:
 * 
 * <pre>
 * suite.addTest(new BundledProjectSetup(MyPluginActivator.PLUGIN_ID, &quot;myProject&quot;,
 * 		new TestSuite(MyTests.class)));
 * </pre>
 * 
 * where <code>myProject</code> is the folder with the preconfigured project
 * located at <code>bundleRoot/workspace/myProject</code>
 */
public class BundledProjectSetup extends TestSetup {

	static class Helper extends AbstractModelTests {

		public Helper(String bundleName) {
			super(bundleName, BundledProjectSetup.class.getName());
		}

	}

	private final Helper helper;
	private final String projectName;
	private final boolean build;

	public BundledProjectSetup(String bundleName, String projectName, Test test) {
		this(bundleName, projectName, test, false);
	}

	public BundledProjectSetup(String bundleName, String projectName,
			Test test, boolean build) {
		super(test);
		this.helper = new Helper(bundleName);
		this.projectName = projectName;
		this.build = build;
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		AbstractModelTests.disableAutoBulid();
		helper.setUpProject(projectName);
		if (build) {
			getProject().build(IncrementalProjectBuilder.FULL_BUILD, null);
		}
	}

	@Override
	protected void tearDown() throws Exception {
		helper.deleteProject(projectName);
		super.tearDown();
	}

	protected IProject getProject() {
		return AbstractModelTests.getProject(projectName);
	}

}
