/**
 * <copyright>
 * </copyright>
 *
 * $Id: CacheEntry.java,v 1.2 2009/05/19 09:04:45 asobolev Exp $
 */
package org.eclipse.dltk.core.caching.cache;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Entry</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.dltk.core.caching.cache.CacheEntry#getPath <em>Path</em>}</li>
 *   <li>{@link org.eclipse.dltk.core.caching.cache.CacheEntry#getTimestamp <em>Timestamp</em>}</li>
 *   <li>{@link org.eclipse.dltk.core.caching.cache.CacheEntry#getAttributes <em>Attributes</em>}</li>
 *   <li>{@link org.eclipse.dltk.core.caching.cache.CacheEntry#getLastAccessTime <em>Last Access Time</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.dltk.core.caching.cache.CachePackage#getCacheEntry()
 * @model
 * @generated
 */
public interface CacheEntry extends EObject {
	/**
	 * Returns the value of the '<em><b>Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Path</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Path</em>' attribute.
	 * @see #setPath(String)
	 * @see org.eclipse.dltk.core.caching.cache.CachePackage#getCacheEntry_Path()
	 * @model
	 * @generated
	 */
	String getPath();

	/**
	 * Sets the value of the '{@link org.eclipse.dltk.core.caching.cache.CacheEntry#getPath <em>Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Path</em>' attribute.
	 * @see #getPath()
	 * @generated
	 */
	void setPath(String value);

	/**
	 * Returns the value of the '<em><b>Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Timestamp</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Timestamp</em>' attribute.
	 * @see #setTimestamp(long)
	 * @see org.eclipse.dltk.core.caching.cache.CachePackage#getCacheEntry_Timestamp()
	 * @model
	 * @generated
	 */
	long getTimestamp();

	/**
	 * Sets the value of the '{@link org.eclipse.dltk.core.caching.cache.CacheEntry#getTimestamp <em>Timestamp</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Timestamp</em>' attribute.
	 * @see #getTimestamp()
	 * @generated
	 */
	void setTimestamp(long value);

	/**
	 * Returns the value of the '<em><b>Attributes</b></em>' containment reference list.
	 * The list contents are of type {@link org.eclipse.dltk.core.caching.cache.CacheEntryAttribute}.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Attributes</em>' containment reference list isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Attributes</em>' containment reference list.
	 * @see org.eclipse.dltk.core.caching.cache.CachePackage#getCacheEntry_Attributes()
	 * @model containment="true"
	 * @generated
	 */
	EList<CacheEntryAttribute> getAttributes();

	/**
	 * Returns the value of the '<em><b>Last Access Time</b></em>' attribute.
	 * The default value is <code>"0"</code>.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Last Access Time</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Last Access Time</em>' attribute.
	 * @see #setLastAccessTime(long)
	 * @see org.eclipse.dltk.core.caching.cache.CachePackage#getCacheEntry_LastAccessTime()
	 * @model default="0"
	 * @generated
	 */
	long getLastAccessTime();

	/**
	 * Sets the value of the '{@link org.eclipse.dltk.core.caching.cache.CacheEntry#getLastAccessTime <em>Last Access Time</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Last Access Time</em>' attribute.
	 * @see #getLastAccessTime()
	 * @generated
	 */
	void setLastAccessTime(long value);

} // CacheEntry
