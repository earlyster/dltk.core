/**
 * <copyright>
 * </copyright>
 *
 * $Id: CacheIndex.java,v 1.1 2009/05/12 09:42:44 asobolev Exp $
 */
package org.eclipse.dltk.core.caching.cache;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Index</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.dltk.core.caching.cache.CacheIndex#getLastIndex <em>Last Index</em>}</li>
 *   <li>{@link org.eclipse.dltk.core.caching.cache.CacheIndex#getEntries <em>Entries</em>}</li>
 *   <li>{@link org.eclipse.dltk.core.caching.cache.CacheIndex#getEnvironment <em>Environment</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.dltk.core.caching.cache.CachePackage#getCacheIndex()
 * @model
 * @generated
 */
public interface CacheIndex extends EObject {
	/**
	 * Returns the value of the '<em><b>Last Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Last Index</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Last Index</em>' attribute.
	 * @see #setLastIndex(long)
	 * @see org.eclipse.dltk.core.caching.cache.CachePackage#getCacheIndex_LastIndex()
	 * @model
	 * @generated
	 */
	long getLastIndex();

	/**
	 * Sets the value of the '{@link org.eclipse.dltk.core.caching.cache.CacheIndex#getLastIndex <em>Last Index</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Last Index</em>' attribute.
	 * @see #getLastIndex()
	 * @generated
	 */
	void setLastIndex(long value);

	/**
	 * Returns the value of the '<em><b>Entries</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.dltk.core.caching.cache.CacheEntry}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Entries</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Entries</em>' containment reference list.
	 * @see org.eclipse.dltk.core.caching.cache.CachePackage#getCacheIndex_Entries()
	 * @model containment="true"
	 * @generated
	 */
	EList<CacheEntry> getEntries();

	/**
	 * Returns the value of the '<em><b>Environment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Environment</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Environment</em>' attribute.
	 * @see #setEnvironment(String)
	 * @see org.eclipse.dltk.core.caching.cache.CachePackage#getCacheIndex_Environment()
	 * @model
	 * @generated
	 */
	String getEnvironment();

	/**
	 * Sets the value of the '{@link org.eclipse.dltk.core.caching.cache.CacheIndex#getEnvironment <em>Environment</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Environment</em>' attribute.
	 * @see #getEnvironment()
	 * @generated
	 */
	void setEnvironment(String value);

} // CacheIndex
