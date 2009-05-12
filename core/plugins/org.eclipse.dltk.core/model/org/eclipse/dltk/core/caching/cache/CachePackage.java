/**
 * <copyright>
 * </copyright>
 *
 * $Id: CachePackage.java,v 1.1 2009/05/12 09:42:44 asobolev Exp $
 */
package org.eclipse.dltk.core.caching.cache;

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
 * @see org.eclipse.dltk.core.caching.cache.CacheFactory
 * @model kind="package"
 * @generated
 */
public interface CachePackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "cache";

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://eclipse.org/dltk/cache_model";

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "cache";

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	CachePackage eINSTANCE = org.eclipse.dltk.core.caching.cache.impl.CachePackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.dltk.core.caching.cache.impl.CacheEntryImpl <em>Entry</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.dltk.core.caching.cache.impl.CacheEntryImpl
	 * @see org.eclipse.dltk.core.caching.cache.impl.CachePackageImpl#getCacheEntry()
	 * @generated
	 */
	int CACHE_ENTRY = 0;

	/**
	 * The feature id for the '<em><b>Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CACHE_ENTRY__PATH = 0;

	/**
	 * The feature id for the '<em><b>Timestamp</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CACHE_ENTRY__TIMESTAMP = 1;

	/**
	 * The feature id for the '<em><b>Attributes</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CACHE_ENTRY__ATTRIBUTES = 2;

	/**
	 * The number of structural features of the '<em>Entry</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CACHE_ENTRY_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.dltk.core.caching.cache.impl.CacheEntryAttributeImpl <em>Entry Attribute</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.dltk.core.caching.cache.impl.CacheEntryAttributeImpl
	 * @see org.eclipse.dltk.core.caching.cache.impl.CachePackageImpl#getCacheEntryAttribute()
	 * @generated
	 */
	int CACHE_ENTRY_ATTRIBUTE = 1;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CACHE_ENTRY_ATTRIBUTE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Location</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CACHE_ENTRY_ATTRIBUTE__LOCATION = 1;

	/**
	 * The number of structural features of the '<em>Entry Attribute</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CACHE_ENTRY_ATTRIBUTE_FEATURE_COUNT = 2;

	/**
	 * The meta object id for the '{@link org.eclipse.dltk.core.caching.cache.impl.CacheIndexImpl <em>Index</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.dltk.core.caching.cache.impl.CacheIndexImpl
	 * @see org.eclipse.dltk.core.caching.cache.impl.CachePackageImpl#getCacheIndex()
	 * @generated
	 */
	int CACHE_INDEX = 2;

	/**
	 * The feature id for the '<em><b>Last Index</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CACHE_INDEX__LAST_INDEX = 0;

	/**
	 * The feature id for the '<em><b>Entries</b></em>' containment reference list.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CACHE_INDEX__ENTRIES = 1;

	/**
	 * The feature id for the '<em><b>Environment</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CACHE_INDEX__ENVIRONMENT = 2;

	/**
	 * The number of structural features of the '<em>Index</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int CACHE_INDEX_FEATURE_COUNT = 3;


	/**
	 * Returns the meta object for class '{@link org.eclipse.dltk.core.caching.cache.CacheEntry <em>Entry</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Entry</em>'.
	 * @see org.eclipse.dltk.core.caching.cache.CacheEntry
	 * @generated
	 */
	EClass getCacheEntry();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.core.caching.cache.CacheEntry#getPath <em>Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Path</em>'.
	 * @see org.eclipse.dltk.core.caching.cache.CacheEntry#getPath()
	 * @see #getCacheEntry()
	 * @generated
	 */
	EAttribute getCacheEntry_Path();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.core.caching.cache.CacheEntry#getTimestamp <em>Timestamp</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Timestamp</em>'.
	 * @see org.eclipse.dltk.core.caching.cache.CacheEntry#getTimestamp()
	 * @see #getCacheEntry()
	 * @generated
	 */
	EAttribute getCacheEntry_Timestamp();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.dltk.core.caching.cache.CacheEntry#getAttributes <em>Attributes</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Attributes</em>'.
	 * @see org.eclipse.dltk.core.caching.cache.CacheEntry#getAttributes()
	 * @see #getCacheEntry()
	 * @generated
	 */
	EReference getCacheEntry_Attributes();

	/**
	 * Returns the meta object for class '{@link org.eclipse.dltk.core.caching.cache.CacheEntryAttribute <em>Entry Attribute</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Entry Attribute</em>'.
	 * @see org.eclipse.dltk.core.caching.cache.CacheEntryAttribute
	 * @generated
	 */
	EClass getCacheEntryAttribute();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.core.caching.cache.CacheEntryAttribute#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.dltk.core.caching.cache.CacheEntryAttribute#getName()
	 * @see #getCacheEntryAttribute()
	 * @generated
	 */
	EAttribute getCacheEntryAttribute_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.core.caching.cache.CacheEntryAttribute#getLocation <em>Location</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Location</em>'.
	 * @see org.eclipse.dltk.core.caching.cache.CacheEntryAttribute#getLocation()
	 * @see #getCacheEntryAttribute()
	 * @generated
	 */
	EAttribute getCacheEntryAttribute_Location();

	/**
	 * Returns the meta object for class '{@link org.eclipse.dltk.core.caching.cache.CacheIndex <em>Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Index</em>'.
	 * @see org.eclipse.dltk.core.caching.cache.CacheIndex
	 * @generated
	 */
	EClass getCacheIndex();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.core.caching.cache.CacheIndex#getLastIndex <em>Last Index</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Last Index</em>'.
	 * @see org.eclipse.dltk.core.caching.cache.CacheIndex#getLastIndex()
	 * @see #getCacheIndex()
	 * @generated
	 */
	EAttribute getCacheIndex_LastIndex();

	/**
	 * Returns the meta object for the containment reference list '{@link org.eclipse.dltk.core.caching.cache.CacheIndex#getEntries <em>Entries</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the containment reference list '<em>Entries</em>'.
	 * @see org.eclipse.dltk.core.caching.cache.CacheIndex#getEntries()
	 * @see #getCacheIndex()
	 * @generated
	 */
	EReference getCacheIndex_Entries();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.core.caching.cache.CacheIndex#getEnvironment <em>Environment</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Environment</em>'.
	 * @see org.eclipse.dltk.core.caching.cache.CacheIndex#getEnvironment()
	 * @see #getCacheIndex()
	 * @generated
	 */
	EAttribute getCacheIndex_Environment();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	CacheFactory getCacheFactory();

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
		 * The meta object literal for the '{@link org.eclipse.dltk.core.caching.cache.impl.CacheEntryImpl <em>Entry</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.dltk.core.caching.cache.impl.CacheEntryImpl
		 * @see org.eclipse.dltk.core.caching.cache.impl.CachePackageImpl#getCacheEntry()
		 * @generated
		 */
		EClass CACHE_ENTRY = eINSTANCE.getCacheEntry();

		/**
		 * The meta object literal for the '<em><b>Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CACHE_ENTRY__PATH = eINSTANCE.getCacheEntry_Path();

		/**
		 * The meta object literal for the '<em><b>Timestamp</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CACHE_ENTRY__TIMESTAMP = eINSTANCE.getCacheEntry_Timestamp();

		/**
		 * The meta object literal for the '<em><b>Attributes</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CACHE_ENTRY__ATTRIBUTES = eINSTANCE.getCacheEntry_Attributes();

		/**
		 * The meta object literal for the '{@link org.eclipse.dltk.core.caching.cache.impl.CacheEntryAttributeImpl <em>Entry Attribute</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.dltk.core.caching.cache.impl.CacheEntryAttributeImpl
		 * @see org.eclipse.dltk.core.caching.cache.impl.CachePackageImpl#getCacheEntryAttribute()
		 * @generated
		 */
		EClass CACHE_ENTRY_ATTRIBUTE = eINSTANCE.getCacheEntryAttribute();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CACHE_ENTRY_ATTRIBUTE__NAME = eINSTANCE.getCacheEntryAttribute_Name();

		/**
		 * The meta object literal for the '<em><b>Location</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CACHE_ENTRY_ATTRIBUTE__LOCATION = eINSTANCE.getCacheEntryAttribute_Location();

		/**
		 * The meta object literal for the '{@link org.eclipse.dltk.core.caching.cache.impl.CacheIndexImpl <em>Index</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.dltk.core.caching.cache.impl.CacheIndexImpl
		 * @see org.eclipse.dltk.core.caching.cache.impl.CachePackageImpl#getCacheIndex()
		 * @generated
		 */
		EClass CACHE_INDEX = eINSTANCE.getCacheIndex();

		/**
		 * The meta object literal for the '<em><b>Last Index</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CACHE_INDEX__LAST_INDEX = eINSTANCE.getCacheIndex_LastIndex();

		/**
		 * The meta object literal for the '<em><b>Entries</b></em>' containment reference list feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference CACHE_INDEX__ENTRIES = eINSTANCE.getCacheIndex_Entries();

		/**
		 * The meta object literal for the '<em><b>Environment</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute CACHE_INDEX__ENVIRONMENT = eINSTANCE.getCacheIndex_Environment();

	}

} //CachePackage
