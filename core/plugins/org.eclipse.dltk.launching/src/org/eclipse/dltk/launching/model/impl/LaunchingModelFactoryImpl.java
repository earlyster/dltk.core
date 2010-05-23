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
 * $Id: LaunchingModelFactoryImpl.java,v 1.1 2010/05/23 14:20:39 apanchenk Exp $
 */
package org.eclipse.dltk.launching.model.impl;

import org.eclipse.dltk.launching.model.*;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.impl.EFactoryImpl;

import org.eclipse.emf.ecore.plugin.EcorePlugin;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Factory</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class LaunchingModelFactoryImpl extends EFactoryImpl implements LaunchingModelFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static LaunchingModelFactory init() {
		try {
			LaunchingModelFactory theLaunchingModelFactory = (LaunchingModelFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/dltk/launching.ecore"); //$NON-NLS-1$ 
			if (theLaunchingModelFactory != null) {
				return theLaunchingModelFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new LaunchingModelFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LaunchingModelFactoryImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public EObject create(EClass eClass) {
		switch (eClass.getClassifierID()) {
			case LaunchingModelPackage.INTERPRETER_INFO: return createInterpreterInfo();
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT: return createInterpreterGeneratedContent();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InterpreterInfo createInterpreterInfo() {
		InterpreterInfoImpl interpreterInfo = new InterpreterInfoImpl();
		return interpreterInfo;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public InterpreterGeneratedContent createInterpreterGeneratedContent() {
		InterpreterGeneratedContentImpl interpreterGeneratedContent = new InterpreterGeneratedContentImpl();
		return interpreterGeneratedContent;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LaunchingModelPackage getLaunchingModelPackage() {
		return (LaunchingModelPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static LaunchingModelPackage getPackage() {
		return LaunchingModelPackage.eINSTANCE;
	}

} //LaunchingModelFactoryImpl
