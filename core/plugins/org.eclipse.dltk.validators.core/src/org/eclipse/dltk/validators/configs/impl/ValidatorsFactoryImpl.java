/**
 * <copyright>
 * </copyright>
 *
 * $Id: ValidatorsFactoryImpl.java,v 1.1 2009/02/27 09:14:25 apanchenk Exp $
 */
package org.eclipse.dltk.validators.configs.impl;

import org.eclipse.dltk.validators.configs.*;

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
public class ValidatorsFactoryImpl extends EFactoryImpl implements ValidatorsFactory {
	/**
	 * Creates the default factory implementation.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public static ValidatorsFactory init() {
		try {
			ValidatorsFactory theValidatorsFactory = (ValidatorsFactory)EPackage.Registry.INSTANCE.getEFactory("http://www.eclipse.org/dltk/validators"); //$NON-NLS-1$ 
			if (theValidatorsFactory != null) {
				return theValidatorsFactory;
			}
		}
		catch (Exception exception) {
			EcorePlugin.INSTANCE.log(exception);
		}
		return new ValidatorsFactoryImpl();
	}

	/**
	 * Creates an instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ValidatorsFactoryImpl() {
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
			default:
				throw new IllegalArgumentException("The class '" + eClass.getName() + "' is not a valid classifier"); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ValidatorsPackage getValidatorsPackage() {
		return (ValidatorsPackage)getEPackage();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @deprecated
	 * @generated
	 */
	@Deprecated
	public static ValidatorsPackage getPackage() {
		return ValidatorsPackage.eINSTANCE;
	}

} //ValidatorsFactoryImpl
