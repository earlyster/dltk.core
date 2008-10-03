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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.problem.IProblemReporter;

public interface IBuildParticipant {

	/**
	 * Validates specified ISourceModule or it's AST and reports any problems
	 * found via {@link IProblemReporter}
	 * 
	 * @param module
	 * @param ast
	 * @param reporter
	 */
	void build(IBuildContext context) throws CoreException;

}
