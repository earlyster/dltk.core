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
package org.eclipse.dltk.internal.ui.typehierarchy;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.IType;

public class CumulativeType {

	public class Part {
		public final IType type;

		/**
		 * @param type
		 */
		public Part(IType type) {
			this.type = type;
		}

		public CumulativeType getParent() {
			return CumulativeType.this;
		}

		/*
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return type.hashCode();
		}

		/*
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if (obj instanceof CumulativeType.Part) {
				return type.equals(((CumulativeType.Part) obj).type);
			}
			return false;
		}

	}

	private final String qName;
	private final IType[] types;

	/**
	 * @param value
	 */
	public CumulativeType(String qName, IType[] types) {
		this.qName = qName;
		this.types = types;
		Assert.isTrue(types != null && types.length != 0);
	}

	/**
	 * @return
	 */
	public IType getFirst() {
		return types[0];
	}

	/**
	 * @param inputElement
	 * @return
	 */
	public boolean contains(Object input) {
		for (int i = 0; i < types.length; ++i) {
			if (types[i].equals(input)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return
	 */
	public IType[] getTypes() {
		return types;
	}

	/**
	 * @param children
	 * @param index
	 */
	public void insertTo(List list, int index) {
		for (int i = 0; i < types.length; ++i) {
			list.add(index, new Part(types[i]));
		}
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return qName.hashCode();
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj instanceof CumulativeType) {
			return qName.equals(((CumulativeType) obj).qName);
		}
		return false;
	}
}
