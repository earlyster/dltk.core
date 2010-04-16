/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.core.search.matching2;


public class MatchingCollector<E> {
	private final IMatchingPredicate<E> predicate;
	private final IMatchingNodeSet<E> nodeSet;

	public MatchingCollector(IMatchingPredicate<E> predicate,
			IMatchingNodeSet<E> nodeSet) {
		this.predicate = predicate;
		this.nodeSet = nodeSet;
	}

	public void report(E node) {
		MatchLevel level = predicate.match(node);
		if (level != null) {
			nodeSet.addMatch(node, level);
		}
	}
}
