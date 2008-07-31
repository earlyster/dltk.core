/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.formatter;

import org.eclipse.core.resources.IProject;
import org.eclipse.dltk.core.DLTKContributionExtensionManager;
import org.eclipse.dltk.ui.preferences.AbstractConfigurationBlockPropertyAndPreferencePage;
import org.eclipse.dltk.ui.preferences.AbstractOptionsBlock;
import org.eclipse.dltk.ui.preferences.ContributedExtensionOptionsBlock;
import org.eclipse.dltk.ui.preferences.PreferenceKey;
import org.eclipse.dltk.ui.util.IStatusChangeListener;
import org.eclipse.ui.preferences.IWorkbenchPreferenceContainer;

public abstract class AbstractFormatterPreferencePage extends
		AbstractConfigurationBlockPropertyAndPreferencePage {

	protected class FormatterSelectionBlock extends
			ContributedExtensionOptionsBlock {

		public FormatterSelectionBlock(IStatusChangeListener context,
				IProject project, IWorkbenchPreferenceContainer container) {
			super(context, project,
					new PreferenceKey[] { getFormatterPreferenceKey() },
					container);
		}

		protected DLTKContributionExtensionManager getExtensionManager() {
			return ScriptFormatterManager.getInstance();
		}

		protected String getSelectorGroupLabel() {
			return FormatterMessages.FormatterPreferencePage_groupName;
		}

		protected String getSelectorNameLabel() {
			return FormatterMessages.FormatterPreferencePage_selectionLabel;
		}

		protected String getPreferenceLinkMessage() {
			return FormatterMessages.FormatterPreferencePage_settingsLink;
		}

		protected PreferenceKey getSavedContributionKey() {
			return AbstractFormatterPreferencePage.this
					.getFormatterPreferenceKey();
		}

		protected String getNatureId() {
			return AbstractFormatterPreferencePage.this.getNatureId();
		}

	}

	protected AbstractOptionsBlock createOptionsBlock(
			IStatusChangeListener newStatusChangedListener, IProject project,
			IWorkbenchPreferenceContainer container) {

		return new FormatterSelectionBlock(newStatusChangedListener, project,
				container);
	}

	protected abstract String getNatureId();

	protected abstract PreferenceKey getFormatterPreferenceKey();

	protected String getHelpId() {
		return null;
	}

	protected String getPreferencePageId() {
		return null;
	}

	protected String getProjectHelpId() {
		return null;
	}

	protected String getPropertyPageId() {
		return null;
	}

	protected void setPreferenceStore() {
		// empty
	}

}
