package org.eclipse.dltk.core.model.binary;

import java.io.InputStream;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.CompletionRequestor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.IScriptFolder;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ITypeHierarchy;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.WorkingCopyOwner;
import org.eclipse.dltk.internal.core.ModelElement;
import org.eclipse.dltk.utils.CorePrinter;

/**
 * @since 2.0
 */
public class BinaryType extends BinaryMember implements IType, IParent {
	private static final IField[] NO_FIELDS = new IField[0];
	private static final IMethod[] NO_METHODS = new IMethod[0];
	private static final IType[] NO_TYPES = new IType[0];
	private String[] superclasses;

	public BinaryType(ModelElement parent, String name) {
		super(parent, name);
	}

	public IScriptFolder getScriptFolder() {
		return (IScriptFolder) getAncestor(IModelElement.SCRIPT_FOLDER);
	}

	@Override
	public void printNode(CorePrinter output) {
	}

	public int getElementType() {
		return TYPE;
	}

	public void codeComplete(char[] snippet, int insertion, int position,
			char[][] localVariableTypeNames, char[][] localVariableNames,
			int[] localVariableModifiers, boolean isStatic,
			CompletionRequestor requestor) throws ModelException {
	}

	public void codeComplete(char[] snippet, int insertion, int position,
			char[][] localVariableTypeNames, char[][] localVariableNames,
			int[] localVariableModifiers, boolean isStatic,
			CompletionRequestor requestor, WorkingCopyOwner owner)
			throws ModelException {
	}

	public IMethod[] findMethods(IMethod method) {
		return null;
	}

	public IField getField(String name) {
		return new BinaryField(this, name);
	}

	public IField[] getFields() throws ModelException {
		List<IModelElement> list = getChildrenOfType(FIELD);
		int size;
		if ((size = list.size()) == 0) {
			return NO_FIELDS;
		} else {
			IField[] array = new IField[size];
			list.toArray(array);
			return array;
		}
	}

	public String getFullyQualifiedName(String enclosingTypeSeparator) {
		try {
			return getFullyQualifiedName(enclosingTypeSeparator, false);
		} catch (ModelException e) {
			DLTKCore.error(
					"Failed to return fully qualified name of binaryType", e);
		}
		return null;
	}

	public String getFullyQualifiedName() {
		return getFullyQualifiedName("$");
	}

	public IMethod getMethod(String name) {
		return new BinaryMethod(this, name);
	}

	public IMethod[] getMethods() throws ModelException {
		List<IModelElement> list = getChildrenOfType(METHOD);
		int size;
		if ((size = list.size()) == 0) {
			return NO_METHODS;
		} else {
			IMethod[] array = new IMethod[size];
			list.toArray(array);
			return array;
		}
	}

	public String[] getSuperClasses() throws ModelException {
		return superclasses;
	}

	public IType getType(String name) {
		return null;
	}

	public String getTypeQualifiedName() {
		return this.getTypeQualifiedName("$");
	}

	public String getTypeQualifiedName(String enclosingTypeSeparator) {
		try {
			return getTypeQualifiedName(enclosingTypeSeparator, false);
		} catch (ModelException e) {
			DLTKCore.error("Failed to retrive type qualifier", e);
		}
		return null;
	}

	public IType[] getTypes() throws ModelException {
		List<IModelElement> list = getChildrenOfType(TYPE);
		int size;
		if ((size = list.size()) == 0) {
			return NO_TYPES;
		} else {
			IType[] array = new IType[size];
			list.toArray(array);
			return array;
		}
	}

	public ITypeHierarchy loadTypeHierachy(InputStream input,
			IProgressMonitor monitor) throws ModelException {
		return null;
	}

	public ITypeHierarchy newSupertypeHierarchy(IProgressMonitor monitor)
			throws ModelException {
		return null;
	}

	public ITypeHierarchy newSupertypeHierarchy(ISourceModule[] workingCopies,
			IProgressMonitor monitor) throws ModelException {
		return null;
	}

	public ITypeHierarchy newSupertypeHierarchy(WorkingCopyOwner owner,
			IProgressMonitor monitor) throws ModelException {
		return null;
	}

	public ITypeHierarchy newTypeHierarchy(IScriptProject project,
			IProgressMonitor monitor) throws ModelException {
		return null;
	}

	public ITypeHierarchy newTypeHierarchy(IScriptProject project,
			WorkingCopyOwner owner, IProgressMonitor monitor)
			throws ModelException {
		return null;
	}

	public ITypeHierarchy newTypeHierarchy(IProgressMonitor monitor)
			throws ModelException {
		return null;
	}

	public ITypeHierarchy newTypeHierarchy(ISourceModule[] workingCopies,
			IProgressMonitor monitor) throws ModelException {
		return null;
	}

	public ITypeHierarchy newTypeHierarchy(WorkingCopyOwner owner,
			IProgressMonitor monitor) throws ModelException {
		return null;
	}

	void setSuperclassNames(String[] superclasses) {
		this.superclasses = superclasses;
	}
}
