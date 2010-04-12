/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.search.matching;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.IProjectFragment;

/**
 * A set of PossibleMatches that is sorted by package fragment roots.
 */
public class PossibleMatchSet {
	private Map<IPath, List<PossibleMatch>> rootsToMatches = new HashMap<IPath, List<PossibleMatch>>(
			5);
	private int elementCount = 0;

	public void add(PossibleMatch possibleMatch) {
		IPath path = possibleMatch.openable.getProjectFragment().getPath();
		List<PossibleMatch> possibleMatches = rootsToMatches.get(path);
		if (possibleMatches != null) {
			if (possibleMatches.contains(possibleMatch))
				return;
		} else {
			rootsToMatches.put(path,
					possibleMatches = new ArrayList<PossibleMatch>());
		}
		possibleMatches.add(possibleMatch);
		elementCount++;
	}

	public PossibleMatch[] getPossibleMatches(IProjectFragment[] roots) {
		PossibleMatch[] result = new PossibleMatch[elementCount];
		int index = 0;
		for (int i = 0, length = roots.length; i < length; i++) {
			List<PossibleMatch> possibleMatches = rootsToMatches.get(roots[i]
					.getPath());
			if (possibleMatches != null) {
				for (int j = 0, size = possibleMatches.size(); j < size; ++j)
					result[index++] = possibleMatches.get(j);
			}
		}
		if (index < elementCount)
			System.arraycopy(result, 0, result = new PossibleMatch[index], 0,
					index);
		return result;
	}

	public void reset() {
		rootsToMatches.clear();
		elementCount = 0;
	}
}
