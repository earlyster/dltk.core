/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.tests.model;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.AbstractLanguageToolkit;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelStatus;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.internal.core.util.Messages;
import org.eclipse.osgi.util.NLS;

public class TestLanguageToolkit extends AbstractLanguageToolkit implements
		IDLTKLanguageToolkit {
	private static TestLanguageToolkit toolkit = new TestLanguageToolkit();

	private IStatus validateSourceModule(String name) {
		if (isScriptLikeFileName(name)) {
			return IModelStatus.VERIFIED_OK;
		}
		return new Status(IStatus.ERROR, "TEST", -1, NLS.bind(
				Messages.convention_unit_notScriptName, "txt", "Test"), null);
	}

	public boolean languageSupportZIPBuildpath() {
		return true;
	}

	public boolean validateSourcePackage(IPath path, IEnvironment env) {
		return true;
	}

	public String getNatureId() {
		return ModelTestsPlugin.TEST_NATURE;
	}

	public IStatus validateSourceModule(IResource resource) {
		return validateSourceModule(resource.getName());
	}

	public static IDLTKLanguageToolkit getDefault() {
		return toolkit;
	}

	public String getLanguageName() {
		return "Test";
	}

	private boolean isScriptLikeFileName(String name) {
		return name.endsWith(".txt");
	}

	public String getLanguageContentType() {
		return "org.eclipse.dltk.core.test.testContentType";
	}
}
