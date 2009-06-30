package org.eclipse.dltk.internal.ui;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.ui.IFileEditorInput;

public class FileEditorInputAdapterFactory implements IAdapterFactory {

	private static Class[] PROPERTIES = new Class[] { IModelElement.class };

	public Object getAdapter(Object element, Class key) {
		if (IModelElement.class.equals(key)) {

			// Performance optimization, see https://bugs.eclipse.org/bugs/
			// show_bug.cgi?id=133141
			if (element instanceof IFileEditorInput) {
				IModelElement je = DLTKUIPlugin.getDefault()
						.getWorkingCopyManager().getWorkingCopy(
								(IFileEditorInput) element);
				if (je != null && je.exists()) {
					return je;
				}
			}

			return DLTKCore.create(((IFileEditorInput) element).getFile());
		}
		return null;
	}

	public Class[] getAdapterList() {
		return PROPERTIES;
	}

}
