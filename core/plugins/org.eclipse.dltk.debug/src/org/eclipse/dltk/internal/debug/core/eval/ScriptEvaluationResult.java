package org.eclipse.dltk.internal.debug.core.eval;

import org.eclipse.debug.core.DebugException;
import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.debug.core.eval.IScriptEvaluationResult;
import org.eclipse.dltk.debug.core.model.IScriptThread;
import org.eclipse.dltk.debug.core.model.IScriptValue;

public class ScriptEvaluationResult implements IScriptEvaluationResult {
	private final IScriptThread thread;
	private final String snippet;
	private final IScriptValue value;

	public ScriptEvaluationResult(IScriptThread thread, String snippet,
			IScriptValue value) {
		this.thread = thread;
		this.value = value;
		this.snippet = snippet;
	}

	public String getSnippet() {
		return snippet;
	}

	public IScriptValue getValue() {
		return value;
	}

	public IScriptThread getThread() {
		return thread;
	}

	public String[] getErrorMessages() {
		return CharOperation.NO_STRINGS;
	}

	public DebugException getException() {
		return null;
	}

	public boolean hasErrors() {
		return false;
	}
}
