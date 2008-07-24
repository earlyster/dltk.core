package org.eclipse.dltk.ui.tests.core;

import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.tests.model.TestLanguageToolkit;
import org.eclipse.dltk.ui.AbstractDLTKUILanguageToolkit;
import org.eclipse.dltk.ui.tests.DLTKUITestsPlugin;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class DLTKUITestLanguageToolkit extends AbstractDLTKUILanguageToolkit {

	protected AbstractUIPlugin getUIPLugin() {
		return DLTKUITestsPlugin.getDefault();
	}

	public IDLTKLanguageToolkit getCoreToolkit() {
		return TestLanguageToolkit.getDefault();
	}

}
