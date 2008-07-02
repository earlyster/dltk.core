package org.eclipse.dltk.debug.core.model;

public interface IScriptTypeFactory {

	static String STRING = "string";
	static String ARRAY = "array";
	static String HASH = "hash";

	IScriptType buildType(String type);
}
