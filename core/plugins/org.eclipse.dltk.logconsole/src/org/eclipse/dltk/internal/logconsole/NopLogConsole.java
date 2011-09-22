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
package org.eclipse.dltk.internal.logconsole;

import org.eclipse.dltk.logconsole.ILogCategory;
import org.eclipse.dltk.logconsole.ILogConsoleStream;
import org.eclipse.dltk.logconsole.LogConsoleType;
import org.eclipse.dltk.logconsole.impl.AbstractLogConsole;

public class NopLogConsole extends AbstractLogConsole {

	protected NopLogConsole(LogConsoleType consoleType, Object identifier) {
		super(consoleType, identifier);
	}

	public void println(ILogConsoleStream stream, Object message) {
	}

	public void println(ILogCategory category, Object message) {
	}

}
