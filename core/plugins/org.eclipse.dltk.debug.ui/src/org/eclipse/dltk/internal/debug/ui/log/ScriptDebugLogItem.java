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
package org.eclipse.dltk.internal.debug.ui.log;

public class ScriptDebugLogItem {

	private final long timestamp;
	private final String type;
	private final String message;

	public ScriptDebugLogItem(String type, String message) {
		this(System.currentTimeMillis(), type, message);
	}

	/**
	 * @param message
	 * @param timestamp
	 * @param type
	 */
	public ScriptDebugLogItem(long timestamp, String type, String message) {
		this.message = message;
		this.timestamp = timestamp;
		this.type = type;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public String getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return type + '\t' + message;
	}

}
