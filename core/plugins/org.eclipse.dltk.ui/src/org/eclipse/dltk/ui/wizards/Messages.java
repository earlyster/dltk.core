package org.eclipse.dltk.ui.wizards;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.dltk.ui.wizards.messages"; //$NON-NLS-1$
	public static String ConfigureFolders_description;
	public static String ConfigureFolders_localNameColumn;
	public static String ConfigureFolders_pathColumn;
	public static String ConfigureFolders_title;
	public static String ConfigureFolders_typeColumn;
	public static String ConfigureFolders_rootPath;
	public static String GenericDLTKProjectWizard_createNewDltkProject;
	public static String GenericDLTKProjectWizard_natureMustBeSpecified;
	public static String GenericDLTKProjectWizard_newDltkProject;
	public static String LinkedFolders_directory_label;
	public static String LinkedFolders_environment_label;
	public static String LinkedFolders_initializingFolders_taskName;
	public static String NewSourceModulePage_ConfigureTemplates;
	public static String NewSourceModulePage_file;
	public static String NewSourceModulePage_fileAlreadyExists;
	public static String NewSourceModulePage_noFoldersAvailable;
	public static String NewSourceModulePage_noTemplate;
	public static String NewSourceModulePage_pathCannotBeEmpty;
	public static String NewSourceModulePage_selectScriptFolder;
	public static String NewSourceModulePage_Template;
	public static String NewSourceModuleWizard_errorInOpenInEditor;
	public static String ProjectFolder_kind_libraryFolder;
	public static String ProjectFolder_kind_folder;
	public static String ProjectFolder_kind_other;
	public static String ProjectFolder_kind_sourceFolder;
	public static String ScriptProjectWizardSecondPage_operation_initialize;
	public static String SelectFolders_description;
	public static String SelectFolders_title;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
