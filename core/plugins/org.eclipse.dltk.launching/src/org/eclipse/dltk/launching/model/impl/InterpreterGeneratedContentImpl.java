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
 * $Id: InterpreterGeneratedContentImpl.java,v 1.1 2010/05/23 14:20:39 apanchenk Exp $
 */
package org.eclipse.dltk.launching.model.impl;

import java.util.Collection;
import java.util.Date;

import org.eclipse.dltk.launching.model.InterpreterGeneratedContent;
import org.eclipse.dltk.launching.model.LaunchingModelPackage;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.util.EDataTypeEList;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Interpreter Generated Content</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.dltk.launching.model.impl.InterpreterGeneratedContentImpl#getKey <em>Key</em>}</li>
 *   <li>{@link org.eclipse.dltk.launching.model.impl.InterpreterGeneratedContentImpl#getInterpreterLastModified <em>Interpreter Last Modified</em>}</li>
 *   <li>{@link org.eclipse.dltk.launching.model.impl.InterpreterGeneratedContentImpl#getFetchedAt <em>Fetched At</em>}</li>
 *   <li>{@link org.eclipse.dltk.launching.model.impl.InterpreterGeneratedContentImpl#getValue <em>Value</em>}</li>
 *   <li>{@link org.eclipse.dltk.launching.model.impl.InterpreterGeneratedContentImpl#getLastModified <em>Last Modified</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public class InterpreterGeneratedContentImpl extends EObjectImpl implements InterpreterGeneratedContent {
	/**
	 * The default value of the '{@link #getKey() <em>Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKey()
	 * @generated
	 * @ordered
	 */
	protected static final String KEY_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getKey() <em>Key</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getKey()
	 * @generated
	 * @ordered
	 */
	protected String key = KEY_EDEFAULT;

	/**
	 * The default value of the '{@link #getInterpreterLastModified() <em>Interpreter Last Modified</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInterpreterLastModified()
	 * @generated
	 * @ordered
	 */
	protected static final Date INTERPRETER_LAST_MODIFIED_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getInterpreterLastModified() <em>Interpreter Last Modified</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getInterpreterLastModified()
	 * @generated
	 * @ordered
	 */
	protected Date interpreterLastModified = INTERPRETER_LAST_MODIFIED_EDEFAULT;

	/**
	 * The default value of the '{@link #getFetchedAt() <em>Fetched At</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFetchedAt()
	 * @generated
	 * @ordered
	 */
	protected static final Date FETCHED_AT_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getFetchedAt() <em>Fetched At</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getFetchedAt()
	 * @generated
	 * @ordered
	 */
	protected Date fetchedAt = FETCHED_AT_EDEFAULT;

	/**
	 * The cached value of the '{@link #getValue() <em>Value</em>}' attribute list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValue()
	 * @generated
	 * @ordered
	 */
	protected EList<String> value;

	/**
	 * The default value of the '{@link #getLastModified() <em>Last Modified</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLastModified()
	 * @generated
	 * @ordered
	 */
	protected static final Date LAST_MODIFIED_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getLastModified() <em>Last Modified</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getLastModified()
	 * @generated
	 * @ordered
	 */
	protected Date lastModified = LAST_MODIFIED_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected InterpreterGeneratedContentImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return LaunchingModelPackage.Literals.INTERPRETER_GENERATED_CONTENT;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getKey() {
		return key;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setKey(String newKey) {
		String oldKey = key;
		key = newKey;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__KEY, oldKey, key));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Date getInterpreterLastModified() {
		return interpreterLastModified;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setInterpreterLastModified(Date newInterpreterLastModified) {
		Date oldInterpreterLastModified = interpreterLastModified;
		interpreterLastModified = newInterpreterLastModified;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__INTERPRETER_LAST_MODIFIED, oldInterpreterLastModified, interpreterLastModified));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Date getFetchedAt() {
		return fetchedAt;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setFetchedAt(Date newFetchedAt) {
		Date oldFetchedAt = fetchedAt;
		fetchedAt = newFetchedAt;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__FETCHED_AT, oldFetchedAt, fetchedAt));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EList<String> getValue() {
		if (value == null) {
			value = new EDataTypeEList<String>(String.class, this, LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__VALUE);
		}
		return value;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public Date getLastModified() {
		return lastModified;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setLastModified(Date newLastModified) {
		Date oldLastModified = lastModified;
		lastModified = newLastModified;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__LAST_MODIFIED, oldLastModified, lastModified));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__KEY:
				return getKey();
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__INTERPRETER_LAST_MODIFIED:
				return getInterpreterLastModified();
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__FETCHED_AT:
				return getFetchedAt();
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__VALUE:
				return getValue();
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__LAST_MODIFIED:
				return getLastModified();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__KEY:
				setKey((String)newValue);
				return;
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__INTERPRETER_LAST_MODIFIED:
				setInterpreterLastModified((Date)newValue);
				return;
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__FETCHED_AT:
				setFetchedAt((Date)newValue);
				return;
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__VALUE:
				getValue().clear();
				getValue().addAll((Collection<? extends String>)newValue);
				return;
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__LAST_MODIFIED:
				setLastModified((Date)newValue);
				return;
		}
		super.eSet(featureID, newValue);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eUnset(int featureID) {
		switch (featureID) {
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__KEY:
				setKey(KEY_EDEFAULT);
				return;
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__INTERPRETER_LAST_MODIFIED:
				setInterpreterLastModified(INTERPRETER_LAST_MODIFIED_EDEFAULT);
				return;
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__FETCHED_AT:
				setFetchedAt(FETCHED_AT_EDEFAULT);
				return;
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__VALUE:
				getValue().clear();
				return;
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__LAST_MODIFIED:
				setLastModified(LAST_MODIFIED_EDEFAULT);
				return;
		}
		super.eUnset(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public boolean eIsSet(int featureID) {
		switch (featureID) {
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__KEY:
				return KEY_EDEFAULT == null ? key != null : !KEY_EDEFAULT.equals(key);
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__INTERPRETER_LAST_MODIFIED:
				return INTERPRETER_LAST_MODIFIED_EDEFAULT == null ? interpreterLastModified != null : !INTERPRETER_LAST_MODIFIED_EDEFAULT.equals(interpreterLastModified);
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__FETCHED_AT:
				return FETCHED_AT_EDEFAULT == null ? fetchedAt != null : !FETCHED_AT_EDEFAULT.equals(fetchedAt);
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__VALUE:
				return value != null && !value.isEmpty();
			case LaunchingModelPackage.INTERPRETER_GENERATED_CONTENT__LAST_MODIFIED:
				return LAST_MODIFIED_EDEFAULT == null ? lastModified != null : !LAST_MODIFIED_EDEFAULT.equals(lastModified);
		}
		return super.eIsSet(featureID);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public String toString() {
		if (eIsProxy()) return super.toString();

		StringBuffer result = new StringBuffer(super.toString());
		result.append(" (key: "); //$NON-NLS-1$
		result.append(key);
		result.append(", interpreterLastModified: "); //$NON-NLS-1$
		result.append(interpreterLastModified);
		result.append(", fetchedAt: "); //$NON-NLS-1$
		result.append(fetchedAt);
		result.append(", value: "); //$NON-NLS-1$
		result.append(value);
		result.append(", lastModified: "); //$NON-NLS-1$
		result.append(lastModified);
		result.append(')');
		return result.toString();
	}

} //InterpreterGeneratedContentImpl
