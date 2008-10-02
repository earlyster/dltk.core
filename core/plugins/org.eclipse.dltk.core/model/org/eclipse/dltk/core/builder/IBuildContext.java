/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.core.builder;

import org.eclipse.core.resources.IFile;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.environment.IFileHandle;

/**
 * The context of the building a module.
 */
public interface IBuildContext {

	/**
	 * Returns the contents of the source module
	 * 
	 * @return
	 */
	char[] getContents();

	/**
	 * Returns the source module being compiled
	 * 
	 * @return
	 */
	ISourceModule getModelElement();

	/**
	 * Returns the workspace {@link IFile} being compiled or <code>null</code>
	 * if building external module.
	 * 
	 * @return
	 */
	IFile getFile();

	/**
	 * Returns the external {@link IFileHandle} being compiled
	 * 
	 * @return
	 */
	IFileHandle getFileHandle();

	/**
	 * Adds problem to the current build result. Line number is calculated
	 * automatically
	 * 
	 * @param problemId
	 * @param message
	 * @param arguments
	 * @param start
	 * @param end
	 * @param severity
	 */
	void reportProblem(int problemId, String message, String[] arguments,
			int start, int end, int severity);

	/**
	 * Adds problem to the current build result
	 * 
	 * @param problemId
	 * @param message
	 * @param arguments
	 * @param lineNumber
	 * @param start
	 * @param end
	 * @param severity
	 */
	void reportProblem(int problemId, String message, String[] arguments,
			int lineNumber, int start, int end, int severity);

	/**
	 * Adds task. Line number is calculated automatically
	 * 
	 * @param message
	 * @param priority
	 * @param charStart
	 * @param charEnd
	 */
	void reportTask(String message, int priority, int charStart, int charEnd);

	/**
	 * Adds task
	 * 
	 * @param message
	 * @param lineNumber
	 * @param priority
	 * @param charStart
	 * @param charEnd
	 */
	void reportTask(String message, int lineNumber, int priority,
			int charStart, int charEnd);

	/**
	 * Returns the value of the specified attribute.
	 * 
	 * {@link org.eclipse.dltk.ast.declarations.ModuleDeclaration} have the key
	 * org.eclipse.dltk.ast.declarations.ModuleDeclaration.class.getName(). If
	 * there are unrecoverable compilation errors then it would be
	 * <code>null</code>
	 * 
	 * @param attribute
	 * @return
	 */
	Object get(String attribute);

	/**
	 * Sets the value of the specified attribute
	 * 
	 * @param attribute
	 * @param value
	 */
	void set(String attribute, Object value);

}
