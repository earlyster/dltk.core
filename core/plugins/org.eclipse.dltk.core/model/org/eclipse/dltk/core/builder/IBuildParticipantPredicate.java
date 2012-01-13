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
 * Created via {@link IBuildParticipantPredicateFactory}
 * 
 * @since 4.0
 */
public interface IBuildParticipantPredicate {

	/**
	 * Returns if the specified {@link IBuildParticipant} should handle the file
	 * identified with the context.
	 * 
	 * @param participant
	 * @param context
	 * @return
	 */
	boolean apply(IBuildParticipant participant, IBuildContext context);

}
