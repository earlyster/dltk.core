package org.eclipse.dltk.internal.core.caching;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IElementChangedListener;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementDelta;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;

public abstract class ModelCacheListener implements IResourceChangeListener,
		IElementChangedListener {

	protected void remove(ISourceModule module) {
	}

	protected void remove(IScriptProject element) {
	}

	protected void remove(IScriptFolder element) {
	}

	protected void remove(IProject element) {
	}

	protected void remove(IProjectFragment element) {
	}

	public void elementChanged(ElementChangedEvent event) {
		IModelElementDelta delta = event.getDelta();
		processDelta(delta);
	}

	private void processDelta(IModelElementDelta delta) {
		IModelElement element = delta.getElement();
		if (delta.getKind() == IModelElementDelta.REMOVED
				|| delta.getKind() == IModelElementDelta.CHANGED
				|| (delta.getFlags() & IModelElementDelta.F_REMOVED_FROM_BUILDPATH) != 0
				|| (delta.getFlags() & IModelElementDelta.CHANGED) != 0) {
			if (element.getElementType() != IModelElement.SOURCE_MODULE
					&& element.getElementType() != IModelElement.PROJECT_FRAGMENT
					&& element.getElementType() != IModelElement.SCRIPT_FOLDER
					&& element.getElementType() != IModelElement.SCRIPT_MODEL
					&& element.getElementType() != IModelElement.SCRIPT_PROJECT) {
				ISourceModule module = (ISourceModule) element
						.getAncestor(IModelElement.SOURCE_MODULE);
				remove(module);
			}
			if (element.getElementType() == IModelElement.SOURCE_MODULE) {
				remove((ISourceModule) element);
			}
		}

		if (element.getElementType() == IModelElement.SCRIPT_PROJECT
				&& delta.getKind() == IModelElementDelta.CHANGED
				&& (delta.getFlags() & IModelElementDelta.F_BUILDPATH_CHANGED) != 0) {
			remove((IScriptProject) element);
			return;
		}

		if ((delta.getFlags() & IModelElementDelta.F_CHILDREN) != 0) {
			IModelElementDelta[] affectedChildren = delta.getAffectedChildren();
			for (int i = 0; i < affectedChildren.length; i++) {
				IModelElementDelta child = affectedChildren[i];
				processDelta(child);
			}
		} else if ((delta.getKind() == IModelElementDelta.REMOVED || delta
				.getKind() == IModelElementDelta.CHANGED)
				&& element.getElementType() == IModelElement.SCRIPT_FOLDER) {
			if (delta.getAffectedChildren().length == 0) {
				remove((IScriptFolder) element);
			}
		} else if ((delta.getKind() == IModelElementDelta.REMOVED || delta
				.getKind() == IModelElementDelta.CHANGED)
				&& element.getElementType() == IModelElement.PROJECT_FRAGMENT) {
			if (delta.getAffectedChildren().length == 0) {
				remove((IProjectFragment) element);
			}
		}
	}

	public void resourceChanged(IResourceChangeEvent event) {
		int eventType = event.getType();
		IResource resource = event.getResource();
		// IResourceDelta delta = event.getDelta();

		switch (eventType) {
		case IResourceChangeEvent.PRE_DELETE:
			if (resource.getType() == IResource.PROJECT
					&& DLTKLanguageManager.hasScriptNature((IProject) resource)) {
				remove((IProject) resource);
			}
			return;
		}
	}
}
