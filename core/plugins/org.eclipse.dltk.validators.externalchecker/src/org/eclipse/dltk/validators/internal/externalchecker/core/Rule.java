package org.eclipse.dltk.validators.internal.externalchecker.core;

import org.eclipse.dltk.validators.core.IValidatorProblem;

public class Rule {
	private String rule;
	private String type;

	public Rule(String rule, String type) {
		this.rule = rule;
		this.setType(type);
	}

	public String getDescription() {
		return rule;
	}

	public void setDescription(String s) {
		this.rule = s;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public IValidatorProblem.Type getProblemType() {
		if (type.indexOf(Messages.ExternalChecker_error) != -1) {
			return IValidatorProblem.Type.ERROR;
		}

		if (type.indexOf(Messages.ExternalChecker_warning) != -1) {
			return IValidatorProblem.Type.WARN;
		}

		return IValidatorProblem.Type.INFO;
	}
}
