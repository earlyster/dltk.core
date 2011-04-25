/*******************************************************************************
 * Copyright (c) 2011 NumberFour AG
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     NumberFour AG - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.core.model;

import java.util.HashMap;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IBuffer;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IOpenable;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ISourceReference;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.SourceRange;
import org.eclipse.dltk.core.WorkingCopyOwner;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.SourceRefElement;
import org.eclipse.dltk.internal.core.util.MementoTokenizer;
import org.eclipse.dltk.internal.core.util.Util;

public class UnresolvedElement extends SourceRefElement {

	private final String name;
	private final int start, end;

	/**
	 * @param parent
	 *            the parent of the element
	 * @param name
	 *            the name of the element
	 * @param start
	 *            the position of the element name start
	 * @param end
	 *            the position of the element name end (including - the position
	 *            of the last character)
	 */
	public UnresolvedElement(IModelElement parent, String name, int start,
			int end) {
		super((ModelElement) parent);
		this.name = name;
		this.start = start;
		this.end = end;
	}

	@Override
	protected void closing(Object info) {
		// an unresolved element has no info
	}

	@Override
	protected Object createElementInfo() {
		// an unresolved element has no info
		return null;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof UnresolvedElement))
			return false;
		final UnresolvedElement other = (UnresolvedElement) o;
		return this.start == other.start && this.end == other.end
				&& super.equals(o);
	}

	@Override
	public boolean exists() {
		return this.parent.exists();
	}

	@Override
	protected void generateInfos(Object info, HashMap newElements,
			IProgressMonitor pm) {
		// an unresolved element has no info
	}

	@Override
	public IModelElement getHandleFromMemento(String token,
			MementoTokenizer memento, WorkingCopyOwner owner) {
		switch (token.charAt(0)) {
		case JEM_COUNT:
			return getHandleUpdatingCountFromMemento(memento, owner);
		}
		return this;
	}

	@Override
	public void getHandleMemento(StringBuffer buff) {
		((ModelElement) getParent()).getHandleMemento(buff);
		buff.append(getHandleMementoDelimiter());
		buff.append(this.name);
		buff.append(JEM_COUNT);
		buff.append(this.start);
		buff.append(JEM_COUNT);
		buff.append(this.end);
		if (this.occurrenceCount > 1) {
			buff.append(JEM_COUNT);
			buff.append(this.occurrenceCount);
		}
	}

	@Override
	protected char getHandleMementoDelimiter() {
		return ModelElement.JEM_LOCALVARIABLE;
	}

	@Override
	public IResource getCorrespondingResource() {
		return null;
	}

	@Override
	public String getElementName() {
		return this.name;
	}

	public int getElementType() {
		return LOCAL_VARIABLE;
	}

	@Override
	public IPath getPath() {
		return this.parent.getPath();
	}

	/**
	 * @see ISourceReference
	 */
	@Override
	public String getSource() throws ModelException {
		IOpenable openable = this.parent.getOpenableParent();
		IBuffer buffer = openable.getBuffer();
		if (buffer == null) {
			return null;
		}
		ISourceRange range = getSourceRange();
		int offset = range.getOffset();
		int length = range.getLength();
		if (offset == -1 || length == 0) {
			return null;
		}
		try {
			return buffer.getText(offset, length);
		} catch (RuntimeException e) {
			return null;
		}
	}

	@Override
	public ISourceRange getSourceRange() {
		return new SourceRange(this.start, this.end - this.start + 1);
	}

	@Override
	public IResource getUnderlyingResource() throws ModelException {
		return this.parent.getUnderlyingResource();
	}

	@Override
	public int hashCode() {
		return Util.combineHashCodes(this.parent.hashCode(), this.start);
	}

	@Override
	public boolean isStructureKnown() throws ModelException {
		return true;
	}

	@Override
	protected void toStringInfo(int tab, StringBuffer buffer, Object info,
			boolean showResolvedInfo) {
		buffer.append(tabString(tab));
		toStringName(buffer);
	}

}
