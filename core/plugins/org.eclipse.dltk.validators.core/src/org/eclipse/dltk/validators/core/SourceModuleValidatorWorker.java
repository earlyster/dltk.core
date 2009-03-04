package org.eclipse.dltk.validators.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.ISourceModule;

/**
 * Abstract base class that may be used to validate <code>ISourceModule</code>
 * objects contained within an <code>IScriptProject</code>.
 */
public abstract class SourceModuleValidatorWorker extends
		AbstractValidatorWorker implements ISourceModuleValidator {

	public final void clean(ISourceModule[] modules) {
		final List<IResource> resources = new ArrayList<IResource>(
				modules.length);
		for (int i = 0; i < modules.length; ++i) {
			final IResource resource = modules[i].getResource();
			if (resource != null) {
				resources.add(resource);
			}
		}

		final IResource[] results = new IResource[resources.size()];
		resources.toArray(results);

		clean(results);
	}

	public final IStatus validate(ISourceModule[] modules,
			IValidatorOutput console, IProgressMonitor monitor) {
		return doValidate(modules, console, monitor);
	}

	/**
	 * Returns <code>true</code> if the source module can be acted upon by the
	 * validator, <code>false</code> otherwise.
	 */
	protected abstract boolean isValidSourceModule(ISourceModule module);

	/**
	 * Executes the validator against the given source module
	 */
	protected abstract void runValidator(ISourceModule module,
			IValidatorOutput console, IValidatorReporter reporter,
			IProgressMonitor monitor) throws CoreException;

	@Override
	protected final IResource getResource(Object object) {
		return ((ISourceModule) object).getResource();
	}

	@Override
	protected final boolean isValidResource(Object object) {
		return isValidSourceModule((ISourceModule) object);
	}

	@Override
	protected final void runValidator(Object object, IValidatorOutput console,
			IValidatorReporter reporter, IProgressMonitor monitor)
			throws CoreException {
		runValidator((ISourceModule) object, console, reporter, monitor);
	}
}
