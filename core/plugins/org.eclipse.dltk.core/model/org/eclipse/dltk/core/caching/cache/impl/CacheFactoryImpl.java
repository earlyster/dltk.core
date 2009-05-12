/**
 * <copyright>
 * </copyright>
 *
 * $Id: CacheFactoryImpl.java,v 1.1 2009/05/12 09:42:46 asobolev Exp $
 */
package org.eclipse.dltk.core.caching.cache.impl;

import org.eclipse.dltk.core.caching.cache.*;

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
public class CacheFactoryImpl extends EFactoryImpl implements CacheFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static CacheFactory init() {
		try {
			CacheFactory theCacheFactory = (CacheFactory)EPackage.Registry.INSTANCE.getEFactory("http://eclipse.org/dltk/cache_model"); 
			if (theCacheFactory != null) {
				return theCacheFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new CacheFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CacheFactoryImpl() {
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
			case CachePackage.CACHE_ENTRY: return createCacheEntry();
			case CachePackage.CACHE_ENTRY_ATTRIBUTE: return createCacheEntryAttribute();
			case CachePackage.CACHE_INDEX: return createCacheIndex();
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier");
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CacheEntry createCacheEntry() {
		CacheEntryImpl cacheEntry = new CacheEntryImpl();
		return cacheEntry;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CacheEntryAttribute createCacheEntryAttribute() {
		CacheEntryAttributeImpl cacheEntryAttribute = new CacheEntryAttributeImpl();
		return cacheEntryAttribute;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CacheIndex createCacheIndex() {
		CacheIndexImpl cacheIndex = new CacheIndexImpl();
		return cacheIndex;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public CachePackage getCachePackage() {
		return (CachePackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static CachePackage getPackage() {
		return CachePackage.eINSTANCE;
	}

} //CacheFactoryImpl
