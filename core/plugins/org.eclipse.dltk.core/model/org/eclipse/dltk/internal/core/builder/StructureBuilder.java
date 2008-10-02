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
package org.eclipse.dltk.internal.core.builder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.dltk.core.builder.IBuildParticipantExtension;

public class StructureBuilder {

	public static void build(String natureId, ISourceModule module,
			IProblemReporter reporter) {
		final NullProgressMonitor monitor = new NullProgressMonitor();
		final IScriptProject project = module.getScriptProject();
		final IBuildParticipant[] validators = BuildParticipantManager
				.getBuildParticipants(project, natureId);
		if (validators.length == 0) {
			return;
		}
		for (int j = 0; j < validators.length; ++j) {
			final IBuildParticipant participant = validators[j];
			if (participant instanceof IBuildParticipantExtension) {
				((IBuildParticipantExtension) participant)
						.beginBuild(IBuildParticipantExtension.RECONCILE_BUILD);
			}
		}
		final ModuleDeclaration moduleDeclaration = SourceParserUtil
				.getModuleDeclaration(module);
		for (int k = 0; k < validators.length; ++k) {
			final IBuildParticipant participant = validators[k];
			try {
				participant.build(module, moduleDeclaration, reporter);
			} catch (CoreException e) {
				DLTKCore.error("error", e);
			}
		}
		for (int j = 0; j < validators.length; ++j) {
			final IBuildParticipant participant = validators[j];
			if (participant instanceof IBuildParticipantExtension) {
				((IBuildParticipantExtension) participant).endBuild(monitor);
			}
		}
	}

}
