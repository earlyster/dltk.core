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
 * $Id: LaunchingModelPackageImpl.java,v 1.1 2010/05/23 14:20:39 apanchenk Exp $
 */
package org.eclipse.dltk.launching.model.impl;

import org.eclipse.dltk.launching.model.InterpreterGeneratedContent;
import org.eclipse.dltk.launching.model.InterpreterInfo;
import org.eclipse.dltk.launching.model.LaunchingModelFactory;
import org.eclipse.dltk.launching.model.LaunchingModelPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class LaunchingModelPackageImpl extends EPackageImpl implements LaunchingModelPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass interpreterInfoEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass interpreterGeneratedContentEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.dltk.launching.model.LaunchingModelPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private LaunchingModelPackageImpl() {
		super(eNS_URI, LaunchingModelFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this model, and for any others upon which it depends.
	 * 
	 * <p>This method is used to initialize {@link LaunchingModelPackage#eINSTANCE} when that field is accessed.
	 * Clients should not invoke it directly. Instead, they should simply access that field to obtain the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static LaunchingModelPackage init() {
		if (isInited) return (LaunchingModelPackage)EPackage.Registry.INSTANCE.getEPackage(LaunchingModelPackage.eNS_URI);

		// Obtain or create and register package
		LaunchingModelPackageImpl theLaunchingModelPackage = (LaunchingModelPackageImpl)(EPackage.Registry.INSTANCE.get(eNS_URI) instanceof LaunchingModelPackageImpl ? EPackage.Registry.INSTANCE.get(eNS_URI) : new LaunchingModelPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theLaunchingModelPackage.createPackageContents();

		// Initialize created meta-data
		theLaunchingModelPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theLaunchingModelPackage.freeze();

  
		// Update the registry and return the package
		EPackage.Registry.INSTANCE.put(LaunchingModelPackage.eNS_URI, theLaunchingModelPackage);
		return theLaunchingModelPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getInterpreterInfo() {
		return interpreterInfoEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getInterpreterInfo_Environment() {
		return (EAttribute)interpreterInfoEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getInterpreterInfo_Location() {
		return (EAttribute)interpreterInfoEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getInterpreterInfo_Contents() {
		return (EReference)interpreterInfoEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getInterpreterGeneratedContent() {
		return interpreterGeneratedContentEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getInterpreterGeneratedContent_Key() {
		return (EAttribute)interpreterGeneratedContentEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getInterpreterGeneratedContent_InterpreterLastModified() {
		return (EAttribute)interpreterGeneratedContentEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getInterpreterGeneratedContent_FetchedAt() {
		return (EAttribute)interpreterGeneratedContentEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getInterpreterGeneratedContent_Value() {
		return (EAttribute)interpreterGeneratedContentEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getInterpreterGeneratedContent_LastModified() {
		return (EAttribute)interpreterGeneratedContentEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public LaunchingModelFactory getLaunchingModelFactory() {
		return (LaunchingModelFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		interpreterInfoEClass = createEClass(INTERPRETER_INFO);
		createEAttribute(interpreterInfoEClass, INTERPRETER_INFO__ENVIRONMENT);
		createEAttribute(interpreterInfoEClass, INTERPRETER_INFO__LOCATION);
		createEReference(interpreterInfoEClass, INTERPRETER_INFO__CONTENTS);

		interpreterGeneratedContentEClass = createEClass(INTERPRETER_GENERATED_CONTENT);
		createEAttribute(interpreterGeneratedContentEClass, INTERPRETER_GENERATED_CONTENT__KEY);
		createEAttribute(interpreterGeneratedContentEClass, INTERPRETER_GENERATED_CONTENT__INTERPRETER_LAST_MODIFIED);
		createEAttribute(interpreterGeneratedContentEClass, INTERPRETER_GENERATED_CONTENT__FETCHED_AT);
		createEAttribute(interpreterGeneratedContentEClass, INTERPRETER_GENERATED_CONTENT__VALUE);
		createEAttribute(interpreterGeneratedContentEClass, INTERPRETER_GENERATED_CONTENT__LAST_MODIFIED);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(interpreterInfoEClass, InterpreterInfo.class, "InterpreterInfo", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getInterpreterInfo_Environment(), ecorePackage.getEString(), "environment", null, 0, 1, InterpreterInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getInterpreterInfo_Location(), ecorePackage.getEString(), "location", null, 0, 1, InterpreterInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getInterpreterInfo_Contents(), ecorePackage.getEObject(), null, "contents", null, 0, -1, InterpreterInfo.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(interpreterGeneratedContentEClass, InterpreterGeneratedContent.class, "InterpreterGeneratedContent", !IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getInterpreterGeneratedContent_Key(), ecorePackage.getEString(), "key", null, 0, 1, InterpreterGeneratedContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getInterpreterGeneratedContent_InterpreterLastModified(), ecorePackage.getEDate(), "interpreterLastModified", null, 0, 1, InterpreterGeneratedContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getInterpreterGeneratedContent_FetchedAt(), ecorePackage.getEDate(), "fetchedAt", null, 0, 1, InterpreterGeneratedContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getInterpreterGeneratedContent_Value(), ecorePackage.getEString(), "value", null, 0, -1, InterpreterGeneratedContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, !IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getInterpreterGeneratedContent_LastModified(), ecorePackage.getEDate(), "lastModified", null, 0, 1, InterpreterGeneratedContent.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		// Create resource
		createResource(eNS_URI);
	}

} //LaunchingModelPackageImpl
