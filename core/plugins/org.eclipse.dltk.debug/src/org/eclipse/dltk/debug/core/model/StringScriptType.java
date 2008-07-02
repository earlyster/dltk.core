package org.eclipse.dltk.debug.core.model;

public class StringScriptType extends AtomicScriptType {
	public StringScriptType(String name) {
		super(name);
	}

	public boolean isString() {
		return true;
	}

	public String formatValue(IScriptValue value) {
		String string = value.getRawValue();

		if (string == null) {
			return null;
		}
		StringBuffer escaped = new StringBuffer();
		if ((!string.startsWith("'") || !string.endsWith("'")) //$NON-NLS-1$ //$NON-NLS-2$
				&& (!string.startsWith("\"") || !string.endsWith("\""))) //$NON-NLS-1$ //$NON-NLS-2$
			escaped.append('"');
		for (int i = 0; i < string.length(); i++) {
			char c = string.charAt(i);
			switch (c) {
			case '"':
				escaped.append("\\\""); //$NON-NLS-1$
				break;
			default:
				escaped.append(c);
				break;
			}
		}
		if ((!string.startsWith("'") || !string.endsWith("'")) //$NON-NLS-1$ //$NON-NLS-2$
				&& (!string.startsWith("\"") || !string.endsWith("\""))) //$NON-NLS-1$ //$NON-NLS-2$
			escaped.append('"');
		return escaped.toString();
	}
}
