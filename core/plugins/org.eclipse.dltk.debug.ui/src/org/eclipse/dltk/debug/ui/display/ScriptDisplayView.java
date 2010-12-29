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

import org.eclipse.dltk.debug.ui.DLTKDebugUIPlugin;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.SubActionBars;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.PageSite;
import org.eclipse.ui.part.ViewPart;

public class ScriptDisplayView extends ViewPart implements IConsoleView {

	private DebugConsole console;

	private PageSite pageSite;
	private IPageBookViewPage page;
	private IContextActivation fContextActivation;

	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		console = new DebugConsole(Messages.ScriptDisplayView_consoleName,
				DebugConsole.class.getName(), new DebugScriptInterpreter(this));
		final IPreferenceStore preferences = getPreferences();
		console.getHistory().restoreState(
				preferences.getString(CONSOLE_HISTORY));
		page = console.createPage(this);
		pageSite = new PageSite(getViewSite());
		page.init(pageSite);
		((DebugConsolePage) page).setResetOnLaunch(!preferences
				.getBoolean(KEEP_ON_LAUNCH));
	}

	private static final String CONSOLE_HISTORY = "debug.console.history"; //$NON-NLS-1$
	private static final String KEEP_ON_LAUNCH = "debug.console.keep_on_launch"; //$NON-NLS-1$

	public void dispose() {
		if (fContextActivation != null) {
			IContextService ctxService = (IContextService) getSite()
					.getService(IContextService.class);
			if (ctxService != null) {
				ctxService.deactivateContext(fContextActivation);
			}
		}
		if (page != null || console != null) {
			final IPreferenceStore preferences = getPreferences();
			if (console != null) {
				preferences.setValue(CONSOLE_HISTORY, console.getHistory()
						.saveState());
			}
			if (page != null) {
				preferences.setValue(KEEP_ON_LAUNCH,
						!((DebugConsolePage) page).isResetOnLaunch());
			}
			DLTKDebugUIPlugin.getDefault().savePluginPreferences();
		}
		if (page != null) {
			page.dispose();
			page = null;
		}
		if (console != null) {
			console.dispose();
			console = null;
		}
		super.dispose();
	}

	private IPreferenceStore getPreferences() {
		return DLTKDebugUIPlugin.getDefault().getPreferenceStore();
	}

	public void createPartControl(Composite parent) {
		IToolBarManager toolBarManager = pageSite.getActionBars()
				.getToolBarManager();
		toolBarManager.add(new GroupMarker(IConsoleConstants.OUTPUT_GROUP));
		toolBarManager.add(new GroupMarker(IConsoleConstants.LAUNCH_GROUP));
		page.createControl(parent);
		IContextService ctxService = (IContextService) getSite().getService(
				IContextService.class);
		if (ctxService != null) {
			fContextActivation = ctxService
					.activateContext(DLTKUIPlugin.CONTEXT_VIEWS);
		}
		((SubActionBars) pageSite.getActionBars()).activate();
	}

	public void setFocus() {
		page.setFocus();
	}

	public void display(IConsole console) {
		// NOP
	}

	public IConsole getConsole() {
		return console;
	}

	public boolean getScrollLock() {
		return false;
	}

	public boolean isPinned() {
		return false;
	}

	public void pin(IConsole console) {
		// NOP
	}

	public void setPinned(boolean pin) {
		// NOP
	}

	public void setScrollLock(boolean scrollLock) {
		// NOP
	}

	public void warnOfContentChange(IConsole console) {
		// NOP
	}

}
