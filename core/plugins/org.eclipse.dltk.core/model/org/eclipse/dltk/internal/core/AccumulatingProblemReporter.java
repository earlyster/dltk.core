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
package org.eclipse.dltk.internal.core;

import org.eclipse.dltk.compiler.problem.IProblem;
import org.eclipse.dltk.compiler.problem.IProblemFactory;
import org.eclipse.dltk.compiler.problem.IProblemSeverityTranslator;
import org.eclipse.dltk.compiler.problem.ProblemCollector;
import org.eclipse.dltk.compiler.problem.ProblemSeverity;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IProblemRequestor;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;

class AccumulatingProblemReporter extends ProblemCollector {

	private final ISourceModule module;
	private final IProblemRequestor problemRequestor;

	/**
	 * @param module
	 * @param problemRequestor
	 */
	public AccumulatingProblemReporter(ISourceModule module,
			IProblemRequestor problemRequestor) {
		this.module = module;
		this.problemRequestor = problemRequestor;
	}

	private IProblemSeverityTranslator getTranslator() {
		final IScriptProject project = module.getScriptProject();
		if (!ExternalScriptProject.EXTERNAL_PROJECT_NAME.equals(project
				.getElementName())) {
			final IProblemFactory problemFactory = DLTKLanguageManager
					.getProblemFactory(module);
			return problemFactory.createSeverityTranslator(project);
		}
		return IProblemSeverityTranslator.IDENTITY;
	}

	public void reportToRequestor() {
		final IProblemSeverityTranslator translator = getTranslator();
		problemRequestor.beginReporting();
		for (final IProblem problem : problems) {
			final ProblemSeverity severity = problem.getSeverity();
			if (severity != null) {
				final ProblemSeverity newSeverity = translator.getSeverity(
						problem.getID(), severity);
				if (newSeverity == ProblemSeverity.IGNORE) {
					continue;
				}
				if (newSeverity != severity) {
					problem.setSeverity(newSeverity);
				}
			}
			problemRequestor.acceptProblem(problem);
		}
		problemRequestor.endReporting();
	}

}
