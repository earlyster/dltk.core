/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.core.search.indexing;

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.compiler.IBinaryElementRequestor;
import org.eclipse.dltk.compiler.ISourceElementRequestor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISearchFactory;
import org.eclipse.dltk.core.ISearchPatternProcessor;

/**
 * This class is used by the JavaParserIndexer. When parsing thescriptfile, the
 * requestor recognises thescriptelements (methods, fields, ...) and add them to
 * an index.
 */
public class SourceIndexerRequestor implements ISourceElementRequestor,
		IBinaryElementRequestor, IIndexConstants, IIndexRequestor {
	protected AbstractIndexer indexer;
	// char[] packageName = CharOperation.NO_CHAR;
	protected char[][] enclosingTypeNames = new char[5][];
	protected int depth = 0;
	protected int methodDepth = 0;
	protected char[] pkgName = CharOperation.NO_CHAR;

	protected ISearchFactory searchFactory;
	protected ISearchPatternProcessor searchPatternProcessor;

	public SourceIndexerRequestor(AbstractIndexer indexer) {
		this.indexer = indexer;
	}

	public SourceIndexerRequestor() {
	}

	public void setIndexer(AbstractIndexer indexer) {
		this.indexer = indexer;
	}

	public void setSearchFactory(ISearchFactory searchFactory) {
		this.searchFactory = searchFactory;
		if (searchFactory != null) {
			searchPatternProcessor = searchFactory
					.createSearchPatternProcessor();
		} else {
			searchPatternProcessor = null;
		}
	}

	/**
	 * @see ISourceElementRequestor#acceptFieldReference(char[], int)
	 */
	public void acceptFieldReference(char[] fieldName, int sourcePosition) {
		this.indexer.addFieldReference(fieldName);
	}

	/**
	 * @see ISourceElementRequestor#acceptImport(int, int, char[][], boolean,
	 *      int)
	 */
	public void acceptImport(int declarationStart, int declarationEnd,
			char[][] tokens, boolean onDemand, int modifiers) {
		// imports have already been reported while creating the ImportRef node
		// (see
		// SourceElementParser#comsume*ImportDeclarationName() methods)
	}

	/**
	 * @see ISourceElementRequestor#acceptLineSeparatorPositions(int[])
	 */
	public void acceptLineSeparatorPositions(int[] positions) {
		// implements interface method
	}

	/**
	 * @see ISourceElementRequestor#acceptMethodReference(char[], int, int, int)
	 */
	public void acceptMethodReference(char[] methodName, int argCount,
			int sourcePosition, int sourceEndPosition) {
		this.indexer.addMethodReference(methodName, argCount);
	}

	// /**
	// * @see ISourceElementRequestor#acceptPackage(int, int, char[])
	// */
	// public void acceptPackage(int declarationStart, int declarationEnd,
	// char[] name) {
	// this.packageName = name;
	// }

	/**
	 * @see ISourceElementRequestor#acceptTypeReference(char[][], int, int)
	 */
	public void acceptTypeReference(char[][] typeName, int sourceStart,
			int sourceEnd) {
		int length = typeName.length;
		for (int i = 0; i < length - 1; i++)
			acceptUnknownReference(typeName[i], 0); // ?
		acceptTypeReference(typeName[length - 1], 0);
	}

	/**
	 * @see ISourceElementRequestor#acceptTypeReference(char[], int)
	 */
	public void acceptTypeReference(char[] simpleTypeName, int sourcePosition) {
		this.indexer.addTypeReference(simpleTypeName);
	}

	/**
	 * @see ISourceElementRequestor#acceptUnknownReference(char[][], int, int)
	 */
	public void acceptUnknownReference(char[][] name, int sourceStart,
			int sourceEnd) {
		for (int i = 0; i < name.length; i++) {
			acceptUnknownReference(name[i], 0);
		}
	}

	/**
	 * @see ISourceElementRequestor#acceptUnknownReference(char[], int)
	 */
	public void acceptUnknownReference(char[] name, int sourcePosition) {
		this.indexer.addNameReference(name);
	}

	/*
	 * Rebuild the proper qualification for the current source type:
	 * 
	 * java.lang.Object ---> null java.util.Hashtable$Entry --> [Hashtable]
	 * x.y.A$B$C --> [A, B]
	 */
	public char[][] enclosingTypeNames() {
		if (depth == 0)
			return null;
		char[][] qualification = new char[this.depth][];
		System.arraycopy(this.enclosingTypeNames, 0, qualification, 0,
				this.depth);

		return qualification;
	}

	/**
	 * @since 2.0
	 */
	public void enterType(TypeInfo typeInfo) {
		// eliminate possible qualifications, given they need to be fully
		// resolved again
		if (typeInfo.superclasses != null) {
			// typeInfo.superclasses = typeInfo.superclasses;
			for (int i = 0, length = typeInfo.superclasses.length; i < length; i++) {
				typeInfo.superclasses[i] = getSimpleName(typeInfo.superclasses[i]);
			}
			// add implicit constructor reference to default constructor
			if (DLTKCore.DEBUG) {
				System.err.println("TODO: Add constructore references..."); //$NON-NLS-1$
			}
			// this.indexer.addConstructorReference(typeInfo.superclasss, 0);
		}
		char[][] typeNames;
		if (this.methodDepth > 0) {
			typeNames = ONE_ZERO_CHAR;
		} else {
			typeNames = this.enclosingTypeNames();
		}
		this.indexer.addTypeDeclaration(typeInfo.modifiers, this.pkgName,
				typeInfo.name, typeNames, typeInfo.superclasses);
		this.pushTypeName(typeInfo.name.toCharArray());
	}

	/**
	 * @since 2.0
	 */
	public void enterConstructor(MethodInfo methodInfo) {
		this.indexer.addConstructorDeclaration(methodInfo.name,
				methodInfo.parameterNames, methodInfo.exceptionTypes);
		this.methodDepth++;
	}

	/**
	 * @see ISourceElementRequestor#enterField(FieldInfo)
	 * @since 2.0
	 */
	public void enterField(FieldInfo fieldInfo) {
		this.indexer.addFieldDeclaration(fieldInfo.name.toCharArray());
		this.methodDepth++;
	}

	/**
	 * @see ISourceElementRequestor#enterMethod(MethodInfo)
	 * @since 2.0
	 */
	public void enterMethod(MethodInfo methodInfo) {
		this.indexer.addMethodDeclaration(methodInfo.modifiers, this.pkgName,
				this.enclosingTypeNames(), methodInfo.name,
				methodInfo.parameterNames, methodInfo.exceptionTypes);
		this.methodDepth++;
	}

	/**
	 * @see ISourceElementRequestor#exitType(int)
	 */
	public void exitType(int declarationEnd) {
		popTypeName();
	}

	/*
	 * Returns the unqualified name without parameters from the given type name.
	 */
	private String getSimpleName(String typeName) {
		if (searchPatternProcessor != null) {
			return searchPatternProcessor.extractTypeChars(typeName);
		} else {
			return typeName;
		}
	}

	public void popTypeName() {
		if (depth > 0) {
			// System.out.println("POPNAME:" + new String(
			// enclosingTypeNames[depth-1]));
			enclosingTypeNames[--depth] = null;
		}
		// else if (JobManager.VERBOSE) {
		// // dump a trace so it can be tracked down
		// try {
		// enclosingTypeNames[-1] = null;
		// } catch (ArrayIndexOutOfBoundsException e) {
		// e.printStackTrace();
		// }
		// }
	}

	public void pushTypeName(char[] typeName) {
		if (depth == enclosingTypeNames.length)
			System.arraycopy(enclosingTypeNames, 0,
					enclosingTypeNames = new char[depth * 2][], 0, depth);
		enclosingTypeNames[depth++] = typeName;
	}

	/**
	 * @since 2.0
	 */
	public void enterMethodRemoveSame(MethodInfo info) {
		if (DLTKCore.DEBUG) {
			System.out.println("TODO: Add replace method code."); //$NON-NLS-1$
		}
	}

	public void enterModule() {
	}

	public void exitField(int declarationEnd) {
		this.methodDepth--;
	}

	public void exitMethod(int declarationEnd) {
		this.methodDepth--;
	}

	public void exitModule(int declarationEnd) {
	}

	public void setPackageName(String pkgName) {
		this.pkgName = pkgName.toCharArray();
	}

	public void acceptPackage(int declarationStart, int declarationEnd,
			char[] name) {
	}

	/**
	 * @since 2.0
	 */
	public boolean enterFieldCheckDuplicates(FieldInfo info) {
		this.indexer.addFieldDeclaration(info.name.toCharArray());
		this.methodDepth++;
		return true;
	}

	/**
	 * @since 2.0
	 */
	public boolean enterMethodWithParentType(MethodInfo info,
			String parentName, String delimiter) {
		this.enterMethod(info);
		// this.methodDepth++;
		return true;
	}

	/**
	 * @since 2.0
	 */
	public boolean enterFieldWithParentType(FieldInfo info, String parentName,
			String delimiter) {
		this.indexer.addFieldDeclaration(info.name.toCharArray());
		this.methodDepth++;
		return true;
	}

	/**
	 * @since 2.0
	 */
	public boolean enterTypeAppend(TypeInfo info, String fullName,
			String delimiter) {
		enterType(info);
		return true;
	}

	public void enterModuleRoot() {
		// TODO Auto-generated method stub

	}

	public boolean enterTypeAppend(String fullName, String delimiter) {
		return false;
	}

	public void exitModuleRoot() {
	}

	/**
	 * @since 2.0
	 */
	public void acceptImport(ImportInfo importInfo) {
	}

}
