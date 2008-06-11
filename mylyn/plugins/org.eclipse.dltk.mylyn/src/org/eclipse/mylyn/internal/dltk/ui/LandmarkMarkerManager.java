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
 * Created on Feb 16, 2005
 */
package org.eclipse.mylyn.internal.dltk.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.IInteractionContext;
//import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.dltk.DLTKStructureBridge;
import org.eclipse.mylyn.internal.dltk.MylynStatusHandler;


public class LandmarkMarkerManager /*implements IInteractionContextListener*/extends AbstractContextListener {


	private static final String MARKER_ID_LANDMARK = "org.eclipse.mylyn.ui.landmark";
	private Map markerMap = new HashMap();

	DLTKStructureBridge bridge;
	public LandmarkMarkerManager() {
		super();
		this.bridge = new DLTKStructureBridge();
		
	}

	public void contextActivated(IInteractionContext taskscape) {
		modelUpdated();
	}

	public void contextDeactivated(IInteractionContext taskscape) {
		modelUpdated();
	}

	private void modelUpdated() {
		try {
			//for (IInteractionElement node : markerMap.keySet()) {
			for (Iterator it = markerMap.keySet().iterator(); it.hasNext();) {
				IInteractionElement node = (IInteractionElement) it.next();
				landmarkRemoved(node);
			}
			markerMap.clear();
			//for (IInteractionElement node : ContextCorePlugin.getContextManager().getActiveLandmarks()) {
			for(Iterator it = ContextCorePlugin.getContextManager().getActiveLandmarks().iterator(); it.hasNext();) {
				IInteractionElement node = (IInteractionElement) it.next();
				landmarkAdded(node);
			}
		} catch (Throwable t) {
			MylynStatusHandler.fail(t, "Could not update landmark markers", false);
		}
	}

	public void interestChanged(List nodes) {
		// don't care when the interest changes
//		// TBR
//		for (IInteractionElement el : nodes)
//		{
//			landmarkAdded(el);
//			modelUpdated();
//		}
		
	}

	public void landmarkAdded(final IInteractionElement node) {
		if (node == null || node.getContentType() == null)
			return;
		if (node.getContentType().equals(bridge.contentType)) {
			final IModelElement element = DLTKCore.create(node.getHandleIdentifier());
			if (!element.exists())
				return;
			if (element instanceof IMember) {
				try {
					final ISourceRange range = ((IMember) element).getNameRange();
					final IResource resource = element.getUnderlyingResource();
					if (resource instanceof IFile) {
						IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
							public void run(IProgressMonitor monitor) throws CoreException {
								IMarker marker = resource.createMarker(MARKER_ID_LANDMARK);
								if (marker != null && range != null) {
									marker.setAttribute(IMarker.CHAR_START, range.getOffset());
									marker.setAttribute(IMarker.CHAR_END, range.getOffset() + range.getLength());
									marker.setAttribute(IMarker.MESSAGE, "Mylyn Landmark");
									marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
									markerMap.put(node, new LongWrapper(marker.getId()));
								}
							}
						}; 
						resource.getWorkspace().run(runnable, null);
					}
				} catch (ModelException e) {
					MylynStatusHandler.fail(e, "couldn't update marker", false);
				} catch (CoreException e) {
					MylynStatusHandler.fail(e, "couldn't update marker", false);
				}
			}
		}
	}

	public void landmarkRemoved(final IInteractionElement node) {
		if (node == null)
			return;
		if (node.getContentType().equals(bridge.contentType)) {
			IModelElement element = DLTKCore.create(node.getHandleIdentifier());
			if (!element.exists())
				return;
			if (element.getAncestor(IModelElement.SOURCE_MODULE) != null // stuff
																			// from
																			// .class
																			// files
					&& element instanceof ISourceReference) {
				try {
					final IResource resource = element.getUnderlyingResource();
					IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
						public void run(IProgressMonitor monitor) throws CoreException {
							if (resource != null) {
								try {
									if (markerMap.containsKey(node)) {
										long id = ((LongWrapper)(markerMap.get(node))).value();
										IMarker marker = resource.getMarker(id);
										if (marker != null)
											marker.delete();
									}
								} catch (NullPointerException e) {
									MylynStatusHandler.log(e, "could not update markers");
								}
							}
						}
					};
					resource.getWorkspace().run(runnable, null);
				} catch (ModelException e) {
					// ignore the Java Model errors
//					MylarStatusHandler.fail(e, "couldn't update landmark marker", false);
				} catch (CoreException e) {
					MylynStatusHandler.fail(e, "couldn't update landmark marker", false);
				}
			}
		}
	}

	public void relationsChanged(IInteractionElement node) {
		// don't care when the relationships changed
	}

/*	public void presentationSettingsChanging(UpdateKind kind) {
		// don't care when there is a presentations setting change
	}

	public void presentationSettingsChanged(UpdateKind kind) {
		// don't care when there is a presentation setting change
	}
*/
	public void elementDeleted(IInteractionElement node) {
		// don't care when a node is deleted
	}

	public void contextCleared(IInteractionContext arg0) {
		// ignore
	}
	
	private class LongWrapper {
		private long val = 0;
		
		public LongWrapper(long v)
		{
			val = v;
		}
		
		public long value()
		{
			return val;
		}
	}
		
}
