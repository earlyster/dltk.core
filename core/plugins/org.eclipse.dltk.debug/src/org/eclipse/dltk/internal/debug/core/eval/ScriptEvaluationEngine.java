package org.eclipse.dltk.internal.debug.core.eval;

import java.util.WeakHashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.dbgp.IDbgpProperty;
import org.eclipse.dltk.dbgp.IDbgpSession;
import org.eclipse.dltk.dbgp.commands.IDbgpExtendedCommands;
import org.eclipse.dltk.debug.core.eval.IScriptEvaluationEngine;
import org.eclipse.dltk.debug.core.eval.IScriptEvaluationListener;
import org.eclipse.dltk.debug.core.eval.IScriptEvaluationResult;
import org.eclipse.dltk.debug.core.model.IScriptDebugTarget;
import org.eclipse.dltk.debug.core.model.IScriptStackFrame;
import org.eclipse.dltk.debug.core.model.IScriptThread;
import org.eclipse.dltk.debug.core.model.IScriptValue;
import org.eclipse.dltk.internal.debug.core.model.ScriptDebugTarget;
import org.eclipse.dltk.internal.debug.core.model.ScriptValue;
import org.eclipse.osgi.util.NLS;

public class ScriptEvaluationEngine implements IScriptEvaluationEngine {
	private final IScriptThread thread;

	// private int count;
	private final WeakHashMap cache;

	protected void putToCache(String snippet, IScriptEvaluationResult result) {
		// if (result != null) {
		// cache.put(snippet, result);
		// }
	}

	protected IScriptEvaluationResult getFromCache(String snippet) {
		return null;
		// int newCount = thread.getModificationsCount();
		// if (count != newCount) {
		// cache.clear();
		// count = newCount;
		// return null;
		// }
		//
		// return (IScriptEvaluationResult) cache.get(snippet);
	}

	private IScriptEvaluationResult evaluate(String snippet,
			IScriptStackFrame frame) {
		IScriptEvaluationResult result = null;
		try {
			final IDbgpSession session = thread.getDbgpSession();

			final IDbgpExtendedCommands extended = session
					.getExtendedCommands();

			final IDbgpProperty property = extended.evaluate(snippet);

			if (property != null) {
				IScriptValue value = ScriptValue.createValue(frame, property);
				result = new ScriptEvaluationResult(thread, snippet, value);
			} else {
				result = new FailedScriptEvaluationResult(
						thread,
						snippet,
						new String[] { Messages.ScriptEvaluationEngine_cantEvaluate });
			}

		} catch (Exception e) {
			// TODO: improve
			result = new FailedScriptEvaluationResult(thread, snippet,
					new String[] { e.getMessage() });
		}

		return result;
	}

	public ScriptEvaluationEngine(IScriptThread thread) {
		this.thread = thread;
		// this.count = thread.getModificationsCount();
		this.cache = new WeakHashMap();
	}

	public IScriptDebugTarget getScriptDebugTarget() {
		return (ScriptDebugTarget) thread.getDebugTarget();
	}

	public IScriptEvaluationResult syncEvaluate(String snippet,
			IScriptStackFrame frame) {
		snippet = snippet.trim();
		synchronized (cache) {
			IScriptEvaluationResult result = getFromCache(snippet);

			if (result == null) {
				result = evaluate(snippet, frame);
			}

			putToCache(snippet, result);

			return result;
		}
	}

	public void asyncEvaluate(final String snippet,
			final IScriptStackFrame frame,
			final IScriptEvaluationListener listener) {
		Job job = new Job(NLS.bind(
				Messages.ScriptEvaluationEngine_evaluationOf, snippet)) {
			protected IStatus run(IProgressMonitor monitor) {
				if (getScriptDebugTarget().isTerminated()) {
					listener.evaluationComplete(new NoEvaluationResult(snippet,
							thread));
				} else {
					listener.evaluationComplete(syncEvaluate(snippet, frame));
				}
				return Status.OK_STATUS;
			}
		};

		job.setSystem(true);
		job.setUser(false);
		job.schedule();
	}

	public void dispose() {
		// TODO Auto-generated method stub
	}
}
