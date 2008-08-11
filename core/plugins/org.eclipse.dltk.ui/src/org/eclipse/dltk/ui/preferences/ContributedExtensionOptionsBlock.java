package org.eclipse.dltk.ui.preferences;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.compiler.util.Util;
import org.eclipse.dltk.core.DLTKContributionExtensionManager;
import org.eclipse.dltk.core.IDLTKContributedExtension;
import org.eclipse.dltk.ui.dialogs.PropertyLinkArea;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.dialogs.PreferenceLinkArea;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

/**
 */
public abstract class ContributedExtensionOptionsBlock extends
		AbstractOptionsBlock {
	private Map contribToDescMap = new HashMap();

	private ComboViewerBlock viewer;

	private Composite descriptionPlace;

	public ContributedExtensionOptionsBlock(IStatusChangeListener context,
			IProject project, PreferenceKey[] allKeys,
			IWorkbenchPreferenceContainer container) {
		super(context, project, allKeys, container);
	}

	// ~ Methods

	public final Control createOptionsBlock(Composite parent) {
		Composite composite = SWTFactory.createComposite(parent, parent
				.getFont(), 1, 1, GridData.FILL);

		createSelectorBlock(composite);

		return composite;
	}

	/**
	 * Returns the extension manager for the contributed extension.
	 */
	protected abstract DLTKContributionExtensionManager getExtensionManager();

	/**
	 * Returns the language's nature id.
	 */
	protected abstract String getNatureId();

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

	protected void createSelectorBlock(Composite composite) {
		final int groupColumns = getSelectorGroupColumns();
		// Group
		Composite group = createSelectorGroup(composite, groupColumns);

		// Name
		SWTFactory.createLabel(group, getSelectorNameLabel(), 1);

		viewer = createComboViewerBlock(group);

		// Description
		descriptionPlace = SWTFactory.createComposite(group, group.getFont(),
				1, groupColumns, GridData.FILL);
		descriptionPlace.setLayout(new StackLayout());
	}

	protected ComboViewerBlock createComboViewerBlock(Composite group) {
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		return new ComboViewerBlock(group, gd) {
			protected String getObjectName(Object element) {
				final IDLTKContributedExtension item = (IDLTKContributedExtension) element;
				if (item.getName() != null && item.getName().length() != 0) {
					return item.getName();
				}
				return item.getClass().getName();
			}

			protected void selectedObjectChanged(Object element) {
				updateSelection((IDLTKContributedExtension) element);
			}

			protected String getObjectId(Object element) {
				return ((IDLTKContributedExtension) element).getId();
			}

			protected Object getDefaultObject() {
				/*
				 * no preference value has been set so we want a contribution
				 * that is returned based upon the 'select by priority' logic
				 * 
				 * this is done to handle the case where the plugin implementor
				 * did not configure a default value via a preference
				 * initializer
				 */
				return getExtensionManager().getPriorityContribution(
						getProject(), getNatureId());
			}

			protected String getSavedObjectId() {
				return getValue(getSavedContributionKey());
			}

			protected Object getObjectById(String id) {
				return getExtensionManager().getContributionById(id);
			}
		};
	}

	protected int getSelectorGroupColumns() {
		return 1;
	}

	protected Composite createSelectorGroup(Composite composite,
			int groupColumns) {
		return SWTFactory.createGroup(composite, getSelectorGroupLabel(),
				groupColumns, 1, GridData.FILL_HORIZONTAL);
	}

	protected String[] getFullBuildDialogStrings(boolean workspaceSettings) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Returns the label that will be used for the selector group.
	 */
	protected abstract String getSelectorGroupLabel();

	/**
	 * Returns the label that will be used for the selector name.
	 */
	protected abstract String getSelectorNameLabel();

	protected void initialize() {
		super.initialize();

		IDLTKContributedExtension[] contributions = getExtensionManager()
				.getContributions(getNatureId());

		for (int i = 0; i < contributions.length; i++) {
			IDLTKContributedExtension contrib = contributions[i];
			contribToDescMap.put(contrib.getId(), createDescription(
					descriptionPlace, contrib));
		}

		viewer.initialize(contributions);
	}

	public void performDefaults() {
		super.performDefaults();
		viewer.performDefaults();
	}

	protected final void updateSelection(IDLTKContributedExtension contrib) {
		String id = contrib.getId();
		setValue(getSavedContributionKey(), id);

		Composite composite = (Composite) contribToDescMap.get(id);

		((StackLayout) descriptionPlace.getLayout()).topControl = composite;
		descriptionPlace.layout();
		selectionChanged(contrib);
	}

	protected void selectionChanged(IDLTKContributedExtension extension) {
		// empty, override in descendants
	}

	protected IDLTKContributedExtension getSelectedExtension() {
		return (IDLTKContributedExtension) viewer.getSelectedObject();
	}

	private boolean hasValidId(String id) {
		return (id != null && !"".equals(id)); //$NON-NLS-1$
	}
}
