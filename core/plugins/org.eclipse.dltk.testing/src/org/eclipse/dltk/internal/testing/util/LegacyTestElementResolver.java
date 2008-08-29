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
package org.eclipse.dltk.internal.testing.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.internal.testing.MemberResolverManager;
import org.eclipse.dltk.internal.testing.model.TestElement;
import org.eclipse.dltk.internal.testing.model.TestRoot;
import org.eclipse.dltk.launching.ScriptLaunchConfigurationConstants;
import org.eclipse.dltk.testing.DLTKTestingConstants;
import org.eclipse.dltk.testing.ITestElementResolver;
import org.eclipse.dltk.testing.ITestingElementResolver;
import org.eclipse.dltk.testing.TestElementResolution;
import org.eclipse.dltk.testing.model.ITestCaseElement;
import org.eclipse.dltk.testing.model.ITestElement;

public class LegacyTestElementResolver implements ITestElementResolver {

	private final IScriptProject project;
	private final ILaunchConfiguration launchConfiguration;

	/**
	 * @param launchedProject
	 * @param launchConfiguration
	 */
	public LegacyTestElementResolver(IScriptProject project,
			ILaunchConfiguration configuration) {
		this.project = project;
		this.launchConfiguration = configuration;
	}

	public TestElementResolution resolveElement(ITestElement testElement) {
		final String engineId;
		try {
			engineId = launchConfiguration.getAttribute(
					DLTKTestingConstants.ATTR_ENGINE_ID, (String) null);
		} catch (CoreException e) {
			return null;
		}
		if (engineId == null) {
			return null;
		}
		final ITestingElementResolver resolver = MemberResolverManager
				.getResolver(engineId);
		if (resolver == null) {
			return null;
		}
		final ISourceModule module = resolveSourceModule();
		if (module == null) {
			return null;
		}
		final String relativeName = getRootRelativeName((TestElement) testElement);
		final IModelElement element = resolver.resolveElement(project,
				launchConfiguration, module, relativeName);
		if (element == null) {
			return null;
		}
		final String method = testElement instanceof ITestCaseElement ? ((ITestCaseElement) testElement)
				.getTestMethodName()
				: null;
		ISourceRange range = resolver.resolveRange(project,
				launchConfiguration, relativeName, module, element, method);
		return new TestElementResolution(element, range);
	}

	protected ISourceModule resolveSourceModule() {
		String scriptName;
		try {
			scriptName = launchConfiguration.getAttribute(
					ScriptLaunchConfigurationConstants.ATTR_MAIN_SCRIPT_NAME,
					(String) null);
		} catch (CoreException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
			return null;
		}
		IProject prj = project.getProject();
		IResource file = prj.findMember(new Path(scriptName));
		if (file instanceof IFile) {
			return (ISourceModule) DLTKCore.create(file);
		}
		return null;
	}

	private String getRootRelativeName(TestElement testCase) {
		String name = Util.EMPTY_STRING;
		TestElement el = testCase;
		while (el != null) {
			if (name.length() != 0) {
				name = el.getClassName() + "." + name;
			} else {
				name = el.getClassName();
			}
			el = el.getParent();
			if (el instanceof TestRoot) {
				break;
			}
		}
		if (name.startsWith(".")) {
			return name.substring(1);
		}
		return name;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

}
