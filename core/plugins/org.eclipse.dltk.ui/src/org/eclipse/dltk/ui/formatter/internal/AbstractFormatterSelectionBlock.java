/******************************************************************************* 
 * Copyright (c) 2008 xored software, Inc.  
 * 
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0 
 * which accompanies this distribution, and is available at 
 * http://www.eclipse.org/legal/epl-v10.html  
 * 
 * Contributors: 
 *     xored software, Inc. - initial API and Implementation (Yuri Strot) 
 *******************************************************************************/
package org.eclipse.dltk.ui.formatter.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKContributionExtensionManager;
import org.eclipse.dltk.core.IDLTKContributedExtension;
import org.eclipse.dltk.core.IPreferencesSaveDelegate;
import org.eclipse.dltk.internal.ui.formatter.profiles.CustomProfile;
import org.eclipse.dltk.internal.ui.formatter.profiles.Profile;
import org.eclipse.dltk.internal.ui.formatter.profiles.ProfileManager;
import org.eclipse.dltk.internal.ui.formatter.profiles.ProfileStore;
import org.eclipse.dltk.internal.ui.util.SWTUtil;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.dialogs.PropertyLinkArea;
import org.eclipse.dltk.ui.formatter.AlreadyExistsDialog;
import org.eclipse.dltk.ui.formatter.CreateProfileDialog;
import org.eclipse.dltk.ui.formatter.FormatterMessages;
import org.eclipse.dltk.ui.formatter.IFormatterModifyDialog;
import org.eclipse.dltk.ui.formatter.IFormatterModifyDialogOwner;
import org.eclipse.dltk.ui.formatter.IProfile;
import org.eclipse.dltk.ui.formatter.IProfileVersioner;
import org.eclipse.dltk.ui.formatter.IScriptFormatterFactory;
import org.eclipse.dltk.ui.formatter.ScriptFormatterManager;
import org.eclipse.dltk.ui.preferences.AbstractOptionsBlock;
import org.eclipse.dltk.ui.preferences.PreferenceKey;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.dltk.ui.util.PixelConverter;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.window.Window;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public abstract class AbstractFormatterSelectionBlock extends
		AbstractOptionsBlock {

	protected abstract IFormatterModifyDialogOwner createDialogOwner();

	/**
	 * Returns the extension manager for the contributed extension.
	 */
	protected abstract DLTKContributionExtensionManager getExtensionManager();

	/**
	 * Returns the message that will be used to create the link to the
	 * preference or property page.
	 */
	protected abstract String getPreferenceLinkMessage();

	/**
	 * Returns the preference key that will be used to store the contribution
	 * preference.
	 */
	protected abstract PreferenceKey getSavedContributionKey();

	protected abstract void updatePreview();

	protected abstract SourceViewer createPreview(Composite parent);

	public AbstractFormatterSelectionBlock(IStatusChangeListener context,
			IProject project, PreferenceKey formatterKey, String natureId,
			IWorkbenchPreferenceContainer container) {
		super(context, project, collectPreferenceKeys(TEMP_LIST, natureId,
				formatterKey), container);
		factories = (IScriptFormatterFactory[]) TEMP_LIST
				.toArray(new IScriptFormatterFactory[TEMP_LIST.size()]);
		TEMP_LIST = new ArrayList();
	}

	protected ProfileManager getProfileManager() {
		return getProfileManager(getSelectedExtension());
	}

	protected ProfileManager getProfileManager(IScriptFormatterFactory factory) {
		ProfileManager manager = (ProfileManager) profileByFactory.get(factory);
		if (manager == null) {
			String profilesSource = getValue(factory.getProfilesKey());
			String profileId = getValue(factory.getActiveProfileKey());

			ProfileStore store = getProfileStore(factory);

			List allProfiles = new ArrayList();
			List buitinProfiles = factory.getBuiltInProfiles();
			if (buitinProfiles != null && buitinProfiles.size() > 0) {
				allProfiles.addAll(buitinProfiles);
			} else {
				DLTKUIPlugin
						.logErrorMessage(NLS
								.bind(
										FormatterMessages.AbstractFormatterSelectionBlock_noBuiltInProfiles,
										factory.getId()));
			}

			try {
				if (profilesSource != null && profilesSource.length() > 0) {
					List profiles = store
							.readProfilesFromString(profilesSource);
					allProfiles.addAll(profiles);
				}
			} catch (Exception e) {
				DLTKUIPlugin.log(e);
			}

			Profile profile = (Profile) allProfiles.get(0);
			manager = new ProfileManager(allProfiles, profile, profileId);
			profileByFactory.put(factory, manager);
		}
		return manager;
	}

	protected ProfileStore getProfileStore() {
		return getProfileStore(getSelectedExtension());
	}

	protected ProfileStore getProfileStore(IScriptFormatterFactory factory) {
		ProfileStore store = (ProfileStore) storeByFactory.get(factory);
		if (store == null) {
			IProfileVersioner versioner = factory.getProfileVersioner();
			store = new ProfileStore(versioner);
			storeByFactory.put(factory, store);
		}
		return store;
	}

	protected void applyPreferences(boolean profileChanged) {
		IScriptFormatterFactory factory = getSelectedExtension();
		ProfileManager manager = getProfileManager(factory);
		Profile profile = (Profile) manager.getSelected();
		Map settings = new HashMap(profile.getSettings());
		String activeKey = factory.getActiveProfileKey().getName();
		String profilesKey = factory.getProfilesKey().getName();
		manager.getSelected().getID();
		settings.put(activeKey, profile.getID());

		if (profileChanged) {
			try {
				ProfileStore store = getProfileStore(factory);
				String profiles = store.writeProfiles(manager
						.getSortedProfiles());
				settings.put(profilesKey, profiles);
			} catch (CoreException e) {
				DLTKUIPlugin.log(e);
			}
		} else {
			settings.remove(profilesKey);
		}

		IPreferencesSaveDelegate delegate = new SaveDelegate();
		factory.savePreferences(settings, delegate);
		updatePreview();
	}

	protected static PreferenceKey[] collectPreferenceKeys(List factories,
			String natureId, PreferenceKey formatterKey) {
		List result = new ArrayList();
		result.add(formatterKey);
		IDLTKContributedExtension[] extensions = ScriptFormatterManager
				.getInstance().getContributions(natureId);
		for (int i = 0; i < extensions.length; ++i) {
			IScriptFormatterFactory factory = (IScriptFormatterFactory) extensions[i];
			factories.add(factory);
			final PreferenceKey[] keys = factory.getPreferenceKeys();
			if (keys != null) {
				for (int j = 0; j < keys.length; ++j) {
					final PreferenceKey prefKey = keys[j];
					result.add(prefKey);
				}
			}
		}
		return (PreferenceKey[]) result
				.toArray(new PreferenceKey[result.size()]);
	}

	// ~ Methods

	public final Control createOptionsBlock(Composite parent) {
		return createSelectorBlock(parent);
	}

	protected Composite createDescription(Composite parent,
			IDLTKContributedExtension contrib) {
		Composite composite = SWTFactory.createComposite(parent, parent
				.getFont(), 1, 1, GridData.FILL);

		String desc = contrib.getDescription();
		if (desc == null) {
			desc = Util.EMPTY_STRING;
		}
		SWTFactory.createLabel(composite, desc, 1);

		String prefPageId = contrib.getPreferencePageId();
		String propPageId = contrib.getPropertyPageId();

		// we're a property page
		if (isProjectPreferencePage() && hasValidId(propPageId)) {
			new PropertyLinkArea(composite, SWT.NONE, propPageId, fProject,
					getPreferenceLinkMessage(), getPreferenceContainer());
		}

		// we're a preference page
		if (!isProjectPreferencePage() && hasValidId(prefPageId)) {
			new PreferenceLinkArea(composite, SWT.NONE, prefPageId,
					getPreferenceLinkMessage(), getPreferenceContainer(), null);
		}

		return composite;
	}

	protected Composite createSelectorBlock(Composite parent) {
		final int numColumns = 5;

		PixelConverter fPixConv = new PixelConverter(parent);
		fComposite = createComposite(parent, numColumns);

		Label profileLabel = new Label(fComposite, SWT.NONE);
		profileLabel
				.setText(FormatterMessages.AbstractFormatterSelectionBlock_activeProfile);
		GridData data = new GridData(SWT.FILL, SWT.FILL, true, false);
		data.horizontalSpan = numColumns;
		profileLabel.setLayoutData(data);

		fProfileCombo = createProfileCombo(fComposite, 3, fPixConv
				.convertWidthInCharsToPixels(20));
		updateComboFromProfiles();
		fProfileCombo.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				updateSelection();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				updateSelection();
			}
		});

		fEditButton = createButton(fComposite,
				FormatterMessages.AbstractFormatterSelectionBlock_editProfile,
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		fEditButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				editButtonPressed();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				editButtonPressed();
			}
		});
		fDeleteButton = createButton(
				fComposite,
				FormatterMessages.AbstractFormatterSelectionBlock_removeProfile,
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		fDeleteButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				doDelete();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				doDelete();
			}

			protected void doDelete() {
				if (MessageDialog
						.openQuestion(
								fComposite.getShell(),
								FormatterMessages.AbstractFormatterSelectionBlock_confirmRemoveLabel,
								NLS
										.bind(
												FormatterMessages.AbstractFormatterSelectionBlock_confirmRemoveMessage,
												getProfileManager()
														.getSelected()
														.getName()))) {
					getProfileManager().deleteSelected();
					updateComboFromProfiles();
					applyPreferences(true);
				}
			}
		});

		fNewButton = createButton(fComposite,
				FormatterMessages.AbstractFormatterSelectionBlock_newProfile,
				GridData.HORIZONTAL_ALIGN_BEGINNING);
		fNewButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				createNewProfile();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				createNewProfile();
			}

			protected void createNewProfile() {
				final CreateProfileDialog p = new CreateProfileDialog(
						fComposite.getShell(), getProfileManager(),
						getProfileStore().getVersioner());
				if (p.open() != Window.OK)
					return;
				updateComboFromProfiles();
				if (!p.openEditDialog())
					return;
				editButtonPressed();
			}
		});

		fLoadButton = createButton(
				fComposite,
				FormatterMessages.AbstractFormatterSelectionBlock_importProfile,
				GridData.HORIZONTAL_ALIGN_END);
		fLoadButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				doImport();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				doImport();
			}

			protected void doImport() {
				final FileDialog dialog = new FileDialog(fComposite.getShell(),
						SWT.OPEN);
				dialog
						.setText(FormatterMessages.AbstractFormatterSelectionBlock_importProfileLabel);
				dialog.setFilterExtensions(new String[] { "*.xml" }); //$NON-NLS-1$
				final String path = dialog.open();
				if (path == null)
					return;

				final File file = new File(path);
				Collection profiles = null;
				ProfileStore store = getProfileStore();
				try {
					profiles = store.readProfilesFromFile(file);
				} catch (CoreException e) {
					DLTKUIPlugin
							.logErrorMessage(
									FormatterMessages.AbstractFormatterSelectionBlock_notValidProfile,
									e);
				}
				if (profiles == null || profiles.isEmpty())
					return;

				final CustomProfile profile = (CustomProfile) profiles
						.iterator().next();

				IProfileVersioner versioner = store.getVersioner();

				if (!versioner.getFormatterId()
						.equals(profile.getFormatterId())) {
					final String title = FormatterMessages.AbstractFormatterSelectionBlock_importProfileLabel;
					final String message = NLS
							.bind(
									FormatterMessages.AbstractFormatterSelectionBlock_notValidFormatter,
									versioner.getFormatterId(), profile
											.getFormatterId());
					MessageDialog.openError(fComposite.getShell(), title,
							message);
					return;
				}

				if (profile.getVersion() > versioner.getCurrentVersion()) {
					final String title = FormatterMessages.AbstractFormatterSelectionBlock_importingProfile;
					final String message = FormatterMessages.AbstractFormatterSelectionBlock_moreRecentVersion;
					MessageDialog.openWarning(fComposite.getShell(), title,
							message);
				}

				if (getProfileManager().containsName(profile.getName())) {
					final AlreadyExistsDialog aeDialog = new AlreadyExistsDialog(
							fComposite.getShell(), profile, getProfileManager());
					if (aeDialog.open() != Window.OK)
						return;
				}
				profile.setVersion(1);
				getProfileManager().addProfile(profile);
				updateComboFromProfiles();
				applyPreferences(true);
			}
		});
		createLabel(fComposite, "", 3); //$NON-NLS-1$

		configurePreview(fComposite, numColumns);
		updateButtons();
		applyPreferences(false);

		return fComposite;
	}

	protected void configurePreview(Composite composite, int numColumns) {
		createLabel(composite,
				FormatterMessages.AbstractFormatterSelectionBlock_preview,
				numColumns);
		fPreviewViewer = createPreview(composite);

		final GridData gd = new GridData(GridData.FILL_VERTICAL
				| GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = numColumns;
		gd.verticalSpan = 7;
		gd.widthHint = 0;
		gd.heightHint = 0;
		fPreviewViewer.getControl().setLayoutData(gd);
	}

	protected IScriptFormatterFactory getSelectedExtension() {
		return factories[0];
	}

	protected final void updateSelection() {
		// IScriptFormatterFactory factory = getSelectedExtension();
		//
		// String id = factory.getId();
		// setValue(getSavedContributionKey(), id);
		Profile selected = (Profile) getProfileManager().getSortedProfiles()
				.get(fProfileCombo.getSelectionIndex());
		getProfileManager().setSelected(selected);
		updateButtons();
		applyPreferences(false);
		updatePreview();
	}

	protected void editButtonPressed() {
		IScriptFormatterFactory factory = getSelectedExtension();
		if (factory != null) {
			ProfileManager manager = getProfileManager();
			final IFormatterModifyDialog dialog = factory
					.createDialog(createDialogOwner());
			if (dialog != null) {
				IProfile profile = manager.getSelected();
				Map settings = profile.getSettings();
				dialog.setPreferences(settings);
				if (dialog.open() == Window.OK) {
					profile = (Profile) manager.getSelected();
					updateComboFromProfiles();
					final Map newSettings = dialog.getPreferences();
					if (!profile.getSettings().equals(newSettings)) {
						profile.setSettings(newSettings);
						applyPreferences(true);
					}
				}
			}
		}
	}

	protected void updateComboFromProfiles() {
		fProfileCombo.removeAll();

		List profiles = getProfileManager().getSortedProfiles();
		IProfile selected = getProfileManager().getSelected();
		Iterator it = profiles.iterator();
		int selection = 0, index = 0;
		while (it.hasNext()) {
			Profile profile = (Profile) it.next();
			fProfileCombo.add(profile.getName());
			if (profile.equals(selected))
				selection = index;
			index++;
		}
		fProfileCombo.select(selection);
		updateButtons();
	}

	protected void updateButtons() {
		if (fDeleteButton != null && !fDeleteButton.isDisposed()) {
			IProfile selected = getProfileManager().getSelected();
			fDeleteButton.setEnabled(!selected.isBuiltInProfile());
		}
	}

	private class SaveDelegate implements IPreferencesSaveDelegate {

		private PreferenceKey getPrefKey(String qualifier, String key) {
			return new PreferenceKey(qualifier, key);
		}

		public void setBoolean(String qualifier, String key, boolean value) {
			PreferenceKey pKey = getPrefKey(qualifier, key);
			if (pKey != null) {
				setValue(pKey, value);
			}
		}

		public void setInt(String qualifier, String key, int value) {
			PreferenceKey pKey = getPrefKey(qualifier, key);
			if (pKey != null) {
				setValue(pKey, String.valueOf(value));
			}
		}

		public void setString(String qualifier, String key, String value) {
			PreferenceKey pKey = getPrefKey(qualifier, key);
			if (pKey != null) {
				setValue(pKey, value);
			}
		}

	}

	private Composite createComposite(Composite parent, int numColumns) {
		final Composite composite = new Composite(parent, SWT.NONE);
		composite.setFont(parent.getFont());

		final GridLayout layout = new GridLayout(numColumns, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		composite.setLayout(layout);
		return composite;
	}

	private static Combo createProfileCombo(Composite composite, int span,
			int widthHint) {
		final GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = span;
		gd.widthHint = widthHint;

		final Combo combo = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setFont(composite.getFont());
		combo.setLayoutData(gd);

		return combo;
	}

	private static Button createButton(Composite composite, String text,
			final int style) {
		final Button button = new Button(composite, SWT.PUSH);
		button.setFont(composite.getFont());
		button.setText(text);

		final GridData gd = new GridData(style);
		gd.widthHint = SWTUtil.getButtonWidthHint(button);
		button.setLayoutData(gd);
		return button;
	}

	protected static Label createLabel(Composite composite, String text,
			int numColumns) {
		final GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.horizontalSpan = numColumns;
		gd.widthHint = 0;

		final Label label = new Label(composite, SWT.WRAP);
		label.setFont(composite.getFont());
		label.setText(text);
		label.setLayoutData(gd);
		return label;
	}

	protected void initialize() {
		super.initialize();
	}

	public void performDefaults() {
		super.performDefaults();
	}

	private boolean hasValidId(String id) {
		return (id != null && !"".equals(id)); //$NON-NLS-1$
	}

	private Composite fComposite;
	private Combo fProfileCombo;
	private Button fEditButton;
	private Button fDeleteButton;
	private Button fNewButton;
	private Button fLoadButton;

	private IScriptFormatterFactory[] factories;
	private Map storeByFactory = new HashMap();
	private Map profileByFactory = new HashMap();
	protected SourceViewer fPreviewViewer;

	private static List TEMP_LIST = new ArrayList();

}
