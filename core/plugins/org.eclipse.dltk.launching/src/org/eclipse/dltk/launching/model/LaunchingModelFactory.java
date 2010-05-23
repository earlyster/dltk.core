/**
 * Copyright (c) 2010 xored software, Inc.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *
 * $Id: LaunchingModelFactory.java,v 1.1 2010/05/23 14:20:39 apanchenk Exp $
 */
package org.eclipse.dltk.launching.model;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.dltk.launching.model.LaunchingModelPackage
 * @generated
 */
public interface LaunchingModelFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	LaunchingModelFactory eINSTANCE = org.eclipse.dltk.launching.model.impl.LaunchingModelFactoryImpl.init();

	/**
	 * Returns a new object of class '<em>Interpreter Info</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Interpreter Info</em>'.
	 * @generated
	 */
	InterpreterInfo createInterpreterInfo();

	/**
	 * Returns a new object of class '<em>Interpreter Generated Content</em>'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return a new object of class '<em>Interpreter Generated Content</em>'.
	 * @generated
	 */
	InterpreterGeneratedContent createInterpreterGeneratedContent();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	LaunchingModelPackage getLaunchingModelPackage();

} //LaunchingModelFactory
