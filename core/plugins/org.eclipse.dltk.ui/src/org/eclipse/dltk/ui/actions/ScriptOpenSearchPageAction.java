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
package org.eclipse.dltk.ui.actions;

import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

public abstract class ScriptOpenSearchPageAction implements
		IWorkbenchWindowActionDelegate {

	private IWorkbenchWindow window;

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void run(IAction action) {
		if (window == null || window.getActivePage() == null) {
			beep();
			return;
		}

		NewSearchUI.openSearchDialog(window, getSearchPageId());
	}

	protected abstract String getSearchPageId();

	public void selectionChanged(IAction action, ISelection selection) {
	}

	public void dispose() {
		window = null;
	}

	protected void beep() {
		final Shell shell = DLTKUIPlugin.getActiveWorkbenchShell();
		if (shell != null && shell.getDisplay() != null)
			shell.getDisplay().beep();
	}
}
