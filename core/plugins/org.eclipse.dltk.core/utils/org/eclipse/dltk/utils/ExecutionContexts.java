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
