package org.eclipse.dltk.validators.internal.externalchecker.ui;

import org.eclipse.dltk.validators.internal.externalchecker.core.Rule;

public interface IRulesListViewer {

	public void addRule(Rule r);
	public void removeRule(Rule r);
	public void updateRule(Rule r);

}
