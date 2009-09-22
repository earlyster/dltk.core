package org.eclipse.dltk.core.model.binary;

import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.utils.CorePrinter;

/**
 * @since 2.0
 */
public class BinaryMethod extends BinaryMember implements IMethod {

	private String[] parameters;
	private boolean isConstructur;
	private String[] parameterInitializers;

	public BinaryMethod(ModelElement parent, String name) {
		super(parent, name);
	}

	@Override
	public void printNode(CorePrinter output) {
	}

	public int getElementType() {
		return METHOD;
	}

	@Override
	protected char getHandleMementoDelimiter() {
		return JEM_USER_ELEMENT;
	}

	public boolean equals(Object o) {
		if (!(o instanceof BinaryMethod)) {
			return false;
		}
		return super.equals(o);
	}

	public String getFullyQualifiedName(String enclosingTypeSeparator) {
		try {
			return getFullyQualifiedName(enclosingTypeSeparator, false/*
																	 * don't
																	 * show
																	 * parameters
																	 */);
		} catch (ModelException e) {
			// exception thrown only when showing parameters
			return null;
		}
	}

	public String getFullyQualifiedName() {
		return getFullyQualifiedName("$"); //$NON-NLS-1$
	}

	public String[] getParameterInitializers() throws ModelException {
		return parameterInitializers;
	}

	public String[] getParameters() throws ModelException {
		return parameters;
	}

	public boolean isConstructor() throws ModelException {
		return isConstructur;
	}

	void setParameters(String[] parameterNames) {
		this.parameters = parameterNames;
	}

	void setIsConstructur(boolean isConstructor) {
		this.isConstructur = isConstructor;
	}

	void setParameterInitializers(String[] parameterInitializers) {
		this.parameterInitializers = parameterInitializers;
	}
}
