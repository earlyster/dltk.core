package org.eclipse.dltk.debug.core.model;

import org.eclipse.debug.core.DebugException;

public class CollectionScriptType implements IScriptType {
	private String name;

	protected CollectionScriptType(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public boolean isAtomic() {
		return false;
	}

	public boolean isCollection() {
		return true;
	}

	public boolean isString() {
		return false;
	}

	protected void addInstanceId(IScriptValue value, StringBuffer buffer) {
		String id = value.getInstanceId();
		if (id != null) {
			buffer.append(" (id = " + id + ")"); // TODO add constant //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	public String formatValue(IScriptValue value) {
		StringBuffer sb = new StringBuffer();

		try {
			if (value.getVariables().length > 0)
				sb.append(value.getVariable(0).getReferenceTypeName());
			else
				sb.append(getName());
		} catch (DebugException e) {
			sb.append(getName());
		}

		try {
			sb.append("[" + value.getVariables().length + "]"); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (DebugException e) {
			sb.append("[]"); //$NON-NLS-1$
		}

		addInstanceId(value, sb);

		return sb.toString();
	}
}
