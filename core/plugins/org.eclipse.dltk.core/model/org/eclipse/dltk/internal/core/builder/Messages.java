package org.eclipse.dltk.internal.core.builder;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.internal.core.builder.messages"; //$NON-NLS-1$
	public static String ScriptBuilder_building;
	public static String ScriptBuilder_building_N_externalModules;
	public static String ScriptBuilder_building_N_localModules;
	public static String ScriptBuilder_buildingScriptsIn;
	public static String ScriptBuilder_cleaningScriptsIn;
	public static String ScriptBuilder_errorBuildElements;
	public static String ScriptBuilder_locatingResourcesFor;
	public static String ScriptBuilder_Locating_source_modules;
	public static String ScriptBuilder_scanningExternalFolder;
	public static String ScriptBuilder_scanningExternalFolders;
	public static String ScriptBuilder_scanningProject;
	public static String ScriptBuilder_scanningProjectFolder;
	public static String BuildParticipantManager_buildParticipantCreateError;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	public static String ValidatorDefinitionsContainer_failedToLoadValidatorFromXml;
	public static String ValidatorDefinitionsContainer_unknownValidatorType;
	public static String ValidatorRuntime_badFormat;
	public static String ValidatorBuilder_buildingModules;
	public static String ValidatorBuilder_buildModuleSubTask;
	public static String ValidatorBuilder_buildExternalModuleSubTask;
	public static String ValidatorBuilder_clearingResourceMarkers;
	public static String ValidatorBuilder_errorDeleteResourceMarkers;
	public static String ValidatorBuilder_finalizeBuild;
	public static String ValidatorBuilder_InitializeBuilders;
	public static String ValidatorBuilder_unknownError;
	public static String ValidatorsCore_exception;

	private Messages() {
	}
}
