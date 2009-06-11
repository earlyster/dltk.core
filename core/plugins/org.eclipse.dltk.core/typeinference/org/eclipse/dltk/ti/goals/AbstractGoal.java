/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 *******************************************************************************/
package org.eclipse.dltk.ti.goals;

import org.eclipse.dltk.ti.IContext;

public abstract class AbstractGoal implements IGoal {

	protected final IContext context;

	public AbstractGoal(IContext context) {
		this.context = context;
	}

	public IContext getContext() {
		return context;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((context == null) ? 0 : context.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractGoal other = (AbstractGoal) obj;
		if (context == null) {
			if (other.context != null)
				return false;
		} else if (!context.equals(other.context))
			return false;
		return true;
	}

	protected String getClassName() {
		String name = getClass().getName();
		int pos = name.lastIndexOf('.');
		if (pos > 0) {
			return name.substring(pos + 1);
		} else {
			return name;
		}
	}

}
