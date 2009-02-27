/**
 * <copyright>
 * </copyright>
 *
 * $Id: ValidatorEnvironmentInstanceImpl.java,v 1.2 2009/02/27 15:44:46 apanchenk Exp $
 */
package org.eclipse.dltk.validators.configs.impl;

import org.eclipse.dltk.validators.configs.ValidatorEnvironmentInstance;
import org.eclipse.dltk.validators.configs.ValidatorInstance;
import org.eclipse.dltk.validators.configs.ValidatorsPackage;

import org.eclipse.emf.common.notify.Notification;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Validator Environment Instance</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.dltk.validators.configs.impl.ValidatorEnvironmentInstanceImpl#getEnvironmentId <em>Environment Id</em>}</li>
 *   <li>{@link org.eclipse.dltk.validators.configs.impl.ValidatorEnvironmentInstanceImpl#getExecutablePath <em>Executable Path</em>}</li>
 *   <li>{@link org.eclipse.dltk.validators.configs.impl.ValidatorEnvironmentInstanceImpl#isAutomatic <em>Automatic</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class ValidatorEnvironmentInstanceImpl extends EObjectImpl implements ValidatorEnvironmentInstance {
	/**
	 * The default value of the '{@link #getEnvironmentId() <em>Environment Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEnvironmentId()
	 * @generated
	 * @ordered
	 */
	protected static final String ENVIRONMENT_ID_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getEnvironmentId() <em>Environment Id</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getEnvironmentId()
	 * @generated
	 * @ordered
	 */
	protected String environmentId = ENVIRONMENT_ID_EDEFAULT;

	/**
	 * The default value of the '{@link #getExecutablePath() <em>Executable Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExecutablePath()
	 * @generated
	 * @ordered
	 */
	protected static final String EXECUTABLE_PATH_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getExecutablePath() <em>Executable Path</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getExecutablePath()
	 * @generated
	 * @ordered
	 */
	protected String executablePath = EXECUTABLE_PATH_EDEFAULT;

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
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ValidatorEnvironmentInstanceImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ValidatorsPackage.Literals.VALIDATOR_ENVIRONMENT_INSTANCE;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getEnvironmentId() {
		return environmentId;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setEnvironmentId(String newEnvironmentId) {
		String oldEnvironmentId = environmentId;
		environmentId = newEnvironmentId;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.VALIDATOR_ENVIRONMENT_INSTANCE__ENVIRONMENT_ID, oldEnvironmentId, environmentId));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getExecutablePath() {
		return executablePath;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setExecutablePath(String newExecutablePath) {
		String oldExecutablePath = executablePath;
		executablePath = newExecutablePath;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.VALIDATOR_ENVIRONMENT_INSTANCE__EXECUTABLE_PATH, oldExecutablePath, executablePath));
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
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.VALIDATOR_ENVIRONMENT_INSTANCE__AUTOMATIC, oldAutomatic, automatic));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public abstract ValidatorInstance getValidatorInstance();

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ValidatorsPackage.VALIDATOR_ENVIRONMENT_INSTANCE__ENVIRONMENT_ID:
				return getEnvironmentId();
			case ValidatorsPackage.VALIDATOR_ENVIRONMENT_INSTANCE__EXECUTABLE_PATH:
				return getExecutablePath();
			case ValidatorsPackage.VALIDATOR_ENVIRONMENT_INSTANCE__AUTOMATIC:
				return isAutomatic() ? Boolean.TRUE : Boolean.FALSE;
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
			case ValidatorsPackage.VALIDATOR_ENVIRONMENT_INSTANCE__ENVIRONMENT_ID:
				setEnvironmentId((String)newValue);
				return;
			case ValidatorsPackage.VALIDATOR_ENVIRONMENT_INSTANCE__EXECUTABLE_PATH:
				setExecutablePath((String)newValue);
				return;
			case ValidatorsPackage.VALIDATOR_ENVIRONMENT_INSTANCE__AUTOMATIC:
				setAutomatic(((Boolean)newValue).booleanValue());
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
			case ValidatorsPackage.VALIDATOR_ENVIRONMENT_INSTANCE__ENVIRONMENT_ID:
				setEnvironmentId(ENVIRONMENT_ID_EDEFAULT);
				return;
			case ValidatorsPackage.VALIDATOR_ENVIRONMENT_INSTANCE__EXECUTABLE_PATH:
				setExecutablePath(EXECUTABLE_PATH_EDEFAULT);
				return;
			case ValidatorsPackage.VALIDATOR_ENVIRONMENT_INSTANCE__AUTOMATIC:
				setAutomatic(AUTOMATIC_EDEFAULT);
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
			case ValidatorsPackage.VALIDATOR_ENVIRONMENT_INSTANCE__ENVIRONMENT_ID:
				return ENVIRONMENT_ID_EDEFAULT == null ? environmentId != null : !ENVIRONMENT_ID_EDEFAULT.equals(environmentId);
			case ValidatorsPackage.VALIDATOR_ENVIRONMENT_INSTANCE__EXECUTABLE_PATH:
				return EXECUTABLE_PATH_EDEFAULT == null ? executablePath != null : !EXECUTABLE_PATH_EDEFAULT.equals(executablePath);
			case ValidatorsPackage.VALIDATOR_ENVIRONMENT_INSTANCE__AUTOMATIC:
				return automatic != AUTOMATIC_EDEFAULT;
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
		result.append(" (environmentId: "); //$NON-NLS-1$
		result.append(environmentId);
		result.append(", executablePath: "); //$NON-NLS-1$
		result.append(executablePath);
		result.append(", automatic: "); //$NON-NLS-1$
		result.append(automatic);
		result.append(')');
		return result.toString();
	}

} //ValidatorEnvironmentInstanceImpl
