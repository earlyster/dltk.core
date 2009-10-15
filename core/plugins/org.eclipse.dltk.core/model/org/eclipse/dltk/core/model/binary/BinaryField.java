package org.eclipse.dltk.core.model.binary;

import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.utils.CorePrinter;

/**
 * @since 2.0
 */
public class BinaryField extends BinaryMember implements IField {

	public BinaryField(ModelElement parent, String name) {
		super(parent, name);
	}

	@Override
	public void printNode(CorePrinter output) {
	}

	public int getElementType() {
		return FIELD;
	}

	public boolean equals(Object o) {
		if (!(o instanceof BinaryField)) {
			return false;
		}
		return super.equals(o);
	}

	public String getFullyQualifiedName(String enclosingTypeSeparator) {
		try {
			return getFullyQualifiedName(enclosingTypeSeparator, false/*
																	 * don't
																	 * show
																	 * parameters
																	 */);
		} catch (ModelException e) {
			// exception thrown only when showing parameters
			return null;
		}
	}

	public String getFullyQualifiedName() {
		return getFullyQualifiedName("$"); //$NON-NLS-1$
	}

	public String getType() throws ModelException {
		BinaryFieldElementInfo info = (BinaryFieldElementInfo) getElementInfo();
		if (info != null) {
			return info.getType();
		}
		return null;
	}
}
