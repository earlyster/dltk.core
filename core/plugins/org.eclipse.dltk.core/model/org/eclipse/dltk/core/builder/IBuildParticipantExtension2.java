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
package org.eclipse.dltk.core.builder;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.builder.IScriptBuilder.DependencyResponse;

public interface IBuildParticipantExtension2 extends IBuildParticipantExtension {

	/**
	 * @param buildType
	 * @param localElements
	 * @param externalElements
	 * @param oldExternalFolders
	 * @param externalFolders
	 * @return
	 */
	DependencyResponse getDependencies(int buildType,
			Set<ISourceModule> localElements,
			Set<ISourceModule> externalElements, Set<IPath> oldExternalFolders,
			Set<IPath> externalFolders);

	void buildExternalModule(IBuildContext context) throws CoreException;

}
