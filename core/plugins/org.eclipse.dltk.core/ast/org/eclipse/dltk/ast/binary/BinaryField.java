package org.eclipse.dltk.ast.binary;

import org.eclipse.dltk.ast.declarations.FieldDeclaration;
import org.eclipse.dltk.core.IField;

/**
 * @since 2.0
 */
public class BinaryField extends FieldDeclaration {

	private IField element;

	public BinaryField(IField type) {
		super(type.getElementName(), 0, 0, 0, 0);
		this.element = type;
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
}
