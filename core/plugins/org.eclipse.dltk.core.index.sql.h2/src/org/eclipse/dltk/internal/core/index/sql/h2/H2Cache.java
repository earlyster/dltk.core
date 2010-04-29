/*******************************************************************************
 * Copyright (c) 2009 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Zend Technologies
 *******************************************************************************/
package org.eclipse.dltk.internal.core.index.sql.h2;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.jobs.ILock;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.index.sql.Container;
import org.eclipse.dltk.core.index.sql.DbFactory;
import org.eclipse.dltk.core.index.sql.Element;
import org.eclipse.dltk.core.index.sql.File;
import org.eclipse.dltk.core.index.sql.IElementDao;
import org.eclipse.dltk.core.index.sql.IElementHandler;
import org.eclipse.dltk.core.index.sql.h2.H2Index;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;

/**
 * This is a cache layer between H2 database and model access
 * 
 * @author michael
 */
public class H2Cache {

	private static final ILock containerLock = Job.getJobManager().newLock();
	private static final Map<Integer, Container> containerById = new HashMap<Integer, Container>();

	private static final ILock fileLock = Job.getJobManager().newLock();
	private static final Map<Integer, File> fileById = new HashMap<Integer, File>();

	private static final ILock elementLock = Job.getJobManager().newLock();
	private static final Map<Integer, Map<Integer, Set<Element>>> elementsMap = new HashMap<Integer, Map<Integer, Set<Element>>>();

	private static final ILock loadedLock = Job.getJobManager().newLock();
	private static boolean isLoaded;

	public static void addContainer(Container container) {
		containerLock.acquire();
		try {
			containerById.put(container.getId(), container);
		} finally {
			containerLock.release();
		}
	}

	public static void addElement(Element element) {
		elementLock.acquire();
		try {
			int elementType = element.getType();
			Map<Integer, Set<Element>> elementsByFile = elementsMap
					.get(elementType);
			if (elementsByFile == null) {
				elementsByFile = new HashMap<Integer, Set<Element>>();
				elementsMap.put(elementType, elementsByFile);
			}
			int fileId = element.getFileId();
			Set<Element> elementsSet = elementsByFile.get(fileId);
			if (elementsSet == null) {
				elementsSet = new HashSet<Element>();
				elementsByFile.put(fileId, elementsSet);
			}
			elementsSet.add(element);
		} finally {
			elementLock.release();
		}
	}

	public static void addFile(File file) {
		fileLock.acquire();
		try {
			fileById.put(file.getId(), file);
		} finally {
			fileLock.release();
		}
	}

	public static void deleteContainerById(int id) {
		containerLock.acquire();
		try {
			containerById.remove(id);
			deleteFilesByContainerId(id);
		} finally {
			containerLock.release();
		}
	}

	public static void deleteContainerByPath(String path) {
		containerLock.acquire();
		try {
			Container container = selectContainerByPath(path);
			if (container != null) {
				deleteContainerById(container.getId());
			}
		} finally {
			containerLock.release();
		}
	}

	public static void deleteElementsByFileId(int id) {
		elementLock.acquire();
		try {
			Iterator<Map<Integer, Set<Element>>> i = elementsMap.values()
					.iterator();
			while (i.hasNext()) {
				Map<Integer, Set<Element>> elementsByFile = i.next();
				elementsByFile.remove(id);
			}
		} finally {
			elementLock.release();
		}
	}

	public static void deleteFileByContainerIdAndPath(int containerId,
			String path) {
		fileLock.acquire();
		try {
			File file = selectFileByContainerIdAndPath(containerId, path);
			if (file != null) {
				deleteFileById(file.getId());
			}
		} finally {
			fileLock.release();
		}
	}

	public static void deleteFileById(int id) {
		fileLock.acquire();
		try {
			fileById.remove(id);
			deleteElementsByFileId(id);
		} finally {
			fileLock.release();
		}
	}

	public static void deleteFilesByContainerId(int id) {
		fileLock.acquire();
		try {
			Collection<File> files = selectFilesByContainerId(id);
			for (File file : files) {
				deleteFileById(file.getId());
			}
		} finally {
			fileLock.release();
		}
	}

	public static Container selectContainerById(int id) {
		containerLock.acquire();
		try {
			return containerById.get(id);
		} finally {
			containerLock.release();
		}
	}

	public static Container selectContainerByPath(String path) {
		containerLock.acquire();
		try {
			Iterator<Container> i = containerById.values().iterator();
			while (i.hasNext()) {
				Container container = i.next();
				if (container.getPath().equals(path)) {
					return container;
				}
			}
			return null;
		} finally {
			containerLock.release();
		}
	}

	public static Collection<Element> selectElementsByFileId(int id) {
		elementLock.acquire();
		try {
			List<Element> elements = new LinkedList<Element>();
			Iterator<Map<Integer, Set<Element>>> i = elementsMap.values()
					.iterator();
			while (i.hasNext()) {
				Map<Integer, Set<Element>> elementsByFile = i.next();
				Set<Element> elementsSet = elementsByFile.get(id);
				if (elementsSet != null) {
					elements.addAll(elementsSet);
				}
			}
			return elements;
		} finally {
			elementLock.release();
		}
	}

	public static File selectFileByContainerIdAndPath(int containerId,
			String path) {
		fileLock.acquire();
		try {
			Iterator<File> i = fileById.values().iterator();
			while (i.hasNext()) {
				File file = i.next();
				if (file.getContainerId() == containerId
						&& file.getPath().equals(path)) {
					return file;
				}
			}
		} finally {
			fileLock.release();
		}
		return null;
	}

	public static File selectFileById(int id) {
		fileLock.acquire();
		try {
			return fileById.get(id);
		} finally {
			fileLock.release();
		}
	}

	public static Collection<File> selectFilesByContainerId(int id) {
		fileLock.acquire();
		try {
			List<File> files = new LinkedList<File>();
			Iterator<File> i = fileById.values().iterator();
			while (i.hasNext()) {
				File file = i.next();
				if (file.getContainerId() == id) {
					files.add(file);
				}
			}
			return files;
		} finally {
			fileLock.release();
		}
	}

	public static Collection<Element> searchElements(String pattern,
			MatchRule matchRule, int elementType, int trueFlags,
			int falseFlags, String qualifier, String parent, int[] filesId,
			int containersId[], String natureId, int limit) {

		Set<Integer> filesIds = new HashSet<Integer>();
		if (filesId != null) {
			for (int fileId : filesId) {
				filesIds.add(fileId);
			}
		} else if (containersId != null) {
			containerLock.acquire();
			try {
				for (int containerId : containersId) {
					Collection<File> files = selectFilesByContainerId(containerId);
					for (File file : files) {
						filesIds.add(file.getId());
					}
				}
			} finally {
				containerLock.release();
			}
		}

		elementLock.acquire();
		try {
			Set<String> patternSet = null;
			Pattern posixPattern = null;
			String patternLC = null;
			if (pattern != null) {
				patternLC = pattern.toLowerCase();
			}

			if (matchRule == MatchRule.SET) {
				patternSet = new HashSet<String>();
				String[] parts = pattern.split(",");
				for (String part : parts) {
					if (part.length() > 0) {
						patternSet.add(part.toLowerCase());
					}
				}
			} else if (matchRule == MatchRule.PATTERN) {
				posixPattern = createPosixPattern(pattern);
			}

			List<Element> elements = new LinkedList<Element>();
			Map<Integer, Set<Element>> elementsByFile = elementsMap
					.get(elementType);
			if (elementsByFile != null) {

				Iterator<Entry<Integer, Set<Element>>> i = elementsByFile
						.entrySet().iterator();

				while (i.hasNext()) {
					Entry<Integer, Set<Element>> elementEntry = i.next();

					if (filesIds.size() == 0
							|| filesIds.contains(elementEntry.getKey())) {

						Iterator<Element> i2 = elementEntry.getValue()
								.iterator();
						while (i2.hasNext()) {

							Element element = i2.next();
							if ((trueFlags == 0 || (element.getFlags() & trueFlags) != 0)
									&& (falseFlags == 0 || (element.getFlags() & falseFlags) == 0)) {

								if (qualifier == null
										|| qualifier.length() == 0
										|| qualifier.equals(element
												.getQualifier())) {

									if (parent == null
											|| parent.length() == 0
											|| parent.equals(element
													.getParent())) {

										String elementName = element.getName();
										if (pattern == null
												|| pattern.length() == 0
												|| (matchRule == MatchRule.EXACT && pattern
														.equalsIgnoreCase(elementName))
												|| (matchRule == MatchRule.PREFIX && elementName
														.toLowerCase()
														.startsWith(patternLC))
												|| (matchRule == MatchRule.CAMEL_CASE
														&& element
																.getCamelCaseName() != null && element
														.getCamelCaseName()
														.startsWith(
																pattern
																		.toUpperCase()))
												|| (matchRule == MatchRule.SET && patternSet
														.contains(elementName
																.toLowerCase()))
												|| (matchRule == MatchRule.PATTERN && posixPattern
														.matcher(elementName)
														.matches())) {

											elements.add(element);

											if (--limit == 0) {
												break;
											}
										}
									}
								}
							}
						}
					}
				}
			}
			return elements;

		} finally {
			elementLock.release();
		}
	}

	private static Pattern createPosixPattern(String pattern) {
		StringBuilder buf = new StringBuilder();
		boolean inQuoted = false;
		for (int i = 0; i < pattern.length(); ++i) {
			char ch = pattern.charAt(i);
			if (ch == '*') {
				if (inQuoted) {
					buf.append("\\E");
					inQuoted = false;
				}
				buf.append(".*");
			} else if (ch == '?') {
				if (inQuoted) {
					buf.append("\\E");
					inQuoted = false;
				}
				buf.append(".?");
			} else {
				if (!inQuoted) {
					buf.append("\\Q");
					inQuoted = true;
				}
				buf.append(ch);
			}
		}
		return Pattern.compile(buf.toString(), Pattern.CASE_INSENSITIVE);
	}

	public static boolean isLoaded() {
		loadedLock.acquire();
		try {
			return isLoaded;
		} finally {
			loadedLock.release();
		}
	}

	public static void load() {
		loadedLock.acquire();
		try {
			if (!isLoaded) {
				try {
					DbFactory dbFactory = DbFactory.getInstance();
					Connection connection = dbFactory.createConnection();
					try {
						IElementDao elementDao = dbFactory.getElementDao();
						elementDao.search(connection, null, MatchRule.PREFIX,
								IModelElement.FIELD, 0, 0, null, null, null,
								null, "org.eclipse.php.core.PHPNature", 0,
								false, new IElementHandler() {
									public void handle(Element element) {
									}
								}, new NullProgressMonitor());

						elementDao.search(connection, null, MatchRule.PREFIX,
								IModelElement.TYPE, 0, 0, null, null, null,
								null, "org.eclipse.php.core.PHPNature", 0,
								false, new IElementHandler() {
									public void handle(Element element) {
									}
								}, new NullProgressMonitor());

						elementDao.search(connection, null, MatchRule.PREFIX,
								IModelElement.METHOD, 0, 0, null, null, null,
								null, "org.eclipse.php.core.PHPNature", 0,
								false, new IElementHandler() {
									public void handle(Element element) {
									}
								}, new NullProgressMonitor());

						elementDao.search(connection, null, MatchRule.PREFIX,
								IModelElement.IMPORT_DECLARATION, 0, 0, null,
								null, null, null,
								"org.eclipse.php.core.PHPNature", 0, false,
								new IElementHandler() {
									public void handle(Element element) {
									}
								}, new NullProgressMonitor());
					} finally {
						connection.close();
					}
				} catch (SQLException e) {
					if (H2Index.DEBUG) {
						e.printStackTrace();
					}
				} finally {
					isLoaded = true;
				}
			}
		} finally {
			loadedLock.release();
		}
	}
}
