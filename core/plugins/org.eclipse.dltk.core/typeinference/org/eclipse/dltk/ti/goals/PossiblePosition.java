/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 *******************************************************************************/
/**
 *
 */
package org.eclipse.dltk.ti.goals;

import org.eclipse.core.resources.IResource;
import org.eclipse.dltk.ast.ASTNode;

public class PossiblePosition {
	private final IResource resource;
	private final int offset;
	private final int length;
	private final ASTNode node;

	public PossiblePosition(IResource resource, int offset, int length) {
		super();
		this.resource = resource;
		this.offset = offset;
		this.length = length;
		this.node = null;
	}

	public PossiblePosition(IResource resource, int offset, int length,
			ASTNode node) {
		super();
		this.resource = resource;
		this.offset = offset;
		this.length = length;
		this.node = node;
	}

	public IResource getResource() {
		return resource;
	}

	public int getOffset() {
		return offset;
	}

	public int getLength() {
		return length;
	}

	/**
	 * Node could be null
	 */
	public ASTNode getNode() {
		return node;
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + offset;
		result = prime * result + length;
		result = prime * result
				+ ((resource == null) ? 0 : resource.hashCode());
		return result;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PossiblePosition other = (PossiblePosition) obj;
		if (offset != other.offset)
			return false;
		if (length != other.length)
			return false;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		if (resource == null) {
			if (other.resource != null)
				return false;
		} else if (!resource.equals(other.resource))
			return false;
		return true;
	}

}