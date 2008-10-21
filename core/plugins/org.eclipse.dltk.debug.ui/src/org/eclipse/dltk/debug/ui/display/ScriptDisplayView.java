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

import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.SubActionBars;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.part.IPageBookViewPage;
import org.eclipse.ui.part.PageSite;
import org.eclipse.ui.part.ViewPart;

public class ScriptDisplayView extends ViewPart implements IConsoleView {

	private DebugConsole console;

	private PageSite pageSite;
	private IPageBookViewPage page;

	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		console = new DebugConsole(Messages.ScriptDisplayView_consoleName,
				DebugConsole.class.getName(), new DebugScriptInterpreter(this));
		page = console.createPage(this);
		pageSite = new PageSite(getViewSite());
		page.init(pageSite);
	}

	public void dispose() {
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

	public void createPartControl(Composite parent) {
		IToolBarManager toolBarManager = pageSite.getActionBars()
				.getToolBarManager();
		toolBarManager.add(new GroupMarker(IConsoleConstants.OUTPUT_GROUP));
		toolBarManager.add(new GroupMarker(IConsoleConstants.LAUNCH_GROUP));
		page.createControl(parent);
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
