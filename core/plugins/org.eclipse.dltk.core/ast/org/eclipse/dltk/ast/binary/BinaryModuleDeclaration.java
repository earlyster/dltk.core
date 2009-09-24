package org.eclipse.dltk.ast.binary;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.model.binary.IBinaryModule;

/**
 * @since 2.0
 */
public class BinaryModuleDeclaration extends ModuleDeclaration {

	private IBinaryModule module;

	public BinaryModuleDeclaration(IBinaryModule module) {
		super(0);
		this.module = module;
		// Fill in top level structures
		IModelElement[] children;
		try {
			children = module.getChildren();
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
		} catch (ModelException e) {
			e.printStackTrace();
		}
	}

	public IBinaryModule getBinaryModule() {
		return this.module;
	}

	@Override
	public boolean isEmpty() {
		try {
			return !module.hasChildren();
		} catch (ModelException e) {
			return false;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BinaryModuleDeclaration) {
			return module.equals(((BinaryModuleDeclaration) obj).module);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.module.hashCode();
	}
}
