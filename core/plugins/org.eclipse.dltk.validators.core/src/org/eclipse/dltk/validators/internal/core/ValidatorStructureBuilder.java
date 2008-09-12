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
package org.eclipse.dltk.validators.internal.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.SourceParserUtil;
import org.eclipse.dltk.internal.core.IStructureBuilder;
import org.eclipse.dltk.validators.core.IBuildParticipant;
import org.eclipse.dltk.validators.core.IBuildParticipantExtension;
import org.eclipse.dltk.validators.core.ValidatorRuntime;

public class ValidatorStructureBuilder implements IStructureBuilder {

	public void buildStructure(String natureId, ISourceModule module,
			IProblemReporter reporter) {
		final IScriptProject project = module.getScriptProject();
		final IBuildParticipant[] validators = ValidatorRuntime
				.getBuildParticipants(project, natureId, ValidatorRuntime.ALL);
		if (validators.length == 0) {
			return;
		}
		for (int j = 0; j < validators.length; ++j) {
			final IBuildParticipant participant = validators[j];
			if (participant instanceof IBuildParticipantExtension) {
				((IBuildParticipantExtension) participant)
						.beginBuild(IBuildParticipantExtension.STRUCTURE_BUILD);
			}
		}
		final ModuleDeclaration moduleDeclaration = SourceParserUtil
				.getModuleDeclaration(module);
		for (int k = 0; k < validators.length; ++k) {
			final IBuildParticipant participant = validators[k];
			try {
				participant.build(module, moduleDeclaration, reporter);
			} catch (CoreException e) {
				ValidatorsCore.log(e.getStatus());
			}
		}
		for (int j = 0; j < validators.length; ++j) {
			final IBuildParticipant participant = validators[j];
			if (participant instanceof IBuildParticipantExtension) {
				((IBuildParticipantExtension) participant).endBuild();
			}
		}
	}

}
