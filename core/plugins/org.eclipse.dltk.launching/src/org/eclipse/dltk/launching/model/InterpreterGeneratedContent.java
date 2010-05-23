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
 * $Id: InterpreterGeneratedContent.java,v 1.1 2010/05/23 14:20:39 apanchenk Exp $
 */
package org.eclipse.dltk.launching.model;

import java.util.Date;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Interpreter Generated Content</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getKey <em>Key</em>}</li>
 *   <li>{@link org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getInterpreterLastModified <em>Interpreter Last Modified</em>}</li>
 *   <li>{@link org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getFetchedAt <em>Fetched At</em>}</li>
 *   <li>{@link org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getValue <em>Value</em>}</li>
 *   <li>{@link org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getLastModified <em>Last Modified</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.dltk.launching.model.LaunchingModelPackage#getInterpreterGeneratedContent()
 * @model
 * @generated
 */
public interface InterpreterGeneratedContent extends EObject {
	/**
	 * Returns the value of the '<em><b>Key</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Key</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Key</em>' attribute.
	 * @see #setKey(String)
	 * @see org.eclipse.dltk.launching.model.LaunchingModelPackage#getInterpreterGeneratedContent_Key()
	 * @model
	 * @generated
	 */
	String getKey();

	/**
	 * Sets the value of the '{@link org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getKey <em>Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Key</em>' attribute.
	 * @see #getKey()
	 * @generated
	 */
	void setKey(String value);

	/**
	 * Returns the value of the '<em><b>Interpreter Last Modified</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Interpreter Last Modified</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Interpreter Last Modified</em>' attribute.
	 * @see #setInterpreterLastModified(Date)
	 * @see org.eclipse.dltk.launching.model.LaunchingModelPackage#getInterpreterGeneratedContent_InterpreterLastModified()
	 * @model
	 * @generated
	 */
	Date getInterpreterLastModified();

	/**
	 * Sets the value of the '{@link org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getInterpreterLastModified <em>Interpreter Last Modified</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Interpreter Last Modified</em>' attribute.
	 * @see #getInterpreterLastModified()
	 * @generated
	 */
	void setInterpreterLastModified(Date value);

	/**
	 * Returns the value of the '<em><b>Fetched At</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Fetched At</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Fetched At</em>' attribute.
	 * @see #setFetchedAt(Date)
	 * @see org.eclipse.dltk.launching.model.LaunchingModelPackage#getInterpreterGeneratedContent_FetchedAt()
	 * @model
	 * @generated
	 */
	Date getFetchedAt();

	/**
	 * Sets the value of the '{@link org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getFetchedAt <em>Fetched At</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Fetched At</em>' attribute.
	 * @see #getFetchedAt()
	 * @generated
	 */
	void setFetchedAt(Date value);

	/**
	 * Returns the value of the '<em><b>Value</b></em>' attribute list.
	 * The list contents are of type {@link java.lang.String}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Value</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Value</em>' attribute list.
	 * @see org.eclipse.dltk.launching.model.LaunchingModelPackage#getInterpreterGeneratedContent_Value()
	 * @model unique="false"
	 * @generated
	 */
	EList<String> getValue();

	/**
	 * Returns the value of the '<em><b>Last Modified</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Last Modified</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Last Modified</em>' attribute.
	 * @see #setLastModified(Date)
	 * @see org.eclipse.dltk.launching.model.LaunchingModelPackage#getInterpreterGeneratedContent_LastModified()
	 * @model
	 * @generated
	 */
	Date getLastModified();

	/**
	 * Sets the value of the '{@link org.eclipse.dltk.launching.model.InterpreterGeneratedContent#getLastModified <em>Last Modified</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Last Modified</em>' attribute.
	 * @see #getLastModified()
	 * @generated
	 */
	void setLastModified(Date value);

} // InterpreterGeneratedContent
