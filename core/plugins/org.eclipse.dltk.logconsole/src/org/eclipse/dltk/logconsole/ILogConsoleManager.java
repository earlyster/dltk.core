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

/**
 * The manager of the {@link ILogConsole}s.
 * 
 * The instance of this manager can be obtained via call to
 * {@link LogConsolePlugin#getConsoleManager()}
 */
public interface ILogConsoleManager {

	/**
	 * Returns console of the specified type. This method is equivalent to
	 * <code>getConsole(consoleType,null)</code>
	 * 
	 * @param consoleType
	 * @return
	 */
	ILogConsole getConsole(LogConsoleType consoleType);

	/**
	 * Returns console of the specified type with the specified key.
	 * 
	 * @param consoleType
	 * @param identifier
	 *            the unique identifier of the console (if you want separate
	 *            consoles of the same type) or <code>null</code>
	 * @return
	 */
	ILogConsole getConsole(LogConsoleType consoleType, Object identifier);

	/**
	 * Lists all the consoles of the specified type.
	 * 
	 * @param logConsoleType
	 * @return array of consoles, if there are no matching consoles empty array
	 *         is returned
	 */
	ILogConsole[] list(LogConsoleType logConsoleType);
}
