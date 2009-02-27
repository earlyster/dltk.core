/**
 * <copyright>
 * </copyright>
 *
 * $Id: ValidatorsPackageImpl.java,v 1.2 2009/02/27 15:44:46 apanchenk Exp $
 */
package org.eclipse.dltk.validators.configs.impl;

import org.eclipse.dltk.validators.configs.ValidatorConfig;
import org.eclipse.dltk.validators.configs.ValidatorEnvironmentInstance;
import org.eclipse.dltk.validators.configs.ValidatorInstance;
import org.eclipse.dltk.validators.configs.ValidatorsFactory;
import org.eclipse.dltk.validators.configs.ValidatorsPackage;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.impl.EPackageImpl;

/**
 * <!-- begin-user-doc -->
 * An implementation of the model <b>Package</b>.
 * <!-- end-user-doc -->
 * @generated
 */
public class ValidatorsPackageImpl extends EPackageImpl implements ValidatorsPackage {
	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass validatorConfigEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass validatorEnvironmentInstanceEClass = null;

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private EClass validatorInstanceEClass = null;

	/**
	 * Creates an instance of the model <b>Package</b>, registered with
	 * {@link org.eclipse.emf.ecore.EPackage.Registry EPackage.Registry} by the package
	 * package URI value.
	 * <p>Note: the correct way to create the package is via the static
	 * factory method {@link #init init()}, which also performs
	 * initialization of the package, or returns the registered package,
	 * if one already exists.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.emf.ecore.EPackage.Registry
	 * @see org.eclipse.dltk.validators.configs.ValidatorsPackage#eNS_URI
	 * @see #init()
	 * @generated
	 */
	private ValidatorsPackageImpl() {
		super(eNS_URI, ValidatorsFactory.eINSTANCE);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private static boolean isInited = false;

	/**
	 * Creates, registers, and initializes the <b>Package</b> for this
	 * model, and for any others upon which it depends.  Simple
	 * dependencies are satisfied by calling this method on all
	 * dependent packages before doing anything else.  This method drives
	 * initialization for interdependent packages directly, in parallel
	 * with this package, itself.
	 * <p>Of this package and its interdependencies, all packages which
	 * have not yet been registered by their URI values are first created
	 * and registered.  The packages are then initialized in two steps:
	 * meta-model objects for all of the packages are created before any
	 * are initialized, since one package's meta-model objects may refer to
	 * those of another.
	 * <p>Invocation of this method will not affect any packages that have
	 * already been initialized.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see #eNS_URI
	 * @see #createPackageContents()
	 * @see #initializePackageContents()
	 * @generated
	 */
	public static ValidatorsPackage init() {
		if (isInited) return (ValidatorsPackage)EPackage.Registry.INSTANCE.getEPackage(ValidatorsPackage.eNS_URI);

		// Obtain or create and register package
		ValidatorsPackageImpl theValidatorsPackage = (ValidatorsPackageImpl)(EPackage.Registry.INSTANCE.getEPackage(eNS_URI) instanceof ValidatorsPackageImpl ? EPackage.Registry.INSTANCE.getEPackage(eNS_URI) : new ValidatorsPackageImpl());

		isInited = true;

		// Create package meta-data objects
		theValidatorsPackage.createPackageContents();

		// Initialize created meta-data
		theValidatorsPackage.initializePackageContents();

		// Mark meta-data to indicate it can't be changed
		theValidatorsPackage.freeze();

		return theValidatorsPackage;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getValidatorConfig() {
		return validatorConfigEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getValidatorConfig_Name() {
		return (EAttribute)validatorConfigEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getValidatorConfig_ReadOnly() {
		return (EAttribute)validatorConfigEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getValidatorConfig_CommandLineOptions() {
		return (EAttribute)validatorConfigEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getValidatorEnvironmentInstance() {
		return validatorEnvironmentInstanceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getValidatorEnvironmentInstance_EnvironmentId() {
		return (EAttribute)validatorEnvironmentInstanceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getValidatorEnvironmentInstance_ExecutablePath() {
		return (EAttribute)validatorEnvironmentInstanceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getValidatorEnvironmentInstance_Automatic() {
		return (EAttribute)validatorEnvironmentInstanceEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EClass getValidatorInstance() {
		return validatorInstanceEClass;
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getValidatorInstance_Name() {
		return (EAttribute)validatorInstanceEClass.getEStructuralFeatures().get(0);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getValidatorInstance_Automatic() {
		return (EAttribute)validatorInstanceEClass.getEStructuralFeatures().get(1);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getValidatorInstance_ValidatorType() {
		return (EAttribute)validatorInstanceEClass.getEStructuralFeatures().get(2);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EAttribute getValidatorInstance_ValidatorNature() {
		return (EAttribute)validatorInstanceEClass.getEStructuralFeatures().get(3);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public EReference getValidatorInstance_ValidatorFavoriteConfig() {
		return (EReference)validatorInstanceEClass.getEStructuralFeatures().get(4);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public ValidatorsFactory getValidatorsFactory() {
		return (ValidatorsFactory)getEFactoryInstance();
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isCreated = false;

	/**
	 * Creates the meta-model objects for the package.  This method is
	 * guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void createPackageContents() {
		if (isCreated) return;
		isCreated = true;

		// Create classes and their features
		validatorConfigEClass = createEClass(VALIDATOR_CONFIG);
		createEAttribute(validatorConfigEClass, VALIDATOR_CONFIG__NAME);
		createEAttribute(validatorConfigEClass, VALIDATOR_CONFIG__READ_ONLY);
		createEAttribute(validatorConfigEClass, VALIDATOR_CONFIG__COMMAND_LINE_OPTIONS);

		validatorEnvironmentInstanceEClass = createEClass(VALIDATOR_ENVIRONMENT_INSTANCE);
		createEAttribute(validatorEnvironmentInstanceEClass, VALIDATOR_ENVIRONMENT_INSTANCE__ENVIRONMENT_ID);
		createEAttribute(validatorEnvironmentInstanceEClass, VALIDATOR_ENVIRONMENT_INSTANCE__EXECUTABLE_PATH);
		createEAttribute(validatorEnvironmentInstanceEClass, VALIDATOR_ENVIRONMENT_INSTANCE__AUTOMATIC);

		validatorInstanceEClass = createEClass(VALIDATOR_INSTANCE);
		createEAttribute(validatorInstanceEClass, VALIDATOR_INSTANCE__NAME);
		createEAttribute(validatorInstanceEClass, VALIDATOR_INSTANCE__AUTOMATIC);
		createEAttribute(validatorInstanceEClass, VALIDATOR_INSTANCE__VALIDATOR_TYPE);
		createEAttribute(validatorInstanceEClass, VALIDATOR_INSTANCE__VALIDATOR_NATURE);
		createEReference(validatorInstanceEClass, VALIDATOR_INSTANCE__VALIDATOR_FAVORITE_CONFIG);
	}

	/**
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	private boolean isInitialized = false;

	/**
	 * Complete the initialization of the package and its meta-model.  This
	 * method is guarded to have no affect on any invocation but its first.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	public void initializePackageContents() {
		if (isInitialized) return;
		isInitialized = true;

		// Initialize package
		setName(eNAME);
		setNsPrefix(eNS_PREFIX);
		setNsURI(eNS_URI);

		// Create type parameters

		// Set bounds for type parameters

		// Add supertypes to classes

		// Initialize classes and features; add operations and parameters
		initEClass(validatorConfigEClass, ValidatorConfig.class, "ValidatorConfig", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getValidatorConfig_Name(), ecorePackage.getEString(), "name", null, 0, 1, ValidatorConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getValidatorConfig_ReadOnly(), ecorePackage.getEBoolean(), "readOnly", null, 0, 1, ValidatorConfig.class, IS_TRANSIENT, IS_VOLATILE, !IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getValidatorConfig_CommandLineOptions(), ecorePackage.getEString(), "commandLineOptions", null, 0, 1, ValidatorConfig.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		initEClass(validatorEnvironmentInstanceEClass, ValidatorEnvironmentInstance.class, "ValidatorEnvironmentInstance", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getValidatorEnvironmentInstance_EnvironmentId(), ecorePackage.getEString(), "environmentId", null, 0, 1, ValidatorEnvironmentInstance.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getValidatorEnvironmentInstance_ExecutablePath(), ecorePackage.getEString(), "executablePath", null, 0, 1, ValidatorEnvironmentInstance.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getValidatorEnvironmentInstance_Automatic(), ecorePackage.getEBoolean(), "automatic", null, 0, 1, ValidatorEnvironmentInstance.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		addEOperation(validatorEnvironmentInstanceEClass, this.getValidatorInstance(), "getValidatorInstance", 0, 1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

		initEClass(validatorInstanceEClass, ValidatorInstance.class, "ValidatorInstance", IS_ABSTRACT, !IS_INTERFACE, IS_GENERATED_INSTANCE_CLASS); //$NON-NLS-1$
		initEAttribute(getValidatorInstance_Name(), ecorePackage.getEString(), "name", null, 0, 1, ValidatorInstance.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getValidatorInstance_Automatic(), ecorePackage.getEBoolean(), "automatic", null, 0, 1, ValidatorInstance.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getValidatorInstance_ValidatorType(), ecorePackage.getEString(), "validatorType", null, 0, 1, ValidatorInstance.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEAttribute(getValidatorInstance_ValidatorNature(), ecorePackage.getEString(), "validatorNature", null, 0, 1, ValidatorInstance.class, !IS_TRANSIENT, !IS_VOLATILE, IS_CHANGEABLE, !IS_UNSETTABLE, !IS_ID, IS_UNIQUE, !IS_DERIVED, IS_ORDERED); //$NON-NLS-1$
		initEReference(getValidatorInstance_ValidatorFavoriteConfig(), this.getValidatorConfig(), null, "validatorFavoriteConfig", null, 0, 1, ValidatorInstance.class, IS_TRANSIENT, IS_VOLATILE, IS_CHANGEABLE, !IS_COMPOSITE, !IS_RESOLVE_PROXIES, !IS_UNSETTABLE, IS_UNIQUE, IS_DERIVED, IS_ORDERED); //$NON-NLS-1$

		addEOperation(validatorInstanceEClass, this.getValidatorConfig(), "getValidatorConfigs", 0, -1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

		addEOperation(validatorInstanceEClass, this.getValidatorEnvironmentInstance(), "getValidatorEnvironments", 0, -1, IS_UNIQUE, IS_ORDERED); //$NON-NLS-1$

		// Create resource
		createResource(eNS_URI);
	}

} //ValidatorsPackageImpl
