package org.eclipse.dltk.utils;

import org.eclipse.core.runtime.NullProgressMonitor;

public class ExecutionContexts {

	private static IExecutionContextManager fManager;

	private static IExecutionContextManager fDefaultManager = new IExecutionContextManager() {

		public void executeInBackground(IExecutableOperation operation) {
			operation.execute(new NullProgressMonitor());
		}

		public boolean isRunningInUIThread() {
			return false;
		}

	};

	public static synchronized IExecutionContextManager getManager() {
		if (fManager != null) {
			return fManager;
		} else {
			return fDefaultManager;
		}
	}

	public static synchronized void setManager(IExecutionContextManager manager) {
		fManager = manager;
	}

}
