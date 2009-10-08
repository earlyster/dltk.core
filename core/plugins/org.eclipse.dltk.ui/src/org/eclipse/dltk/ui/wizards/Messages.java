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
	public static String NewSourceModulePage_InvalidFileName;
	public static String NewSourceModulePage_LinkToFolder;
	public static String NewSourceModulePage_noFoldersAvailable;
	public static String NewSourceModulePage_noTemplate;
	public static String NewSourceModulePage_pathCannotBeEmpty;
	public static String NewSourceModulePage_remoteFolder_BrowseButton;
	public static String NewSourceModulePage_remoteFolder_label;
	public static String NewSourceModulePage_remoteFolderCannotBeEmpty;
	public static String NewSourceModulePage_remoteFolderNotExist;
	public static String NewSourceModulePage_selectScriptFolder;
	public static String NewSourceModulePage_Template;
	/**
	 * @since 2.0
	 */
	public static String NewSourceModulePage_error_uri_location_unkown;

	/**
	 * @since 2.0
	 */
	public static String NewSourceModulePage_error_TypeNameExistsDifferentCase;

	public static String NewSourceModuleWizard_errorInOpenInEditor;
	static {
		// initialize resource bundle
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
