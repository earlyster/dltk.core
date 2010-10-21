package org.eclipse.dltk.core;

/**
 * @since 3.0
 */
public interface INamespace {

	/**
	 * Returns the parts of this namespace
	 * 
	 * @return
	 */
	String[] getStrings();

	/**
	 * Returns the qualified name of this namespace. This consists of the simple
	 * names, separated by <code>'$'</code>. This is a handle-only method.
	 * 
	 * @return the qualified name of this namespace
	 */
	String getQualifiedName();

	/**
	 * Returns the qualified name of this namespace. This consists of the simple
	 * names, separated by <code>separator</code>. This is a handle-only method.
	 * 
	 * @param separator
	 *            the specified enclosing type separator
	 * @return the qualified name of this package
	 * 
	 */
	String getQualifiedName(String separator);

	boolean isRoot();
}
