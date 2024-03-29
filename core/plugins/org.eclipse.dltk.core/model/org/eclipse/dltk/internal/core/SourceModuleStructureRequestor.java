/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 *******************************************************************************/
package org.eclipse.dltk.internal.core;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.compiler.ISourceElementRequestorExtension;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.INamespace;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;

public class SourceModuleStructureRequestor implements ISourceElementRequestor,
		ISourceElementRequestorExtension {

	private DuplicateResolver.Resolver counters = DuplicateResolver.create();

	private final static String[] EMPTY = new String[0];

	/**
	 * The handle to the source module being parsed
	 */
	private ISourceModule module;

	/**
	 * The info object for the module being parsed
	 */
	private SourceModuleElementInfo moduleInfo;

	private Stack<INamespace> namespaces = new Stack<INamespace>();

	/**
	 * The import container info - null until created
	 */
	protected Map<String, ImportContainer> importContainers = null;

	/**
	 * Hashtable of children elements of the source module. Children are added
	 * to the table as they are found by the parser. Keys are handles, values
	 * are corresponding info objects.
	 */
	private Map<IModelElement, ModelElementInfo> newElements;

	/**
	 * Stack of parent scope info objects. The info on the top of the stack is
	 * the parent of the next element found. For example, when we locate a
	 * method, the parent info object will be the type the method is contained
	 * in.
	 */
	private Stack<ModelElementInfo> infoStack;

	/**
	 * Stack of parent handles, corresponding to the info stack. We keep both,
	 * since info objects do not have back pointers to handles.
	 */
	private Stack<ModelElement> handleStack;

	protected boolean hasSyntaxErrors = false;

	public SourceModuleStructureRequestor(ISourceModule module,
			SourceModuleElementInfo moduleInfo, Map newElements) {
		this.module = module;
		this.moduleInfo = moduleInfo;
		this.newElements = newElements;
	}

	/**
	 * Resolves duplicate handles by incrementing the occurrence count of the
	 * handle being created until there is no conflict.
	 */
	protected void resolveDuplicates(SourceRefElement handle) {
		counters.resolveDuplicates(handle);
		Assert.isTrue(!this.newElements.containsKey(handle));
	}

	public void enterModule() {
		this.infoStack = new Stack<ModelElementInfo>();
		this.handleStack = new Stack<ModelElement>();
		this.enterModuleRoot();
	}

	public void enterModuleRoot() {
		this.infoStack.push(this.moduleInfo);
		this.handleStack.push((ModelElement) this.module);
	}

	public void enterField(FieldInfo fieldInfo) {

		ModelElementInfo parentInfo = this.infoStack.peek();
		ModelElement parentHandle = this.handleStack.peek();

		this.createField(fieldInfo, parentInfo, parentHandle);
	}

	private void createField(FieldInfo fieldInfo, ModelElementInfo parentInfo,
			ModelElement parentHandle) {
		ModelManager manager = ModelManager.getModelManager();

		SourceField handle = new SourceField(parentHandle,
				manager.intern(fieldInfo.name));
		this.resolveDuplicates(handle);

		SourceFieldElementInfo info = new SourceFieldElementInfo();
		info.setNameSourceStart(fieldInfo.nameSourceStart);
		info.setNameSourceEnd(fieldInfo.nameSourceEnd);
		info.setSourceRangeStart(fieldInfo.declarationStart);
		info.setFlags(fieldInfo.modifiers);
		info.setType(fieldInfo.type);

		parentInfo.addChild(handle);
		this.newElements.put(handle, info);

		this.infoStack.push(info);
		this.handleStack.push(handle);
	}

	/*
	 * @see ISourceElementRequestor#updateField(FieldInfo, int)
	 */
	public void updateField(FieldInfo fieldInfo, int flags) {
		final ModelElement handle = this.handleStack.peek();
		if (handle instanceof SourceField) {
			final SourceField field = (SourceField) handle;
			if (fieldInfo.name.equals(field.getElementName())) {
				final SourceFieldElementInfo info = (SourceFieldElementInfo) this.infoStack
						.peek();
				if ((flags & UPDATE_TYPE) != 0) {
					info.setType(fieldInfo.type);
				}
			}
		}
	}

	public boolean enterFieldCheckDuplicates(FieldInfo fieldInfo,
			ModelElementInfo parentInfo, ModelElement parentHandle) {
		IModelElement[] childrens = parentInfo.getChildren();
		for (int i = 0; i < childrens.length; ++i) {
			if (childrens[i] instanceof SourceField
					&& childrens[i].getElementName().equals(fieldInfo.name)) {
				// we should go inside existent element
				SourceField handle = (SourceField) childrens[i];
				SourceFieldElementInfo info = (SourceFieldElementInfo) this.newElements
						.get(handle);
				this.infoStack.push(info);
				this.handleStack.push(handle);
				return true;
			}
		}
		if (parentInfo instanceof SourceMethodElementInfo) {
			SourceMethodElementInfo method = (SourceMethodElementInfo) parentInfo;
			String[] args = method.getArgumentNames();
			for (int i = 0; i < args.length; ++i) {
				if (args[i].equals(fieldInfo.name)) {
					return false;
				}
			}
		}
		this.createField(fieldInfo, parentInfo, parentHandle);
		return true;
	}

	public boolean enterFieldCheckDuplicates(FieldInfo fieldInfo) {
		ModelElementInfo parentInfo = this.infoStack.peek();
		ModelElement parentHandle = this.handleStack.peek();
		return this.enterFieldCheckDuplicates(fieldInfo, parentInfo,
				parentHandle);
	}

	public void enterMethodRemoveSame(MethodInfo methodInfo) {
		ModelElementInfo parentInfo = this.infoStack.peek();
		IModelElement[] childrens = parentInfo.getChildren();
		for (int i = 0; i < childrens.length; ++i) {
			if (childrens[i].getElementName().equals(methodInfo.name)) {
				parentInfo.removeChild(childrens[i]);
			}
		}
		this.enterMethod(methodInfo);
	}

	public void enterMethod(MethodInfo methodInfo) {
		ModelElementInfo parentInfo = this.infoStack.peek();
		ModelElement parentHandle = this.handleStack.peek();

		this.processMethod(methodInfo, parentInfo, parentHandle);
	}

	private void processMethod(MethodInfo methodInfo,
			ModelElementInfo parentInfo, ModelElement parentHandle) {
		String nameString = methodInfo.name;
		ModelManager manager = ModelManager.getModelManager();
		SourceMethod handle = new SourceMethod(parentHandle,
				manager.intern(nameString));
		this.resolveDuplicates(handle);

		SourceMethodElementInfo info = new SourceMethodElementInfo();
		info.setSourceRangeStart(methodInfo.declarationStart);
		info.setFlags(methodInfo.modifiers);
		info.setNameSourceStart(methodInfo.nameSourceStart);
		info.setNameSourceEnd(methodInfo.nameSourceEnd);
		info.setIsConstructor(methodInfo.isConstructor);
		info.setReturnType(methodInfo.returnType);

		String[] parameterNames = methodInfo.parameterNames == null ? EMPTY
				: methodInfo.parameterNames;
		if (parameterNames.length == 0) {
			info.setArguments(SourceMethodUtils.NO_PARAMETERS);
		} else {
			final MethodParameterInfo[] params = new MethodParameterInfo[parameterNames.length];
			for (int i = 0, length = parameterNames.length; i < length; i++) {
				String name = manager.intern(parameterNames[i]);
				String type = null;
				String defaultValue = null;
				if (methodInfo.parameterTypes != null
						&& i < methodInfo.parameterTypes.length) {
					type = methodInfo.parameterTypes[i];
					if (type != null) {
						type = manager.intern(type);
					}
				}
				if (methodInfo.parameterInitializers != null
						&& i < methodInfo.parameterInitializers.length) {
					defaultValue = methodInfo.parameterInitializers[i];
					if (defaultValue != null) {
						defaultValue = manager.intern(defaultValue);
					}
				}
				params[i] = new MethodParameterInfo(name, type, defaultValue);
			}
			info.setArguments(params);
		}

		parentInfo.addChild(handle);
		this.newElements.put(handle, info);
		this.infoStack.push(info);
		this.handleStack.push(handle);
	}

	/**
	 * Returns type in which we currently are. If we are not in type, returns
	 * null.
	 * 
	 * @return
	 */
	private SourceType getCurrentType() {
		SourceType t = null;
		for (ModelElement o : this.handleStack) {
			if (o instanceof SourceType) {
				t = (SourceType) o;
			}
		}
		return t;
	}

	/**
	 * Searches for a type already in the model. If founds, returns it. If
	 * <code>parentName</code> starts with a delimiter, searches starting from
	 * current source module (i.e. in global), else from the current level.
	 * 
	 * @param parentName
	 * @param delimiter
	 * @return null if type not found
	 */
	private SourceType getExistentType(String parentName, String delimiter) {
		try {
			SourceType element = null;
			if (parentName.startsWith(delimiter)) {
				if (this.module != null) {
					element = this.findTypeFrom(this.module.getChildren(),
							"", parentName, delimiter); //$NON-NLS-1$
				}
				return element;
			} else {
				parentName = delimiter + parentName;
				SourceType enc = this.getCurrentType();
				if (enc == null) {
					if (this.module != null) {
						element = this.findTypeFrom(this.module.getChildren(),
								"", parentName, delimiter); //$NON-NLS-1$
					}
				} else {
					element = this.findTypeFrom(enc.getChildren(),
							"", parentName, delimiter); //$NON-NLS-1$
				}
				return element;
			}

		} catch (ModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	private SourceType findTypeFrom(IModelElement[] childs, String name,
			String parentName, String delimiter) {
		try {
			for (int i = 0; i < childs.length; ++i) {
				if (childs[i] instanceof SourceType) {
					SourceType type = (SourceType) childs[i];
					String qname = name + delimiter + type.getElementName();
					if (qname.equals(parentName)) {
						return type;
					}
					SourceType val = this.findTypeFrom(type.getChildren(),
							qname, parentName, delimiter);
					if (val != null) {
						return val;
					}
				}
			}
		} catch (ModelException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void enterType(TypeInfo typeInfo) {
		ModelElementInfo parentInfo = this.infoStack.peek();
		ModelElement parentHandle = this.handleStack.peek();
		this.processType(typeInfo, parentInfo, parentHandle);
	}

	public boolean enterTypeAppend(String fullName, String delimiter) {
		try {
			ModelElement element = this.getExistentType(fullName, delimiter);
			if (element == null) {
				return false;
			} else {
				ModelElementInfo info = (ModelElementInfo) element
						.getElementInfo();
				this.infoStack.push(info);
				this.handleStack.push(element);
				return true;
			}
		} catch (ModelException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void enterNamespace(String[] namespace) {
		namespaces.push(new SourceNamespace(namespace));
	}

	public void exitNamespace() {
		namespaces.pop();
	}

	private void processType(TypeInfo typeInfo, ModelElementInfo parentInfo,
			ModelElement parentHandle) {
		String nameString = typeInfo.name;
		SourceType handle = new SourceType(parentHandle, nameString);
		// NB: occurenceCount is computed in resolveDuplicates
		this.resolveDuplicates(handle);

		SourceTypeElementInfo info = new SourceTypeElementInfo();
		if (parentHandle.getElementType() == IModelElement.SOURCE_MODULE
				&& !namespaces.isEmpty()) {
			// TODO review the condition above
			info.setNamespace(namespaces.peek());
		}
		info.setHandle(handle);
		info.setSourceRangeStart(typeInfo.declarationStart);
		info.setFlags(typeInfo.modifiers);
		info.setNameSourceStart(typeInfo.nameSourceStart);
		info.setNameSourceEnd(typeInfo.nameSourceEnd);
		ModelManager manager = ModelManager.getModelManager();
		String[] superclasses = typeInfo.superclasses;
		for (int i = 0, length = superclasses == null ? 0 : superclasses.length; i < length; i++) {
			superclasses[i] = manager.intern(superclasses[i]);
		}
		info.setSuperclassNames(superclasses);
		parentInfo.addChild(handle);
		this.newElements.put(handle, info);
		this.infoStack.push(info);
		this.handleStack.push(handle);
	}

	public void exitModule(int declarationEnd) {
		this.moduleInfo.setSourceLength(declarationEnd + 1);

		// determine if there were any parsing errors
		this.moduleInfo.setIsStructureKnown(!this.hasSyntaxErrors);
	}

	public void exitModuleRoot() {
		this.infoStack.pop();
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
		Object object = this.infoStack.pop();
		SourceRefElementInfo info = (SourceRefElementInfo) object;
		info.setSourceRangeEnd(declarationEnd);
		this.handleStack.pop();
	}

	public void acceptPackage(int declarationStart, int declarationEnd,
			String name) {
		ModelElementInfo parentInfo = this.infoStack.peek();
		ModelElement parentHandle = this.handleStack.peek();
		PackageDeclaration handle = null;

		// if (parentHandle.getElementType() == IModelElement.SOURCE_MODULE) {
		handle = new PackageDeclaration(parentHandle, name);

		this.resolveDuplicates(handle);

		SourceRefElementInfo info = new SourceRefElementInfo();
		info.setSourceRangeStart(declarationStart);
		info.setSourceRangeEnd(declarationEnd);

		parentInfo.addChild(handle);
		this.newElements.put(handle, info);
	}

	public void acceptFieldReference(String fieldName, int sourcePosition) {
	}

	public void acceptMethodReference(String methodName, int argCount,
			int sourcePosition, int sourceEndPosition) {
	}

	public void acceptTypeReference(String typeName, int sourcePosition) {
	}

	public void acceptImport(ImportInfo importInfo) {
		final ModelElement parentHandle = this.handleStack.peek();
		final ISourceModule parentCU = parentHandle.getSourceModule();

		final ImportContainerInfo importContainerInfo;
		ImportContainer importContainer;

		// create the import container and its info
		if (this.importContainers == null) {
			importContainers = new HashMap<String, ImportContainer>();
		}
		String containerName = importInfo.containerName;
		if (containerName == null) {
			containerName = Util.EMPTY_STRING;
		}
		importContainer = importContainers.get(containerName);
		if (importContainer == null) {
			importContainer = createImportContainer(parentCU, containerName);
			importContainers.put(containerName, importContainer);
			importContainerInfo = new ImportContainerInfo();
			ModelElementInfo parentInfo = this.infoStack.peek();
			parentInfo.addChild(importContainer);
			this.newElements.put(importContainer, importContainerInfo);
		} else {
			importContainerInfo = (ImportContainerInfo) newElements
					.get(importContainer);
		}

		String elementName = ModelManager.getModelManager().intern(
				importInfo.name);
		ImportDeclaration handle = createImportDeclaration(importContainer,
				elementName, importInfo.version);
		resolveDuplicates(handle);

		ImportDeclarationElementInfo info = new ImportDeclarationElementInfo();
		info.setSourceRangeStart(importInfo.sourceStart);
		info.setSourceRangeEnd(importInfo.sourceEnd);

		importContainerInfo.addChild(handle);
		this.newElements.put(handle, info);
	}

	protected ImportContainer createImportContainer(ISourceModule parent,
			String container) {
		return new ImportContainer((AbstractSourceModule) parent, container);
	}

	protected ImportDeclaration createImportDeclaration(ImportContainer parent,
			String name, String version) {
		return new ImportDeclaration(parent, name, version);
	}

	public int getMode() {
		return MODE_STRUCTURE;
	}

}
