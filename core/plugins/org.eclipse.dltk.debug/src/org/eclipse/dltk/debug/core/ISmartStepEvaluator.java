package org.eclipse.dltk.debug.core;

import org.eclipse.dltk.debug.core.model.IScriptThread;

public interface ISmartStepEvaluator {

	/**
	 * Tests if the stack of the specified thread should be skipped. Returns
	 * <code>true</code> if execution should automatically continue or
	 * <code>false</code> if not.
	 * 
	 * @param filters
	 * @param thread
	 * @return
	 */
	boolean isFiltered(String[] filters, IScriptThread thread);
}
