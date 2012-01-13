/*******************************************************************************
 * Copyright (c) 2012 NumberFour AG
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.core.builder;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;

/**
 * Contributed via <code>org.eclipse.dltk.core.buildParticipant/predicate</code>
 * extension point.
 * 
 * @since 4.0
 */
public interface IBuildParticipantPredicateFactory {

	/**
	 * Creates the predicate for checking if each {@link IBuildParticipant}
	 * should be called for the file or not. Returns <code>null</code> if no
	 * predicate is required for the project.
	 * 
	 * @param project
	 * @return
	 * @throws CoreException
	 */
	IBuildParticipantPredicate createPredicate(IScriptProject project)
			throws CoreException;

}
