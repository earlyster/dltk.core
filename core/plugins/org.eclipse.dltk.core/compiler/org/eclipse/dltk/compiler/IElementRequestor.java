package org.eclipse.dltk.compiler;

/**
 * @since 2.0
 */
public interface IElementRequestor {
	public abstract static class ElementInfo {
		public int declarationStart;
		public int modifiers;
		public String name;
		public int nameSourceStart;
		public int nameSourceEnd;
	}

	public static class TypeInfo extends ElementInfo {
		public String[] superclasses;
	}

	public static class MethodInfo extends ElementInfo {
		public String[] parameterNames;
		public String[] parameterInitializers;
		public String[] parameterTypes;
		public String[] exceptionTypes;
		public String returnType;
		public boolean isConstructor;
	}

	public static class FieldInfo extends ElementInfo {
		public String type;
	}

	public static class ImportInfo {
		public String containerName;
		public String name;
		public String version;
		public int sourceStart;
		public int sourceEnd;
	}

	void acceptFieldReference(String fieldName, int sourcePosition);

	void acceptImport(ImportInfo importInfo);

	void acceptMethodReference(String methodName, int argCount,
			int sourcePosition, int sourceEndPosition);

	void acceptPackage(int declarationStart, int declarationEnd, String name);

	void acceptTypeReference(char[] typeName, int sourcePosition);

	void acceptTypeReference(char[][] typeName, int sourceStart, int sourceEnd);

	void enterField(FieldInfo info);

	void enterMethod(MethodInfo info);

	void enterModule();

	void enterModuleRoot();

	void enterType(TypeInfo info);

	void exitField(int declarationEnd);

	void exitMethod(int declarationEnd);

	void exitModule(int declarationEnd);

	void exitModuleRoot();

	void exitType(int declarationEnd);

}
