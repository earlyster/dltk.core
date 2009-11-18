package org.eclipse.dltk.core.caching;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.core.DLTKCore;

public class StructureModelCollector extends AbstractDataSaver implements
		ISourceElementRequestor, IStructureConstants {

	private ISourceElementRequestor baseRequestor;

	public StructureModelCollector(ISourceElementRequestor requestor) {
		super(new ByteArrayOutputStream());
		this.baseRequestor = requestor;
	}

	private void writeString(char[] fieldName) throws IOException {
		if (fieldName == null) {
			writeString((String) null);
		} else {
			writeString(new String(fieldName));
		}
	}

	private void writeString(char[][] typeName) throws IOException {
		if (typeName == null) {
			out.writeInt(0);
		} else {
			out.writeInt(typeName.length);
			for (int i = 0; i < typeName.length; i++) {
				writeString(typeName[i]);
			}
		}
	}

	private void writeString(String[] strs) throws IOException {
		if (strs == null) {
			out.writeInt(0);
		} else {
			out.writeInt(strs.length);
			for (int i = 0; i < strs.length; i++) {
				writeString(strs[i]);
			}
		}
	}

	public void acceptFieldReference(String fieldName, int sourcePosition) {
		this.baseRequestor.acceptFieldReference(fieldName, sourcePosition);
		try {
			out.writeInt(TAG_FIELD_REFERENCE);
			writeString(fieldName);
			out.writeInt(sourcePosition);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void acceptMethodReference(String methodName, int argCount,
			int sourcePosition, int sourceEndPosition) {
		this.baseRequestor.acceptMethodReference(methodName, argCount,
				sourcePosition, sourceEndPosition);
		try {
			out.writeInt(TAG_METHOD_REF1);
			writeString(methodName);
			out.writeInt(argCount);
			out.writeInt(sourcePosition);
			out.writeInt(sourceEndPosition);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void acceptPackage(int declarationStart, int declarationEnd,
			String name) {
		this.baseRequestor
				.acceptPackage(declarationStart, declarationEnd, name);
		try {
			out.writeInt(TAG_PACKAGE);
			writeString(name);
			out.writeInt(declarationStart);
			out.writeInt(declarationEnd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void acceptTypeReference(char[][] typeName, int sourceStart,
			int sourceEnd) {
		this.baseRequestor
				.acceptTypeReference(typeName, sourceStart, sourceEnd);
		try {
			out.writeInt(TAG_TYPE_REFERENCE1);
			writeString(typeName);
			out.writeInt(sourceStart);
			out.writeInt(sourceEnd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void acceptTypeReference(char[] typeName, int sourcePosition) {
		this.baseRequestor.acceptTypeReference(typeName, sourcePosition);
		try {
			out.writeInt(TAG_TYPE_REFERENCE2);
			writeString(typeName);
			out.writeInt(sourcePosition);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @since 2.0
	 */
	public void enterField(FieldInfo info) {
		this.baseRequestor.enterField(info);
		try {
			out.writeInt(TAG_ENTER_FIELD);
			writeFieldInfo(info);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void writeFieldInfo(FieldInfo info) throws IOException {
		writeElementInfo(info);
	}

	private void writeMethodInfo(MethodInfo info) throws IOException {
		writeElementInfo(info);
		writeString(info.parameterNames);
		writeString(info.parameterInitializers);
		writeString(info.exceptionTypes);
		out.writeBoolean(info.isConstructor);
	}

	private void writeTypeInfo(TypeInfo info) throws IOException {
		writeElementInfo(info);
		writeString(info.superclasses);
	}

	private void writeElementInfo(ElementInfo info) throws IOException {
		writeString(info.name);
		out.writeInt(info.modifiers);
		out.writeInt(info.nameSourceStart);
		out.writeInt(info.nameSourceEnd);
		out.writeInt(info.declarationStart);
	}

	/**
	 * @since 2.0
	 */
	public boolean enterFieldCheckDuplicates(FieldInfo info) {
		boolean result = this.baseRequestor.enterFieldCheckDuplicates(info);
		try {
			out.writeInt(TAG_ENTER_FIELD_DUPL);
			writeFieldInfo(info);
			out.writeBoolean(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @since 2.0
	 */
	public boolean enterFieldWithParentType(FieldInfo info, String parentName,
			String delimiter) {
		boolean result = this.baseRequestor.enterFieldWithParentType(info,
				parentName, delimiter);
		try {
			out.writeInt(TAG_ENTER_FIELD_WITH_PARENT);
			writeFieldInfo(info);
			writeString(parentName);
			writeString(delimiter);
			out.writeBoolean(result);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @since 2.0
	 */
	public void enterMethod(MethodInfo info) {
		this.baseRequestor.enterMethod(info);
		try {
			out.writeInt(TAG_ENTER_METHOD);
			writeMethodInfo(info);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @since 2.0
	 */
	public void enterMethodRemoveSame(MethodInfo info) {
		this.baseRequestor.enterMethodRemoveSame(info);
		try {
			out.writeInt(TAG_ENTER_METHOD_REMOVE_SAME);
			writeMethodInfo(info);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @since 2.0
	 */
	public boolean enterMethodWithParentType(MethodInfo info,
			String parentName, String delimiter) {
		boolean result = this.baseRequestor.enterMethodWithParentType(info,
				parentName, delimiter);
		try {
			out.writeInt(TAG_ENTER_METHOD_WITH_PARENT);
			writeMethodInfo(info);
			writeString(parentName);
			writeString(delimiter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void enterModule() {
		this.baseRequestor.enterModule();
		try {
			out.writeInt(TAG_ENTER_MODULE);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void enterModuleRoot() {
		this.baseRequestor.enterModuleRoot();
		try {
			out.writeInt(TAG_ENTER_MODULE_ROOT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @since 2.0
	 */
	public void enterType(TypeInfo info) {
		this.baseRequestor.enterType(info);
		try {
			out.writeInt(TAG_ENTER_TYPE);
			writeTypeInfo(info);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean enterTypeAppend(String fullName, String delimiter) {
		boolean result = this.baseRequestor
				.enterTypeAppend(fullName, delimiter);
		try {
			out.writeInt(TAG_ENTER_TYPE_APPEND);
			writeString(fullName);
			writeString(delimiter);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}

	public void exitField(int declarationEnd) {
		this.baseRequestor.exitField(declarationEnd);
		try {
			out.writeInt(TAG_EXIT_FIELD);
			out.writeInt(declarationEnd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exitMethod(int declarationEnd) {
		this.baseRequestor.exitMethod(declarationEnd);
		try {
			out.writeInt(TAG_EXIT_METHOD);
			out.writeInt(declarationEnd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exitModule(int declarationEnd) {
		this.baseRequestor.exitModule(declarationEnd);
		try {
			out.writeInt(TAG_EXIT_MODULE);
			out.writeInt(declarationEnd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exitModuleRoot() {
		this.baseRequestor.exitModuleRoot();
		try {
			out.writeInt(TAG_EXIT_MODULE_ROOT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void exitType(int declarationEnd) {
		this.baseRequestor.exitType(declarationEnd);
		try {
			out.writeInt(TAG_EXIT_TYPE);
			out.writeInt(declarationEnd);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @since 2.0
	 */
	public void acceptImport(ImportInfo importInfo) {
		this.baseRequestor.acceptImport(importInfo);
		try {
			out.writeInt(TAG_ACCEPT_IMPORT);
			out.writeInt(importInfo.sourceStart);
			out.writeInt(importInfo.sourceEnd);
			writeString(importInfo.containerName);
			writeString(importInfo.name);
			writeString(importInfo.version);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public byte[] getBytes() {
		try {
			stream.flush();
			storeStringIndex();
		} catch (IOException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
		return ((ByteArrayOutputStream) stream).toByteArray();
	}
}
