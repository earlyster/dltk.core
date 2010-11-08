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
import org.eclipse.dltk.compiler.problem.ProblemCollector;
import org.eclipse.dltk.core.IProblemRequestor;

class AccumulatingProblemReporter extends ProblemCollector {

	private final IProblemRequestor problemRequestor;

	/**
	 * @param problemRequestor
	 */
	public AccumulatingProblemReporter(IProblemRequestor problemRequestor) {
		this.problemRequestor = problemRequestor;
	}

	public void reportToRequestor() {
		problemRequestor.beginReporting();
		for (final IProblem problem : problems) {
			problemRequestor.acceptProblem(problem);
		}
		problemRequestor.endReporting();
	}

}
