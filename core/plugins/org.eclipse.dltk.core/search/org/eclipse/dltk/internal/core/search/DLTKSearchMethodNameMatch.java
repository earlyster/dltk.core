/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core.search;

import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.search.MethodNameMatch;

/**
 * DLTK Search concrete type for a type name match.
 * 
 */
public class DLTKSearchMethodNameMatch extends MethodNameMatch {

	private IMethod method;
	private int modifiers = -1; // store modifiers to avoid java model

	// population

	/**
	 * Creates a new Java Search type name match.
	 */
	public DLTKSearchMethodNameMatch(IMethod type, int modifiers) {
		this.method = type;
		this.modifiers = modifiers;
	}

	/*
	 * (non-Javadoc) Returns whether the matched type is equals to the given
	 * object or not.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (obj == this)
			return true; // avoid unnecessary calls for identical objects
		if (obj instanceof MethodNameMatch) {
			MethodNameMatch match = (MethodNameMatch) obj;
			if (this.method == null) {
				return match.getMethod() == null
						&& match.getModifiers() == this.modifiers;
			}
			return this.method.equals(match.getMethod())
					&& match.getModifiers() == this.modifiers;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.core.search.TypeNameMatch#getModifiers()
	 */
	public int getModifiers() {
		return this.modifiers;
	}

	/*
	 * (non-Javadoc) Note that returned handle exists as it matches a type
	 * accepted from up-to-date index file.
	 * 
	 * @see org.eclipse.jdt.core.search.TypeNameMatch#getType()
	 */
	public IMethod getMethod() {
		return this.method;
	}

	/*
	 * (non-Javadoc) Returns the hash code of the matched type.
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		if (this.method == null)
			return this.modifiers;
		return this.method.hashCode();
	}

	/**
	 * Set modifiers of the matched type.
	 * 
	 * @param modifiers
	 *            the modifiers of the matched type.
	 */
	public void setModifiers(int modifiers) {
		this.modifiers = modifiers;
	}

	/**
	 * Set matched type.
	 * 
	 * @param type
	 *            the matched type.
	 */
	public void setMethod(IMethod method) {
		this.method = method;
	}

	/*
	 * (non-Javadoc) Returns the string of the matched type.
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		if (this.method == null)
			return super.toString();
		return this.method.toString();
	}
}
