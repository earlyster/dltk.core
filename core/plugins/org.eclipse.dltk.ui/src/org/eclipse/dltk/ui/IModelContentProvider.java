package org.eclipse.dltk.ui;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;

public interface IModelContentProvider {
	/**
	 * Called for each model element buildStructure.
	 * 
	 * Can remove some elements from children's set.
	 * 
	 * Any new elements need to implement @see:IModelElementMemento to handle
	 * inner element references.
	 * 
	 * @param iTreeContentProvider
	 */
	void provideModelChanges(Object parentElement, List children,
			ITreeContentProvider iTreeContentProvider);

	Object getParentElement(Object element,
			ITreeContentProvider iTreeContentProvider);
}
