package org.eclipse.dltk.debug.core.model;

public class ComplexScriptType extends AtomicScriptType {

	public ComplexScriptType(String name) {
		super(name);
	}

	public boolean isAtomic() {
		return false;
	}

	public boolean isComplex() {
		return true;
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
