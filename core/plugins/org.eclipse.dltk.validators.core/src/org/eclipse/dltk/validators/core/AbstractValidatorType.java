/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.validators.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.dltk.core.environment.IEnvironment;

public abstract class AbstractValidatorType implements IValidatorType {
	protected final Map validators = new HashMap();

	public IValidator[] getValidators() {
		return (IValidator[]) validators.values().toArray(
				new IValidator[validators.size()]);
	}

	public IValidator findValidator(String id) {
		return (IValidator) this.validators.get(id);
	}

	public void addValidator(IValidator validator) {
		Assert.isLegal(!isBuiltin(),
				"could not add to the built-in validator type"); //$NON-NLS-1$
		if (validator.getValidatorType() != this) {
			throw new IllegalArgumentException("Wrong validator type"); //$NON-NLS-1$
		}
		validators.put(validator.getID(), validator);
	}

	public void disposeValidator(String id) {
		Assert.isTrue(!isBuiltin());
		final IValidator validator = (IValidator) validators.remove(id);
		if (validator != null) {
			ValidatorRuntime.fireValidatorRemoved(validator);
		}
	}

	public boolean isConfigurable() {
		return true;
	}

	/*
	 * @see org.eclipse.dltk.validators.core.IValidatorType#getAllValidators()
	 */
	public IValidator[] getAllValidators(IEnvironment environment) {
		return getValidators();
	}

}
