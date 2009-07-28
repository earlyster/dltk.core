package org.eclipse.dltk.validators.ui;

import org.eclipse.dltk.validators.core.AbstractValidateJob;
import org.eclipse.dltk.validators.core.IValidatorOutput;

public abstract class AbstractConsoleValidateJob extends AbstractValidateJob {

	public AbstractConsoleValidateJob(String jobName) {
		super(jobName);
	}

	@Override
	protected IValidatorOutput createOutput() {
		if (isConsoleRequired()) {
			return new ConsoleValidatorOutput(getName());
		}
		return super.createOutput();
	}

	protected boolean isConsoleRequired() {
		return true;
	}
}
