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
package org.eclipse.dltk.logconsole.impl;

import org.eclipse.dltk.logconsole.ILogConsole;
import org.eclipse.dltk.logconsole.LogConsoleType;

/**
 * Abstract base class of the {@link ILogConsole} implementations.
 */
public abstract class AbstractLogConsole implements ILogConsole {

	private final LogConsoleType consoleType;
	private final Object identifier;

	protected AbstractLogConsole(LogConsoleType consoleType, Object identifier) {
		this.consoleType = consoleType;
		this.identifier = identifier;
	}

	public LogConsoleType getConsoleType() {
		return consoleType;
	}

	public Object getIdentifier() {
		return identifier;
	}

	public void println(Object message) {
		println(STDOUT, message);
	}

	public void activate() {
	}

}
