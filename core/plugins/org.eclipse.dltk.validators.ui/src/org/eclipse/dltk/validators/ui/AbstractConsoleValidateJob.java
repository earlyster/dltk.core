package org.eclipse.dltk.validators.ui;

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.dltk.validators.core.AbstractValidateJob;
import org.eclipse.dltk.validators.core.IValidatorOutput;
import org.eclipse.osgi.util.NLS;

public abstract class AbstractConsoleValidateJob extends AbstractValidateJob {

	public AbstractConsoleValidateJob(String jobName) {
		super(jobName);
	}

	protected IValidatorOutput createOutput() {
		if (isConsoleRequired()) {
			return new ConsoleValidatorOutput(getConsoleName());
		}
		return super.createOutput();
	}

	protected String getConsoleName() {
		final String timestamp = DateFormat.getDateTimeInstance(
				DateFormat.MEDIUM, DateFormat.MEDIUM).format(
				new Date(System.currentTimeMillis()));
		final String message = Messages.AbstractValidateSelectionWithConsole_dltkValidatorOutput;
		return NLS.bind(message, getName(), timestamp);
	}

	protected boolean isConsoleRequired() {
		return true;
	}
}
