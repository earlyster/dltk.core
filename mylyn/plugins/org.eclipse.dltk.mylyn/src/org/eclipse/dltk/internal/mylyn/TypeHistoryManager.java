/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.dltk.internal.mylyn;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.search.DLTKSearchTypeNameMatch;
import org.eclipse.dltk.internal.corext.util.OpenTypeHistory;
import org.eclipse.dltk.ui.DLTKUILanguageManager;
import org.eclipse.dltk.ui.IDLTKUILanguageToolkit;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.IInteractionElement;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class TypeHistoryManager extends AbstractContextListener {

	@Override
	public void contextChanged(ContextChangeEvent event) {
		switch (event.getEventKind()) {
		case ACTIVATED:
			for (IInteractionElement node : event.getContext().getInteresting()) {
				updateTypeHistory(node, true);
			}
			break;
		case DEACTIVATED:
			clearTypeHistory();
			break;
		case CLEARED:
			if (event.isActiveContext()) {
				clearTypeHistory();
			}
			break;
		case INTEREST_CHANGED:
			for (IInteractionElement node : event.getElements()) {
				updateTypeHistory(node, true);
			}
			break;
		case ELEMENTS_DELETED:
			for (IInteractionElement element : event.getElements()) {
				updateTypeHistory(element, false);
			}
			break;
		}
	}

	/**
	 * Path has to be compatible with ITypeNameRequestor
	 */
	private void updateTypeHistory(IInteractionElement node, boolean add) {
		IModelElement element = DLTKCore.create(node.getHandleIdentifier());
		if (element instanceof IType) {
			IType type = (IType) element;
			try {
				if (type.exists() && /*!type.isAnonymous() &&*/!isAspectjType(type)) {
					DLTKSearchTypeNameMatch typeNameMatch = new DLTKSearchTypeNameMatch(type, type.getFlags());

					IDLTKUILanguageToolkit toolkit = DLTKUILanguageManager.getLanguageToolkit(element);
					final OpenTypeHistory history = OpenTypeHistory.getInstance(toolkit);
					if (add && !history.contains(typeNameMatch)) {
						history.accessed(typeNameMatch);
					} else {
						history.remove(typeNameMatch);
					}
				}
			} catch (ModelException e) {
				StatusHandler.log(new Status(IStatus.ERROR, DLTKUiBridgePlugin.ID_PLUGIN,
						"Failed to update history for a type", e)); //$NON-NLS-1$
			}
		}
	}

	/**
	 * HACK: to avoid adding AspectJ types, for example: class: =TJP Example/src<tjp{Demo.java[Demo aspect: =TJP
	 * Example/src<tjp*GetInfo.aj}GetInfo
	 */
	private boolean isAspectjType(IType type) {
		if (type.getHandleIdentifier().indexOf('}') != -1) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Public for testing
	 */
	public void clearTypeHistory() {
//		TypeNameMatch[] typeInfos = OpenTypeHistory.getInstance().getTypeInfos();
//		for (TypeNameMatch typeInfo : typeInfos) {
//			OpenTypeHistory.getInstance().remove(typeInfo);
//		}
	}
}
