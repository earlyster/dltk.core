/**
 * <copyright>
 * </copyright>
 *
 * $Id: ValidatorInstanceImpl.java,v 1.2 2009/02/27 15:44:46 apanchenk Exp $
 */
package org.eclipse.dltk.validators.configs.impl;

import org.eclipse.dltk.validators.configs.ValidatorConfig;
import org.eclipse.dltk.validators.configs.ValidatorEnvironmentInstance;
import org.eclipse.dltk.validators.configs.ValidatorInstance;
import org.eclipse.dltk.validators.configs.ValidatorsPackage;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Validator Instance</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.dltk.validators.configs.impl.ValidatorInstanceImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.dltk.validators.configs.impl.ValidatorInstanceImpl#isAutomatic <em>Automatic</em>}</li>
 *   <li>{@link org.eclipse.dltk.validators.configs.impl.ValidatorInstanceImpl#getValidatorType <em>Validator Type</em>}</li>
 *   <li>{@link org.eclipse.dltk.validators.configs.impl.ValidatorInstanceImpl#getValidatorNature <em>Validator Nature</em>}</li>
 *   <li>{@link org.eclipse.dltk.validators.configs.impl.ValidatorInstanceImpl#getValidatorFavoriteConfig <em>Validator Favorite Config</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class ValidatorInstanceImpl extends EObjectImpl implements ValidatorInstance {
	/**
	 * The default value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected static final String NAME_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getName() <em>Name</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getName()
	 * @generated
	 * @ordered
	 */
	protected String name = NAME_EDEFAULT;

	/**
	 * The default value of the '{@link #isAutomatic() <em>Automatic</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAutomatic()
	 * @generated
	 * @ordered
	 */
	protected static final boolean AUTOMATIC_EDEFAULT = false;

	/**
	 * The cached value of the '{@link #isAutomatic() <em>Automatic</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isAutomatic()
	 * @generated
	 * @ordered
	 */
	protected boolean automatic = AUTOMATIC_EDEFAULT;

	/**
	 * The default value of the '{@link #getValidatorType() <em>Validator Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValidatorType()
	 * @generated
	 * @ordered
	 */
	protected static final String VALIDATOR_TYPE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getValidatorType() <em>Validator Type</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValidatorType()
	 * @generated
	 * @ordered
	 */
	protected String validatorType = VALIDATOR_TYPE_EDEFAULT;

	/**
	 * The default value of the '{@link #getValidatorNature() <em>Validator Nature</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValidatorNature()
	 * @generated
	 * @ordered
	 */
	protected static final String VALIDATOR_NATURE_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getValidatorNature() <em>Validator Nature</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getValidatorNature()
	 * @generated
	 * @ordered
	 */
	protected String validatorNature = VALIDATOR_NATURE_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ValidatorInstanceImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ValidatorsPackage.Literals.VALIDATOR_INSTANCE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getName() {
		return name;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setName(String newName) {
		String oldName = name;
		name = newName;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.VALIDATOR_INSTANCE__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public boolean isAutomatic() {
		return automatic;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setAutomatic(boolean newAutomatic) {
		boolean oldAutomatic = automatic;
		automatic = newAutomatic;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.VALIDATOR_INSTANCE__AUTOMATIC, oldAutomatic, automatic));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getValidatorType() {
		return validatorType;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setValidatorType(String newValidatorType) {
		String oldValidatorType = validatorType;
		validatorType = newValidatorType;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.VALIDATOR_INSTANCE__VALIDATOR_TYPE, oldValidatorType, validatorType));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getValidatorNature() {
		return validatorNature;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setValidatorNature(String newValidatorNature) {
		String oldValidatorNature = validatorNature;
		validatorNature = newValidatorNature;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.VALIDATOR_INSTANCE__VALIDATOR_NATURE, oldValidatorNature, validatorNature));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public abstract ValidatorConfig getValidatorFavoriteConfig();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public abstract void setValidatorFavoriteConfig(ValidatorConfig newValidatorFavoriteConfig);

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public abstract EList<ValidatorConfig> getValidatorConfigs();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public abstract EList<ValidatorEnvironmentInstance> getValidatorEnvironments();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ValidatorsPackage.VALIDATOR_INSTANCE__NAME:
				return getName();
			case ValidatorsPackage.VALIDATOR_INSTANCE__AUTOMATIC:
				return isAutomatic() ? Boolean.TRUE : Boolean.FALSE;
			case ValidatorsPackage.VALIDATOR_INSTANCE__VALIDATOR_TYPE:
				return getValidatorType();
			case ValidatorsPackage.VALIDATOR_INSTANCE__VALIDATOR_NATURE:
				return getValidatorNature();
			case ValidatorsPackage.VALIDATOR_INSTANCE__VALIDATOR_FAVORITE_CONFIG:
				return getValidatorFavoriteConfig();
		}
		return super.eGet(featureID, resolve, coreType);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public void eSet(int featureID, Object newValue) {
		switch (featureID) {
			case ValidatorsPackage.VALIDATOR_INSTANCE__NAME:
				setName((String)newValue);
				return;
			case ValidatorsPackage.VALIDATOR_INSTANCE__AUTOMATIC:
				setAutomatic(((Boolean)newValue).booleanValue());
				return;
			case ValidatorsPackage.VALIDATOR_INSTANCE__VALIDATOR_TYPE:
				setValidatorType((String)newValue);
				return;
			case ValidatorsPackage.VALIDATOR_INSTANCE__VALIDATOR_NATURE:
				setValidatorNature((String)newValue);
				return;
			case ValidatorsPackage.VALIDATOR_INSTANCE__VALIDATOR_FAVORITE_CONFIG:
				setValidatorFavoriteConfig((ValidatorConfig)newValue);
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
			case ValidatorsPackage.VALIDATOR_INSTANCE__NAME:
				setName(NAME_EDEFAULT);
				return;
			case ValidatorsPackage.VALIDATOR_INSTANCE__AUTOMATIC:
				setAutomatic(AUTOMATIC_EDEFAULT);
				return;
			case ValidatorsPackage.VALIDATOR_INSTANCE__VALIDATOR_TYPE:
				setValidatorType(VALIDATOR_TYPE_EDEFAULT);
				return;
			case ValidatorsPackage.VALIDATOR_INSTANCE__VALIDATOR_NATURE:
				setValidatorNature(VALIDATOR_NATURE_EDEFAULT);
				return;
			case ValidatorsPackage.VALIDATOR_INSTANCE__VALIDATOR_FAVORITE_CONFIG:
				setValidatorFavoriteConfig((ValidatorConfig)null);
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
			case ValidatorsPackage.VALIDATOR_INSTANCE__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case ValidatorsPackage.VALIDATOR_INSTANCE__AUTOMATIC:
				return automatic != AUTOMATIC_EDEFAULT;
			case ValidatorsPackage.VALIDATOR_INSTANCE__VALIDATOR_TYPE:
				return VALIDATOR_TYPE_EDEFAULT == null ? validatorType != null : !VALIDATOR_TYPE_EDEFAULT.equals(validatorType);
			case ValidatorsPackage.VALIDATOR_INSTANCE__VALIDATOR_NATURE:
				return VALIDATOR_NATURE_EDEFAULT == null ? validatorNature != null : !VALIDATOR_NATURE_EDEFAULT.equals(validatorNature);
			case ValidatorsPackage.VALIDATOR_INSTANCE__VALIDATOR_FAVORITE_CONFIG:
				return getValidatorFavoriteConfig() != null;
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
		result.append(" (name: "); //$NON-NLS-1$
		result.append(name);
		result.append(", automatic: "); //$NON-NLS-1$
		result.append(automatic);
		result.append(", validatorType: "); //$NON-NLS-1$
		result.append(validatorType);
		result.append(", validatorNature: "); //$NON-NLS-1$
		result.append(validatorNature);
		result.append(')');
		return result.toString();
	}

} //ValidatorInstanceImpl
