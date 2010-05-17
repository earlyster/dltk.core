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
package org.eclipse.dltk.internal.core.util;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.dltk.core.IModelElement;

public class ElementTypeDescriber {
	private final Map<Integer, String> names = new HashMap<Integer, String>();

	public ElementTypeDescriber() {
		for (Field field : IModelElement.class.getFields()) {
			if (Modifier.isPublic(field.getModifiers())
					&& Modifier.isStatic(field.getModifiers())) {
				try {
					Integer value = (Integer) field.get(null);
					names.put(value.intValue(), convert(field.getName()));
				} catch (Exception e) {
					//
				}
			}
		}
	}

	/**
	 * @param name
	 * @return
	 */
	private String convert(String name) {
		StringBuilder sb = new StringBuilder();
		for (StringTokenizer e = new StringTokenizer(name, "_"); e
				.hasMoreElements();) {
			String token = e.nextToken();
			if (token.length() != 0) {
				sb.append(Character.toUpperCase(token.charAt(0)));
				sb.append(token.substring(1).toLowerCase());
			}
		}
		return sb.toString();
	}

	public String describe(int elementType) {
		String name = names.get(elementType);
		if (name == null) {
			name = "#" + elementType;
		}
		return name;
	}
}
