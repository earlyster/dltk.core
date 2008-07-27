package org.eclipse.dltk.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

public class CollectionScriptType extends AtomicScriptType {

	private static final int MAX_STRING_VALUE = 512;

	protected CollectionScriptType(String name) {
		super(name);
	}

	public boolean isAtomic() {
		return false;
	}

	public boolean isCollection() {
		return true;
	}

	protected void addInstanceId(IScriptValue value, StringBuffer buffer) {
		String id = value.getInstanceId();
		if (id != null) {
			buffer.append(" (id = " + id + ")"); // TODO add constant //$NON-NLS-1$ //$NON-NLS-2$
		}

	}

	public String formatDetails(IScriptValue value) {
		final StringBuffer sb = new StringBuffer();
		try {
			IVariable[] variables2 = value.getVariables();
			if (variables2.length > 0) {
				sb.append("{"); // == Array
				for (int i = 0; i < variables2.length; i++) {
					sb.append(variables2[i].getValue().getValueString());
					sb.append(",");
				}
				sb.setLength(sb.length() - 1);
				sb.append("}"); // == Array
			}
		} catch (DebugException ex) {
			ex.printStackTrace();
		}

		addInstanceId(value, sb);

		return sb.toString();
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
