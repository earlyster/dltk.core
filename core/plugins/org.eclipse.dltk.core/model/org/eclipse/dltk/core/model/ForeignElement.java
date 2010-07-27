package org.eclipse.dltk.core.model;

import java.util.HashMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.IDocumentableElement;
import org.eclipse.dltk.core.IForeignElement;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.WorkingCopyOwner;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.util.MementoTokenizer;

/**
 * @since 3.0
 */
public abstract class ForeignElement extends ModelElement implements
		IForeignElement, IDocumentableElement {

	protected ForeignElement(IModelElement parent)
			throws IllegalArgumentException {
		super((ModelElement) parent);
	}

	public IResource getResource() {
		return null;
	}

	public IPath getPath() {
		IResource r = getResource();
		if (r != null)
			return r.getFullPath();
		return Path.EMPTY;
	}

	public IResource getUnderlyingResource() throws ModelException {
		return getResource();
	}

	public IResource getCorrespondingResource() throws ModelException {
		return getResource();
	}

	public boolean isStructureKnown() throws ModelException {
		return true;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public void close() throws ModelException {
	}

	@Override
	protected void closing(Object info) throws ModelException {
	}

	private static class ForeignInfo {
	}

	@Override
	protected Object createElementInfo() {
		return new ForeignInfo();
	}

	@Override
	protected void generateInfos(Object info, HashMap newElements,
			IProgressMonitor pm) throws ModelException {
		newElements.put(this, info);
	}

	@Override
	public IModelElement getHandleFromMemento(String token,
			MementoTokenizer memento, WorkingCopyOwner owner) {
		return null;
	}

	@Override
	protected char getHandleMementoDelimiter() {
		return 0;
	}

	public int getElementType() {
		return IModelElement.USER_ELEMENT;
	}
}
