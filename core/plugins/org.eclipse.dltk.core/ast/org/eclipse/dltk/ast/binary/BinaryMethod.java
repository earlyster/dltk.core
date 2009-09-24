package org.eclipse.dltk.ast.binary;

import org.eclipse.dltk.ast.declarations.Argument;
import org.eclipse.dltk.ast.declarations.MethodDeclaration;
import org.eclipse.dltk.ast.references.SimpleReference;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;

/**
 * @since 2.0
 */
public class BinaryMethod extends MethodDeclaration {

	private IMethod element;

	public BinaryMethod(IMethod type) {
		super(type.getElementName(), 0, 0, 0, 0);
		this.element = type;
		IModelElement[] children;
		try {
			children = element.getChildren();
			for (IModelElement element : children) {
				switch (element.getElementType()) {
				case IModelElement.TYPE:
					getStatements().add(new BinaryType((IType) element));
					break;
				case IModelElement.METHOD:
					getStatements().add(new BinaryMethod((IMethod) element));
					break;
				case IModelElement.FIELD:
					getStatements().add(new BinaryField((IField) element));
					break;
				}
			}
			String[] parameters = element.getParameters();
			for (String paramName : parameters) {
				addArgument(new Argument(new SimpleReference(0, 0, paramName),
						0, null, 0));
			}
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
}
