/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 *******************************************************************************/
package org.eclipse.dltk.ti.goals;

import java.util.Arrays;

import org.eclipse.dltk.ti.InstanceContext;
import org.eclipse.dltk.ti.types.IEvaluatedType;

public class MethodReturnTypeGoal extends AbstractTypeGoal {

	private final String methodName;
	private final IEvaluatedType[] arguments;

	public String getMethodName() {
		return methodName;
	}

	public IEvaluatedType[] getArguments() {
		return arguments;
	}

	public MethodReturnTypeGoal(InstanceContext context, String methodName,
			IEvaluatedType[] arguments) {
		super(context);
		this.methodName = methodName;
		this.arguments = arguments;
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result
				+ ((methodName == null) ? 0 : methodName.hashCode());
		return result;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodReturnTypeGoal other = (MethodReturnTypeGoal) obj;
		if (!Arrays.equals(arguments, other.arguments))
			return false;
		if (methodName == null) {
			if (other.methodName != null)
				return false;
		} else if (!methodName.equals(other.methodName))
			return false;
		return true;
	}

}
