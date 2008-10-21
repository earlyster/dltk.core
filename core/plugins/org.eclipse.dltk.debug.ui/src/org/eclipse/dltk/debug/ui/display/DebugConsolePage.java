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
package org.eclipse.dltk.debug.ui.display;

import org.eclipse.dltk.console.ui.ScriptConsole;
import org.eclipse.dltk.console.ui.internal.ScriptConsolePage;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.console.IConsoleView;

public class DebugConsolePage extends ScriptConsolePage {

	/**
	 * @param console
	 * @param view
	 * @param cfg
	 */
	public DebugConsolePage(ScriptConsole console, IConsoleView view,
			SourceViewerConfiguration cfg) {
		super(console, view, cfg);
	}

	protected IAction createTerminateConsoleAction() {
		return null;
	}

}
