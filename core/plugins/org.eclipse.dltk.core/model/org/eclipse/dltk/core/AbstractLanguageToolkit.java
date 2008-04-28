package org.eclipse.dltk.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.environment.IEnvironment;

public abstract class AbstractLanguageToolkit implements IDLTKLanguageToolkit {
	public AbstractLanguageToolkit() {
	}
	
	public boolean languageSupportZIPBuildpath() {
		return false;
	}

	public boolean validateSourcePackage(IPath path, IEnvironment environment) {
		return true;
	}

	public IType[] getParentTypes(IType type) {
		return null;
	}
	public IStatus validateSourceModule(IResource resource) {
		return Status.OK_STATUS; 
	}
}
