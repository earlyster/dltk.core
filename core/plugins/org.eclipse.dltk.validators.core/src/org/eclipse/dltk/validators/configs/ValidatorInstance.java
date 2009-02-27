/**
 * <copyright>
 * </copyright>
 *
 * $Id: ValidatorInstance.java,v 1.2 2009/02/27 15:44:46 apanchenk Exp $
 */
package org.eclipse.dltk.validators.configs;

import org.eclipse.emf.common.util.EList;

import org.eclipse.emf.ecore.EObject;

/**
 * <!-- begin-user-doc -->
 * A representation of the model object '<em><b>Validator Instance</b></em>'.
 * <!-- end-user-doc -->
 *
 * <p>
 * The following features are supported:
 * <ul>
 *   <li>{@link org.eclipse.dltk.validators.configs.ValidatorInstance#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.dltk.validators.configs.ValidatorInstance#isAutomatic <em>Automatic</em>}</li>
 *   <li>{@link org.eclipse.dltk.validators.configs.ValidatorInstance#getValidatorType <em>Validator Type</em>}</li>
 *   <li>{@link org.eclipse.dltk.validators.configs.ValidatorInstance#getValidatorNature <em>Validator Nature</em>}</li>
 *   <li>{@link org.eclipse.dltk.validators.configs.ValidatorInstance#getValidatorFavoriteConfig <em>Validator Favorite Config</em>}</li>
 * </ul>
 * </p>
 *
 * @see org.eclipse.dltk.validators.configs.ValidatorsPackage#getValidatorInstance()
 * @model abstract="true"
 * @generated
 */
public interface ValidatorInstance extends EObject {
	/**
	 * Returns the value of the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Name</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Name</em>' attribute.
	 * @see #setName(String)
	 * @see org.eclipse.dltk.validators.configs.ValidatorsPackage#getValidatorInstance_Name()
	 * @model
	 * @generated
	 */
	String getName();

	/**
	 * Sets the value of the '{@link org.eclipse.dltk.validators.configs.ValidatorInstance#getName <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Name</em>' attribute.
	 * @see #getName()
	 * @generated
	 */
	void setName(String value);

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
	 * @see org.eclipse.dltk.validators.configs.ValidatorsPackage#getValidatorInstance_Automatic()
	 * @model
	 * @generated
	 */
	boolean isAutomatic();

	/**
	 * Sets the value of the '{@link org.eclipse.dltk.validators.configs.ValidatorInstance#isAutomatic <em>Automatic</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Automatic</em>' attribute.
	 * @see #isAutomatic()
	 * @generated
	 */
	void setAutomatic(boolean value);

	/**
	 * Returns the value of the '<em><b>Validator Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Validator Type</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Validator Type</em>' attribute.
	 * @see #setValidatorType(String)
	 * @see org.eclipse.dltk.validators.configs.ValidatorsPackage#getValidatorInstance_ValidatorType()
	 * @model
	 * @generated
	 */
	String getValidatorType();

	/**
	 * Sets the value of the '{@link org.eclipse.dltk.validators.configs.ValidatorInstance#getValidatorType <em>Validator Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Validator Type</em>' attribute.
	 * @see #getValidatorType()
	 * @generated
	 */
	void setValidatorType(String value);

	/**
	 * Returns the value of the '<em><b>Validator Nature</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Validator Nature</em>' attribute isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Validator Nature</em>' attribute.
	 * @see #setValidatorNature(String)
	 * @see org.eclipse.dltk.validators.configs.ValidatorsPackage#getValidatorInstance_ValidatorNature()
	 * @model
	 * @generated
	 */
	String getValidatorNature();

	/**
	 * Sets the value of the '{@link org.eclipse.dltk.validators.configs.ValidatorInstance#getValidatorNature <em>Validator Nature</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Validator Nature</em>' attribute.
	 * @see #getValidatorNature()
	 * @generated
	 */
	void setValidatorNature(String value);

	/**
	 * Returns the value of the '<em><b>Validator Favorite Config</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <p>
	 * If the meaning of the '<em>Validator Favorite Config</em>' reference isn't clear,
	 * there really should be more of a description here...
	 * </p>
	 * <!-- end-user-doc -->
	 * @return the value of the '<em>Validator Favorite Config</em>' reference.
	 * @see #setValidatorFavoriteConfig(ValidatorConfig)
	 * @see org.eclipse.dltk.validators.configs.ValidatorsPackage#getValidatorInstance_ValidatorFavoriteConfig()
	 * @model resolveProxies="false" transient="true" volatile="true" derived="true"
	 * @generated
	 */
	ValidatorConfig getValidatorFavoriteConfig();

	/**
	 * Sets the value of the '{@link org.eclipse.dltk.validators.configs.ValidatorInstance#getValidatorFavoriteConfig <em>Validator Favorite Config</em>}' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @param value the new value of the '<em>Validator Favorite Config</em>' reference.
	 * @see #getValidatorFavoriteConfig()
	 * @generated
	 */
	void setValidatorFavoriteConfig(ValidatorConfig value);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	EList<ValidatorConfig> getValidatorConfigs();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @model kind="operation"
	 * @generated
	 */
	EList<ValidatorEnvironmentInstance> getValidatorEnvironments();

} // ValidatorInstance
