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
package org.eclipse.dltk.validators.core;

import java.io.OutputStream;

/**
 * The instance of the object to send {@link IValidatorWorker} output to.
 */
public interface IValidatorOutput {

	/**
	 * The name of the attribute to store command line.
	 */
	static String COMMAND_LINE = "org.eclipse.dltk.validators.core.IValidatorOutput#commandLine"; //$NON-NLS-1$

	/**
	 * Checks if output is enabled.
	 * 
	 * @return <code>true</code> if this object is operational or
	 *         <code>false</code> if not.
	 */
	boolean isEnabled();

	/**
	 * Checks error state.
	 * 
	 * @return <code>true</code> if this object has encountered an error
	 */
	boolean checkError();

	/**
	 * Returns output stream to send validator output to.
	 * 
	 * @return
	 */
	OutputStream getStream();

	/**
	 * Prints the specified line to the output or do nothing if this instance is
	 * not enabled.
	 * 
	 * @param x
	 */
	void println(String x);

	/**
	 * Closes this instance.
	 */
	void close();

	/**
	 * Returns the value of the attribute with the specified name
	 * 
	 * @param name
	 * @return
	 */
	Object getAttribute(String name);

	/**
	 * Sets the value of the attribute with the specified name
	 * 
	 * @param name
	 * @param value
	 */
	void setAttribute(String name, Object value);

}
