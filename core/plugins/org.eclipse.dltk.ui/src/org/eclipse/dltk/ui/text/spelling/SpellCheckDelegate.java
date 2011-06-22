/*******************************************************************************
 * Copyright (c) 2011 NumberFour AG
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.text.spelling;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITypedRegion;

public class SpellCheckDelegate {

	public SpellCheckDelegate() {
		this(IDocument.DEFAULT_CONTENT_TYPE);
	}

	public SpellCheckDelegate(String... contentTypes) {
		for (String contentType : contentTypes) {
			ignoredContentTypes.add(contentType);
		}
	}

	private final Set<String> ignoredContentTypes = new HashSet<String>();

	/**
	 * returns the (sub-)regions to be checked in the specified partition or
	 * <code>null</code> if partition shouldn't be checked at all.
	 * 
	 * @param partition
	 * @return
	 */
	public IRegion[] computeRegions(ITypedRegion partition) {
		if (ignoredContentTypes.contains(partition.getType())) {
			return null;
		} else {
			return asArray(partition);
		}
	}

	/**
	 * Returns the specified region as array of the length 1
	 * 
	 * @param region
	 * @return
	 */
	protected final IRegion[] asArray(IRegion region) {
		fRegions[0] = region;
		return fRegions;
	}

	/**
	 * Region array, used to prevent us from creating a new array on each
	 * reconcile pass.
	 */
	private final IRegion[] fRegions = new IRegion[1];

}
