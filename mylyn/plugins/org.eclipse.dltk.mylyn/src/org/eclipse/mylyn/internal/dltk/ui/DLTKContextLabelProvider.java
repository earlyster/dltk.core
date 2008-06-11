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
/*
 * Created on Aug 6, 2004
 */
package org.eclipse.mylyn.internal.dltk.ui;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.ui.ScriptElementLabels;
import org.eclipse.dltk.ui.viewsupport.AppearanceAwareLabelProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.core.IInteractionRelation;
import org.eclipse.mylyn.internal.context.ui.ContextUiImages;
import org.eclipse.mylyn.internal.dltk.DLTKStructureBridge;
import org.eclipse.mylyn.internal.dltk.MylynDLTKPlugin;
import org.eclipse.swt.graphics.Image;

public class DLTKContextLabelProvider extends AppearanceAwareLabelProvider {

	private static final String LABEL_ELEMENT_MISSING = "<missing element>";

	private static final ImageDescriptor EDGE_REF_JUNIT = MylynDLTKPlugin
			.getImageDescriptor("icons/elcl16/edge-ref-junit.gif");


	public DLTKContextLabelProvider() {
		super(AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS
				| ScriptElementLabels.P_COMPRESSED,
				AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS,
				MylynDLTKPlugin.getDefault().getPreferenceStore());
	}

	public String getText(Object object) {
		if (object instanceof IInteractionElement) {
			IInteractionElement node = (IInteractionElement) object;
			//if (bridge.contentType.equals(node.getContentType())) 
			{
				IModelElement element = DLTKCore.create(node
						.getHandleIdentifier());
				if (element == null) {
					return LABEL_ELEMENT_MISSING;
				} else {
					return getTextForElement(element);
				}
			}
		} else if (object instanceof IInteractionRelation) {
			return getNameForRelationship(((IInteractionRelation) object)
					.getRelationshipHandle());
		} else if (object instanceof IModelElement) {
			return getTextForElement((IModelElement) object);
		}
		return super.getText(object);
	}

	private String getTextForElement(IModelElement element) {
		// if (DelegatingContextLabelProvider.isQualifyNamesMode()) {
		if (element instanceof IMember && !(element instanceof IType)) {
			String parentName = ((IMember) element).getParent()
					.getElementName();
			if (parentName != null && parentName != "") {
				return parentName + '.' + super.getText(element);
			}
		}
		// }
		if (element.exists()) {
			return super.getText(element);
		} else {
			return LABEL_ELEMENT_MISSING;
		}
	}

	public Image getImage(Object object) {
		if (object instanceof IInteractionElement) {
			IInteractionElement node = (IInteractionElement) object;
//			if (node.getContentType().equals(bridge.contentType)) 
			{
				return super.getImage(DLTKCore.create(node
						.getHandleIdentifier()));
			}
		} else if (object instanceof IInteractionRelation) {
			ImageDescriptor descriptor = getIconForRelationship(((IInteractionRelation) object)
					.getRelationshipHandle());
			if (descriptor != null) {
				return ContextUiImages.getImage(descriptor);
			} else {
				return null;
			}
		}
		return super.getImage(object);
	}

	private ImageDescriptor getIconForRelationship(String relationshipHandle) {
		// if
		// (relationshipHandle.equals(AbstractJavaRelationProvider.ID_GENERIC))
		// {
		// return ContextUiImages.EDGE_REFERENCE;
		// } else if (relationshipHandle.equals(JavaReferencesProvider.ID)) {
		// return ContextUiImages.EDGE_REFERENCE;
		// } else if (relationshipHandle.equals(JavaImplementorsProvider.ID)) {
		// return ContextUiImages.EDGE_INHERITANCE;
		// } else if (relationshipHandle.equals(JUnitReferencesProvider.ID)) {
		// return EDGE_REF_JUNIT;
		// } else if (relationshipHandle.equals(JavaWriteAccessProvider.ID)) {
		// return ContextUiImages.EDGE_ACCESS_WRITE;
		// } else if (relationshipHandle.equals(JavaReadAccessProvider.ID)) {
		// return ContextUiImages.EDGE_ACCESS_READ;
		// } else {
		// return null;
		// }

		return null;
	}

	private String getNameForRelationship(String relationshipHandle) {
		// if
		// (relationshipHandle.equals(AbstractJavaRelationProvider.ID_GENERIC))
		// {
		// return AbstractJavaRelationProvider.NAME;
		// } else if (relationshipHandle.equals(JavaReferencesProvider.ID)) {
		// return JavaReferencesProvider.NAME;
		// } else if (relationshipHandle.equals(JavaImplementorsProvider.ID)) {
		// return JavaImplementorsProvider.NAME;
		// } else if (relationshipHandle.equals(JUnitReferencesProvider.ID)) {
		// return JUnitReferencesProvider.NAME;
		// } else if (relationshipHandle.equals(JavaWriteAccessProvider.ID)) {
		// return JavaWriteAccessProvider.NAME;
		// } else if (relationshipHandle.equals(JavaReadAccessProvider.ID)) {
		// return JavaReadAccessProvider.NAME;
		// } else if (relationshipHandle.equals(MylarContextManager.
		// CONTAINMENT_PROPAGATION_ID)) {
		// return "Containment"; // TODO: make this generic?
		// } else {
		// return null;
		// }
		return null;
	}

	public static AppearanceAwareLabelProvider createJavaUiLabelProvider() {
		AppearanceAwareLabelProvider scriptUiLabelProvider = new AppearanceAwareLabelProvider(
				AppearanceAwareLabelProvider.DEFAULT_TEXTFLAGS
						| ScriptElementLabels.P_COMPRESSED,
				AppearanceAwareLabelProvider.DEFAULT_IMAGEFLAGS,
				MylynDLTKPlugin.getDefault().getPreferenceStore());
		// javaUiLabelProvider.addLabelDecorator(new
		// TreeHierarchyLayoutProblemsDecorator());
		return scriptUiLabelProvider;
	}
}
