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
package org.eclipse.dltk.logconsole;

public class LogConsoleType {

	private final String consoleType;

	public LogConsoleType(String consoleType) {
		this.consoleType = consoleType;
	}

	public String getType() {
		return consoleType;
	}

	@Override
	public int hashCode() {
		return consoleType.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof LogConsoleType) {
			final LogConsoleType other = (LogConsoleType) obj;
			return consoleType.equals(other.consoleType);
		}
		return false;
	}

	public String computeTitle(Object identifier) {
		final String type = getConsoleTypeName();
		if (identifier != null) {
			return type + " - " + identifier.toString();
		} else {
			return type;
		}
	}

	protected String getConsoleTypeName() {
		String type = consoleType;
		int index = type.lastIndexOf('.');
		if (index > 0) {
			type = type.substring(index + 1);
		}
		return type;
	}

	public ILogConsole getConsole(Object identifier) {
		return LogConsolePlugin.getConsoleManager()
				.getConsole(this, identifier);
	}

	public ILogConsole[] list() {
		return LogConsolePlugin.getConsoleManager().list(this);
	}
}
