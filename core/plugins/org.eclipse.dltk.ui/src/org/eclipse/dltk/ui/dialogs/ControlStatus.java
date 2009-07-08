/*******************************************************************************
 * Copyright (c) 2009 xored software, Inc.  
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html  
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.dialogs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.swt.widgets.Control;

/**
 * @since 2.0
 */
public class ControlStatus implements IStatus {

	private final int fSeverity;
	private final String fMessage;
	private final Control fControl;

	public ControlStatus(int severity, String message, Control control) {
		this.fSeverity = severity;
		this.fMessage = message;
		this.fControl = control;
	}

	public IStatus[] getChildren() {
		return Status.OK_STATUS.getChildren();
	}

	public int getCode() {
		return 0;
	}

	public Throwable getException() {
		return null;
	}

	public String getMessage() {
		return fMessage;
	}

	public String getPlugin() {
		return DLTKUIPlugin.PLUGIN_ID;
	}

	public int getSeverity() {
		return fSeverity;
	}

	public boolean isMultiStatus() {
		return false;
	}

	public boolean isOK() {
		return fSeverity == IStatus.OK;
	}

	public boolean matches(int severityMask) {
		return (fSeverity & severityMask) != 0;
	}

	public Control getControl() {
		return fControl;
	}

}
