package org.eclipse.dltk.ast.binary;

import org.eclipse.dltk.ast.ASTListNode;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;

/**
 * @since 2.0
 */
public class BinaryType extends TypeDeclaration {

	private IType element;

	public BinaryType(IType type, BinaryElementIndexer indexer) {
		super(type.getElementName(), 0, 0, 0, indexer.getIndex());
		try {
			ISourceRange nameRange = type.getNameRange();
			setNameStart(nameRange.getOffset());
			setNameEnd(nameRange.getOffset() + nameRange.getLength());
		} catch (ModelException e1) {
			DLTKCore.error(e1);
		}
		this.element = type;
		IModelElement[] children;
		try {
			children = element.getChildren();
			for (IModelElement element : children) {
				switch (element.getElementType()) {
				case IModelElement.TYPE:
					getStatements().add(
							new BinaryType((IType) element, indexer));
					break;
				case IModelElement.METHOD:
					getStatements().add(
							new BinaryMethod((IMethod) element, indexer));
					break;
				case IModelElement.FIELD:
					getStatements().add(
							new BinaryField((IField) element, indexer));
					break;
				}
			}
			setModifiers(element.getFlags());
			String[] superClasses = element.getSuperClasses();
			ASTListNode supers = new ASTListNode();
			if (superClasses != null) {
				for (String superName : superClasses) {
					supers.addNode(new SimpleReference(0, 0, superName));
				}
			}
			setSuperClasses(supers);
		} catch (ModelException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BinaryType) {
			return element.equals(((BinaryType) obj).element);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.element.hashCode();
	}
}
