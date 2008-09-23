package org.eclipse.dltk.debug.core.model;

public class AtomicScriptType implements IScriptType {
	private String name;

	public AtomicScriptType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public boolean isAtomic() {
		return true;
	}

	public boolean isComplex() {
		return false;
	}

	public boolean isCollection() {
		return false;
	}

	public boolean isString() {
		return false;
	}

	public String formatDetails(IScriptValue value) {
		return formatValue(value);
	}

	public String formatValue(IScriptValue value) {
		return value.getRawValue();
	}

	protected void appendInstanceId(IScriptValue value, StringBuffer buffer) {
		String id = value.getInstanceId();
		if (id == null) {
			id = "?"; //$NON-NLS-1$
		}

		buffer.append(" ("); //$NON-NLS-1$
		buffer.append(ScriptModelMessages.variableInstanceId + "=" + id); //$NON-NLS-1$
		buffer.append(")"); //$NON-NLS-1$
	}
}
