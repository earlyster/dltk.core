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
package org.eclipse.dltk.compiler.task;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.ast.ASTNode;
import org.eclipse.dltk.ast.ASTVisitor;
import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.expressions.Literal;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;

public class TodoTaskAstParser extends TodoTaskSimpleParser {

	/**
	 * @param preferences
	 */
	public TodoTaskAstParser(ITodoTaskPreferences preferences) {
		super(preferences);
	}

	private static final int ALLOC_INCREMENT = 1024;

	private int[] ranges = new int[ALLOC_INCREMENT];
	private int rangeCount = 0;

	/**
	 * @param sourceStart
	 * @param sourceEnd
	 */
	protected void addRange(int sourceStart, int sourceEnd) {
		if (rangeCount * 2 >= ranges.length) {
			final int[] newArray = new int[ranges.length + ALLOC_INCREMENT];
			System.arraycopy(ranges, 0, newArray, 0, ranges.length);
			ranges = newArray;
		}
		ranges[rangeCount * 2] = sourceStart;
		ranges[rangeCount * 2 + 1] = sourceEnd;
		++rangeCount;
	}

	/**
	 * @param i
	 * @return
	 */
	private boolean checkRange(int location) {
		for (int i = 0; i < rangeCount; ++i) {
			if (location >= ranges[i * 2] && location < ranges[i * 2 + 1]) {
				return false;
			}
		}
		return true;
	}

	/**
	 * @param module
	 */
	public void build(ISourceModule module, ModuleDeclaration ast,
			IProblemReporter reporter) throws CoreException {
		if (!isValid()) {
			return;
		}
		final ITaskReporter taskReporter = (ITaskReporter) reporter
				.getAdapter(ITaskReporter.class);
		if (taskReporter == null) {
			return;
		}
		final ASTVisitor visitor = new ASTVisitor() {

			public boolean visitGeneral(ASTNode node) throws Exception {
				if (isSimpleNode(node)) {
					addRange(node.sourceStart(), node.sourceEnd());
				}
				return true;
			}

		};
		try {
			ast.traverse(visitor);
		} catch (Exception e) {
			DLTKCore.error("Unexpected error", e); //$NON-NLS-1$
		}
		parse(taskReporter, module.getSourceAsCharArray());
	}

	protected boolean isSimpleNode(ASTNode node) {
		return node instanceof Literal;
	}

	protected int findCommentStart(char[] content, int begin, int end) {
		for (int i = begin; i < end; ++i) {
			if (content[i] == '#' && checkRange(i)) {
				return i + 1;
			}
		}
		return -1;
	}

}
