/**
 * <copyright>
 * </copyright>
 *
 * $Id: ValidatorsPackage.java,v 1.3 2009/02/28 03:24:29 apanchenk Exp $
 */
package org.eclipse.dltk.validators.configs;

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
 * @see org.eclipse.dltk.validators.configs.ValidatorsFactory
 * @model kind="package"
 * @generated
 */
public interface ValidatorsPackage extends EPackage {
	/**
	 * The package name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNAME = "configs"; //$NON-NLS-1$

	/**
	 * The package namespace URI.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_URI = "http://www.eclipse.org/dltk/validators"; //$NON-NLS-1$

	/**
	 * The package namespace name.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	String eNS_PREFIX = "validators"; //$NON-NLS-1$

	/**
	 * The singleton instance of the package.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 */
	ValidatorsPackage eINSTANCE = org.eclipse.dltk.validators.configs.impl.ValidatorsPackageImpl.init();

	/**
	 * The meta object id for the '{@link org.eclipse.dltk.validators.configs.impl.ValidatorConfigImpl <em>Validator Config</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.dltk.validators.configs.impl.ValidatorConfigImpl
	 * @see org.eclipse.dltk.validators.configs.impl.ValidatorsPackageImpl#getValidatorConfig()
	 * @generated
	 */
	int VALIDATOR_CONFIG = 0;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_CONFIG__NAME = 0;

	/**
	 * The feature id for the '<em><b>Read Only</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_CONFIG__READ_ONLY = 1;

	/**
	 * The feature id for the '<em><b>Command Line Options</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_CONFIG__COMMAND_LINE_OPTIONS = 2;

	/**
	 * The number of structural features of the '<em>Validator Config</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_CONFIG_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.dltk.validators.configs.impl.ValidatorEnvironmentInstanceImpl <em>Validator Environment Instance</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.dltk.validators.configs.impl.ValidatorEnvironmentInstanceImpl
	 * @see org.eclipse.dltk.validators.configs.impl.ValidatorsPackageImpl#getValidatorEnvironmentInstance()
	 * @generated
	 */
	int VALIDATOR_ENVIRONMENT_INSTANCE = 1;

	/**
	 * The feature id for the '<em><b>Environment Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_ENVIRONMENT_INSTANCE__ENVIRONMENT_ID = 0;

	/**
	 * The feature id for the '<em><b>Executable Path</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_ENVIRONMENT_INSTANCE__EXECUTABLE_PATH = 1;

	/**
	 * The feature id for the '<em><b>Automatic</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_ENVIRONMENT_INSTANCE__AUTOMATIC = 2;

	/**
	 * The number of structural features of the '<em>Validator Environment Instance</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_ENVIRONMENT_INSTANCE_FEATURE_COUNT = 3;

	/**
	 * The meta object id for the '{@link org.eclipse.dltk.validators.configs.impl.ValidatorInstanceImpl <em>Validator Instance</em>}' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @see org.eclipse.dltk.validators.configs.impl.ValidatorInstanceImpl
	 * @see org.eclipse.dltk.validators.configs.impl.ValidatorsPackageImpl#getValidatorInstance()
	 * @generated
	 */
	int VALIDATOR_INSTANCE = 2;

	/**
	 * The feature id for the '<em><b>Name</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_INSTANCE__NAME = 0;

	/**
	 * The feature id for the '<em><b>Automatic</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_INSTANCE__AUTOMATIC = 1;

	/**
	 * The feature id for the '<em><b>Validator Type</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_INSTANCE__VALIDATOR_TYPE = 2;

	/**
	 * The feature id for the '<em><b>Validator Nature</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_INSTANCE__VALIDATOR_NATURE = 3;

	/**
	 * The feature id for the '<em><b>Validator Favorite Config</b></em>' reference.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_INSTANCE__VALIDATOR_FAVORITE_CONFIG = 4;

	/**
	 * The feature id for the '<em><b>Id</b></em>' attribute.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_INSTANCE__ID = 5;

	/**
	 * The number of structural features of the '<em>Validator Instance</em>' class.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @generated
	 * @ordered
	 */
	int VALIDATOR_INSTANCE_FEATURE_COUNT = 6;


	/**
	 * Returns the meta object for class '{@link org.eclipse.dltk.validators.configs.ValidatorConfig <em>Validator Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Validator Config</em>'.
	 * @see org.eclipse.dltk.validators.configs.ValidatorConfig
	 * @generated
	 */
	EClass getValidatorConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.validators.configs.ValidatorConfig#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.dltk.validators.configs.ValidatorConfig#getName()
	 * @see #getValidatorConfig()
	 * @generated
	 */
	EAttribute getValidatorConfig_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.validators.configs.ValidatorConfig#isReadOnly <em>Read Only</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Read Only</em>'.
	 * @see org.eclipse.dltk.validators.configs.ValidatorConfig#isReadOnly()
	 * @see #getValidatorConfig()
	 * @generated
	 */
	EAttribute getValidatorConfig_ReadOnly();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.validators.configs.ValidatorConfig#getCommandLineOptions <em>Command Line Options</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Command Line Options</em>'.
	 * @see org.eclipse.dltk.validators.configs.ValidatorConfig#getCommandLineOptions()
	 * @see #getValidatorConfig()
	 * @generated
	 */
	EAttribute getValidatorConfig_CommandLineOptions();

	/**
	 * Returns the meta object for class '{@link org.eclipse.dltk.validators.configs.ValidatorEnvironmentInstance <em>Validator Environment Instance</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Validator Environment Instance</em>'.
	 * @see org.eclipse.dltk.validators.configs.ValidatorEnvironmentInstance
	 * @generated
	 */
	EClass getValidatorEnvironmentInstance();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.validators.configs.ValidatorEnvironmentInstance#getEnvironmentId <em>Environment Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Environment Id</em>'.
	 * @see org.eclipse.dltk.validators.configs.ValidatorEnvironmentInstance#getEnvironmentId()
	 * @see #getValidatorEnvironmentInstance()
	 * @generated
	 */
	EAttribute getValidatorEnvironmentInstance_EnvironmentId();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.validators.configs.ValidatorEnvironmentInstance#getExecutablePath <em>Executable Path</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Executable Path</em>'.
	 * @see org.eclipse.dltk.validators.configs.ValidatorEnvironmentInstance#getExecutablePath()
	 * @see #getValidatorEnvironmentInstance()
	 * @generated
	 */
	EAttribute getValidatorEnvironmentInstance_ExecutablePath();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.validators.configs.ValidatorEnvironmentInstance#isAutomatic <em>Automatic</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Automatic</em>'.
	 * @see org.eclipse.dltk.validators.configs.ValidatorEnvironmentInstance#isAutomatic()
	 * @see #getValidatorEnvironmentInstance()
	 * @generated
	 */
	EAttribute getValidatorEnvironmentInstance_Automatic();

	/**
	 * Returns the meta object for class '{@link org.eclipse.dltk.validators.configs.ValidatorInstance <em>Validator Instance</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for class '<em>Validator Instance</em>'.
	 * @see org.eclipse.dltk.validators.configs.ValidatorInstance
	 * @generated
	 */
	EClass getValidatorInstance();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.validators.configs.ValidatorInstance#getName <em>Name</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Name</em>'.
	 * @see org.eclipse.dltk.validators.configs.ValidatorInstance#getName()
	 * @see #getValidatorInstance()
	 * @generated
	 */
	EAttribute getValidatorInstance_Name();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.validators.configs.ValidatorInstance#isAutomatic <em>Automatic</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Automatic</em>'.
	 * @see org.eclipse.dltk.validators.configs.ValidatorInstance#isAutomatic()
	 * @see #getValidatorInstance()
	 * @generated
	 */
	EAttribute getValidatorInstance_Automatic();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.validators.configs.ValidatorInstance#getValidatorType <em>Validator Type</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Validator Type</em>'.
	 * @see org.eclipse.dltk.validators.configs.ValidatorInstance#getValidatorType()
	 * @see #getValidatorInstance()
	 * @generated
	 */
	EAttribute getValidatorInstance_ValidatorType();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.validators.configs.ValidatorInstance#getValidatorNature <em>Validator Nature</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Validator Nature</em>'.
	 * @see org.eclipse.dltk.validators.configs.ValidatorInstance#getValidatorNature()
	 * @see #getValidatorInstance()
	 * @generated
	 */
	EAttribute getValidatorInstance_ValidatorNature();

	/**
	 * Returns the meta object for the reference '{@link org.eclipse.dltk.validators.configs.ValidatorInstance#getValidatorFavoriteConfig <em>Validator Favorite Config</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the reference '<em>Validator Favorite Config</em>'.
	 * @see org.eclipse.dltk.validators.configs.ValidatorInstance#getValidatorFavoriteConfig()
	 * @see #getValidatorInstance()
	 * @generated
	 */
	EReference getValidatorInstance_ValidatorFavoriteConfig();

	/**
	 * Returns the meta object for the attribute '{@link org.eclipse.dltk.validators.configs.ValidatorInstance#getId <em>Id</em>}'.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the meta object for the attribute '<em>Id</em>'.
	 * @see org.eclipse.dltk.validators.configs.ValidatorInstance#getId()
	 * @see #getValidatorInstance()
	 * @generated
	 */
	EAttribute getValidatorInstance_Id();

	/**
	 * Returns the factory that creates the instances of the model.
	 * <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * @return the factory that creates the instances of the model.
	 * @generated
	 */
	ValidatorsFactory getValidatorsFactory();

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
		 * The meta object literal for the '{@link org.eclipse.dltk.validators.configs.impl.ValidatorConfigImpl <em>Validator Config</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.dltk.validators.configs.impl.ValidatorConfigImpl
		 * @see org.eclipse.dltk.validators.configs.impl.ValidatorsPackageImpl#getValidatorConfig()
		 * @generated
		 */
		EClass VALIDATOR_CONFIG = eINSTANCE.getValidatorConfig();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATOR_CONFIG__NAME = eINSTANCE.getValidatorConfig_Name();

		/**
		 * The meta object literal for the '<em><b>Read Only</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATOR_CONFIG__READ_ONLY = eINSTANCE.getValidatorConfig_ReadOnly();

		/**
		 * The meta object literal for the '<em><b>Command Line Options</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATOR_CONFIG__COMMAND_LINE_OPTIONS = eINSTANCE.getValidatorConfig_CommandLineOptions();

		/**
		 * The meta object literal for the '{@link org.eclipse.dltk.validators.configs.impl.ValidatorEnvironmentInstanceImpl <em>Validator Environment Instance</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.dltk.validators.configs.impl.ValidatorEnvironmentInstanceImpl
		 * @see org.eclipse.dltk.validators.configs.impl.ValidatorsPackageImpl#getValidatorEnvironmentInstance()
		 * @generated
		 */
		EClass VALIDATOR_ENVIRONMENT_INSTANCE = eINSTANCE.getValidatorEnvironmentInstance();

		/**
		 * The meta object literal for the '<em><b>Environment Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATOR_ENVIRONMENT_INSTANCE__ENVIRONMENT_ID = eINSTANCE.getValidatorEnvironmentInstance_EnvironmentId();

		/**
		 * The meta object literal for the '<em><b>Executable Path</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATOR_ENVIRONMENT_INSTANCE__EXECUTABLE_PATH = eINSTANCE.getValidatorEnvironmentInstance_ExecutablePath();

		/**
		 * The meta object literal for the '<em><b>Automatic</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATOR_ENVIRONMENT_INSTANCE__AUTOMATIC = eINSTANCE.getValidatorEnvironmentInstance_Automatic();

		/**
		 * The meta object literal for the '{@link org.eclipse.dltk.validators.configs.impl.ValidatorInstanceImpl <em>Validator Instance</em>}' class.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @see org.eclipse.dltk.validators.configs.impl.ValidatorInstanceImpl
		 * @see org.eclipse.dltk.validators.configs.impl.ValidatorsPackageImpl#getValidatorInstance()
		 * @generated
		 */
		EClass VALIDATOR_INSTANCE = eINSTANCE.getValidatorInstance();

		/**
		 * The meta object literal for the '<em><b>Name</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATOR_INSTANCE__NAME = eINSTANCE.getValidatorInstance_Name();

		/**
		 * The meta object literal for the '<em><b>Automatic</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATOR_INSTANCE__AUTOMATIC = eINSTANCE.getValidatorInstance_Automatic();

		/**
		 * The meta object literal for the '<em><b>Validator Type</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATOR_INSTANCE__VALIDATOR_TYPE = eINSTANCE.getValidatorInstance_ValidatorType();

		/**
		 * The meta object literal for the '<em><b>Validator Nature</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATOR_INSTANCE__VALIDATOR_NATURE = eINSTANCE.getValidatorInstance_ValidatorNature();

		/**
		 * The meta object literal for the '<em><b>Validator Favorite Config</b></em>' reference feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EReference VALIDATOR_INSTANCE__VALIDATOR_FAVORITE_CONFIG = eINSTANCE.getValidatorInstance_ValidatorFavoriteConfig();

		/**
		 * The meta object literal for the '<em><b>Id</b></em>' attribute feature.
		 * <!-- begin-user-doc -->
		 * <!-- end-user-doc -->
		 * @generated
		 */
		EAttribute VALIDATOR_INSTANCE__ID = eINSTANCE.getValidatorInstance_Id();

	}

} //ValidatorsPackage
