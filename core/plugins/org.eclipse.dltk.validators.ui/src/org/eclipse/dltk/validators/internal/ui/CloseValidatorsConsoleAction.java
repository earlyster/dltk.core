/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.validators.internal.ui;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;

public class CloseValidatorsConsoleAction extends Action {

	private final IConsole console;

	public CloseValidatorsConsoleAction(IConsole console) {
		this.console = console;
		setText(Messages.ValidatorsConsolePageParticipant_close);
		setToolTipText(Messages.ValidatorsConsolePageParticipant_closeConsole);
		setImageDescriptor(ValidatorsUI.getDefault().getImageDescriptor(
				"icons/remove_console.gif")); //$NON-NLS-1$
	}

	public void run() {
		ConsolePlugin.getDefault().getConsoleManager().removeConsoles(
				new IConsole[] { this.console });
	}

	public void update() {
		setEnabled(true);
	}

}
