package org.eclipse.dltk.validators.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;

/**
 * Abstract base class that may be used to validate <code>IResource</code>
 * objects contained within an <code>IScriptProject</code>.
 */
public abstract class ResourceValidatorWorker extends AbstractValidatorWorker
		implements IResourceValidator {

	@Override
	public final void clean(IResource[] resources) {
		super.clean(resources);
	}

	public final IStatus validate(IResource[] resources,
			IValidatorOutput output, IProgressMonitor monitor) {
		return doValidate(resources, output, monitor);
	}

	/**
	 * Returns <code>true</code> if the resource can be acted upon by the
	 * validator, <code>false</code> otherwise.
	 */
	protected abstract boolean isValidResource(IResource resource);

	/**
	 * Executes the validator against the given resource
	 */
	protected abstract void runValidator(IResource resource,
			IValidatorOutput console, IValidatorReporter reporter,
			IProgressMonitor monitor) throws CoreException;

	@Override
	protected final IResource getResource(Object object) {
		return (IResource) object;
	}

	@Override
	protected final boolean isValidResource(Object object) {
		return isValidResource((IResource) object);
	}

	@Override
	protected final void runValidator(Object object, IValidatorOutput console,
			IValidatorReporter reporter, IProgressMonitor monitor)
			throws CoreException {
		runValidator((IResource) object, console, reporter, monitor);
	}
}
