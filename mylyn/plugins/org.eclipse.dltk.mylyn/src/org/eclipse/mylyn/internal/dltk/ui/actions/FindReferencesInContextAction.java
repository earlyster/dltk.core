/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.dltk.ui.actions;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.search.IDLTKSearchConstants;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.internal.ui.actions.SelectionConverter;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.internal.ui.search.DLTKSearchQuery;
import org.eclipse.dltk.internal.ui.search.DLTKSearchScopeFactory;
import org.eclipse.dltk.ui.search.ElementQuerySpecification;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.context.ui.ContextWorkingSetManager;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * @author Shawn Minto
 */
public class FindReferencesInContextAction extends Action implements IWorkbenchWindowActionDelegate {

	public void run(IAction action) {
		IEditorPart editor = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editor != null && editor instanceof ScriptEditor) {
			IModelElement[] resolved;
			try {
				resolved = SelectionConverter.codeResolve((ScriptEditor) editor);
				if (resolved != null && resolved.length == 1 && resolved[0] != null) {
					IModelElement element = resolved[0];
				
					ContextWorkingSetManager updater = ContextWorkingSetManager.getDefault();					
					
					
					if (updater != null && updater.getWorkingSet() != null) {
						IDLTKSearchScope scope = DLTKSearchScopeFactory.getInstance().createSearchScope(
								updater.getWorkingSet(), false,null);
						DLTKSearchQuery query = new DLTKSearchQuery(new ElementQuerySpecification(element,
								IDLTKSearchConstants.REFERENCES, scope, "Mylyn Current Task Context"));
						if (query != null) {
							NewSearchUI.activateSearchResultView();

							if (query.canRunInBackground()) {
								NewSearchUI.runQueryInBackground(query);
							} else {
								IProgressService progressService = PlatformUI.getWorkbench().getProgressService();
								NewSearchUI.runQueryInForeground(progressService, query);
							}
						}
					}
				}
			} catch (ModelException e) {
				// ignore search if can't resolve
			}

		}

	}

	public void dispose() {
	}

	public void init(IWorkbenchWindow window) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
	}
}
