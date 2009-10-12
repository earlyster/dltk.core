package org.eclipse.dltk.core.model.binary;

import org.eclipse.dltk.internal.core.ModelElementInfo;

/**
 * @since 2.0
 */
public class BinaryMemberInfo extends ModelElementInfo {
	private int flags;

	public void setFlags(int flags) {
		this.flags = flags;
	}

	public int getFlags() {
		return flags;
	}
}
