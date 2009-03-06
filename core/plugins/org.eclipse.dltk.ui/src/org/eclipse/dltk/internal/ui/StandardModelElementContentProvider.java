/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.internal.ui;

/**
 * A base content provider for Java elements. It provides access to the Java
 * element hierarchy without listening to changes in the Java model. If updating
 * the presentation on Java model change is required than clients have to
 * subclass, listen to Java model changes and have to update the UI using
 * corresponding methods provided by the JFace viewers or their own UI
 * presentation.
 * <p>
 * The following Java element hierarchy is surfaced by this content provider:
 * <p>
 * 
 * <pre>
 *  Java model (
 * &lt;code&gt;
 * IJavaModel
 * &lt;/code&gt;
 * )
 *  Java project (
 * &lt;code&gt;
 * IJavaProject
 * &lt;/code&gt;
 * )
 *  package fragment root (
 * &lt;code&gt;
 * IPackageFragmentRoot
 * &lt;/code&gt;
 * )
 *  package fragment (
 * &lt;code&gt;
 * IPackageFragment
 * &lt;/code&gt;
 * )
 *  compilation unit (
 * &lt;code&gt;
 * ICompilationUnit
 * &lt;/code&gt;
 * )
 *  binary class file (
 * &lt;code&gt;
 * IClassFile
 * &lt;/code&gt;
 * )
 * </pre>
 * 
 * </p>
 * <p>
 * Note that when the entire Java project is declared to be package fragment
 * root, the corresponding package fragment root element that normally appears
 * between the Java project and the package fragments is automatically filtered
 * out.
 * </p>
 * 
 * @deprecated use this class from the public API
 */
public class StandardModelElementContentProvider extends
		org.eclipse.dltk.ui.StandardModelElementContentProvider {

	/**
	 * Creates a new content provider. The content provider does not provide
	 * members of compilation units or class files.
	 */
	public StandardModelElementContentProvider() {
		super();
	}

	/**
	 * Creates a new <code>StandardModelElementContentProvider</code>.
	 * 
	 * @param provideMembers
	 *            if <code>true</code> members below compilation units and class
	 *            files are provided.
	 */
	public StandardModelElementContentProvider(boolean provideMembers) {
		super(provideMembers);
	}

}
