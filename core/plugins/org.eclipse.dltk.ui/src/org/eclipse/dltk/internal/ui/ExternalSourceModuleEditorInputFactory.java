/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.ui;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IExternalSourceModule;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.internal.ui.editor.ExternalStorageEditorInput;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IElementFactory;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPersistableElement;

/**
 * The ExternalSourceModuleEditorInputFactory is used to save and recreate an
 * {@link IEditorInput} for {@link IExternalSourceModule} object.
 * 
 * @see IMemento
 * @see IPersistableElement
 * @see IElementFactory
 */
public class ExternalSourceModuleEditorInputFactory implements IElementFactory {

	private static final String KEY = "elementID"; //$NON-NLS-1$
	private static final String FACTORY_ID = "org.eclipse.dltk.ui.ExternalSourceModuleEditorInputFactory"; //$NON-NLS-1$

	/**
	 * @see IElementFactory
	 */
	public IAdaptable createElement(IMemento memento) {
		String identifier = memento.getString(KEY);
		if (identifier != null) {
			final IModelElement element = DLTKCore.create(identifier);
			if (element instanceof IExternalSourceModule) {
				return new ExternalStorageEditorInput((IStorage) element);
			}
		}
		return null;
	}

	public static IPersistableElement createPersistableElement(
			final IExternalSourceModule module) {
		return new IPersistableElement() {

			public String getFactoryId() {
				return FACTORY_ID;
			}

			public void saveState(IMemento memento) {
				memento.putString(KEY, module.getHandleIdentifier());
			}
		};
	}
}
