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

import org.eclipse.dltk.console.IScriptInterpreter;
import org.eclipse.dltk.console.ui.ScriptConsole;
import org.eclipse.dltk.console.ui.internal.ScriptConsolePage;
import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.console.IConsoleView;

public class DebugConsole extends ScriptConsole {

	/**
	 * @param consoleName
	 * @param consoleType
	 */
	public DebugConsole(String consoleName, String consoleType,
			IScriptInterpreter interpreter) {
		super(consoleName, consoleType);
		setInterpreter(interpreter);
		setContentAssistProcessor(new DebugConsoleContentAssistProcessor());
		getHistory().restoreState(getPreferences().getString(CONSOLE_HISTORY));
	}

	private static final String CONSOLE_HISTORY = "debug.console.history"; //$NON-NLS-1$

	public void dispose() {
		getPreferences().setValue(CONSOLE_HISTORY, getHistory().saveState());
		DLTKDebugUIPlugin.getDefault().savePluginPreferences();
		super.dispose();
	}

	private IPreferenceStore getPreferences() {
		return DLTKDebugUIPlugin.getDefault().getPreferenceStore();
	}

	protected ScriptConsolePage createPage(IConsoleView view,
			SourceViewerConfiguration cfg) {
		return new DebugConsolePage(this, view, cfg);
	}

}
