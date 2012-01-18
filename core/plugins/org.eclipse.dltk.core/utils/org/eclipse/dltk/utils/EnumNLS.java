/*******************************************************************************
 * Copyright (c) 2011 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.utils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

/**
 * Common superclass for all message bundle classes. Provides convenience
 * methods for manipulating messages.
 * <p>
 * The <code>#bind</code> methods perform string substitution and should be
 * considered a convenience and <em>not</em> a full substitute replacement for
 * <code>MessageFormat#format</code> method calls.
 * </p>
 * <p>
 * Text appearing within curly braces in the given message, will be interpreted
 * as a numeric index to the corresponding substitution object in the given
 * array. Calling the <code>#bind</code> methods with text that does not map to
 * an integer will result in an {@link IllegalArgumentException}.
 * </p>
 * <p>
 * Text appearing within single quotes is treated as a literal. A single quote
 * is escaped by a preceding single quote.
 * </p>
 * <p>
 * Clients who wish to use the full substitution power of the
 * <code>MessageFormat</code> class should call that class directly and not use
 * these <code>#bind</code> methods.
 * </p>
 * <p>
 * Clients may subclass this type.
 * </p>
 * 
 * @since 3.0
 */
public class EnumNLS {

	private static final String EXTENSION = ".properties"; //$NON-NLS-1$
	private static String[] nlSuffixes;
	private static final boolean DEBUG = false;

	static final int SEVERITY_ERROR = 0x04;
	static final int SEVERITY_WARNING = 0x02;

	/*
	 * This object is assigned to the value of a field map to indicate that a
	 * translated message has already been assigned to that field.
	 */
	static enum Assigned {
		ASSIGNED
	}

	/**
	 * Creates a new NLS instance.
	 */
	private EnumNLS() {
	}

	private static Class<?> classOf(Enum<?>[] values) {
		@SuppressWarnings("rawtypes")
		Class<? extends Enum> clazz = null;
		for (Enum<?> value : values) {
			if (clazz == null) {
				clazz = value.getClass();
			} else if (clazz != value.getClass()) {
				throw new IllegalArgumentException("Mix of different enums");
			}
		}
		return clazz;
	}

	/**
	 * Initialize the given class with the values from the specified message
	 * bundle.
	 * 
	 * @param values
	 *            the list of enum values use expression like
	 *            <code>MyEnum.values()</code>
	 * @param fieldName
	 *            the name of the field to set
	 */
	public static void initializeMessages(final Enum<?>[] values,
			final String fieldName) {
		final Class<?> clazz = classOf(values);
		if (System.getSecurityManager() == null) {
			load(clazz.getName(), clazz, values, fieldName);
			return;
		}
		AccessController.doPrivileged(new PrivilegedAction<Object>() {
			public Object run() {
				load(clazz.getName(), clazz, values, fieldName);
				return null;
			}
		});
	}

	/*
	 * Build an array of property files to search. The returned array contains
	 * the property fields in order from most specific to most generic. So, in
	 * the FR_fr locale, it will return file_fr_FR.properties, then
	 * file_fr.properties, and finally file.properties.
	 */
	private static String[] buildVariants(String root) {
		if (nlSuffixes == null) {
			// build list of suffixes for loading resource bundles
			String nl = Locale.getDefault().toString();
			ArrayList<String> result = new ArrayList<String>(4);
			int lastSeparator;
			while (true) {
				result.add('_' + nl + EXTENSION);
				lastSeparator = nl.lastIndexOf('_');
				if (lastSeparator == -1)
					break;
				nl = nl.substring(0, lastSeparator);
			}
			// add the empty suffix last (most general)
			result.add(EXTENSION);
			nlSuffixes = result.toArray(new String[result.size()]);
		}
		root = root.replace('.', '/');
		String[] variants = new String[nlSuffixes.length];
		for (int i = 0; i < variants.length; i++)
			variants[i] = root + nlSuffixes[i];
		return variants;
	}

	private static void computeMissingMessages(String bundleName,
			Class<?> clazz, Field field, Map<String, Enum<?>> fieldMap,
			Enum<?>[] fieldArray) {
		// iterate over the fields in the class to make sure that there aren't
		// any empty ones
		final int numFields = fieldArray.length;
		for (int i = 0; i < numFields; i++) {
			Enum<?> item = fieldArray[i];
			// if the field has a a value assigned, there is nothing to do
			if (fieldMap.get(item.name()) == Assigned.ASSIGNED)
				continue;
			try {
				// Set a value for this empty field. We should never get an
				// exception here because
				// we know we have a public static non-final field. If we do get
				// an exception, silently
				// log it and continue. This means that the field will (most
				// likely) be un-initialized and
				// will fail later in the code and if so then we will see both
				// the NPE and this error.
				String value = "NLS missing message: " + item.name() + " in: " + bundleName; //$NON-NLS-1$ //$NON-NLS-2$
				log(SEVERITY_WARNING, value, null);
				field.set(item, value);
			} catch (Exception e) {
				log(SEVERITY_ERROR,
						"Error setting the missing message value for: " + field.getName(), e); //$NON-NLS-1$
			}
		}
	}

	/*
	 * Load the given resource bundle using the specified class loader.
	 */
	static void load(final String bundleName, Class<?> clazz, Enum<?>[] values,
			String fieldName) {
		long start = System.currentTimeMillis();
		final ClassLoader loader = clazz.getClassLoader();
		final Field field;
		try {
			field = clazz.getDeclaredField(fieldName);
		} catch (NoSuchFieldException e) {
			throw new IllegalArgumentException(e);
		}
		boolean isAccessible = (field.getModifiers() & Modifier.PUBLIC) != 0;
		if (!isAccessible) {
			field.setAccessible(true);
		}

		// build a map of field names to Field objects
		final int len = values.length;
		Map<String, Enum<?>> fields = new HashMap<String, Enum<?>>(len * 2);
		for (int i = 0; i < len; i++)
			fields.put(values[i].name(), values[i]);

		// search the variants from most specific to most general, since
		// the MessagesProperties.put method will mark assigned fields
		// to prevent them from being assigned twice
		final String[] variants = buildVariants(bundleName);
		for (int i = 0; i < variants.length; i++) {
			// loader==null if we're launched off the Java boot classpath
			final InputStream input = loader == null ? ClassLoader
					.getSystemResourceAsStream(variants[i]) : loader
					.getResourceAsStream(variants[i]);
			if (input == null)
				continue;
			try {
				final MessagesProperties properties = new MessagesProperties(
						field, fields, bundleName);
				properties.load(input);
			} catch (IOException e) {
				log(SEVERITY_ERROR, "Error loading " + variants[i], e); //$NON-NLS-1$
			} finally {
				if (input != null)
					try {
						input.close();
					} catch (IOException e) {
						// ignore
					}
			}
		}
		computeMissingMessages(bundleName, clazz, field, fields, values);
		if (DEBUG)
			System.out
					.println("Time to load message bundle: " + bundleName + " was " + (System.currentTimeMillis() - start) + "ms."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
	}

	/*
	 * The method adds a log entry based on the error message and exception. The
	 * output is written to the System.err.
	 * 
	 * This method is only expected to be called if there is a problem in the
	 * NLS mechanism. As a result, translation facility is not available here
	 * and messages coming out of this log are generally not translated.
	 * 
	 * @param severity - severity of the message (SEVERITY_ERROR or
	 * SEVERITY_WARNING)
	 * 
	 * @param message - message to log
	 * 
	 * @param e - exception to log
	 */
	static void log(int severity, String message, Exception e) {
		String statusMsg;
		switch (severity) {
		case SEVERITY_ERROR:
			statusMsg = "Error: "; //$NON-NLS-1$
			break;
		case SEVERITY_WARNING:
			// intentionally fall through:
		default:
			statusMsg = "Warning: "; //$NON-NLS-1$
		}
		if (message != null)
			statusMsg += message;
		if (e != null)
			statusMsg += ": " + e.getMessage(); //$NON-NLS-1$
		System.err.println(statusMsg);
		if (e != null)
			e.printStackTrace();
	}

	/*
	 * Class which sub-classes java.util.Properties and uses the #put method to
	 * set field values rather than storing the values in the table.
	 */
	private static class MessagesProperties extends Properties {

		// private static final int MOD_EXPECTED = Modifier.PUBLIC
		// | Modifier.STATIC;
		// private static final int MOD_MASK = MOD_EXPECTED | Modifier.FINAL;
		private static final long serialVersionUID = 1L;

		private final Field field;
		private final String bundleName;
		private final Map<String, Enum<?>> fields;

		public MessagesProperties(Field field, Map<String, Enum<?>> fieldMap,
				String bundleName) {
			super();
			this.field = field;
			this.fields = fieldMap;
			this.bundleName = bundleName;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
		 */
		public synchronized Object put(Object key, Object value) {
			Enum<?> fieldObject = fields.put((String) key, Assigned.ASSIGNED);
			// if already assigned, there is nothing to do
			if (fieldObject == Assigned.ASSIGNED)
				return null;
			if (fieldObject == null) {
				final String msg = "NLS unused message: " + key + " in: " + bundleName;//$NON-NLS-1$ //$NON-NLS-2$
				log(SEVERITY_WARNING, msg, null);
				return null;
			}
			// can only set value of public static non-final fields
			try {
				// Check to see if we are allowed to modify the field. If we
				// aren't (for instance
				// if the class is not public) then change the accessible
				// attribute of the field
				// before trying to set the value.
				// Set the value into the field. We should never get an
				// exception here because
				// we know we have a public static non-final field. If we do get
				// an exception, silently
				// log it and continue. This means that the field will (most
				// likely) be un-initialized and
				// will fail later in the code and if so then we will see both
				// the NPE and this error.

				// Extra care is taken to be sure we create a String with its
				// own backing char[] (bug 287183)
				// This is to ensure we do not keep the key chars in memory.
				field.set(fieldObject,
						new String(((String) value).toCharArray()));
			} catch (Exception e) {
				log(SEVERITY_ERROR, "Exception setting field value.", e); //$NON-NLS-1$
			}
			return null;
		}
	}
}