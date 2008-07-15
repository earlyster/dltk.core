package org.eclipse.dltk.validators.internal.externalchecker.ui;

import org.eclipse.dltk.validators.internal.externalchecker.core.CustomWildcard;

public interface IWildcardListViewer {
	public void addWildcard(CustomWildcard r);
	public void removeWildcard(CustomWildcard r);
	public void updateWildcard(CustomWildcard r);


}
