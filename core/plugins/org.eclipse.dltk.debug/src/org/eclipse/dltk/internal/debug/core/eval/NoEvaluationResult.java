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
package org.eclipse.dltk.internal.debug.core.eval;

import org.eclipse.debug.core.DebugException;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.debug.core.eval.IScriptEvaluationResult;
import org.eclipse.dltk.debug.core.model.IScriptThread;
import org.eclipse.dltk.debug.core.model.IScriptValue;

public class NoEvaluationResult implements IScriptEvaluationResult {

	private final String snippet;
	private final IScriptThread thread;

	/**
	 * @param snippet
	 * @param thread
	 */
	public NoEvaluationResult(String snippet, IScriptThread thread) {
		this.snippet = snippet;
		this.thread = thread;
	}

	public String[] getErrorMessages() {
		return CharOperation.NO_STRINGS;
	}

	public DebugException getException() {
		return null;
	}

	public String getSnippet() {
		return snippet;
	}

	public IScriptThread getThread() {
		return thread;
	}

	public IScriptValue getValue() {
		return null;
	}

	public boolean hasErrors() {
		return false;
	}

}
