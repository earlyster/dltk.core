package org.eclipse.dltk.ui.tests.core;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.tests.model.TestLanguageToolkit;
import org.eclipse.dltk.ui.AbstractDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.tests.DLTKUITestsPlugin;
import org.eclipse.jface.preference.IPreferenceStore;

public class DLTKUITestLanguageToolkit extends AbstractDLTKUILanguageToolkit {

	public IPreferenceStore getPreferenceStore() {
		return DLTKUITestsPlugin.getDefault().getPreferenceStore();
	}

	public IDLTKLanguageToolkit getCoreToolkit() {
		return TestLanguageToolkit.getDefault();
	}

}
