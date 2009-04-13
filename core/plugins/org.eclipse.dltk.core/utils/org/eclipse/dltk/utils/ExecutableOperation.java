package org.eclipse.dltk.utils;

public abstract class ExecutableOperation implements IExecutableOperation {

	private final String operationName;

	public ExecutableOperation(String operationName) {
		this.operationName = operationName;
	}

	public String getOperationName() {
		return operationName;
	}

}
