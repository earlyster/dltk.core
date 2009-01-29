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

import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.ui.console.IOConsole;

public class ValidatorConsole extends IOConsole {

	public static final String TYPE = "org.eclipse.dltk.validators.ConsoleValidatorOutput"; //$NON-NLS-1$

	private final String initialName;
	private boolean closed = false;

	/**
	 * @param name
	 */
	public ValidatorConsole(String name) {
		super(name, TYPE, null);
		this.initialName = name;
	}

	public void close() {
		if (closed) {
			return;
		}
		closed = true;
		Runnable r = new Runnable() {
			public void run() {
				setName(Messages.ValidatorConsole_terminated + initialName);
			}
		};
		DLTKUIPlugin.getStandardDisplay().asyncExec(r);
	}

	public String getInitialName() {
		return initialName;
	}

	/**
	 * @return the closed
	 */
	public boolean isClosed() {
		return closed;
	}

}
