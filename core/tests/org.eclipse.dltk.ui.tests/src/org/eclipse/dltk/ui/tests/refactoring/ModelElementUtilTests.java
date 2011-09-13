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
package org.eclipse.dltk.ui.tests.refactoring;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.tests.model.AbstractModelTests;
import org.eclipse.dltk.internal.corext.refactoring.util.ModelElementUtil;
import org.eclipse.dltk.ui.tests.DLTKUITestsPlugin;

public class ModelElementUtilTests extends AbstractModelTests {

	/**
	 * @param testProjectName
	 * @param name
	 */
	public ModelElementUtilTests(String name) {
		super(DLTKUITestsPlugin.PLUGIN_ID, name);
	}

	public void testGetPackageAndSubpackages() throws CoreException,
			IOException {
		final String projectName = "subpackages";
		setUpScriptProject(projectName);
		try {
			waitUntilIndexesReady();
			IScriptFolder folder0 = getScriptFolder(projectName,
					Util.EMPTY_STRING, new Path("folder0"));
			IScriptFolder[] subfolders0 = ModelElementUtil
					.getPackageAndSubpackages(folder0);
			checkFolders(subfolders0, new String[] { folder0.getElementName() });
			//
			IScriptFolder folder1 = getScriptFolder(projectName,
					Util.EMPTY_STRING, new Path("folder1"));
			IScriptFolder[] subfolders1 = ModelElementUtil
					.getPackageAndSubpackages(folder1);
			checkFolders(subfolders1, new String[] { folder1.getElementName(),
					folder1.getElementName() + "/A" });
			//
			IScriptFolder folder2 = getScriptFolder(projectName,
					Util.EMPTY_STRING, new Path("folder2"));
			IScriptFolder[] subfolders2 = ModelElementUtil
					.getPackageAndSubpackages(folder2);
			checkFolders(subfolders2, new String[] { folder2.getElementName(),
					folder2.getElementName() + "/B",
					folder2.getElementName() + "/C" });
		}
		finally {
			deleteProject(projectName);
		}
	}

	/**
	 * @param folders
	 * @param names
	 */
	private void checkFolders(IScriptFolder[] folders, String[] names) {
		assertEquals(names.length, folders.length);
		final Set set = new HashSet();
		for (int i = 0; i < folders.length; ++i) {
			final String relativePath = folders[i].getPath()
					.removeFirstSegments(1).toString();
			assertTrue("duplicate entry " + relativePath, set.add(relativePath));
		}
		for (int i = 0; i < names.length; ++i) {
			assertTrue(names[i] + " is not found", set.contains(names[i]));
		}
	}
}
