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

import org.eclipse.dltk.internal.logconsole.StandardStreams;

/**
 * The public interface of the log console.
 */
public interface ILogConsole {

	/**
	 * Identifier of the standard output stream
	 */
	ILogConsoleStream STDOUT = StandardStreams.STDOUT;

	/**
	 * Identifier of the standard error stream
	 */
	ILogConsoleStream STDERR = StandardStreams.STDERR;

	/**
	 * Returns the console type specified when creating this console.
	 * 
	 * @return
	 */
	LogConsoleType getConsoleType();

	/**
	 * Returns the identifier of this console or <code>null</code>, if console
	 * was created with the call {@link ILogConsoleManager#getConsole(String)}
	 * 
	 * @return
	 */
	Object getIdentifier();

	/**
	 * Prints the message to the standard output stream
	 * 
	 * @param message
	 * @see #STDOUT
	 */
	void println(Object message);

	/**
	 * Prints the message to the specified stream
	 * 
	 * @param stream
	 * @param message
	 */
	void println(ILogConsoleStream stream, Object message);

	/**
	 * Activates this console - makes it visible to the user
	 */
	void activate();

}
