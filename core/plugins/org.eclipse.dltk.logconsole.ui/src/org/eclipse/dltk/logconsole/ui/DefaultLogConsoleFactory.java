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
package org.eclipse.dltk.logconsole.ui;

import org.eclipse.dltk.logconsole.ILogConsole;
import org.eclipse.dltk.logconsole.LogConsoleType;
import org.eclipse.dltk.logconsole.impl.ILogConsoleFactory;

public class DefaultLogConsoleFactory implements ILogConsoleFactory {

	public ILogConsole create(LogConsoleType consoleType, Object identifier) {
		return new DefaultLogConsole(consoleType, identifier);
	}

}
