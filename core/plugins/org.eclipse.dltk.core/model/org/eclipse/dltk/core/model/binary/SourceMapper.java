package org.eclipse.dltk.core.model.binary;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.compiler.IElementRequestor.FieldInfo;
import org.eclipse.dltk.compiler.IElementRequestor.MethodInfo;
import org.eclipse.dltk.compiler.IElementRequestor.TypeInfo;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.internal.core.SourceRange;

/**
 * @since 2.0
 */
public class SourceMapper {
	private static class Range {
		int start;
		int end;
	}

	private Map<IModelElement, Range> sourceRanges = new HashMap<IModelElement, Range>();

	private Map<IModelElement, Range> typeNameRanges = new HashMap<IModelElement, Range>();

	private ISourceRange getRange(Range range) {
		return new SourceRange(range.start, range.end - range.start + 1);
	}

	public ISourceRange getSourceRange(IModelElement element) {
		return getRange(sourceRanges.get(element));
	}

	public ISourceRange getNameRange(IModelElement element) {
		return getRange(sourceRanges.get(element));
	}

	void reportType(TypeInfo info, IType type) {

	}

	void reportField(FieldInfo info, IField field) {

	}

	void reportMethod(MethodInfo info, IMethod mehtod) {

	}

	public void setRangeEnd(IModelElement element, int declarationEnd) {
	}

}
