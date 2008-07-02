package org.eclipse.dltk.debug.core.model;

public interface IScriptTypeFactory {

	static String STRING = "string"; //$NON-NLS-1$
	static String ARRAY = "array"; //$NON-NLS-1$
	static String HASH = "hash"; //$NON-NLS-1$

	IScriptType buildType(String type);
}
