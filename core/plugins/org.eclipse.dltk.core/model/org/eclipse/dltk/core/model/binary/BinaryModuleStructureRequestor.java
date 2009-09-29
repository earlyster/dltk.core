/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 *******************************************************************************/
package org.eclipse.dltk.core.model.binary;

import java.util.Map;
import java.util.Stack;

import org.eclipse.dltk.compiler.IBinaryElementRequestor;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.core.ImportContainer;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.ModelManager;

/**
 * @since 2.0
 */
public class BinaryModuleStructureRequestor implements IBinaryElementRequestor {

	private final static String[] EMPTY = new String[0];

	/**
	 * The handle to the Binary module being parsed
	 */
	private IBinaryModule module;

	/**
	 * The import container info - null until created
	 */
	protected Map<String, ImportContainer> importContainers = null;

	/**
	 * Stack of parent handles, corresponding to the info stack. We keep both,
	 * since info objects do not have back pointers to handles.
	 */
	private Stack<ModelElement> handleStack;

	private BinaryModuleElementInfo moduleInfo;

	protected boolean hasSyntaxErrors = false;

	private SourceMapper mapper;

	public BinaryModuleStructureRequestor(IBinaryModule module,
			BinaryModuleElementInfo moduleInfo, SourceMapper mapper) {
		this.module = module;
		this.moduleInfo = moduleInfo;
		this.mapper = mapper;
	}

	public void enterModule() {
		this.handleStack = new Stack<ModelElement>();
		this.enterModuleRoot();
	}

	public void enterModuleRoot() {
		this.handleStack.push((ModelElement) this.module);
	}

	public void enterField(FieldInfo fieldInfo) {
		ModelElement parentHandle = this.handleStack.peek();
		this.createField(fieldInfo, parentHandle);
	}

	private void createField(FieldInfo fieldInfo, ModelElement parentHandle) {
		ModelManager manager = ModelManager.getModelManager();

		BinaryField handle = new BinaryField(parentHandle, manager
				.intern(fieldInfo.name));

		addChild(parentHandle, handle);
		handle.setFlags(fieldInfo.modifiers);
		if (mapper != null) {
			mapper.reportField(fieldInfo, handle);
		}
		this.handleStack.push(handle);
	}

	protected void addChild(ModelElement parentHandle, ModelElement handle) {
		if (parentHandle instanceof BinaryMember) {
			((BinaryMember) parentHandle).addChild(handle);
		} else if (parentHandle instanceof ISourceModule) {
			if (this.moduleInfo != null) {
				this.moduleInfo.addChild(handle);
			}
		}
	}

	public void enterMethodRemoveSame(MethodInfo methodInfo) {
		this.enterMethod(methodInfo);
	}

	public void enterMethod(MethodInfo methodInfo) {
		ModelElement parentHandle = this.handleStack.peek();
		this.processMethod(methodInfo, parentHandle);
	}

	private void processMethod(MethodInfo methodInfo, ModelElement parentHandle) {
		String nameString = methodInfo.name;
		ModelManager manager = ModelManager.getModelManager();
		BinaryMethod handle = new BinaryMethod(parentHandle, manager
				.intern(nameString));

		String[] parameterNames = methodInfo.parameterNames == null ? EMPTY
				: methodInfo.parameterNames;

		String[] parameterInitializers = methodInfo.parameterInitializers == null ? EMPTY
				: methodInfo.parameterInitializers;

		handle.setParameters(parameterNames);
		handle.setParameterInitializers(parameterInitializers);
		handle.setIsConstructur(methodInfo.isConstructor);
		handle.setFlags(methodInfo.modifiers);
		if (mapper != null) {
			mapper.reportMethod(methodInfo, handle);
		}

		addChild(parentHandle, handle);
		this.handleStack.push(handle);
	}

	public boolean enterMethodWithParentType(MethodInfo info,
			String parentName, String delimiter) {
		enterMethod(info);
		return true;
	}

	public boolean enterFieldWithParentType(FieldInfo info, String parentName,
			String delimiter) {
		enterField(info);
		return true;
	}

	public void enterType(TypeInfo typeInfo) {
		ModelElement parentHandle = this.handleStack.peek();
		this.processType(typeInfo, parentHandle);
	}

	public boolean enterTypeAppend(String fullName, String delimiter) {
		return false;
	}

	private void processType(TypeInfo typeInfo, ModelElement parentHandle) {
		String nameString = typeInfo.name;
		BinaryType handle = new BinaryType(parentHandle, nameString);

		ModelManager manager = ModelManager.getModelManager();
		String[] superclasses = typeInfo.superclasses;
		for (int i = 0, length = superclasses == null ? 0 : superclasses.length; i < length; i++) {
			superclasses[i] = manager.intern(superclasses[i]);
		}
		handle.setSuperclassNames(superclasses);
		addChild(parentHandle, handle);
		if (mapper != null) {
			mapper.reportType(typeInfo, handle);
		}
		this.handleStack.push(handle);
	}

	public void exitModule(int declarationEnd) {
		// this.moduleInfo.setBinaryLength(declarationEnd + 1);

		// determine if there were any parsing errors
		if (this.moduleInfo != null) {
			this.moduleInfo.setIsStructureKnown(!this.hasSyntaxErrors);
		}
	}

	public void exitModuleRoot() {
		this.handleStack.pop();
	}

	public void exitField(int declarationEnd) {
		this.exitMember(declarationEnd);
	}

	public void exitMethod(int declarationEnd) {
		this.exitMember(declarationEnd);
	}

	public void exitType(int declarationEnd) {
		this.exitMember(declarationEnd);
	}

	protected void exitMember(int declarationEnd) {
		ModelElement element = this.handleStack.pop();
		if (mapper != null) {
			mapper.setRangeEnd(element, declarationEnd);
		}
	}

	public void acceptPackage(int declarationStart, int declarationEnd,
			char[] name) {
		ModelElement parentHandle = this.handleStack.peek();
		BinaryPackageDeclaration handle = new BinaryPackageDeclaration(
				parentHandle, new String(name));
		addChild(parentHandle, handle);

	}

	public void acceptFieldReference(char[] fieldName, int BinaryPosition) {
	}

	public void acceptMethodReference(char[] methodName, int argCount,
			int BinaryPosition, int BinaryEndPosition) {
	}

	public void acceptTypeReference(char[][] typeName, int BinaryStart,
			int BinaryEnd) {
	}

	public void acceptTypeReference(char[] typeName, int BinaryPosition) {
	}

	public void acceptImport(ImportInfo importInfo) {

	}

}
