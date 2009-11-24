/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.wizards;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.ScriptModelUtil;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.ui.util.SWTUtil;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.ComboDialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.SelectionButtonDialogField;
import org.eclipse.dltk.internal.ui.wizards.dialogfields.StringDialogField;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.ModelElementLabelProvider;
import org.eclipse.dltk.ui.dialogs.StatusInfo;
import org.eclipse.dltk.ui.preferences.CodeTemplatesPreferencePage;
import org.eclipse.dltk.ui.text.templates.ICodeTemplateArea;
import org.eclipse.dltk.ui.text.templates.SourceModuleTemplateContext;
import org.eclipse.dltk.ui.util.CodeGeneration;
import org.eclipse.dltk.ui.wizards.INewSourceModuleTemplate.IValidationNotifier;
import org.eclipse.dltk.utils.LazyExtensionManager;
import org.eclipse.dltk.utils.LazyExtensionManager.Descriptor;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.PreferencesUtil;

public abstract class NewSourceModulePage extends NewContainerWizardPage {

	private static final String FILE = "NewSourceModulePage.file"; //$NON-NLS-1$
	private static final String TEMPLATE = "NewSourceModulePage.template"; //$NON-NLS-1$

	private IStatus sourceMoudleStatus;
	private IStatus templateStatus = null;

	private IScriptFolder currentScriptFolder;

	private StringDialogField fileDialogField;

	static class TemplateDescriptor extends
			Descriptor<INewSourceModuleTemplate> {
		final String nature;
		final String name;

		/**
		 * @param configurationElement
		 */
		public TemplateDescriptor(TemplateManager manager,
				IConfigurationElement configurationElement) {
			super(manager, configurationElement);
			this.nature = configurationElement.getAttribute("nature");
			this.name = configurationElement.getAttribute("name");
		}

	}

	static class TemplateManager extends
			LazyExtensionManager<INewSourceModuleTemplate> {

		private String nature;

		/**
		 * @param extensionPoint
		 */
		public TemplateManager(String nature) {
			super(DLTKUIPlugin.PLUGIN_ID + ".sourceModuleTemplate"); //$NON-NLS-1$
			this.nature = nature;
		}

		@Override
		protected boolean isValidDescriptor(
				Descriptor<INewSourceModuleTemplate> descriptor) {
			String natureId = ((TemplateDescriptor) descriptor).nature;
			return natureId == null || this.nature.equals(natureId)
					|| natureId.equals("#");
		}

		@Override
		protected Descriptor<INewSourceModuleTemplate> createDescriptor(
				IConfigurationElement confElement) {
			return new TemplateDescriptor(this, confElement);
		}
	}

	private IStatus fileChanged() {
		StatusInfo status = new StatusInfo();

		if (getFileText().length() == 0) {
			status.setError(Messages.NewSourceModulePage_pathCannotBeEmpty);
		} else {
			if (!Path.EMPTY.isValidSegment(getFileText())) {
				status.setError(Messages.NewSourceModulePage_InvalidFileName);
			}
			if (currentScriptFolder != null) {
				ISourceModule module = currentScriptFolder
						.getSourceModule(getFileName());
				if (module.exists()) {
					status
							.setError(Messages.NewSourceModulePage_fileAlreadyExists);
				} else {
					IResource resource = module.getResource();
					if (resource != null) {
						URI location = resource.getLocationURI();
						if (location != null) {
							try {
								IFileStore store = EFS.getStore(location);
								if (store.fetchInfo().exists()) {
									status
											.setError(Messages.NewSourceModulePage_error_TypeNameExistsDifferentCase);
								}
							} catch (CoreException e) {
								status
										.setError(Messages.NewSourceModulePage_error_uri_location_unkown);
							}
						}
					}
				}
			}
		}

		return status;
	}

	/**
	 * The wizard owning this page is responsible for calling this method with
	 * the current selection. The selection is used to initialize the fields of
	 * the wizard page.
	 * 
	 * @param selection
	 *            used to initialize the fields
	 */
	public void init(IStructuredSelection selection) {
		if (getTemplateArea() != null) {
			createTemplateField();
		}

		IModelElement element = getInitialScriptElement(selection);

		initContainerPage(element);
		updateTemplates();

		updateStatus(new IStatus[] { containerStatus, fileChanged() });
	}

	protected void createFileControls(Composite parent, int nColumns) {
		fileDialogField.doFillIntoGrid(parent, nColumns - 1);
		Text text = fileDialogField.getTextControl(null);
		LayoutUtil.setWidthHint(text, getMaxFieldWidth());
		LayoutUtil.setHorizontalGrabbing(text);
		DialogField.createEmptySpace(parent);
	}

	private static final String NO_TEMPLATE = Util.EMPTY_STRING;
	private Template[] fTemplates;
	private ComboDialogField fTemplateDialogField = null;

	protected void createTemplateControls(Composite parent, int nColumns) {
		fTemplateDialogField.doFillIntoGrid(parent, nColumns - 1);
		LayoutUtil.setWidthHint(fTemplateDialogField.getComboControl(null),
				getMaxFieldWidth());
		final Button configureTemplates = new Button(parent, SWT.PUSH);
		GridData configureData = new GridData(SWT.FILL, SWT.NONE, false, false);
		configureData.widthHint = SWTUtil
				.getButtonWidthHint(configureTemplates);
		configureTemplates.setLayoutData(configureData);
		configureTemplates
				.setText(Messages.NewSourceModulePage_ConfigureTemplates);
		configureTemplates.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String templateName = null;
				final Template template = getSelectedTemplate();
				if (template != null) {
					templateName = template.getName();
				}
				Map data = null;
				if (templateName != null) {
					data = new HashMap();
					data.put(CodeTemplatesPreferencePage.DATA_SELECT_TEMPLATE,
							templateName);
				}
				// TODO handle project specific preferences if any?
				final String prefPageId = getTemplateArea()
						.getTemplatePreferencePageId();
				final PreferenceDialog dialog = PreferencesUtil
						.createPreferenceDialogOn(getShell(), prefPageId,
								new String[] { prefPageId }, data);
				if (dialog.open() == Window.OK) {
					updateTemplates();
				}
			}
		});
	}

	protected void updateTemplates() {
		if (fTemplateDialogField != null) {
			Template selected = getSelectedTemplate();
			String name = selected != null ? selected.getName()
					: getLastUsedTemplateName();
			fTemplates = getApplicableTemplates();
			int idx = 0;
			String[] names = new String[fTemplates.length + 1];
			for (int i = 0; i < fTemplates.length; i++) {
				names[i + 1] = fTemplates[i].getName();
				if (name != null && name.equals(names[i + 1])) {
					idx = i + 1;
				}
			}
			if (idx == 0) {
				final Template template = getDefaultTemplate();
				if (template != null) {
					for (int i = 0; i < fTemplates.length; ++i) {
						if (template == fTemplates[i]) {
							idx = i + 1;
							break;
						}
					}
				}
			}
			names[0] = Messages.NewSourceModulePage_noTemplate;
			fTemplateDialogField.setItems(names);
			fTemplateDialogField.selectItem(idx);
		}
	}

	protected Template getDefaultTemplate() {
		final String defaultTemplateId = getDefaultCodeTemplateId();
		if (defaultTemplateId != null) {
			final ICodeTemplateArea templateArea = getTemplateArea();
			if (templateArea != null) {
				final TemplateStore store = templateArea.getTemplateAccess()
						.getTemplateStore();
				return store.findTemplateById(defaultTemplateId);
			}
		}
		return null;
	}

	protected Template[] getApplicableTemplates() {
		final List<Template> result = new ArrayList<Template>();
		final ICodeTemplateArea templateArea = getTemplateArea();
		if (templateArea != null) {
			final TemplateStore store = templateArea.getTemplateAccess()
					.getTemplateStore();
			final String[] contextTypeIds = getCodeTemplateContextTypeIds();
			for (int i = 0; i < contextTypeIds.length; ++i) {
				Template[] templates = store.getTemplates(contextTypeIds[i]);
				Arrays.sort(templates, new Comparator<Template>() {
					public int compare(Template t0, Template t1) {
						return t0.getName().compareToIgnoreCase(t1.getName());
					}
				});
				for (int j = 0; j < templates.length; ++j) {
					result.add(templates[j]);
				}
			}
		}
		return result.toArray(new Template[result.size()]);
	}

	protected String getLastUsedTemplateKey() {
		return getClass().getName() + "_LAST_USED_TEMPLATE"; //$NON-NLS-1$
	}

	/**
	 * @return the name of the template used in the previous dialog invocation.
	 */
	protected String getLastUsedTemplateName() {
		final IDialogSettings dialogSettings = getDialogSettings();
		return dialogSettings != null ? dialogSettings
				.get(getLastUsedTemplateKey()) : null;
	}

	/**
	 * Saves the name of the last used template.
	 * 
	 * @param name
	 *            the name of a template, or an empty string for no template.
	 */
	protected void saveLastUsedTemplateName(String name) {
		final IDialogSettings dialogSettings = getDialogSettings();
		if (dialogSettings != null) {
			dialogSettings.put(getLastUsedTemplateKey(), name);
		}
	}

	protected Template getSelectedTemplate() {
		if (fTemplateDialogField != null) {
			int index = fTemplateDialogField.getSelectionIndex() - 1;
			if (index >= 0 && index < fTemplates.length) {
				return fTemplates[index];
			}
		}
		return null;
	}

	public NewSourceModulePage() {
		super("wizardPage"); //$NON-NLS-1$
		setTitle(getPageTitle());
		setDescription(getPageDescription());

		sourceMoudleStatus = new StatusInfo();

		// fileDialogField
		fileDialogField = new StringDialogField();
		fileDialogField.setLabelText(Messages.NewSourceModulePage_file);
		fileDialogField.setDialogFieldListener(new IDialogFieldListener() {
			public void dialogFieldChanged(DialogField field) {
				sourceMoudleStatus = fileChanged();
				handleFieldChanged(FILE);
			}
		});
	}

	protected void createTemplateField() {
		fTemplateDialogField = new ComboDialogField(SWT.READ_ONLY);
		fTemplateDialogField
				.setLabelText(Messages.NewSourceModulePage_Template);
	}

	@Override
	protected void handleFieldChanged(String fieldName) {
		super.handleFieldChanged(fieldName);
		if (fieldName == CONTAINER) {
			// IProjectFragment fragment = getProjectFragment();
			// if (fragment != null)
			//				currentScriptFolder = fragment.getScriptFolder(""); //$NON-NLS-1$
			// else
			// currentScriptFolder = null;
			currentScriptFolder = getScriptFolder();
			sourceMoudleStatus = fileChanged();
		}
		// add template statusess here
		templateStatus = null;
		for (SelectionButtonDialogField f : templateFields) {
			if (f.isSelected()) {
				INewSourceModuleTemplate template = templateFieldToTemplate
						.get(f);
				if (template != null) {
					templateStatus = template.validate(getScriptFolder(),
							getFileName());
				}
			}
		}

		updateStatus(new IStatus[] { containerStatus, sourceMoudleStatus,
				templateStatus });
	}

	public ISourceModule createFile(IProgressMonitor monitor)
			throws CoreException {
		if (monitor == null) {
			monitor = new NullProgressMonitor();
		}

		final String fileName = getFileName();
		final ISourceModule module = currentScriptFolder
				.getSourceModule(fileName);
		for (SelectionButtonDialogField f : templateFields) {
			if (f.isSelected()) {
				INewSourceModuleTemplate template = templateFieldToTemplate
						.get(f);
				if (template != null) {
					if (!template.createSourceModule(getScriptFolder(),
							fileName, getFileContent(module))) {
						return null;
					}
				}
			}
		}

		currentScriptFolder.createSourceModule(fileName,
				getFileContent(module), true, monitor);

		return module;
	}

	public void createControl(Composite parent) {
		initializeDialogUnits(parent);

		final int nColumns = 3;

		Composite composite = new Composite(parent, SWT.NONE);
		GridData layoutData = new GridData(SWT.FILL, SWT.NONE, true, false);
		composite.setLayoutData(layoutData);

		composite.setFont(parent.getFont());

		GridLayout layout = new GridLayout();
		layout.numColumns = nColumns;
		composite.setLayout(layout);

		createContentControls(composite, nColumns);

		setControl(composite);
		Dialog.applyDialogFont(composite);
	}

	private List<SelectionButtonDialogField> templateFields = new ArrayList<SelectionButtonDialogField>();
	private Map<SelectionButtonDialogField, INewSourceModuleTemplate> templateFieldToTemplate = new HashMap<SelectionButtonDialogField, INewSourceModuleTemplate>();
	IDialogFieldListener templateEnablementUpdater = new IDialogFieldListener() {
		public void dialogFieldChanged(DialogField field) {
			for (SelectionButtonDialogField f : templateFields) {
				INewSourceModuleTemplate template = templateFieldToTemplate
						.get(f);
				if (template != null) {
					template.updateEnablement(f.equals(field));
				}
			}
			handleFieldChanged(TEMPLATE);
		}
	};
	private IValidationNotifier validationNotifier = new IValidationNotifier() {
		public void validate() {
			handleFieldChanged(TEMPLATE);
		}
	};
	private List<TemplateDescriptor> activeTemplateDescriptors;

	/**
	 * Creates content controls on the specified composite.
	 * 
	 * @param composite
	 * @param nColumns
	 */
	protected void createContentControls(Composite composite, final int nColumns) {
		createContainerControls(composite, nColumns);

		// createPackageControls(composite, nColumns);
		createFileControls(composite, nColumns);
		TemplateManager manager = new TemplateManager(getRequiredNature());
		Descriptor<INewSourceModuleTemplate>[] descriptors = manager
				.getDescriptors();
		IScriptFolder scriptFolder = getScriptFolder();
		IEnvironment env = getEnvironment();
		activeTemplateDescriptors = new ArrayList<TemplateDescriptor>();
		for (Descriptor<INewSourceModuleTemplate> descriptor : descriptors) {
			TemplateDescriptor descr = (TemplateDescriptor) descriptor;
			INewSourceModuleTemplate template = descr.get();
			if (template.isAvailable(env, scriptFolder)) {
				activeTemplateDescriptors.add(descr);
			}
		}
		if (activeTemplateDescriptors.size() > 0) {
			Group contents = new Group(composite, SWT.NONE);
			contents.setText("Contents");
			GridData ggd = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
			ggd.horizontalSpan = nColumns;
			contents.setLayoutData(ggd);
			contents.setLayout(new GridLayout(nColumns, false));
			SelectionButtonDialogField lfield = new SelectionButtonDialogField(
					SWT.RADIO);
			lfield.setLabelText("Create new file in workspace");
			lfield.doFillIntoGrid(contents, nColumns);
			templateFields.add(lfield);
			lfield.setDialogFieldListener(templateEnablementUpdater);
			for (TemplateDescriptor descr : activeTemplateDescriptors) {
				INewSourceModuleTemplate template = descr.get();

				SelectionButtonDialogField field = new SelectionButtonDialogField(
						SWT.RADIO);
				field.setLabelText(descr.name);
				field.doFillIntoGrid(contents, 1);
				Composite parent = new Composite(contents, SWT.NONE);
				GridLayout layout = new GridLayout(3, false);
				layout.marginHeight = 0;
				layout.marginWidth = 0;
				parent.setLayout(layout);
				GridData ld = new GridData(SWT.FILL, SWT.DEFAULT, true, false);
				ld.horizontalSpan = nColumns - 1;
				parent.setLayoutData(ld);
				template.setEnvironment(env);
				template.createControl(parent, 3);
				template.setNotifier(validationNotifier);
				templateFields.add(field);
				templateFieldToTemplate.put(field, template);
				field.setDialogFieldListener(templateEnablementUpdater);
			}
			lfield.setSelection(true);
		}

		if (fTemplateDialogField != null) {
			createTemplateControls(composite, nColumns);
		}
	}

	protected String getFileText() {
		return fileDialogField.getText();
	}

	protected String getFileName() {
		final String fileText = getFileText();

		String[] extensions = getFileExtensions();
		for (int i = 0; i < extensions.length; ++i) {
			String extension = extensions[i];
			if (extension.length() > 0 && fileText.endsWith("." + extension)) { //$NON-NLS-1$
				return fileText;
			}
		}

		return fileText + "." + extensions[0]; //$NON-NLS-1$
	}

	protected String[] getFileExtensions() {
		String requiredNature = getRequiredNature();

		IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(requiredNature);
		String[] extensions = ScriptModelUtil.getFileExtensions(toolkit);
		if (extensions != null) {
			return extensions;
		}

		return new String[] { Util.EMPTY_STRING };
	}

	protected IScriptFolder chooseScriptFolder() {
		ILabelProvider labelProvider = new ModelElementLabelProvider(
				ModelElementLabelProvider.SHOW_DEFAULT);

		ElementListSelectionDialog dialog = new ElementListSelectionDialog(
				getShell(), labelProvider);

		dialog.setIgnoreCase(false);
		dialog.setTitle(Messages.NewSourceModulePage_selectScriptFolder);
		dialog.setMessage(Messages.NewSourceModulePage_selectScriptFolder);
		dialog
				.setEmptyListMessage(Messages.NewSourceModulePage_noFoldersAvailable);

		IProjectFragment projectFragment = getProjectFragment();
		if (projectFragment != null) {
			try {
				dialog.setElements(projectFragment.getChildren());
			} catch (ModelException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
		}

		dialog.setHelpAvailable(false);

		if (currentScriptFolder != null) {
			dialog.setInitialSelections(new Object[] { currentScriptFolder });
		}

		if (dialog.open() == Window.OK) {
			Object element = dialog.getFirstResult();
			if (element instanceof IScriptFolder) {
				return (IScriptFolder) element;
			}
		}

		return null;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			setFocus();
		}
	}

	protected void setFocus() {
		fileDialogField.setFocus();
	}

	protected abstract String getPageTitle();

	protected abstract String getPageDescription();

	protected ICodeTemplateArea getTemplateArea() {
		return null;
	}

	protected String[] getCodeTemplateContextTypeIds() {
		return null;
	}

	protected String getDefaultCodeTemplateId() {
		return null;
	}

	protected String getFileContent(ISourceModule module) throws CoreException {
		final ICodeTemplateArea templateArea = getTemplateArea();
		if (templateArea != null) {
			final Template template = getSelectedTemplate();
			saveLastUsedTemplateName(template != null ? template.getName()
					: NO_TEMPLATE);
			if (template != null) {
				final TemplateContextType contextType = templateArea
						.getTemplateAccess().getContextTypeRegistry()
						.getContextType(template.getContextTypeId());
				// TODO introduce a way to create context by contextType
				final SourceModuleTemplateContext context = new SourceModuleTemplateContext(
						contextType, CodeGeneration
								.getLineDelimiterUsed(module));
				// String fileComment = getFileComment(file, lineDelimiter);
				// context.setVariable(CodeTemplateContextType.FILE_COMMENT,
				//					fileComment != null ? fileComment : ""); //$NON-NLS-1$
				// ICProject cproject = CoreModel.getDefault().create(
				// file.getProject());
				// String includeGuardSymbol = generateIncludeGuardSymbol(file
				// .getName(), cproject);
				// context.setVariable(CodeTemplateContextType.INCLUDE_GUARD_SYMBOL,
				//					includeGuardSymbol != null ? includeGuardSymbol : ""); //$NON-NLS-1$
				context.setSourceModuleVariables(module);
				final String[] fullLine = {};
				final String result = CodeGeneration.evaluateTemplate(context,
						template, fullLine);
				return result != null ? result : Util.EMPTY_STRING;
			}
		}
		return getFileContent();
	}

	protected String getFileContent() {
		return Util.EMPTY_STRING;
	}

	protected IEnvironment getEnvironment() {
		final IProjectFragment fragment = getProjectFragment();
		if (fragment != null) {
			return EnvironmentManager.getEnvironment(fragment
					.getScriptProject().getProject());
		}
		return null;
	}

}
