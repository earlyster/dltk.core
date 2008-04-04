package org.eclipse.dltk.core.environment;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;

public interface IEnvironment extends IAdaptable {
	IFileHandle getFile(IPath path);
	String getId();
	String getSeparator();
	char getSeparatorChar();
	String getName();
	boolean hasProject(IProject project);
	String convertPathToString(IPath path);
	URI getURI(IPath location);
}
