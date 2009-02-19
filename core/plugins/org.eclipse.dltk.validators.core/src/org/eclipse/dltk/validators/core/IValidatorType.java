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
package org.eclipse.dltk.validators.core;

import org.eclipse.dltk.core.environment.IEnvironment;

/**
 * Validator class
 * 
 * @author Haiodo
 */
public interface IValidatorType {
	/**
	 * Return validator identifier, must be equal to extension point id. Used to
	 * determine validator UI configuration preferences.
	 * 
	 * @return
	 */
	String getID();

	/**
	 * Returns the nature of this validator type. Return
	 * {@link ValidatorRuntime#ANY_NATURE} if validator suits for any nature.
	 * 
	 * @return
	 */
	String getNature();

	/**
	 * Returns the name of this validator type.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Checks that the validator has UI and some instances of this validator
	 * could be used. For example external tool validator should return true
	 * here, to support specify external program and arguments.
	 * 
	 * If return <code>true</code>, then this validator has UI,
	 * 
	 * If return <code>false</code>, then this is always running built-in
	 * validator. Static checkers could be here.
	 * 
	 * TODO update isConfigurable() description
	 * 
	 * @return
	 */
	boolean isConfigurable();

	/**
	 * If true then validators of this type could not be added or removed.
	 * 
	 * @return
	 */
	boolean isBuiltin();

	/**
	 * Tests that this type supports the specified type of checks
	 * 
	 * @param validatorType
	 *            {@link ISourceModuleValidator} or {@link IResourceValidator}
	 * @return
	 */
	boolean supports(Class validatorType);

	/**
	 * Creates new {@link IValidator}. The instance is not added to the list of
	 * validators maintained by this type.
	 * 
	 * @param id
	 * @return
	 */
	IValidator createValidator(String id);

	/**
	 * Adds validator to the list of validators maintained by this type.
	 * 
	 * @param validator
	 */
	void addValidator(IValidator validator);

	/**
	 * returns the array of {@link IValidator}s maintained by this type.
	 * 
	 * @return
	 */
	IValidator[] getValidators();

	/**
	 * Return all validator with all flavors
	 * 
	 * @param project
	 * 
	 * @return
	 */
	IValidator[] getAllValidators(IEnvironment environment);

	/**
	 * Removes the validator from the list of the validators maintained by this
	 * type.
	 * 
	 * @param id
	 */
	void disposeValidator(String id);

	/**
	 * Finds {@link IValidator} with the specified <code>id</code>
	 * 
	 * @param id
	 * @return {@link IValidator} found or <code>null</code>
	 */
	IValidator findValidator(String id);

}
