package org.eclipse.dltk.debug.core.model;

import org.eclipse.debug.core.DebugException;
import org.eclipse.debug.core.model.IVariable;

public class CollectionScriptType implements IScriptType {

	private static final int MAX_STRING_VALUE = 512;

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
		final StringBuffer sb = new StringBuffer(MAX_STRING_VALUE);
		sb.append(value.getRawValue()); // == Array

		try {
			IVariable[] variables2 = value.getVariables();
			sb.append("[");
			sb.append(variables2.length);
			sb.append("]");
			if (variables2.length > 0) {
				sb.append("{"); // == Array
				for (int i = 0; i < variables2.length; i++) {
					sb.append(variables2[i].getValue().getValueString());
					sb.append(",");

					if (sb.length() >= MAX_STRING_VALUE) {
						sb.append("....");
						break;
					}
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
}
