package org.eclipse.dltk.ui.text.folding;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceModule;

public interface IElementCommentResolver {

	IModelElement getElementByCommentPosition(ISourceModule module,
			int commentOffset, int commentLength);

}
