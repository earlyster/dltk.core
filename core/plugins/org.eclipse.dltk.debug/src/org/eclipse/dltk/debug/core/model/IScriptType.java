package org.eclipse.dltk.debug.core.model;

public interface IScriptType {
	String getName();

	boolean isAtomic();

	boolean isCollection();

	boolean isString();

	boolean isComplex();

	String formatValue(IScriptValue value);

	String formatDetails(IScriptValue scriptValue);
}
