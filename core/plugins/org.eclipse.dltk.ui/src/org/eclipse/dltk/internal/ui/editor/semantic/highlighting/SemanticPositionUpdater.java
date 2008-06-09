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
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.SourceParserUtil;

public abstract class SemanticPositionUpdater extends PositionUpdater {

	public UpdateResult reconcile(ISourceModule sourceModule,
			SemanticHighlightingPresenter presenter,
			Highlighting[] highlightings, List currentPositions) {
		final List newPositions = calculateNewPositions(sourceModule,
				presenter, highlightings);
		if (newPositions == null) {
			return new UpdateResult(Collections.EMPTY_LIST,
					Collections.EMPTY_LIST);
		}
		Set currentSet = new HashSet(currentPositions);
		List addedPositions = new ArrayList();
		Set removed = new HashSet(currentPositions);
		for (Iterator it = newPositions.iterator(); it.hasNext();) {
			final Object o = it.next();
			if (currentSet.contains(o)) {
				removed.remove(o);
			} else {
				addedPositions.add(o);
			}
		}
		return new UpdateResult(addedPositions, new ArrayList(removed));
	}

	private List calculateNewPositions(ISourceModule sourceModule,
			final SemanticHighlightingPresenter presenter,
			final Highlighting[] highlightings) {
		try {
			final ModuleDeclaration module = SourceParserUtil
					.getModuleDeclaration(sourceModule, null);
			if (module != null) {
				final SemanticUpdateWorker worker = createWorker(sourceModule);
				worker.setPresenter(presenter);
				worker.setHighlightings(highlightings);
				module.traverse(worker);
				return worker.getPositions();
			}
		} catch (Exception e) {
			DLTKCore.error("Error in SemanticPositionUpdater", e); //$NON-NLS-1$
		}
		return null;
	}

	protected abstract SemanticUpdateWorker createWorker(
			ISourceModule sourceModule) throws ModelException;

}
