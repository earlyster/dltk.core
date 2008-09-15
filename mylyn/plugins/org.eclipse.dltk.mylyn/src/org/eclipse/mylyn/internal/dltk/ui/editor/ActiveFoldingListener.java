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
 * Created on May 16, 2005
 */
package org.eclipse.mylyn.internal.dltk.ui.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.ui.editor.ScriptEditor;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.ui.text.folding.IFoldingStructureProviderExtension;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.ui.ContextUiPlugin;
import org.eclipse.mylyn.internal.dltk.DLTKStructureBridge;
import org.eclipse.mylyn.internal.dltk.MylynDLTKPrefConstants;
import org.eclipse.mylyn.internal.dltk.MylynStatusHandler;

public class ActiveFoldingListener extends AbstractContextListener {

	private final ScriptEditor editor;

	private IFoldingStructureProviderExtension updater;

	private static DLTKStructureBridge bridge;

	private boolean enabled = false;

	private IPropertyChangeListener PREFERENCE_LISTENER = new IPropertyChangeListener() {
		public void propertyChange(PropertyChangeEvent event) {
			if (event.getProperty().equals(
					MylynDLTKPrefConstants.ACTIVE_FOLDING_ENABLED)) {
				if (event.getNewValue().equals(Boolean.TRUE.toString())) {
					enabled = true;
				} else {
					enabled = false;
				}
				updateFolding();
			}
		}
	};

	private static synchronized void createBrigde() {
		/*
		 * FIXME why we create bridge here?
		 * 
		 * We should call the same code without bridge.
		 */
		if (bridge == null) {
			bridge = new DLTKStructureBridge();
		}
	}

	public ActiveFoldingListener(ScriptEditor editor) {
		this.editor = editor;
		createBrigde();
		ContextCorePlugin.getContextManager().addListener(this);
		ContextUiPlugin.getDefault().getPluginPreferences()
				.addPropertyChangeListener(PREFERENCE_LISTENER);

		enabled = ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
				MylynDLTKPrefConstants.ACTIVE_FOLDING_ENABLED);
		try {
			Object adapter = editor
					.getAdapter(IFoldingStructureProviderExtension.class);
			if (adapter instanceof IFoldingStructureProviderExtension) {
				updater = (IFoldingStructureProviderExtension) adapter;
			} else {
				MylynStatusHandler.log(
						"Could not install active folding on provider: "
								+ adapter
								+ ", must extend "
								+ IFoldingStructureProviderExtension.class
										.getName(), this);
			}
		} catch (Exception e) {
			MylynStatusHandler.fail(e,
					"could not install auto folding, reflection denied", false);
		}
		updateFolding();
	}

	public void dispose() {
		ContextCorePlugin.getContextManager().removeListener(this);
		ContextUiPlugin.getDefault().getPluginPreferences()
				.removePropertyChangeListener(PREFERENCE_LISTENER);
	}

	public static void resetProjection(ScriptEditor dltk) {
		// XXX: ignore for 3.2, leave for 3.1?
	}

	public void updateFolding() {
		if (!enabled
				|| !ContextCorePlugin.getContextManager().isContextActive()) {
			editor.resetProjection();
		} else if (editor.getEditorInput() == null) {
			return;
		} else {
			try {
				List toExpand = new ArrayList();
				List toCollapse = new ArrayList();
				IModelElement element = DLTKUIPlugin
						.getEditorInputModelElement(editor.getEditorInput());
				if (element instanceof ISourceModule) {
					ISourceModule compilationUnit = (ISourceModule) element;
					List allChildren = getAllChildren(compilationUnit);
					// for (IModelElement child : allChildren) {
					for (ListIterator it = allChildren.listIterator(); it
							.hasNext();) {
						IModelElement child = (IModelElement) it.next();
						IInteractionElement mylarElement = ContextCorePlugin
								.getContextManager().getElement(
										bridge.getHandleIdentifier(child));
						if (mylarElement != null
								&& mylarElement.getInterest().isInteresting()) {
							toExpand.add(child);
						} else {
							toCollapse.add(child);
						}
					}
				}
				if (updater != null) {
					updater.collapseMembers();
					updater.expandElements((IModelElement[]) toExpand
							.toArray(new IModelElement[toExpand.size()]));
				}
			} catch (Exception e) {
				MylynStatusHandler.fail(e, "couldn't update folding", false);
			}
		}
	}

	private static List getAllChildren(IParent parentElement) {
		List allChildren = new ArrayList();
		try {
			// for (IModelElement child : parentElement.getChildren()) {
			for (int i = 0; i < parentElement.getChildren().length; i++) {
				IModelElement child = parentElement.getChildren()[i];
				allChildren.add(child);
				if (child instanceof IParent) {
					allChildren.addAll(getAllChildren((IParent) child));
				}
			}
		} catch (ModelException e) {
			// ignore failures
		}
		return allChildren;
	}

	public void interestChanged(List elements) {
		// for (IInteractionElement element : elements) {
		for (ListIterator it = elements.listIterator(); it.hasNext();) {
			IInteractionElement element = (IInteractionElement) it.next();
			if (updater == null || !enabled) {
				return;
			} else {
				Object object = bridge.getObjectForHandle(element
						.getHandleIdentifier());
				if (object instanceof IMember) {
					IMember member = (IMember) object;
					if (element.getInterest().isInteresting()) {
						updater.expandElements(new IModelElement[] { member });
						// expand the next 2 children down (e.g. anonymous
						// types)
						try {
							IModelElement[] children = ((IParent) member)
									.getChildren();
							if (children.length == 1) {
								updater
										.expandElements(new IModelElement[] { children[0] });
								if (children[0] instanceof IParent) {
									IModelElement[] childsChildren = ((IParent) children[0])
											.getChildren();
									if (childsChildren.length == 1) {
										updater
												.expandElements(new IModelElement[] { childsChildren[0] });
									}
								}
							}
						} catch (ModelException e) {
							// ignore
						}
					} else {
						updater
								.collapseElements(new IModelElement[] { member });
					}
				}
			}
		}
	}

	public void contextActivated(IInteractionContext context) {
		if (ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
				MylynDLTKPrefConstants.ACTIVE_FOLDING_ENABLED)) {
			updateFolding();
		}
	}

	public void contextDeactivated(IInteractionContext context) {
		if (ContextUiPlugin.getDefault().getPreferenceStore().getBoolean(
				MylynDLTKPrefConstants.ACTIVE_FOLDING_ENABLED)) {
			updateFolding();
		}
	}

	// public void
	// presentationSettingsChanging(IInteractionContextListener.UpdateKind kind)
	// {
	// // ignore
	// }
	//
	// public void
	// presentationSettingsChanged(IInteractionContextListener.UpdateKind kind)
	// {
	//		
	// updateFolding();
	// }

	// public void presentationSettingsChanging(IInteractionContextListener
	// kind) {
	// // ignore
	// }

	// public void presentationSettingsChanged(IInteractionContextListener kind)
	// {
	//	
	// updateFolding();
	// }

	public void landmarkAdded(IInteractionElement element) {
		// ignore
	}

	public void landmarkRemoved(IInteractionElement element) {
		// ignore
	}

	public void relationsChanged(IInteractionElement node) {
		// ignore
	}

	public void elementDeleted(IInteractionElement node) {
		// ignore
	}

	public void contextCleared(IInteractionContext arg0) {
		// ignore

	}
}
