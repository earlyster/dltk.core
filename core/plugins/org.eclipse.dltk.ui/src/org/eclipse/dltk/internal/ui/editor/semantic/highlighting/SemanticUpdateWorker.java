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
package org.eclipse.dltk.internal.ui.editor.semantic.highlighting;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.internal.ui.editor.SemanticHighlightingManager.HighlightedPosition;

public class SemanticUpdateWorker extends ASTVisitor {

	private final List newPositions = new ArrayList();
	private int oldPositionCount = 0;
	private HighlightedPosition[] oldPositions;
	private SemanticHighlightingPresenter presenter;

	private Highlighting[] highlightings;

	protected void addHighlightedPosition(int start, int end,
			int highlightingIndex) {
		final int len = end - start;
		final Highlighting hl = highlightings[highlightingIndex];
		for (int i = 0; i < oldPositions.length; ++i) {
			final HighlightedPosition p = oldPositions[i];
			if (p != null && p.isEqual(start, len, hl)) {
				oldPositions[i] = null;
				--oldPositionCount;
				return;
			}
		}
		final HighlightedPosition hp = presenter.createHighlightedPosition(
				start, len, hl);
		newPositions.add(hp);
	}

	/**
	 * @return
	 */
	public List getNewPositions() {
		return newPositions;
	}

	public void setPresenter(SemanticHighlightingPresenter presenter) {
		this.presenter = presenter;
	}

	public void setHighlightings(Highlighting[] highlightings) {
		this.highlightings = highlightings;
	}

	/**
	 * @param oldPositions
	 */
	public void setOldPositions(List oldPositions) {
		this.oldPositionCount = oldPositions.size();
		this.oldPositions = (HighlightedPosition[]) oldPositions
				.toArray(new HighlightedPosition[oldPositionCount]);
	}

	public List getOldPositions() {
		final List result = new ArrayList(oldPositionCount);
		for (int i = 0, size = oldPositions.length; i < size; ++i) {
			final HighlightedPosition p = oldPositions[i];
			if (p != null) {
				result.add(p);
			}
		}
		return result;
	}

}
