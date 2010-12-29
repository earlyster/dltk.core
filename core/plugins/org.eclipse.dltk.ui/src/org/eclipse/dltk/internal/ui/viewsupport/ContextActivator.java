/*******************************************************************************
 * Copyright (c) 2007, 2008 Wind River Systems, Inc. and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Markus Schorn - initial API and implementation
 *******************************************************************************/ 

package org.eclipse.dltk.internal.ui.viewsupport;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.ui.editor.ScriptOutlinePage;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.views.contentoutline.ContentOutline;

/**
 * Once registered as windows listener, observes all part activations. Whenever
 * the DLTK outline view is brought to the front, the DLTK context is set.
 */
public class ContextActivator implements IWindowListener, IPartListener2 {
	private static ContextActivator sInstance= new ContextActivator();
	
	private Map<ContentOutline, IContextActivation> fActivationPerOutline = new HashMap<ContentOutline, IContextActivation>();
	private Map<CommonNavigator, SelectionListener> fActivationPerNavigator= new HashMap<CommonNavigator, SelectionListener>();
	private Collection<IWorkbenchWindow> fWindows= new HashSet<IWorkbenchWindow>();

	
	private ContextActivator() {
	}
	
	public static ContextActivator getInstance() {
		return sInstance;
	}
	
	public void install() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		if (workbench != null) {
			// listen for new windows
			workbench.addWindowListener(this);
			IWorkbenchWindow[] wnds= workbench.getWorkbenchWindows();
			for (int i = 0; i < wnds.length; i++) {
				IWorkbenchWindow window = wnds[i];
				register(window);
			}
			// register open windows
			IWorkbenchWindow ww= PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			if (ww != null) {
				IWorkbenchPage activePage = ww.getActivePage();
				if (activePage != null) {
					IWorkbenchPartReference part= activePage.getActivePartReference();
					if (part != null) {
						partActivated(part);
					}
				}
			}
		}
	}

	public void uninstall() {
		for (Iterator<IWorkbenchWindow> iterator = fWindows.iterator(); iterator.hasNext();) {
			IWorkbenchWindow window = iterator.next();
			unregister(window);
		}
		for (Iterator<SelectionListener> iterator = fActivationPerNavigator.values().iterator(); iterator.hasNext();) {
			SelectionListener l= iterator.next();
			l.uninstall();
		}
	}

	private void register(IWorkbenchWindow wnd) {
		wnd.getPartService().addPartListener(this);
		fWindows.add(wnd);
	}
	
	private void unregister(IWorkbenchWindow wnd) {
		wnd.getPartService().removePartListener(this);
		fWindows.remove(wnd);
	}

	public void windowOpened(IWorkbenchWindow window) {
		register(window);
	}

	public void windowClosed(IWorkbenchWindow window) {
		unregister(window);
	}

	public void windowActivated(IWorkbenchWindow window) {
	}
	
	public void windowDeactivated(IWorkbenchWindow window) {
	}

	private void onContentOutlineClosed(ContentOutline outline) {
		fActivationPerOutline.remove(outline);
	}
	
	private void onContentOutlineActivated(ContentOutline outline) {
		IPage page = outline.getCurrentPage();
		if (page instanceof ScriptOutlinePage) {
			if (!fActivationPerOutline.containsKey(outline)){
				// dltk outline activated for the first time
				IContextService ctxtService = (IContextService)outline.getViewSite().getService(IContextService.class);
				IContextActivation activateContext = ctxtService
						.activateContext(DLTKUIPlugin.CONTEXT_VIEWS);
				fActivationPerOutline.put(outline,activateContext);
			}
		} 
		else {
			IContextActivation activation = fActivationPerOutline.remove(outline); 
			if (activation != null) {
				// other outline page brought to front
				IContextService ctxtService = (IContextService)outline.getViewSite().getService(IContextService.class);
				ctxtService.deactivateContext(activation);
			}
		}
	}

	private static class SelectionListener implements ISelectionChangedListener {
		private IWorkbenchPartSite fSite;
		private IContextService fCtxService;
		private IContextActivation fActivation;

		public SelectionListener(IWorkbenchPartSite site) {
			fSite= site;
			fCtxService= (IContextService)fSite.getService(IContextService.class);
			ISelectionProvider sp= site.getSelectionProvider();
			
			if (sp != null && fCtxService != null) {
				sp.addSelectionChangedListener(this);
				onNewSelection(sp.getSelection());
			}
		}

		public void uninstall() {
			ISelectionProvider sp= fSite.getSelectionProvider();
			if (sp != null && fCtxService != null) {
				onNewSelection(null);
				sp.removeSelectionChangedListener(this);
			}
		}
		
		public void selectionChanged(SelectionChangedEvent event) {
			onNewSelection(event.getSelection());
		}

		private void onNewSelection(ISelection selection) {
			boolean isRelevant= false;
			if (selection instanceof IStructuredSelection && !selection.isEmpty()) {
				if (((IStructuredSelection) selection).getFirstElement() instanceof ModelElement) {
					isRelevant = true;
				}
			}
			if (isRelevant) {
				if (fActivation == null) {
					fActivation = fCtxService
							.activateContext(DLTKUIPlugin.CONTEXT_VIEWS);
				}
			}
			else {
				if (fActivation != null) {
					fCtxService.deactivateContext(fActivation);
					fActivation= null;
				}
			}
		}
	}

	private void onCommonNavigatorActivated(CommonNavigator part) {
		SelectionListener l= fActivationPerNavigator.get(part);
		if (l == null) {
			l= new SelectionListener(part.getSite());
			fActivationPerNavigator.put(part, l);
		}
	}

	private void onCommonNavigatorClosed(CommonNavigator part) {
		fActivationPerNavigator.remove(part);
	}

	public void partActivated(IWorkbenchPartReference partRef) {
		IWorkbenchPart part= partRef.getPart(false);
		if (part instanceof ContentOutline) {
			onContentOutlineActivated((ContentOutline) part);
		}
		else if (part instanceof CommonNavigator) {
			onCommonNavigatorActivated((CommonNavigator) part);
		}
	}

	public void partClosed(IWorkbenchPartReference partRef) {
		IWorkbenchPart part= partRef.getPart(false);
		if (part instanceof ContentOutline) {
			onContentOutlineClosed((ContentOutline)part);
		}
		else if (part instanceof CommonNavigator) {
			onCommonNavigatorClosed((CommonNavigator) part);
		}
	}

	public void partBroughtToTop(IWorkbenchPartReference partRef) {
	}

	public void partDeactivated(IWorkbenchPartReference partRef) {
	}

	public void partOpened(IWorkbenchPartReference partRef) {
	}

	public void partHidden(IWorkbenchPartReference partRef) {
	}

	public void partVisible(IWorkbenchPartReference partRef) {
	}

	public void partInputChanged(IWorkbenchPartReference partRef) {
	}
}
