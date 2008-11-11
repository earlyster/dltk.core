package org.eclipse.dltk.ui.text.rules;

import org.eclipse.jface.text.rules.IWordDetector;

/**
 * Defines the interface by which <code>WordRule</code> determines whether a
 * given character is valid as part of a script word in the current context.
 */
public interface IScriptWordDetector extends IWordDetector {

	/**
	 * Returns <code>true</code> if the character prior to the word start
	 * character is valid for the word to match.
	 * 
	 * <p>
	 * For instance, this can be used to prevent a method name invocation that
	 * also matches a builtin name from being matched.
	 * </p>
	 */
	boolean isPriorCharValid(char c);
}
