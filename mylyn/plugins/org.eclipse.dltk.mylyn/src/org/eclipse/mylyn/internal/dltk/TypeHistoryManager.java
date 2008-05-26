/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.dltk;

import java.util.List;
import java.util.ListIterator;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.monitor.core.IInteractionEventListener;
import org.eclipse.mylyn.monitor.core.InteractionEvent;


public class TypeHistoryManager /*implements IInteractionEventListener*/extends AbstractContextListener {

//	private TypeInfoFactory factory = new TypeInfoFactory();

	public void contextActivated(IInteractionContext context) {
		clearTypeHistory();
		//for (IInteractionElement node : context.getInteresting())
		for (ListIterator it = context.getInteresting().listIterator(); it.hasNext(); )
		{
			IInteractionElement node = (IInteractionElement)it.next();
			updateTypeHistory(node, true);
		}
	}

	/**
	 * Path has to be compatible with ITypeNameRequestor
	 */
	private void updateTypeHistory(IInteractionElement node, boolean add) {
		IModelElement element = DLTKCore.create(node.getHandleIdentifier());
		if (element instanceof IType) {
			IType type = (IType) element;
			if (type != null && type.exists() ) {
//					TypeInfo info = factory.create(type.getPackageFragment().getElementName().toCharArray(), type
//							.getElementName().toCharArray(), enclosingTypeNames(type), type.getFlags(), getPath(type));

//					JavaSearchTypeNameMatch typeNameMatch = new JavaSearchTypeNameMatch(type, type.getFlags());
//					
//					if (add && !OpenTypeHistory.getInstance().contains(typeNameMatch)) {
//						OpenTypeHistory.getInstance().accessed(typeNameMatch);
//					} else {
//						OpenTypeHistory.getInstance().remove(typeNameMatch);
//					}
			}
		}
	}

	/**
	 * HACK: to avoid adding AspectJ types, for example:
	 * 
	 * class: =TJP Example/src<tjp{Demo.java[Demo aspect: =TJP Example/src<tjp*GetInfo.aj}GetInfo
	 */
	private boolean isAspectjType(IType type) {
		if (type.getHandleIdentifier().indexOf('}') != -1) {
			return true;
		} else {
			return false;
		}
	}

	public void contextDeactivated(IInteractionContext context) {
		clearTypeHistory();
	}

//	/**
//	 * Public for testing
//	 */
	public void clearTypeHistory() {
		//TypeNameMatch[] typeInfos = OpenTypeHistory.getInstance().getTypeInfos();
		//for (int i = 0; i < typeInfos.length; i++) {
		//	OpenTypeHistory.getInstance().remove(typeInfos[i]);
		//} 
	}

	public void interestChanged(List nodes) {
		for (ListIterator it = nodes.listIterator(); it.hasNext(); ) {
			IInteractionElement node = (IInteractionElement) it.next();
			updateTypeHistory(node, true);
		}
	}

	public void elementDeleted(IInteractionElement node) {
		updateTypeHistory(node, false);
	}

//	public void presentationSettingsChanging(UpdateKind kind) {
//		// ignore
//	}
//
//	public void presentationSettingsChanged(UpdateKind kind) {
//		// ignore
//	}


	
	public void landmarkAdded(IInteractionElement node) {
		// ignore
	}

	public void landmarkRemoved(IInteractionElement node) {
		// ignore
	}

	public void relationsChanged(IInteractionElement node) {
		// ignore
	}

	public void contextCleared(IInteractionContext arg0) {
		// ignore
		
	}

	public void interactionObserved(InteractionEvent event) {
		// TODO Auto-generated method stub
		
	}

	public void startMonitoring() {
		// TODO Auto-generated method stub
		
	}

	public void stopMonitoring() {
		// TODO Auto-generated method stub
		
	}
}
