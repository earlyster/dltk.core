package org.eclipse.dltk.core.environment;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

public interface IEnvironment extends IAdaptable {
	IFileHandle getFile(IPath path);
	String getId();
	String getSeparator();
	char getSeparatorChar();
	String getName();
}
