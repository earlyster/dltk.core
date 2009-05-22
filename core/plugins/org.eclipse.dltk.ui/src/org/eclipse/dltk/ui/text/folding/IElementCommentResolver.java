package org.eclipse.dltk.ui.text.folding;

import org.eclipse.dltk.core.IModelElement;

public interface IElementCommentResolver {

	IModelElement getElementByCommentPosition(int commentOffset,
			int commentLength);

}
