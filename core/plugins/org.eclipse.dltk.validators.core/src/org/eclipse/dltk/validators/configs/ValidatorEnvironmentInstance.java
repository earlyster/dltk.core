/**
 * <copyright>
 * </copyright>
 *
 * $Id: ValidatorEnvironmentInstance.java,v 1.2 2009/02/27 15:44:46 apanchenk Exp $
 */
package org.eclipse.dltk.validators.configs;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Validator Environment Instance</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.dltk.validators.configs.ValidatorEnvironmentInstance#getEnvironmentId <em>Environment Id</em>}</li>
 *   <li>{@link org.eclipse.dltk.validators.configs.ValidatorEnvironmentInstance#getExecutablePath <em>Executable Path</em>}</li>
 *   <li>{@link org.eclipse.dltk.validators.configs.ValidatorEnvironmentInstance#isAutomatic <em>Automatic</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.dltk.validators.configs.ValidatorsPackage#getValidatorEnvironmentInstance()
 * @model abstract="true"
 * @generated
 */
public interface ValidatorEnvironmentInstance extends EObject {
	/**
	 * Returns the value of the '<em><b>Environment Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Environment Id</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Environment Id</em>' attribute.
	 * @see #setEnvironmentId(String)
	 * @see org.eclipse.dltk.validators.configs.ValidatorsPackage#getValidatorEnvironmentInstance_EnvironmentId()
	 * @model
	 * @generated
	 */
	String getEnvironmentId();

	/**
	 * Sets the value of the '{@link org.eclipse.dltk.validators.configs.ValidatorEnvironmentInstance#getEnvironmentId <em>Environment Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Environment Id</em>' attribute.
	 * @see #getEnvironmentId()
	 * @generated
	 */
	void setEnvironmentId(String value);

	/**
	 * Returns the value of the '<em><b>Executable Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Executable Path</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Executable Path</em>' attribute.
	 * @see #setExecutablePath(String)
	 * @see org.eclipse.dltk.validators.configs.ValidatorsPackage#getValidatorEnvironmentInstance_ExecutablePath()
	 * @model
	 * @generated
	 */
	String getExecutablePath();

	/**
	 * Sets the value of the '{@link org.eclipse.dltk.validators.configs.ValidatorEnvironmentInstance#getExecutablePath <em>Executable Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Executable Path</em>' attribute.
	 * @see #getExecutablePath()
	 * @generated
	 */
	void setExecutablePath(String value);

	/**
	 * Returns the value of the '<em><b>Automatic</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Automatic</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Automatic</em>' attribute.
	 * @see #setAutomatic(boolean)
	 * @see org.eclipse.dltk.validators.configs.ValidatorsPackage#getValidatorEnvironmentInstance_Automatic()
	 * @model
	 * @generated
	 */
	boolean isAutomatic();

	/**
	 * Sets the value of the '{@link org.eclipse.dltk.validators.configs.ValidatorEnvironmentInstance#isAutomatic <em>Automatic</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Automatic</em>' attribute.
	 * @see #isAutomatic()
	 * @generated
	 */
	void setAutomatic(boolean value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	ValidatorInstance getValidatorInstance();

} // ValidatorEnvironmentInstance
