/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.dltk.core.index.sql;

import java.io.Serializable;

/**
 * This POJO represents container path.
 * 
 * @author michael
 * 
 */
public class Container implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String path;

	public Container(int id, String path) {
		this.id = id;
		this.path = path;
	}

	/**
	 * Returns search container path
	 * 
	 * @return
	 */
	public String getPath() {
		return path;
	}

	/**
	 * Returns primary key associated with this entry
	 * 
	 * @return
	 */
	public int getId() {
		return id;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Container other = (Container) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public String toString() {
		return "Container [path=" + path + "]";
	}
}
