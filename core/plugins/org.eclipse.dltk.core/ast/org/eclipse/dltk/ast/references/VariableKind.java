/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ast.references;

public interface VariableKind {

	public static final int FIRST_VARIABLE_ID = 0;

	int getId();

	public static class Implementation implements VariableKind {

		private final int id;

		public Implementation(int id) {
			this.id = id;
		}

		public int getId() {
			return id;
		}

		@Override
		public String toString() {
			return "VariableKind-" + id; //$NON-NLS-1$
		}

	}

	@Deprecated
	public class Local extends Implementation {

		public static final int ID = FIRST_VARIABLE_ID + 1;

		public Local() {
			super(ID);
		}

	}

	@Deprecated
	public class Global extends Implementation {

		public static final int ID = FIRST_VARIABLE_ID + 2;

		public Global() {
			super(ID);
		}

	}

	public static final int LAST_CORE_VARIABLE_ID = FIRST_VARIABLE_ID + 50;

	public static final int LAST_VARIABLE_ID = LAST_CORE_VARIABLE_ID + 50;

	public static final VariableKind UNKNOWN = new Implementation(
			FIRST_VARIABLE_ID + 0);

	public static final VariableKind LOCAL = new Local();

	public static final VariableKind GLOBAL = new Global();

	public static final VariableKind INSTANCE = new Implementation(
			FIRST_VARIABLE_ID + 3);

	public static final VariableKind CLASS = new Implementation(
			FIRST_VARIABLE_ID + 4);

	public static final VariableKind MIXIN = new Implementation(
			FIRST_VARIABLE_ID + 5);

	public static final VariableKind ARGUMENT = new Implementation(
			FIRST_VARIABLE_ID + 6);

}
