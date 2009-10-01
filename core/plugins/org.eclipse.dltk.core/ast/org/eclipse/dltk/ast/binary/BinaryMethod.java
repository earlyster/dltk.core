package org.eclipse.dltk.ast.binary;

import org.eclipse.dltk.ast.declarations.Argument;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;

/**
 * @since 2.0
 */
public class BinaryMethod extends MethodDeclaration {

	private IMethod element;

	public BinaryMethod(IMethod type, BinaryElementFactory factory) {
		super(type.getElementName(), 0, 0, factory.nextIndex(), factory
				.nextIndex());
		this.element = type;
		try {
			ISourceRange nameRange = type.getNameRange();
			setNameStart(nameRange.getOffset());
			setNameEnd(nameRange.getOffset() + nameRange.getLength());
		} catch (ModelException e1) {
			DLTKCore.error(e1);
		}
		IModelElement[] children;
		try {
			children = element.getChildren();
			children = type.getChildren();
			String[] parameters = element.getParameters();
			for (String paramName : parameters) {
				addArgument(new Argument(new SimpleReference(factory
						.nextIndex(), factory.nextIndex(), paramName), 0, null,
						0));
			}
			factory.processModelElements(children, getStatements());
			factory.processReferences(this, getStatements());
			setEnd(factory.getIndexer().getCurrent());
		} catch (ModelException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BinaryMethod) {
			return element.equals(((BinaryMethod) obj).element);
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
