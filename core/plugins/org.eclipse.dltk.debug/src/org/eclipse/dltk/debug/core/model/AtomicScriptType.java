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
}
