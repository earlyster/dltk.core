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

import java.util.Collections;
import java.util.List;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.SourceParserUtil;

public abstract class SemanticPositionUpdater extends PositionUpdater {

	public UpdateResult reconcile(ISourceModule sourceModule,
			SemanticHighlightingPresenter presenter,
			Highlighting[] highlightings, List currentPositions) {
		try {
			final ModuleDeclaration module = SourceParserUtil
					.getModuleDeclaration(sourceModule, null);
			if (module != null) {
				final SemanticUpdateWorker worker = createWorker(sourceModule);
				worker.setPresenter(presenter);
				worker.setHighlightings(highlightings);
				worker.setOldPositions(currentPositions);
				module.traverse(worker);
				worker.checkNewPositionOrdering();
				return new UpdateResult(worker.getNewPositions(), worker
						.getOldPositions());
			}
		} catch (Exception e) {
			DLTKCore.error("Error in SemanticPositionUpdater", e); //$NON-NLS-1$
		}
		return new UpdateResult(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
	}

	protected abstract SemanticUpdateWorker createWorker(
			ISourceModule sourceModule) throws ModelException;

}
