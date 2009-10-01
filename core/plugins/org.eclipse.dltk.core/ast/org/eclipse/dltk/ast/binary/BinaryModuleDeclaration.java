package org.eclipse.dltk.ast.binary;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.model.binary.IBinaryModule;

/**
 * @since 2.0
 * 
 *        Creates all children elements in constructor. Preserves logical
 *        positioning of elements
 */
public class BinaryModuleDeclaration extends ModuleDeclaration {

	private IBinaryModule module;

	public BinaryModuleDeclaration(IBinaryModule module,
			BinaryElementFactory factory) {
		super(0);
		this.module = module;
		// Fill in top level structures
		IModelElement[] children;
		try {
			children = module.getChildren();
			factory.processModelElements(children, getStatements());
			factory.processReferences(this);
			setEnd(factory.getIndexer().getCurrent());
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
