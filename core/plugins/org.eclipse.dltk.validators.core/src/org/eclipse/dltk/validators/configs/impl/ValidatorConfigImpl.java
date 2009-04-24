/**
 * <copyright>
 * </copyright>
 *
 * $Id: ValidatorConfigImpl.java,v 1.1.2.1 2009/04/24 14:47:33 apanchenk Exp $
 */
package org.eclipse.dltk.validators.configs.impl;

import org.eclipse.dltk.validators.configs.ValidatorConfig;
import org.eclipse.dltk.validators.configs.ValidatorsPackage;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.URI;

import org.eclipse.emf.ecore.EClass;

import org.eclipse.emf.ecore.impl.ENotificationImpl;
import org.eclipse.emf.ecore.impl.EObjectImpl;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model object '<em><b>Validator Config</b></em>'.
 * <!-- end-user-doc -->
 * <p>
 * The following features are implemented:
 * <ul>
 *   <li>{@link org.eclipse.dltk.validators.configs.impl.ValidatorConfigImpl#getName <em>Name</em>}</li>
 *   <li>{@link org.eclipse.dltk.validators.configs.impl.ValidatorConfigImpl#isReadOnly <em>Read Only</em>}</li>
 *   <li>{@link org.eclipse.dltk.validators.configs.impl.ValidatorConfigImpl#getCommandLineOptions <em>Command Line Options</em>}</li>
 *   <li>{@link org.eclipse.dltk.validators.configs.impl.ValidatorConfigImpl#getPriority <em>Priority</em>}</li>
 * </ul>
 * </p>
 *
 * @generated
 */
public abstract class ValidatorConfigImpl extends EObjectImpl implements ValidatorConfig {
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
	 * The default value of the '{@link #isReadOnly() <em>Read Only</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #isReadOnly()
	 * @generated
	 * @ordered
	 */
	protected static final boolean READ_ONLY_EDEFAULT = false;

	/**
	 * The default value of the '{@link #getCommandLineOptions() <em>Command Line Options</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCommandLineOptions()
	 * @generated
	 * @ordered
	 */
	protected static final String COMMAND_LINE_OPTIONS_EDEFAULT = null;

	/**
	 * The cached value of the '{@link #getCommandLineOptions() <em>Command Line Options</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getCommandLineOptions()
	 * @generated
	 * @ordered
	 */
	protected String commandLineOptions = COMMAND_LINE_OPTIONS_EDEFAULT;

	/**
	 * The default value of the '{@link #getPriority() <em>Priority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPriority()
	 * @generated
	 * @ordered
	 */
	protected static final int PRIORITY_EDEFAULT = 0;

	/**
	 * The cached value of the '{@link #getPriority() <em>Priority</em>}' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #getPriority()
	 * @generated
	 * @ordered
	 */
	protected int priority = PRIORITY_EDEFAULT;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	protected ValidatorConfigImpl() {
		super();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	protected EClass eStaticClass() {
		return ValidatorsPackage.Literals.VALIDATOR_CONFIG;
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
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.VALIDATOR_CONFIG__NAME, oldName, name));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated NOT
	 */
	public boolean isReadOnly() {
		final Resource r = eResource();
		if (r != null) {
			final URI uri = r.getURI();
			if (uri != null) {
				return uri.isPlatformPlugin();
			}
		}
		return false;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public String getCommandLineOptions() {
		return commandLineOptions;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setCommandLineOptions(String newCommandLineOptions) {
		String oldCommandLineOptions = commandLineOptions;
		commandLineOptions = newCommandLineOptions;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.VALIDATOR_CONFIG__COMMAND_LINE_OPTIONS, oldCommandLineOptions, commandLineOptions));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void setPriority(int newPriority) {
		int oldPriority = priority;
		priority = newPriority;
		if (eNotificationRequired())
			eNotify(new ENotificationImpl(this, Notification.SET, ValidatorsPackage.VALIDATOR_CONFIG__PRIORITY, oldPriority, priority));
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	@Override
	public Object eGet(int featureID, boolean resolve, boolean coreType) {
		switch (featureID) {
			case ValidatorsPackage.VALIDATOR_CONFIG__NAME:
				return getName();
			case ValidatorsPackage.VALIDATOR_CONFIG__READ_ONLY:
				return isReadOnly() ? Boolean.TRUE : Boolean.FALSE;
			case ValidatorsPackage.VALIDATOR_CONFIG__COMMAND_LINE_OPTIONS:
				return getCommandLineOptions();
			case ValidatorsPackage.VALIDATOR_CONFIG__PRIORITY:
				return getPriority();
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
			case ValidatorsPackage.VALIDATOR_CONFIG__NAME:
				setName((String)newValue);
				return;
			case ValidatorsPackage.VALIDATOR_CONFIG__COMMAND_LINE_OPTIONS:
				setCommandLineOptions((String)newValue);
				return;
			case ValidatorsPackage.VALIDATOR_CONFIG__PRIORITY:
				setPriority((Integer)newValue);
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
			case ValidatorsPackage.VALIDATOR_CONFIG__NAME:
				setName(NAME_EDEFAULT);
				return;
			case ValidatorsPackage.VALIDATOR_CONFIG__COMMAND_LINE_OPTIONS:
				setCommandLineOptions(COMMAND_LINE_OPTIONS_EDEFAULT);
				return;
			case ValidatorsPackage.VALIDATOR_CONFIG__PRIORITY:
				setPriority(PRIORITY_EDEFAULT);
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
			case ValidatorsPackage.VALIDATOR_CONFIG__NAME:
				return NAME_EDEFAULT == null ? name != null : !NAME_EDEFAULT.equals(name);
			case ValidatorsPackage.VALIDATOR_CONFIG__READ_ONLY:
				return isReadOnly() != READ_ONLY_EDEFAULT;
			case ValidatorsPackage.VALIDATOR_CONFIG__COMMAND_LINE_OPTIONS:
				return COMMAND_LINE_OPTIONS_EDEFAULT == null ? commandLineOptions != null : !COMMAND_LINE_OPTIONS_EDEFAULT.equals(commandLineOptions);
			case ValidatorsPackage.VALIDATOR_CONFIG__PRIORITY:
				return priority != PRIORITY_EDEFAULT;
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
		result.append(", commandLineOptions: "); //$NON-NLS-1$
		result.append(commandLineOptions);
		result.append(", priority: "); //$NON-NLS-1$
		result.append(priority);
		result.append(')');
		return result.toString();
	}

} //ValidatorConfigImpl
