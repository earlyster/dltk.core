package org.eclipse.dltk.internal.debug.core.model;

import org.eclipse.dltk.dbgp.IDbgpStackLevel;
import org.eclipse.dltk.dbgp.exceptions.DbgpException;
import org.eclipse.dltk.debug.core.DLTKDebugPlugin;
import org.eclipse.dltk.debug.core.model.IScriptStack;
import org.eclipse.dltk.debug.core.model.IScriptStackFrame;

public class ScriptStack implements IScriptStack {
	public static final IScriptStackFrame[] NO_STACK_FRAMES = new IScriptStackFrame[0];

	private IScriptStackFrame[] frames;
	private final Object framesLock = new Object();

	private final ScriptThread thread;

	public ScriptStack(ScriptThread thread) {
		this.thread = thread;
		this.frames = NO_STACK_FRAMES;
	}

	public void update(boolean logErrors) {
		try {
			readFrames();
		} catch (DbgpException e) {
			if (logErrors) {
				DLTKDebugPlugin.log(e);
			}
		}
	}

	protected IDbgpStackLevel[] requrestStackLevels() throws DbgpException {
		return thread.getDbgpSession().getCoreCommands().getStackLevels();
	}

	protected void readFrames() throws DbgpException {
		final IDbgpStackLevel[] levels = requrestStackLevels();
		synchronized (framesLock) {
			final int newSize = levels.length;
			final int oldSize = frames.length;
			final int numToRebind = Math.min(newSize, oldSize);
			final ScriptStackFrame[] newFrames = new ScriptStackFrame[newSize];
			for (int depth = 0; depth < numToRebind; ++depth) {
				final ScriptStackFrame oldFrame = (ScriptStackFrame) frames[oldSize
						- depth - 1];
				newFrames[newSize - depth - 1] = oldFrame.bind(levels[newSize
						- depth - 1]);
			}
			final int newCount = newSize - oldSize;
			for (int i = 0; i < newCount; ++i) {
				newFrames[i] = new ScriptStackFrame(this, levels[i]);
			}
			frames = newFrames;
		}
	}

	public ScriptThread getThread() {
		return thread;
	}

	public int size() {
		synchronized (framesLock) {
			return frames.length;
		}
	}

	public boolean hasFrames() {
		synchronized (framesLock) {
			return frames.length > 0;
		}
	}

	public IScriptStackFrame[] getFrames() {
		synchronized (framesLock) {
			return frames;
		}
	}

	public IScriptStackFrame getTopFrame() {
		synchronized (framesLock) {
			return frames.length > 0 ? frames[0] : null;
		}
	}

	public void updateFrames() {
		synchronized (framesLock) {
			for (int i = 0; i < frames.length; i++) {
				((ScriptStackFrame) frames[i]).updateVariables();
			}
		}
	}

	/**
	 * @return
	 */
	public boolean isInitialized() {
		synchronized (framesLock) {
			return frames != NO_STACK_FRAMES;
		}
	}
}
