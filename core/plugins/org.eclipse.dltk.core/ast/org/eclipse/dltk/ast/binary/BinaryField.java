package org.eclipse.dltk.ast.binary;

import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;

/**
 * @since 2.0
 */
public class BinaryField extends FieldDeclaration {

	private IField element;

	public BinaryField(IField type, BinaryElementFactory factory) {
		super(type.getElementName(), 0, 0, factory.nextIndex(), factory
				.nextIndex());
		this.element = type;
		try {
			ISourceRange nameRange = type.getNameRange();
			setNameStart(nameRange.getOffset());
			setNameEnd(nameRange.getOffset() + nameRange.getLength());
			setModifiers(element.getFlags());
		} catch (ModelException e1) {
			DLTKCore.error(e1);
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BinaryField) {
			return element.equals(((BinaryField) obj).element);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.element.hashCode();
	}

	public IModelElement getElement() {
		return element;
	}
}
