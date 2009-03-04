package org.eclipse.dltk.validators.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import org.eclipse.dltk.validators.internal.core.ValidatorsCore;

abstract class AbstractValidatorWorker {

	/**
	 * Returns the problem marker id
	 */
	protected abstract String getMarkerId();

	/**
	 * Returns the message that will be logged when the <code>IResource</code>
	 * of the object being validated is null.
	 */
	protected abstract String getNullResourceMessage();

	/**
	 * Returns the plugin id of the validator worker implementation.
	 */
	protected abstract String getPluginId();

	/**
	 * Returns the task name that will be passed to the progress monitor once
	 * the validation begins.
	 */
	protected abstract String getTaskName();

	/**
	 * Calculates the total work that will be performed for the progress
	 * monitor.
	 * 
	 * <p>
	 * Default implementation returns the number of objects in the array.
	 * Sub-classes may override this method if they wish to provide a different
	 * calculation.
	 * </p>
	 */
	protected int calcTotalWork(Object[] objects) {
		return objects.length;
	}

	protected void clean(IResource[] resources) {
		final String markerType = getMarkerId();
		// TODO execute single operation via IWorkspaceRunnable ?
		for (int i = 0; i < resources.length; ++i) {
			final IResource resource = resources[i];
			clean(resource, markerType);
		}
	}

	protected void clean(final IResource resource) {
		clean(resource, getMarkerId());
	}

	protected void clean(final IResource resource, final String markerType) {
		try {
			resource.deleteMarkers(markerType, true, IResource.DEPTH_INFINITE);
		} catch (CoreException e) {
			ValidatorsCore.log(e.getStatus());
		}
	}

	/**
	 * Creates an instance of an <code>IValidatorReporter</code>.
	 * 
	 * <p>
	 * Sub-classes are free to override in order to provide their own
	 * implementation.
	 * </p>
	 */
	protected IValidatorReporter createValidatorReporter() {
		return new ValidatorReporter(getMarkerId());
	}

	/**
	 * Returns <code>true</code> if the validator is configured,
	 * <code>false</code> otherwise.
	 * 
	 * <p>
	 * Default returns <code>true</code>. Sub-classes are free to override to
	 * provide any implementation specific checks.
	 * </p>
	 */
	protected boolean isValidatorConfigured() {
		return true;
	}

	abstract IResource getResource(Object resource);

	abstract boolean isValidResource(Object object);

	abstract void runValidator(Object object, IValidatorOutput console,
			IValidatorReporter reporter, IProgressMonitor monitor)
			throws CoreException;

	final IStatus doValidate(Object[] objects, IValidatorOutput console,
			IProgressMonitor monitor) {
		if (!isValidatorConfigured()) {
			// don't bother continuing if we're not properly configured
			return Status.CANCEL_STATUS;
		}

		IValidatorReporter reporter = createValidatorReporter();
		// so it begins... ;)
		monitor.beginTask(getTaskName(), calcTotalWork(objects));

		try {
			for (int i = 0; i < objects.length; i++) {
				if (monitor.isCanceled()) {
					return Status.CANCEL_STATUS;
				}

				if (getResource(objects[i]) == null) {
					// XXX: make this an error level instead?
					IStatus status = new Status(IStatus.WARNING, getPluginId(),
							getNullResourceMessage());
					ValidatorsCore.log(status);
				} else {
					validate(objects[i], reporter, console, monitor);
				}

				monitor.worked(1);
			}
		} finally {
			monitor.done();
		}

		return Status.OK_STATUS;
	}

	private void validate(Object object, IValidatorReporter reporter,
			IValidatorOutput console, IProgressMonitor monitor) {
		if (!isValidResource(object)) {
			return;
		}

		IResource resource = getResource(object);
		clean(resource);

		try {
			runValidator(object, console, reporter, monitor);
		} catch (CoreException e) {
			ValidatorsCore.log(e.getStatus());
		}
	}
}
