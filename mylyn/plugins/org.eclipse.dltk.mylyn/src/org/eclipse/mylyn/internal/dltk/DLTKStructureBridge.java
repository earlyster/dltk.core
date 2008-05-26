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
 * Created on Apr 7, 2005
 */
package org.eclipse.mylyn.internal.dltk;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.ProjectFragment;
import org.eclipse.dltk.internal.ui.scriptview.BuildPathContainer;
import org.eclipse.dltk.ui.util.ExceptionHandler;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.context.core.DegreeOfSeparation;
import org.eclipse.mylyn.internal.resources.ui.ResourceStructureBridge;
import org.eclipse.ui.internal.WorkingSet;
import org.eclipse.ui.views.markers.internal.ConcreteMarker;

public class DLTKStructureBridge extends AbstractContextStructureBridge {

	public final static String CONTENT_TYPE = "script";

	public List providers;

	public DLTKStructureBridge() {
		providers = new ArrayList();
		// providers.add(new JavaReferencesProvider());
		// providers.add(new JavaImplementorsProvider());
		// providers.add(new JavaReadAccessProvider());
		// providers.add(new JavaWriteAccessProvider());
		// providers.add(new JUnitReferencesProvider());
	}

	public String getContentType() {
		return CONTENT_TYPE;
	}

	public Object getAdaptedParent(Object object) {
		if (object instanceof IFile) {
			IFile file = (IFile) object;
			return DLTKCore.create(file.getParent());
		} else {
			return super.getAdaptedParent(object);
		}
	}

	public String getParentHandle(String handle) {
		IModelElement modelElement = (IModelElement) getObjectForHandle(handle);
		if (modelElement != null && modelElement.getParent() != null) {
			return getHandleIdentifier(modelElement.getParent());
		} else {
			return null;
		}
	}

	public List getChildHandles(String handle) {
		Object object = getObjectForHandle(handle);
		if (object instanceof IModelElement) {
			IModelElement element = (IModelElement) object;
			if (element instanceof IParent) {
				IParent parent = (IParent) element;
				IModelElement[] children;
				try {
					children = parent.getChildren();
					List childHandles = new ArrayList();
					for (int i = 0; i < children.length; i++) {
						String childHandle = getHandleIdentifier(children[i]);
						if (childHandle != null)
							childHandles.add(childHandle);
					}
					AbstractContextStructureBridge parentBridge = ContextCorePlugin
							.getDefault().getStructureBridge(parentContentType);
					if (parentBridge != null
							&& parentBridge instanceof ResourceStructureBridge) {
						if (element.getElementType() < IModelElement.TYPE) {
							List resourceChildren = parentBridge
									.getChildHandles(handle);
							if (!resourceChildren.isEmpty())
								childHandles.addAll(resourceChildren);
						}
					}

					return childHandles;
				} catch (ModelException e) {
					// ignore these, usually indicate no-existent element
				} catch (Exception e) {
					MylynStatusHandler.fail(e, "could not get child", false);
				}
			}
		}
		return new ArrayList();
	}

	public Object getObjectForHandle(String handle) {
		try {
			return DLTKCore.create(handle);
		} catch (Throwable t) {
			MylynStatusHandler.log(
					"Could not create script element for handle: " + handle,
					this);
			return null;
		}
	}

	/**
	 * Uses resource-compatible path for projects
	 */

	public String getHandleIdentifier(Object object) {
		if (object instanceof IModelElement) {
			return ((IModelElement) object).getHandleIdentifier();
		} else {
			if (object instanceof IAdaptable) {
				Object adapter = ((IAdaptable) object)
						.getAdapter(IModelElement.class);
				if (adapter instanceof IModelElement) {
					return ((IModelElement) adapter).getHandleIdentifier();
				}
			}
		}
		return null;
	}

	public boolean canBeLandmark(String handle) {
		IModelElement element = (IModelElement) getObjectForHandle(handle);
		if ((element instanceof IMember || element instanceof IType || element instanceof ISourceModule)
				&& element.exists()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * TODO: figure out if the non IModelElement stuff is needed
	 */

	public boolean acceptsObject(Object object) {
		if (object instanceof IResource) {
			Object adapter = ((IResource) object)
					.getAdapter(IModelElement.class);
			return adapter instanceof IModelElement;
		}

		boolean accepts = object instanceof IModelElement
				|| object instanceof ProjectFragment
				|| object instanceof BuildPathContainer.RequiredProjectWrapper // ||
				// object
				// instanceof
				// EntryFile
				|| object instanceof IProjectFragment
				|| object instanceof WorkingSet;

		return accepts;
	}

	/**
	 * Uses special rules for classpath containers since these do not have an
	 * associated interest, i.e. they're not IModelElement(s).
	 */
	public boolean canFilter(Object object) {
		if (object instanceof BuildPathContainer.RequiredProjectWrapper) {
			return true;
		}

		// else if (object instanceof PackageFragmentRootContainer) {
		// // since not in model, check if it contains anything interesting
		// PackageFragmentRootContainer container =
		// (PackageFragmentRootContainer) object;
		//
		// Object[] children = container.getChildren();
		// for (int i = 0; i < children.length; i++) {
		// if (children[i] instanceof JarPackageFragmentRoot) {
		// JarPackageFragmentRoot element = (JarPackageFragmentRoot)
		// children[i];
		// IMylarElement node =
		// ContextCorePlugin.getContextManager().getElement(
		// element.getHandleIdentifier());
		// if (node != null && node.getInterest().isInteresting()) {
		// return false;
		// }
		// }
		// }
		// }

		else if (object instanceof WorkingSet) {
			try {
				WorkingSet workingSet = (WorkingSet) object;
				IAdaptable[] elements = workingSet.getElements();
				for (int i = 0; i < elements.length; i++) {
					IAdaptable adaptable = elements[i];
					IInteractionElement element = ContextCorePlugin
							.getContextManager().getElement(
									getHandleIdentifier(adaptable));
					if (element.getInterest().isInteresting())
						return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	public boolean isDocument(String handle) {
		IModelElement element = (IModelElement) getObjectForHandle(handle);
		return element instanceof ISourceModule;
	}

	public String getHandleForOffsetInObject(Object resource, int offset) {
		if (resource == null || !(resource instanceof ConcreteMarker))
			return null;
		ConcreteMarker marker = (ConcreteMarker) resource;
		try {
			IResource res = marker.getResource();
			ISourceModule compilationUnit = null;
			if (res instanceof IFile) {
				IFile file = (IFile) res;
				if (file.getFileExtension().equals("java")) {
					compilationUnit = DLTKCore.createSourceModuleFrom(file);
				} else {
					return null;
				}
			}
			if (compilationUnit != null) {
				IModelElement javaElement = compilationUnit.getElementAt(marker
						.getMarker().getAttribute(IMarker.CHAR_START, 0));
				if (javaElement != null) {

					return javaElement.getHandleIdentifier();
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (ModelException ex) {
			if (!ex.isDoesNotExist())
				ExceptionHandler.handle(ex,
						"error", "could not find java element"); //$NON-NLS-2$ //$NON-NLS-1$
			return null;
		} catch (Throwable t) {
			MylynStatusHandler.fail(t, "Could not find element for: " + marker,
					false);
			return null;
		}
	}

	public String getContentType(String elementHandle) {
		return getContentType();
	}

	public List getRelationshipProviders() {
		return providers;
	}

	public List getDegreesOfSeparation() {
		List separations = new ArrayList();

		// separations.add(new DegreeOfSeparation(DOS_1_LABEL, 0));
		// separations.add(new DegreeOfSeparation(DOS_1_LABEL, 1));
		// separations.add(new DegreeOfSeparation(DOS_2_LABEL, 2));
		// separations.add(new DegreeOfSeparation(DOS_3_LABEL, 3));
		// separations.add(new DegreeOfSeparation(DOS_4_LABEL, 4));
		// separations.add(new DegreeOfSeparation(DOS_5_LABEL, 5));
		separations.add(new DegreeOfSeparation("disabled", 0));
		separations.add(new DegreeOfSeparation("landmark resources", 1));
		separations.add(new DegreeOfSeparation("interesting resources", 2));
		separations.add(new DegreeOfSeparation("interesting projects", 3));
		separations.add(new DegreeOfSeparation("project dependencies", 4));
		separations.add(new DegreeOfSeparation("entire workspace (slow)", 5));
		return separations;
	}

	/**
	 * Some copying from:
	 * 
	 * @see org.eclipse.jdt.ui.ProblemsLabelDecorator
	 */
	public boolean containsProblem(IInteractionElement node) {
		try {
			IModelElement element = (IModelElement) getObjectForHandle(node
					.getHandleIdentifier());
			switch (element.getElementType()) {
			case IModelElement.SCRIPT_PROJECT:
			case IModelElement.PROJECT_FRAGMENT:
				return getErrorTicksFromMarkers(element.getResource(),
						IResource.DEPTH_INFINITE, null);
			case IModelElement.PACKAGE_DECLARATION:
			case IModelElement.SOURCE_MODULE:
			case IModelElement.BINARY_MODULE:
				return getErrorTicksFromMarkers(element.getResource(),
						IResource.DEPTH_ONE, null);
			case IModelElement.TYPE:
			case IModelElement.METHOD:
			case IModelElement.FIELD:
				ISourceModule cu = (ISourceModule) element
						.getAncestor(IModelElement.SOURCE_MODULE);
				if (cu != null)
					return getErrorTicksFromMarkers(element.getResource(),
							IResource.DEPTH_ONE, null);
			}
		} catch (CoreException e) {
			// ignore
		}
		return false;
	}

	private boolean getErrorTicksFromMarkers(IResource res, int depth,
			ISourceReference sourceElement) throws CoreException {
		if (res == null || !res.isAccessible())
			return false;
		IMarker[] markers = res.findMarkers(IMarker.PROBLEM, true, depth);
		if (markers != null) {
			for (int i = 0; i < markers.length; i++) {
				IMarker curr = markers[i];
				if (sourceElement == null) {
					int priority = curr.getAttribute(IMarker.SEVERITY, -1);
					if (priority == IMarker.SEVERITY_ERROR) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public String getLabel(Object arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
