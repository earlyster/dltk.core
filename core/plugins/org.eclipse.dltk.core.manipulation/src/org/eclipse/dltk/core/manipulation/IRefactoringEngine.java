package org.eclipse.dltk.core.manipulation;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.corext.refactoring.rename.ScriptRenameProcessor;

public interface IRefactoringEngine {
	boolean isRenameAvailable(IModelElement element) throws ModelException;
	ScriptRenameProcessor createRenameProcessor(IModelElement element);
}
