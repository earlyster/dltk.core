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

	private final List positions = new ArrayList();
	private SemanticHighlightingPresenter presenter;

	private Highlighting[] highlightings;

	protected void addHighlightedPosition(int start, int end,
			int highlightingIndex) {
		final HighlightedPosition hp = presenter.createHighlightedPosition(
				start, end - start, highlightings[highlightingIndex]);
		positions.add(hp);
	}

	/**
	 * @return
	 */
	public List getPositions() {
		return positions;
	}

	public void setPresenter(SemanticHighlightingPresenter presenter) {
		this.presenter = presenter;
	}

	public void setHighlightings(Highlighting[] highlightings) {
		this.highlightings = highlightings;
	}

}
