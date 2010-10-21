package org.eclipse.dltk.internal.core;

import java.util.Arrays;

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.INamespace;

public class SourceNamespace implements INamespace {

	private final String[] segments;

	public SourceNamespace(String[] namespace) {
		if (namespace == null || namespace.length == 0) {
			this.segments = CharOperation.NO_STRINGS;
		} else {
			this.segments = new String[namespace.length];
			System.arraycopy(namespace, 0, this.segments, 0, namespace.length);
		}
	}

	public String[] getStrings() {
		final String[] result = new String[segments.length];
		System.arraycopy(this.segments, 0, result, 0, segments.length);
		return result;
	}

	public String getQualifiedName() {
		return getQualifiedName("$");
	}

	public String getQualifiedName(String separator) {
		return new String(CharOperation.concatWith(segments, separator));
	}

	public boolean isRoot() {
		return segments.length == 0;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(segments);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SourceNamespace) {
			final SourceNamespace other = (SourceNamespace) obj;
			return Arrays.equals(segments, other.segments);
		}
		return false;
	}

	@Override
	public String toString() {
		return Arrays.toString(segments);
	}

}
