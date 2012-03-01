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

/**
 * Predicate for checking if the specified {@link IBuildParticipant} should be
 * called for the specified file.
 * 
 * Created via {@link IBuildParticipantFilterFactory}
 * 
 * @since 4.0
 */
public interface IBuildParticipantFilter {

	/**
	 * Filters the array of {@link IBuildParticipant}s and returns only allowed
	 * ones.
	 * 
	 * @param participants
	 * @param context
	 * @return
	 */
	IBuildParticipant[] filter(IBuildParticipant[] participants,
			IBuildContext context);

}
