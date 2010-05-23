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
 * $Id: LaunchingModelPackage.java,v 1.1 2010/05/23 14:20:39 apanchenk Exp $
 */
package org.eclipse.dltk.launching.model;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;

/**
 * <!-- begin-user-doc -->
 * The <b>Package</b> for the model.
 * It contains accessors for the meta objects to represent
 * <ul>
 *   <li>each class,</li>
 *   <li>each feature of each class,</li>
 *   <li>each enum,</li>
 *   <li>and each data type</li>
 * </ul>
 * <!-- end-user-doc -->
 * @see org.eclipse.dltk.launching.model.LaunchingModelFactory
 * @model kind="package"
 * @generated
 */
public interface LaunchingModelPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "model"; //$NON-NLS-1$

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.eclipse.org/dltk/launching.ecore"; //$NON-NLS-1$

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "launch"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	LaunchingModelPackage eINSTANCE = org.eclipse.dltk.launching.model.impl.LaunchingModelPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.dltk.launching.model.impl.InterpreterInfoImpl <em>Interpreter Info</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.dltk.launching.model.impl.InterpreterInfoImpl
	 * @see org.eclipse.dltk.launching.model.impl.LaunchingModelPackageImpl#getInterpreterInfo()
	 * @generated
	 */
	int INTERPRETER_INFO = 0;

	/**
	 * The feature id for the '<em><b>Environment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERPRETER_INFO__ENVIRONMENT = 0;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERPRETER_INFO__LOCATION = 1;

	/**
	 * The feature id for the '<em><b>Contents</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERPRETER_INFO__CONTENTS = 2;

	/**
	 * The number of structural features of the '<em>Interpreter Info</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERPRETER_INFO_FEATURE_COUNT = 3;


	/**
	 * The meta object id for the '{@link org.eclipse.dltk.launching.model.impl.InterpreterGeneratedContentImpl <em>Interpreter Generated Content</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.dltk.launching.model.impl.InterpreterGeneratedContentImpl
	 * @see org.eclipse.dltk.launching.model.impl.LaunchingModelPackageImpl#getInterpreterGeneratedContent()
	 * @generated
	 */
	int INTERPRETER_GENERATED_CONTENT = 1;

	/**
	 * The feature id for the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERPRETER_GENERATED_CONTENT__KEY = 0;

	/**
	 * The feature id for the '<em><b>Interpreter Last Modified</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERPRETER_GENERATED_CONTENT__INTERPRETER_LAST_MODIFIED = 1;

	/**
	 * The feature id for the '<em><b>Fetched At</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERPRETER_GENERATED_CONTENT__FETCHED_AT = 2;

	/**
	 * The feature id for the '<em><b>Value</b></em>' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERPRETER_GENERATED_CONTENT__VALUE = 3;

	/**
	 * The feature id for the '<em><b>Last Modified</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERPRETER_GENERATED_CONTENT__LAST_MODIFIED = 4;

	/**
	 * The number of structural features of the '<em>Interpreter Generated Content</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int INTERPRETER_GENERATED_CONTENT_FEATURE_COUNT = 5;


	/**
	 * Returns the meta object for class '{@link org.eclipse.dltk.launching.model.InterpreterInfo <em>Interpreter Info</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Interpreter Info</em>'.
	 * @see org.eclipse.dltk.launching.model.InterpreterInfo
	 * @generated
	 */
	EClass getInterpreterInfo();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.launching.model.InterpreterInfo#getEnvironment <em>Environment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Environment</em>'.
	 * @see org.eclipse.dltk.launching.model.InterpreterInfo#getEnvironment()
	 * @see #getInterpreterInfo()
	 * @generated
	 */
	EAttribute getInterpreterInfo_Environment();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.launching.model.InterpreterInfo#getLocation <em>Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Location</em>'.
	 * @see org.eclipse.dltk.launching.model.InterpreterInfo#getLocation()
	 * @see #getInterpreterInfo()
	 * @generated
	 */
	EAttribute getInterpreterInfo_Location();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.dltk.launching.model.InterpreterInfo#getContents <em>Contents</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Contents</em>'.
	 * @see org.eclipse.dltk.launching.model.InterpreterInfo#getContents()
	 * @see #getInterpreterInfo()
	 * @generated
	 */
	EReference getInterpreterInfo_Contents();

	/**
	 * Returns the meta object for class '{@link org.eclipse.dltk.launching.model.InterpreterGeneratedContent <em>Interpreter Generated Content</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Interpreter Generated Content</em>'.
	 * @see org.eclipse.dltk.launching.model.InterpreterGeneratedContent
	 * @generated
	 */
	EClass getInterpreterGeneratedContent();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getKey <em>Key</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Key</em>'.
	 * @see org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getKey()
	 * @see #getInterpreterGeneratedContent()
	 * @generated
	 */
	EAttribute getInterpreterGeneratedContent_Key();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getInterpreterLastModified <em>Interpreter Last Modified</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Interpreter Last Modified</em>'.
	 * @see org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getInterpreterLastModified()
	 * @see #getInterpreterGeneratedContent()
	 * @generated
	 */
	EAttribute getInterpreterGeneratedContent_InterpreterLastModified();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getFetchedAt <em>Fetched At</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Fetched At</em>'.
	 * @see org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getFetchedAt()
	 * @see #getInterpreterGeneratedContent()
	 * @generated
	 */
	EAttribute getInterpreterGeneratedContent_FetchedAt();

	/**
	 * Returns the meta object for the attribute list '{@link org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getValue <em>Value</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute list '<em>Value</em>'.
	 * @see org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getValue()
	 * @see #getInterpreterGeneratedContent()
	 * @generated
	 */
	EAttribute getInterpreterGeneratedContent_Value();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getLastModified <em>Last Modified</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Last Modified</em>'.
	 * @see org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getLastModified()
	 * @see #getInterpreterGeneratedContent()
	 * @generated
	 */
	EAttribute getInterpreterGeneratedContent_LastModified();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	LaunchingModelFactory getLaunchingModelFactory();

	/**
	 * <!-- begin-user-doc -->
	 * Defines literals for the meta objects that represent
	 * <ul>
	 *   <li>each class,</li>
	 *   <li>each feature of each class,</li>
	 *   <li>each enum,</li>
	 *   <li>and each data type</li>
	 * </ul>
	 * <!-- end-user-doc -->
	 * @generated
	 */
	interface Literals {
		/**
		 * The meta object literal for the '{@link org.eclipse.dltk.launching.model.impl.InterpreterInfoImpl <em>Interpreter Info</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.dltk.launching.model.impl.InterpreterInfoImpl
		 * @see org.eclipse.dltk.launching.model.impl.LaunchingModelPackageImpl#getInterpreterInfo()
		 * @generated
		 */
		EClass INTERPRETER_INFO = eINSTANCE.getInterpreterInfo();

		/**
		 * The meta object literal for the '<em><b>Environment</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INTERPRETER_INFO__ENVIRONMENT = eINSTANCE.getInterpreterInfo_Environment();

		/**
		 * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INTERPRETER_INFO__LOCATION = eINSTANCE.getInterpreterInfo_Location();

		/**
		 * The meta object literal for the '<em><b>Contents</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference INTERPRETER_INFO__CONTENTS = eINSTANCE.getInterpreterInfo_Contents();

		/**
		 * The meta object literal for the '{@link org.eclipse.dltk.launching.model.impl.InterpreterGeneratedContentImpl <em>Interpreter Generated Content</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.dltk.launching.model.impl.InterpreterGeneratedContentImpl
		 * @see org.eclipse.dltk.launching.model.impl.LaunchingModelPackageImpl#getInterpreterGeneratedContent()
		 * @generated
		 */
		EClass INTERPRETER_GENERATED_CONTENT = eINSTANCE.getInterpreterGeneratedContent();

		/**
		 * The meta object literal for the '<em><b>Key</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INTERPRETER_GENERATED_CONTENT__KEY = eINSTANCE.getInterpreterGeneratedContent_Key();

		/**
		 * The meta object literal for the '<em><b>Interpreter Last Modified</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INTERPRETER_GENERATED_CONTENT__INTERPRETER_LAST_MODIFIED = eINSTANCE.getInterpreterGeneratedContent_InterpreterLastModified();

		/**
		 * The meta object literal for the '<em><b>Fetched At</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INTERPRETER_GENERATED_CONTENT__FETCHED_AT = eINSTANCE.getInterpreterGeneratedContent_FetchedAt();

		/**
		 * The meta object literal for the '<em><b>Value</b></em>' attribute list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INTERPRETER_GENERATED_CONTENT__VALUE = eINSTANCE.getInterpreterGeneratedContent_Value();

		/**
		 * The meta object literal for the '<em><b>Last Modified</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute INTERPRETER_GENERATED_CONTENT__LAST_MODIFIED = eINSTANCE.getInterpreterGeneratedContent_LastModified();

	}

} //LaunchingModelPackage
