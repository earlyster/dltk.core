package org.eclipse.dltk.ast.binary;

import java.util.List;

import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;

/**
 * @since 2.0
 */
public class BinaryElementFactory {
	private BinaryElementIndexer indexer = new BinaryElementIndexer();

	public void processModelElements(IModelElement[] children, List statements) {
		int end = 0;
		for (IModelElement element : children) {
			switch (element.getElementType()) {
			case IModelElement.TYPE:
				statements.add(createTypeElement((IType) element));
				break;
			case IModelElement.METHOD:
				statements.add(createMethodElement((IMethod) element));
				break;
			case IModelElement.FIELD:
				BinaryField field = createFieldElement((IField) element);
				statements.add(field);
				// Process field statements
				processReferences(field, statements);
				break;
			}
		}
	}

	protected BinaryField createFieldElement(IField element) {
		return new BinaryField((IField) element, this);
	}

	protected BinaryMethod createMethodElement(IMethod element) {
		return new BinaryMethod((IMethod) element, this);
	}

	protected BinaryType createTypeElement(IType element) {
		return new BinaryType((IType) element, this);
	}

	public BinaryElementIndexer getIndexer() {
		return indexer;
	}

	public int nextIndex() {
		return indexer.getIndex();
	}

	public void processReferences(BinaryType type, List statements) {
	}

	public void processReferences(BinaryField field, List statements) {
	}

	public void processReferences(BinaryMethod method, List statements) {
	}

	public void processReferences(
			BinaryModuleDeclaration binaryModuleDeclaration) {
	}
}
