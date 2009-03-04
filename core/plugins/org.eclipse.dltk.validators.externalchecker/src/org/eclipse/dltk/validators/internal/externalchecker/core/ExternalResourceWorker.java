package org.eclipse.dltk.validators.internal.externalchecker.core;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.validators.core.IValidatorOutput;
import org.eclipse.dltk.validators.core.IValidatorProblem;
import org.eclipse.dltk.validators.core.IValidatorReporter;
import org.eclipse.dltk.validators.core.ResourceValidatorWorker;

public class ExternalResourceWorker extends ResourceValidatorWorker {

	private ExternalCheckerDelegate delegate;

	public ExternalResourceWorker(IEnvironment environment,
			ExternalChecker externalChecker) {
		delegate = new ExternalCheckerDelegate(environment, externalChecker);
	}

	protected boolean isValidResource(IResource resource) {
		return delegate.isValidExtension(resource.getFileExtension());
	}

	protected void runValidator(final IResource resource,
			IValidatorOutput console, final IValidatorReporter reporter,
			IProgressMonitor monitor) throws CoreException {

		delegate.runValidator(resource, console,
				new ExternalCheckerDelegate.IExternalReporterDelegate() {
					public void report(IValidatorProblem problem)
							throws CoreException {
						reporter.report(resource, problem);
					}
				});
	}
	
	protected IValidatorReporter createValidatorReporter() {
		return delegate.createValidatorReporter();
	}

	protected String getTaskName() {
		return Messages.ExternalChecker_checkingWithExternalExecutable;
	}

	protected String getMarkerId() {
		return delegate.getMarkerId();
	}

	protected String getNullResourceMessage() {
		return Messages.ExternalChecker_resourceIsNull;
	}

	protected String getPluginId() {
		return ExternalCheckerPlugin.PLUGIN_ID;
	}
}
