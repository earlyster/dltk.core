package org.eclipse.dltk.ast.binary;

import org.eclipse.dltk.ast.ASTListNode;
import org.eclipse.dltk.ast.declarations.TypeDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.ast.references.TypeReference;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;

/**
 * @since 2.0
 */
public class BinaryType extends TypeDeclaration {

	private IType element;

	public BinaryType(IType type, BinaryElementFactory factory) {
		super(type.getElementName(), 0, 0, factory.nextIndex(), factory
				.nextIndex());
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
			factory.processModelElements(children, getStatements());
			setModifiers(element.getFlags());
			String[] superClasses = element.getSuperClasses();
			ASTListNode supers = new ASTListNode();
			if (superClasses != null) {
				for (String superName : superClasses) {
					SimpleReference r = new SimpleReference(
							factory.nextIndex(), factory.nextIndex(), superName);
					supers.addNode(r);
					// Super class reference
					getStatements().add(
							new TypeReference(r.sourceStart(), r.sourceEnd(),
									superName));
				}
			}
			setSuperClasses(supers);
			factory.processReferences(this, getStatements());
			setEnd(factory.getIndexer().getCurrent());
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

	public IModelElement getElement() {
		return element;
	}
}
