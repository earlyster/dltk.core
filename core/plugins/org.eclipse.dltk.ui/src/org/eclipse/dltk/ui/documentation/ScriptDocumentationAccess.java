/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.ui.documentation;

import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.ui.DLTKUIPlugin;

/**
 * Helper needed to get access to script documentation.
 * 
 * <p>
 * This class is not intended to be subclassed or instantiated by clients.
 * </p>
 */
public class ScriptDocumentationAccess {
	private static final String DOCUMENTATION_PROVIDERS_EXTENSION_POINT = "org.eclipse.dltk.ui.scriptDocumentationProviders"; //$NON-NLS-1$
	private static final String ATTR_CLASS = "class"; //$NON-NLS-1$
	private static final String ATTR_NATURE = "nature"; //$NON-NLS-1$
	private static IScriptDocumentationProvider[] documentationProviders = null;
	private static Map<IScriptDocumentationProvider, String> providerNatures = new HashMap<IScriptDocumentationProvider, String>();

	private ScriptDocumentationAccess() {
		// do not instantiate
	}

	/**
	 * Creates {@link IScriptDocumentationProvider} objects from configuration
	 * elements.
	 */
	private static IScriptDocumentationProvider[] createProviders(
			IConfigurationElement[] elements) {
		List<IScriptDocumentationProvider> result = new ArrayList<IScriptDocumentationProvider>(
				elements.length);
		for (int i = 0; i < elements.length; i++) {
			IConfigurationElement element = elements[i];
			try {
				IScriptDocumentationProvider pr = (IScriptDocumentationProvider) element
						.createExecutableExtension(ATTR_CLASS);
				result.add(pr);
				providerNatures.put(pr, element.getAttribute(ATTR_NATURE));
			} catch (CoreException e) {
				DLTKUIPlugin.log(e);
			}
		}
		return result.toArray(new IScriptDocumentationProvider[result.size()]);
	}

	/**
	 * Returns all contributed documentation documentationProviders.
	 */
	private static IScriptDocumentationProvider[] getContributedProviders() {
		if (documentationProviders == null) {
			IExtensionRegistry registry = Platform.getExtensionRegistry();
			IConfigurationElement[] elements = registry
					.getConfigurationElementsFor(DOCUMENTATION_PROVIDERS_EXTENSION_POINT);
			providerNatures.clear();
			documentationProviders = createProviders(elements);
		}
		return documentationProviders;
	}

	private static final int BUFF_SIZE = 2048;

	/**
	 * Gets a reader for an IMember documentation. Content are found using
	 * documentation documentationProviders, contributed via extension point.
	 * The content does contain HTML code describing member. It may be for ex.
	 * header comment or a man page. (if <code>allowExternal</code> is
	 * <code>true</code>)
	 * 
	 * @param member
	 *            The member to get documentation for.
	 * @param allowInherited
	 *            For procedures and methods: if member doesn't have it's own
	 *            documentation, look into parent types methods.
	 * @param allowExternal
	 *            Allows external documentation like man-pages.
	 * @return Reader for a content, or <code>null</code> if no documentation is
	 *         found.
	 * @throws ModelException
	 *             is thrown when the elements documentation can not be accessed
	 */
	public static Reader getHTMLContentReader(String nature, IMember member,
			boolean allowInherited, boolean allowExternal)
			throws ModelException {
		IScriptDocumentationProvider[] providers = getContributedProviders();
		StringBuffer buffer = new StringBuffer();
		char[] buff = null;
		boolean prev = false;
		for (int i = 0; i < providers.length; i++) {
			IScriptDocumentationProvider p = providers[i];
			String pNature = providerNatures.get(p);
			if (pNature == null || !pNature.equals(nature))
				continue;
			Reader reader = p.getInfo(member, allowInherited, allowExternal);
			if (reader != null) {
				int size = 0;
				if (buff == null) {
					buff = new char[BUFF_SIZE];
				}
				int len = 0;
				try {
					while ((len = reader.read(buff, 0, BUFF_SIZE)) != -1) {
						buffer.append(buff, 0, len);
					}
				} catch (IOException e) {
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				}
				if (prev && size > 0) {
					buffer.append("<hr/>"); //$NON-NLS-1$
				}
				if (size > 0) {
					prev = true;
				}
			}
		}
		if (buffer.length() > 0) {
			char[] content = new char[buffer.length()];
			buffer.getChars(0, buffer.length(), content, 0);
			return new CharArrayReader(content);
		}
		return null;
	}

	/**
	 * Gets a reader for an keyword documentation. Content are found using ALL
	 * documentation documentationProviders, contributed via extension point.
	 * The content does contain HTML code describing member.
	 * 
	 * @param content
	 *            The keyword to find.
	 * @return Reader for a content, or <code>null</code> if no documentation is
	 *         found.
	 * @throws ModelException
	 *             is thrown when the elements documentation can not be accessed
	 */
	public static Reader getHTMLContentReader(String nature, String content)
			throws ModelException {
		IScriptDocumentationProvider[] providers = getContributedProviders();
		StringBuffer buffer = new StringBuffer();
		char[] buff = null;
		boolean prev = false;
		for (int i = 0; i < providers.length; i++) {
			IScriptDocumentationProvider p = providers[i];
			String pNature = providerNatures.get(p);
			if (pNature == null || !pNature.equals(nature))
				continue;
			Reader reader = p.getInfo(content);
			int size = 0;
			if (reader != null) {
				if (buff == null) {
					buff = new char[BUFF_SIZE];
				}
				int len = 0;
				try {
					while ((len = reader.read(buff, 0, BUFF_SIZE)) != -1) {
						size += len;
						buffer.append(buff, 0, len);
					}
				} catch (IOException e) {
					if (DLTKCore.DEBUG) {
						e.printStackTrace();
					}
				}
			}
			if (prev && size > 0) {
				buffer.append("<hr/>"); //$NON-NLS-1$
			}
			if (size > 0) {
				prev = true;
			}
		}
		if (buffer.length() > 0) {
			char[] cnt = new char[buffer.length()];
			buffer.getChars(0, buffer.length(), cnt, 0);
			return new CharArrayReader(cnt);
		}
		return null;
	}
}
