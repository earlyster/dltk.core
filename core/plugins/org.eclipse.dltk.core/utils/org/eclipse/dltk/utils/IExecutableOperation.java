package org.eclipse.dltk.utils;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IExecutableOperation {

	String getOperationName();

	void execute(IProgressMonitor monitor);

}
