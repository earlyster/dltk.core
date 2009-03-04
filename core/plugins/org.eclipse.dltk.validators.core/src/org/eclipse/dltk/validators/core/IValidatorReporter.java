package org.eclipse.dltk.validators.core;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.ISourceModule;

public interface IValidatorReporter {

	IMarker report (IResource resource, IValidatorProblem problem) throws CoreException;
	
	IMarker report (ISourceModule module, IValidatorProblem problem) throws CoreException;
}
