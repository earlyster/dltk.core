package org.eclipse.dltk.ui.templates;

import java.io.IOException;

import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.text.templates.ITemplateAccess;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;

public abstract class ScriptTemplateAccess implements ITemplateAccess {

	private ContextTypeRegistry fRegistry;
	private TemplateStore fStore;

	public TemplateStore getTemplateStore() {
		if (fStore == null) {
			fStore = new ContributionTemplateStore(getContextTypeRegistry(),
					getPreferenceStore(), getCustomTemplatesKey());
			loadTemplates();
		}
		return fStore;
	}

	public ContextTypeRegistry getContextTypeRegistry() {
		if (fRegistry == null) {
			fRegistry = createContextTypeRegistry();
		}
		return fRegistry;
	}

	protected ContextTypeRegistry createContextTypeRegistry() {
		final ContributionContextTypeRegistry registry = new ContributionContextTypeRegistry();
		registry.addContextType(getContextTypeId());
		return registry;
	}

	protected abstract String getContextTypeId();

	protected abstract String getCustomTemplatesKey();

	protected abstract IPreferenceStore getPreferenceStore();

	public IPreferenceStore getTemplatePreferenceStore() {
		return getPreferenceStore();
	}

	private void loadTemplates() {
		try {
			fStore.load();
		} catch (IOException e) {
			final String msg = NLS
					.bind(TemplateMessages.ScriptTemplateAccess_unableToLoadTemplateStore,
							e);
			DLTKUIPlugin.logErrorMessage(msg, e);
		}
	}
}
