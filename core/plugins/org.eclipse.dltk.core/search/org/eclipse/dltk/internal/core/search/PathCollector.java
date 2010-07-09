/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core.search;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.dltk.core.search.SearchParticipant;
import org.eclipse.dltk.core.search.SearchPattern;
import org.eclipse.dltk.internal.compiler.env.AccessRuleSet;

/**
 * Collects the resource paths reported by a client to this search requestor.
 */
public class PathCollector extends IndexQueryRequestor {

	/* a set of resource paths */
	private final Set<String> paths = new HashSet<String>(5);

	public boolean acceptIndexMatch(String documentPath,
			SearchPattern indexRecord, SearchParticipant participant,
			AccessRuleSet access) {
		paths.add(documentPath);
		return true;
	}

	/**
	 * Returns the paths that have been collected or <code>null</code> if there
	 * are no paths
	 */
	public String[] getPaths() {
		if (paths.isEmpty())
			return null;
		else
			return paths.toArray(new String[paths.size()]);
	}
}
