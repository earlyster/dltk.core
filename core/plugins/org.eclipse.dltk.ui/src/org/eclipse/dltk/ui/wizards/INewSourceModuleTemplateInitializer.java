package org.eclipse.dltk.ui.wizards;

import org.eclipse.dltk.core.IScriptFolder;

@Deprecated
public interface INewSourceModuleTemplateInitializer {
	void initialize(IScriptFolder folder);

	boolean isActive();
}
