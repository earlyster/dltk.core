/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.dialogs;

/**
 * A settable IStatus. Can be an error, warning, info or ok. For error, info and
 * warning states, a message describes the problem.
 * 
 * @deprecated
 */
public class StatusInfo extends org.eclipse.dltk.ui.dialogs.StatusInfo {

	/**
	 * Creates a status set to OK (no message)
	 */
	public StatusInfo() {
		super();
	}

	/**
	 * Creates a status .
	 * 
	 * @param severity
	 *            The status severity: ERROR, WARNING, INFO and OK.
	 * @param message
	 *            The message of the status. Applies only for ERROR, WARNING and
	 *            INFO.
	 */
	public StatusInfo(int severity, String message) {
		super(severity, message);
	}

}
