package org.eclipse.dltk.core.model.binary;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.WorkingCopyOwner;
import org.eclipse.dltk.internal.core.MementoModelElementUtil;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.internal.core.NamedMember;
import org.eclipse.dltk.internal.core.SourceRange;
import org.eclipse.dltk.internal.core.util.MementoTokenizer;

/**
 * @since 2.0
 */
public abstract class BinaryMember extends NamedMember implements
		ISourceMapperProvider {

	public BinaryMember(ModelElement parent, String name) {
		super(parent, name);
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	protected char getHandleMementoDelimiter() {
		return JEM_USER_ELEMENT;
	}

	@Override
	public ISourceRange getSourceRange() throws ModelException {
		final SourceMapper mapper = getSourceMapper();
		if (mapper != null) {
			return mapper.getSourceRange(this);
		}
		return new SourceRange(0, 0);
	}

	@Override
	public ISourceRange getNameRange() throws ModelException {
		final SourceMapper mapper = getSourceMapper();
		if (mapper != null) {
			return mapper.getNameRange(this);
		}
		return new SourceRange(0, 0);
	}

	public SourceMapper getSourceMapper() {
		IModelElement parent = getParent();
		if (parent instanceof ISourceMapperProvider) {
			return ((ISourceMapperProvider) parent).getSourceMapper();
		}
		return null;
	}

	@Override
	public int getFlags() throws ModelException {
		return ((BinaryMemberInfo) getElementInfo()).getFlags();
	}

	public IModelElement getHandleFromMemento(String token,
			MementoTokenizer memento, WorkingCopyOwner workingCopyOwner) {
		switch (token.charAt(0)) {
		case JEM_USER_ELEMENT:
			return MementoModelElementUtil.getHandleFromMemento(memento, this,
					workingCopyOwner);
		}

		return null;
	}
}
