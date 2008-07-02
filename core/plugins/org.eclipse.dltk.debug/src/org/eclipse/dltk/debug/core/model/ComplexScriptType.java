package org.eclipse.dltk.debug.core.model;

public class ComplexScriptType implements IScriptType {
	private String name;

	public ComplexScriptType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public boolean isAtomic() {
		return false;
	}

	public boolean isCollection() {
		return false;
	}

	public boolean isString() {
		return false;
	}

	public String formatValue(IScriptValue value) {
		StringBuffer sb = new StringBuffer();
		sb.append(getName());
		String id = value.getInstanceId();
		if (id != null) {
			sb.append(" (id = " + id + ")"); // TODO add constant //$NON-NLS-1$ //$NON-NLS-2$
		}

		return sb.toString();
	}
}
