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
package org.eclipse.dltk.validators.core.tests;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.validators.core.IValidatorType;
import org.eclipse.dltk.validators.internal.core.ValidatorManager;

public class SimpleValidatorUtils {

	public static SimpleValidatorType find() throws CoreException {
		IValidatorType[] allValidatorTypes;
		allValidatorTypes = ValidatorManager.getAllValidatorTypes();
		for (int i = 0; i < allValidatorTypes.length; i++) {
			if (allValidatorTypes[i] instanceof SimpleValidatorType) {
				return (SimpleValidatorType) allValidatorTypes[i];
			}
		}
		return null;
	}

}
