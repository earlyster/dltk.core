package org.eclipse.dltk.validators.core;

import java.util.Map;

import org.eclipse.core.resources.IMarker;

/**
 * Interface representing a validator problem.
 */
public interface IValidatorProblem {

	enum Type {
		ERROR, INFO, WARN
	}

	/**
	 * Adds additional user defined information to the problem.
	 * 
	 * <p>
	 * Attributes can be used to store additional information that can be used
	 * to provide quick fix resolutions, etc.
	 * </p>
	 * 
	 * @param name
	 *            attribute name
	 * @param value
	 *            attribute value
	 * 
	 * @see IMarker#setAttribute(String, Object)
	 */
	void addAttribute(String name, Object value);

	/**
	 * Returns the additional attributes that will be added to the resource
	 * marker.
	 */
	Map<String, Object> getAttributes();

	/**
	 * Returns the name of the file the resource occured in.
	 */
	String getFileName();

	/**
	 * Returns the line number the problem occured on
	 */
	int getLineNumber();

	/**
	 * Returns the problem description/message.
	 */
	String getMessage();

	/**
	 * Returns <code>true</code> if the problem represents an error.
	 */
	boolean isError();

	/**
	 * Returns <code>true</code> if the problem represents a warning.
	 */
	boolean isWarning();
}
