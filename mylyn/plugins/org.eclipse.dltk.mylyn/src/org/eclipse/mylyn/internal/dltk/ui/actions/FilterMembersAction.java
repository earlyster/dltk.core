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

import org.eclipse.dltk.internal.ui.scriptview.ScriptExplorerPart;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.mylyn.internal.dltk.MylynDLTKPlugin;
import org.eclipse.mylyn.internal.dltk.ui.DLTKDeclarationsFilter;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class FilterMembersAction extends Action implements IViewActionDelegate {

	public static final String PREF_ID = "org.eclipse.dltk.mylyn.ui.explorer.filter.members";

	public FilterMembersAction() {
		super();
		// setChecked(true);
		// try {
		// boolean checked=
		//ContextCorePlugin.getDefault().getPreferenceStore().getBoolean(PREF_ID
		// );
		// valueChanged(true, true);
		// } catch (Exception e) {
		//
		// }
	}

	public void run(IAction action) {
		valueChanged(isChecked(), true);

	}

	private void valueChanged(final boolean on, boolean store) {
		if (store) {
			MylynDLTKPlugin.getDefault().getPreferenceStore().setValue(PREF_ID,
					on);
		}

		setChecked(true);
		ScriptExplorerPart packageExplorer = ScriptExplorerPart
				.getFromActivePerspective();
		ViewerFilter existingFilter = null;
		for (int i = 0; i < packageExplorer.getTreeViewer().getFilters().length; i++) {
			ViewerFilter filter = packageExplorer.getTreeViewer().getFilters()[i];
			if (filter instanceof DLTKDeclarationsFilter)
				existingFilter = filter;
		}
		if (existingFilter != null) {
			packageExplorer.getTreeViewer().removeFilter(existingFilter);
		} else {
			packageExplorer.getTreeViewer().addFilter(
					new DLTKDeclarationsFilter());
		}
	}

	public void init(IViewPart view) {
		// don't need to do anything on init
	}

	public void selectionChanged(IAction action, ISelection selection) {
		// don't care when the selection changes
	}

}
