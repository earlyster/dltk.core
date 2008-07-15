package org.eclipse.dltk.validators.internal.externalchecker.ui;

import org.eclipse.dltk.validators.core.IValidator;

public interface ConfigureWildcardsDialogRequestor {
	public boolean isDuplicateName(String name);
	public void validatorAdded(IValidator validator);

}
