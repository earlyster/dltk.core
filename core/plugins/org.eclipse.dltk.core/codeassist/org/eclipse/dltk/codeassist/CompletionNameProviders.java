/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.codeassist;

import org.eclipse.dltk.core.IMember;

public class CompletionNameProviders {

	private static class MemberCompletionNameProvider implements
			ICompletionNameProvider<IMember> {

		public String getCompletion(IMember t) {
			return t.getElementName();
		}

		public String getName(IMember t) {
			return t.getElementName();
		}

	}

	private static final MemberCompletionNameProvider memberProvider = new MemberCompletionNameProvider();

	@SuppressWarnings("unchecked")
	public static <T extends IMember> ICompletionNameProvider<T> defaultProvider() {
		return (ICompletionNameProvider<T>) memberProvider;
	}

	private static class PrefixCompletionNameProvider implements
			ICompletionNameProvider<IMember> {

		final String prefix;

		public PrefixCompletionNameProvider(String prefix) {
			this.prefix = prefix;
		}

		public String getCompletion(IMember t) {
			return t.getElementName();
		}

		public String getName(IMember t) {
			return prefix + t.getElementName();
		}

	}

	@SuppressWarnings("unchecked")
	public static <T extends IMember> ICompletionNameProvider<T> prefixProvider(
			String prefix) {
		if (prefix != null && prefix.length() != 0) {
			return (ICompletionNameProvider<T>) new PrefixCompletionNameProvider(
					prefix);
		} else {
			return defaultProvider();
		}
	}

}
