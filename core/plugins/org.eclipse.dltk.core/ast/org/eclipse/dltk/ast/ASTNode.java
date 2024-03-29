/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation
 *     xored software, Inc. - Search All occurences bugfix, 
 *     						  hilight only class name when class is in search results ( Alex Panchenko <alex@xored.com>)
 *******************************************************************************/

package org.eclipse.dltk.ast;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceNode;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.SourceRange;
import org.eclipse.dltk.utils.CorePrinter;
import org.eclipse.osgi.util.NLS;

public abstract class ASTNode implements ISourceNode {

	// storage for internal flags (32 bits) BIT USAGE
	public final static int Bit1 = 0x1; // return type (operator) | name
	// reference kind (name ref) | add
	// assertion (type decl) | useful empty
	// statement (empty statement)
	public final static int Bit2 = 0x2; // return type (operator) | name
	// reference kind (name ref) | has local
	// type (type, method, field decl)
	public final static int Bit3 = 0x4; // return type (operator) | name
	// reference kind (name ref) | implicit
	// this (this ref)
	public final static int Bit4 = 0x8; // return type (operator) | first
	// assignment to local (local decl) |
	// undocumented empty block (block, type
	// and method decl)
	public final static int Bit5 = 0x10; // value for return (expression) |
	// has all method bodies (unit) |
	// supertype ref (type ref)
	public final static int Bit6 = 0x20; // depth (name ref, msg) | ignore
	// need cast check (cast expression)
	public final static int Bit7 = 0x40; // depth (name ref, msg) | operator
	// (operator) | need runtime
	// checkcast (cast expression) |
	// label used (labelStatement)
	public final static int Bit8 = 0x80; // depth (name ref, msg) | operator
	// (operator) | unsafe cast (cast
	// expression)
	public final static int Bit9 = 0x100; // depth (name ref, msg) | operator
	// (operator) | is local type (type
	// decl)
	public final static int Bit10 = 0x200; // depth (name ref, msg) | operator
	// (operator) | is anonymous type
	// (type decl)
	public final static int Bit11 = 0x400; // depth (name ref, msg) | operator
	// (operator) | is member type (type
	// decl)
	public final static int Bit12 = 0x800; // depth (name ref, msg) | operator
	// (operator) | has abstract methods
	// (type decl)
	public final static int Bit13 = 0x1000; // depth (name ref, msg) | is
	// secondary type (type decl)
	public final static int Bit14 = 0x2000; // strictly assigned (reference lhs)
	public final static int Bit15 = 0x4000; // is unnecessary cast (expression)
	// | is varargs (type ref) |
	// isSubRoutineEscaping (try
	// statement)
	public final static int Bit16 = 0x8000; // in javadoc comment (name ref,
	// type ref, msg)
	public final static int Bit17 = 0x10000; // compound assigned (reference
	// lhs)
	public final static int Bit18 = 0x20000; // non null (expression)
	public final static int Bit19 = 0x40000;
	public final static int Bit20 = 0x80000;
	public final static int Bit21 = 0x100000;
	public final static int Bit22 = 0x200000; // parenthesis count
	// (expression)
	public final static int Bit23 = 0x400000; // parenthesis count
	// (expression)
	public final static int Bit24 = 0x800000; // parenthesis count
	// (expression)
	public final static int Bit25 = 0x1000000; // parenthesis count
	// (expression)
	public final static int Bit26 = 0x2000000; // parenthesis count
	// (expression)
	public final static int Bit27 = 0x4000000; // parenthesis count
	// (expression)
	public final static int Bit28 = 0x8000000; // parenthesis count
	// (expression)
	public final static int Bit29 = 0x10000000; // parenthesis count
	// (expression)
	public final static int Bit30 = 0x20000000; // elseif (if statement) | try
	// block exit (try statement) |
	// fall-through (case statement)
	public final static int Bit31 = 0x40000000; // local declaration reachable
	// (local decl) | ignore raw
	// type check (type ref) |
	// discard entire assignment
	// (assignment)
	public final static int Bit32 = 0x80000000; // reachable (statement)

	public final static long Bit32L = 0x80000000L;
	public final static long Bit33L = 0x100000000L;
	public final static long Bit34L = 0x200000000L;
	public final static long Bit35L = 0x400000000L;
	public final static long Bit36L = 0x800000000L;
	public final static long Bit37L = 0x1000000000L;
	public final static long Bit38L = 0x2000000000L;
	public final static long Bit39L = 0x4000000000L;
	public final static long Bit40L = 0x8000000000L;
	public final static long Bit41L = 0x10000000000L;
	public final static long Bit42L = 0x20000000000L;
	public final static long Bit43L = 0x40000000000L;
	public final static long Bit44L = 0x80000000000L;
	public final static long Bit45L = 0x100000000000L;
	public final static long Bit46L = 0x200000000000L;
	public final static long Bit47L = 0x400000000000L;
	public final static long Bit48L = 0x800000000000L;
	public final static long Bit49L = 0x1000000000000L;
	public final static long Bit50L = 0x2000000000000L;
	public final static long Bit51L = 0x4000000000000L;
	public final static long Bit52L = 0x8000000000000L;
	public final static long Bit53L = 0x10000000000000L;
	public final static long Bit54L = 0x20000000000000L;
	public final static long Bit55L = 0x40000000000000L;
	public final static long Bit56L = 0x80000000000000L;
	public final static long Bit57L = 0x100000000000000L;
	public final static long Bit58L = 0x200000000000000L;
	public final static long Bit59L = 0x400000000000000L;
	public final static long Bit60L = 0x800000000000000L;
	public final static long Bit61L = 0x1000000000000000L;
	public final static long Bit62L = 0x2000000000000000L;
	public final static long Bit63L = 0x4000000000000000L;
	public final static long Bit64L = 0x8000000000000000L;

	public final static int D_METHOD_DECL = 1;

	public final static int D_TYPE_DECL = 2;

	public final static int D_VAR_DECL = 3;

	private int sourceStart;

	private int sourceEnd;

	protected ASTNode() {
		this(0, 0);
	}

	protected ASTNode(int start, int end) {
		this.sourceStart = start;
		this.sourceEnd = end;
	}

	protected ASTNode(DLTKToken token) {
		this.sourceStart = token.getColumn();
		String tokenValue = token.getText();
		if (tokenValue != null) {
			this.sourceEnd = this.sourceStart + tokenValue.length();
		} else {
			this.sourceEnd = this.sourceStart;
		}
	}

	public final int sourceStart() {
		return this.sourceStart;
	}

	public int start() {
		return sourceStart();
	}

	public final int sourceEnd() {
		return this.sourceEnd;
	}

	public int end() {
		return sourceEnd();
	}

	/**
	 * Returns the offset of the region which should be highlighted when this
	 * item is added to search results.
	 * 
	 * The goal is to highlight only classname when searching for types.
	 */
	public int matchStart() {
		return sourceStart();
	}

	/**
	 * Returns the length of the region which should be highlighted when this
	 * item is added to search results.
	 */
	public int matchLength() {
		return sourceEnd() - sourceStart();
	}

	// TODO: Need to find way to change visibility to protected.
	public void setStart(int start) {
		this.sourceStart = start;
	}

	// TODO: Need to find way to change visibility to protected.
	public void setEnd(int end) {
		this.sourceEnd = end;
	}

	public abstract void traverse(ASTVisitor visitor) throws Exception;

	public void printNode(CorePrinter output) {
		output.println(NLS.bind(
				Messages.ASTNode_nodeDoesntSupportDebugPrinting,
				new Object[] { this.getClass() }));
	}

	protected ISourceRange getSourceRange() {
		return new SourceRange(this.sourceStart(), this.sourceEnd()
				- this.sourceStart() + 1);
	}

	private static String simplifyClassName(String name) {
		int pos = name.lastIndexOf('.');
		return name.substring(pos + 1);
	}

	public String debugString() {
		return simplifyClassName(this.getClass().getName()) + "@" //$NON-NLS-1$
				+ this.getSourceRange().toString();
	}

	public String toString() {
		StringWriter writer = new StringWriter();
		CorePrinter printer = new CorePrinter(writer);
		this.printNode(printer);
		printer.flush();
		printer.close();
		return writer.getBuffer().toString();
	}

	/**
	 * Uses simplest visitor to get childs and returns collection of ASTNode
	 * objects
	 * 
	 * @return
	 */
	public List getChilds() {
		final List<ASTNode> result = new ArrayList<ASTNode>();
		ASTVisitor visitor = new ASTVisitor() {

			public boolean visitGeneral(ASTNode node) throws Exception {
				if (node == ASTNode.this) {
					return true;
				}
				result.add(node);
				return false; // we needn't subchilds and more
			}

		};
		try {
			this.traverse(visitor);
		} catch (Exception e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * Tests if the specified node is located at the same location.
	 * 
	 * @param other
	 * @return
	 */
	public boolean locationMatches(ASTNode other) {
		if (other == this)
			return true;
		if (other == null) {
			return false;
		}
		return other.sourceEnd() >= 0 && other.sourceStart() >= 0
				&& sourceStart() == other.sourceStart()
				&& sourceEnd() == other.sourceEnd();
	}

}
