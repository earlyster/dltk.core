package org.eclipse.dltk.validators.core;

import java.util.HashMap;
import java.util.Map;

public class ValidatorProblem implements IValidatorProblem {

	private Map<String, Object> attributes = new HashMap<String, Object>();

	private String fileName;

	private int lineNo;
	private String message;

	private IValidatorProblem.Type probType;

	public ValidatorProblem(String fileName, String message, int lineNo,
			Type probType) {
		this.fileName = fileName;
		this.message = message;
		this.lineNo = lineNo;
		this.probType = probType;
	}

	public void addAttribute(String key, Object value) {
		attributes.put(key, value);
	}

	public Map<String, Object> getAttributes() {
		return attributes;
	}

	public String getFileName() {
		return fileName;
	}

	public String getMessage() {
		return message;
	}

	public int getLineNumber() {
		return lineNo;
	}

	public boolean isError() {
		return (probType == IValidatorProblem.Type.ERROR) ? true : false;
	}

	public boolean isWarning() {
		return (probType == IValidatorProblem.Type.WARN) ? true : false;
	}
}
