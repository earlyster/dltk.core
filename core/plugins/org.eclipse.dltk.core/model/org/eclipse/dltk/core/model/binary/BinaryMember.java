package org.eclipse.dltk.core.model.binary;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.NamedMember;

/**
 * @since 2.0
 */
public abstract class BinaryMember extends NamedMember {
	private int flags;
	private List<IModelElement> children = new ArrayList<IModelElement>();

	public BinaryMember(ModelElement parent, String name) {
		super(parent, name);
	}

	@Override
	protected char getHandleMementoDelimiter() {
		return JEM_USER_ELEMENT;
	}

	@Override
	public ISourceRange getSourceRange() throws ModelException {
		return super.getSourceRange();
	}

	public SourceMapper getSourceMapper() {
		IModelElement module = getAncestor(SOURCE_MODULE);
		if (module instanceof BinaryModule) {
			return ((BinaryModule) module).getSourceMapper();
		}
		return null;
	}

	@Override
	public int getFlags() throws ModelException {
		return flags;
	}

	void setFlags(int flags) {
		this.flags = flags;
	}

	@Override
	public IModelElement[] getChildren(IProgressMonitor monitor)
			throws ModelException {
		return children.toArray(new IModelElement[children.size()]);
	}

	public void addChild(IModelElement element) {
		this.children.add(element);
	}

	public void removeChild(IModelElement element) {
		this.children.remove(element);
	}
}
