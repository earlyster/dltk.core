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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.dltk.logconsole.ILogConsole;
import org.eclipse.dltk.logconsole.ILogConsoleManager;
import org.eclipse.dltk.logconsole.LogConsoleType;

public class LogConsoleManager implements ILogConsoleManager {

	public ILogConsole getConsole(LogConsoleType consoleType) {
		return getConsole(consoleType, null);
	}

	protected static final LogConsoleType DEFAULT_CONSOLE_TYPE = new LogConsoleType(
			"org.eclipse.dltk.logconsole.DEFAULT");

	private static class ConsoleKey {
		final LogConsoleType consoleType;
		final Object identifier;

		public ConsoleKey(LogConsoleType consoleType, Object identifier) {
			this.consoleType = consoleType != null ? consoleType
					: DEFAULT_CONSOLE_TYPE;
			this.identifier = identifier;
		}

		@Override
		public int hashCode() {
			int result = consoleType.hashCode();
			if (identifier != null) {
				result = result * 13 + identifier.hashCode();
			}
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj instanceof ConsoleKey) {
				final ConsoleKey other = (ConsoleKey) obj;
				if (consoleType.equals(other.consoleType)) {
					return identifier != null ? identifier
							.equals(other.identifier)
							: other.identifier == null;
				}
			}
			return false;
		}

	}

	private final Object lock = new Object();

	private final Map<ConsoleKey, ILogConsole> consoles = new HashMap<ConsoleKey, ILogConsole>();

	public ILogConsole getConsole(LogConsoleType consoleType, Object identifier) {
		final ConsoleKey key = new ConsoleKey(consoleType, identifier);
		synchronized (lock) {
			final ILogConsole console = consoles.get(key);
			if (console != null) {
				return console;
			}
		}
		final ILogConsole console = createConsole(key);
		synchronized (lock) {
			final ILogConsole c = consoles.get(key);
			if (c != null) {
				disposeConsole(console);
				return c;
			}
			consoles.put(key, console);
		}
		return console;
	}

	private void disposeConsole(ILogConsole console) {
		// TODO Auto-generated method stub

	}

	private ILogConsole createConsole(ConsoleKey key) {
		final ILogConsole console = LogConsoleFactoryManager.getInstance()
				.create(key.consoleType, key.identifier);
		if (console != null) {
			return console;
		}
		return new NopLogConsole(key.consoleType, key.identifier);
	}

	public ILogConsole[] list(LogConsoleType consoleType) {
		final List<ILogConsole> result = new ArrayList<ILogConsole>();
		synchronized (lock) {
			for (Map.Entry<ConsoleKey, ILogConsole> entry : consoles.entrySet()) {
				if (consoleType.equals(entry.getKey().consoleType)) {
					result.add(entry.getValue());
				}
			}
		}
		return result.toArray(new ILogConsole[result.size()]);
	}

}
