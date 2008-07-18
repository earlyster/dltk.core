/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.validators.internal.ui;

import org.eclipse.dltk.validators.ui.ConsoleValidatorOutput;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.part.IPageBookViewPage;

public class ValidatorsConsolePageParticipant implements
		IConsolePageParticipant {

	public void activated() {
	}

	public void deactivated() {
	}

	public void dispose() {
	}

	public void init(IPageBookViewPage page, IConsole console) {
		if (console instanceof IOConsole
				&& ConsoleValidatorOutput.CONSOLE_TYPE
						.equals(console.getType())) {
			IActionBars bars = page.getSite().getActionBars();
			IToolBarManager toolbarManager = bars.getToolBarManager();
			toolbarManager.appendToGroup(IConsoleConstants.LAUNCH_GROUP,
					new CloseValidatorsConsoleAction(console));
		}
	}

	public Object getAdapter(Class adapter) {
		return null;
	}
}
