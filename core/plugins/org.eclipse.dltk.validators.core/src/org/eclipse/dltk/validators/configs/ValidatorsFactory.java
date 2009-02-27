/**
 * <copyright>
 * </copyright>
 *
 * $Id: ValidatorsFactory.java,v 1.1 2009/02/27 09:14:24 apanchenk Exp $
 */
package org.eclipse.dltk.validators.configs;

import org.eclipse.emf.ecore.EFactory;

/**
 * <!-- begin-user-doc -->
 * The <b>Factory</b> for the model.
 * It provides a create method for each non-abstract class of the model.
 * <!-- end-user-doc -->
 * @see org.eclipse.dltk.validators.configs.ValidatorsPackage
 * @generated
 */
public interface ValidatorsFactory extends EFactory {
	/**
	 * The singleton instance of the factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ValidatorsFactory eINSTANCE = org.eclipse.dltk.validators.configs.impl.ValidatorsFactoryImpl.init();

	/**
	 * Returns the package supported by this factory.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the package supported by this factory.
	 * @generated
	 */
	ValidatorsPackage getValidatorsPackage();

} //ValidatorsFactory
