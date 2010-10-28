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
package org.eclipse.dltk.validators.internal.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;

public class RemoveAllValidatorConsolesAction extends Action {

	public RemoveAllValidatorConsolesAction() {
		setText(Messages.RemoveAllValidatorConsolesAction_text);
		setToolTipText(Messages.RemoveAllValidatorConsolesAction_toolTipText);
		setImageDescriptor(ValidatorsUI.getDefault().getImageDescriptor(
				"icons/full/elcl16/rem_all_co.gif")); //$NON-NLS-1$
	}

	public void run() {
		final IConsoleManager manager = ConsolePlugin.getDefault()
				.getConsoleManager();
		final IConsole[] consoles = manager.getConsoles();
		final List<IConsole> toRemove = new ArrayList<IConsole>();
		for (int i = 0; i < consoles.length; ++i) {
			IConsole console = consoles[i];
			if (console instanceof ValidatorConsole
					&& ValidatorConsole.TYPE.equals(console.getType())
					&& ((ValidatorConsole) console).isClosed()) {
				toRemove.add(console);
			}
		}
		if (!toRemove.isEmpty()) {
			manager.removeConsoles(toRemove.toArray(new IConsole[toRemove
					.size()]));
		}
	}

	public void update() {
		setEnabled(true);
	}

}
