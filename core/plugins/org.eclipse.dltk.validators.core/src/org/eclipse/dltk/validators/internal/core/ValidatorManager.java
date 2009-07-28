/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation
 *******************************************************************************/
package org.eclipse.dltk.validators.internal.core;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.utils.NatureExtensionManager;
import org.eclipse.dltk.validators.core.IValidatorType;
import org.eclipse.dltk.validators.core.ValidatorRuntime;

public class ValidatorManager extends NatureExtensionManager {

	public final static String LANGUAGE_EXTPOINT = ValidatorsCore.PLUGIN_ID
			+ ".validator"; //$NON-NLS-1$

	private ValidatorManager() {
		super(LANGUAGE_EXTPOINT, IValidatorType.class,
				ValidatorRuntime.ANY_NATURE);
	}

	private static ValidatorManager instance = null;

	private static synchronized ValidatorManager getInstance() {
		if (instance == null) {
			instance = new ValidatorManager();
		}
		return instance;
	}

	private static Map idToValidatorType = null;

	public static IValidatorType getValidatorTypeFromID(String id) {
		if (idToValidatorType == null) {
			idToValidatorType = new HashMap();
			try {
				IValidatorType[] allValidatorTypes = getAllValidatorTypes();
				for (int i = 0; i < allValidatorTypes.length; i++) {
					idToValidatorType.put(allValidatorTypes[i].getID(),
							allValidatorTypes[i]);
				}
			} catch (CoreException e) {
				idToValidatorType = null;
				return null;
			}
		}
		return (IValidatorType) idToValidatorType.get(id);
	}

	private static final IValidatorType[] NO_VALIDATORS = new IValidatorType[0];

	/*
	 * @see org.eclipse.dltk.utils.NatureExtensionManager#createEmptyResult()
	 */
	protected Object[] createEmptyResult() {
		return NO_VALIDATORS;
	}

	/**
	 * Return merged with all elements with nature #. If there are no validators
	 * then the empty array is returned.
	 * 
	 * @param natureId
	 * @return
	 * @throws CoreException
	 */
	public static IValidatorType[] getValidators(String natureId)
			throws CoreException {
		return (IValidatorType[]) getInstance().getInstances(natureId);
	}

	/**
	 * Return all validator types. If there are no validators then the empty
	 * array is returned.
	 * 
	 * @return
	 * @throws CoreException
	 */
	public static IValidatorType[] getAllValidatorTypes() throws CoreException {
		return (IValidatorType[]) getInstance().getAllInstances();
	}

}
