/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *

 *******************************************************************************/
package org.eclipse.dltk.evaluation.types;

import org.eclipse.dltk.core.IModule;
import org.eclipse.dltk.ti.types.IEvaluatedType;

public class ModelModuleType implements IEvaluatedType {

	private IModule fModule = null;
	private int fStepCount = 1;

	public ModelModuleType(IModule module) {
		this.fModule = module;
	}

	public ModelModuleType(IModule module, int stepCount) {
		this.fModule = module;
		this.fStepCount = stepCount;
	}

	public String getTypeName() {
		if (this.fModule != null) {
			return "model module:" + this.fModule.getElementName(); //$NON-NLS-1$
		} else {
			return "model module: unknown"; //$NON-NLS-1$
		}
	}

	public IModule getModule() {
		return this.fModule;
	}

	public int getStepCount() {
		return this.fStepCount;
	}

	public boolean subtypeOf(IEvaluatedType type) {
		// TODO Auto-generated method stub
		return false;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fModule == null) ? 0 : fModule.hashCode());
		result = prime * result + fStepCount;
		return result;
	}

	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ModelModuleType other = (ModelModuleType) obj;
		if (fModule == null) {
			if (other.fModule != null)
				return false;
		} else if (!fModule.equals(other.fModule))
			return false;
		if (fStepCount != other.fStepCount)
			return false;
		return true;
	}
}
