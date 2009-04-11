package org.eclipse.dltk.utils;

public interface IExecutionContextManager {

	boolean isRunningInUIThread();

	/**
	 * Executes specified operation in non-UI thread
	 * 
	 * @param operation
	 */
	void executeInBackground(IExecutableOperation operation);

}
