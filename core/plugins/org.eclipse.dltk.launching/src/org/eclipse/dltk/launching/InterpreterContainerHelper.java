package org.eclipse.dltk.launching;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IBuildpathAttribute;
import org.eclipse.dltk.core.IBuildpathEntry;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.BuildpathEntry;

public class InterpreterContainerHelper {
	private static final char SEPARATOR = '|';
	private static final String PACKAGES_ATTR = "dependencies"; //$NON-NLS-1$
	private static final String PACKAGES_AUTO_ATTR = "auto_dependencies"; //$NON-NLS-1$
	public static final String CONTAINER_PATH = ScriptRuntime.INTERPRETER_CONTAINER;

	public static void getInterpreterContainerDependencies(
			IScriptProject project, Set packages, Set autoPackages) {
		IBuildpathEntry[] rawBuildpath = null;
		try {
			rawBuildpath = project.getRawBuildpath();
		} catch (ModelException e1) {
			if (DLTKCore.DEBUG) {
				e1.printStackTrace();
			}
		}
		if (rawBuildpath == null) {
			return;
		}
		IBuildpathEntry containerEntry = null;
		for (int i = 0; i < rawBuildpath.length; i++) {
			if (rawBuildpath[i].getPath().segment(0).equals(CONTAINER_PATH)) {
				containerEntry = rawBuildpath[i];
			}
		}
		if (containerEntry == null) {
			return;
		}
		IBuildpathAttribute[] extraAttributes = containerEntry
				.getExtraAttributes();
		for (int i = 0; i < extraAttributes.length; i++) {
			if (extraAttributes[i].getName().equals(PACKAGES_ATTR)) {
				String value = extraAttributes[i].getValue();
				String[] split = split(value);
				for (int j = 0; j < split.length; j++) {
					packages.add(split[j]);
				}
			} else if (extraAttributes[i].getName().equals(PACKAGES_AUTO_ATTR)) {
				String value = extraAttributes[i].getValue();
				String[] split = split(value);
				for (int j = 0; j < split.length; j++) {
					autoPackages.add(split[j]);
				}
			}
		}
		return;
	}

	private static String[] split(String value) {
		List result = new ArrayList();
		int start = 0, end = 0;
		for (int i = 0; i < value.length(); i++) {
			if (value.charAt(i) == SEPARATOR && start < end) {
				String sub = value.substring(start, end);
				if (sub.length() > 0) {
					result.add(sub);
				}
				start = end + 1;
			}
			end++;
		}
		if (start < end) {
			String sub = value.substring(start, end);
			if (sub.length() > 0) {
				result.add(sub);
			}
		}
		return (String[]) result.toArray(new String[result.size()]);
	}

	public static void setInterpreterContainerDependencies(
			IScriptProject project, Set packages, Set autoPackages) {
		IBuildpathEntry[] rawBuildpath = null;
		try {
			rawBuildpath = project.getRawBuildpath();
		} catch (ModelException e1) {
			if (DLTKCore.DEBUG) {
				e1.printStackTrace();
			}
		}
		IPath containerName = new Path(CONTAINER_PATH).append(project
				.getElementName());

		List newBuildpath = new ArrayList();
		boolean found = false;
		for (int i = 0; i < rawBuildpath.length; i++) {
			if (!rawBuildpath[i].getPath().segment(0).equals(CONTAINER_PATH)) {
				newBuildpath.add(rawBuildpath[i]);
			} else {
				found = true;
				newBuildpath.add(createPackagesContainer(packages,
						autoPackages, rawBuildpath[i].getPath()));
			}
		}
		if (!found) {
			newBuildpath.add(createPackagesContainer(packages, autoPackages,
					containerName));
		}
		IBuildpathEntry[] nbp = (IBuildpathEntry[]) newBuildpath
				.toArray(new IBuildpathEntry[newBuildpath.size()]);
		try {
			project.setRawBuildpath(nbp, new NullProgressMonitor());
		} catch (ModelException e) {
			if (DLTKCore.DEBUG) {
				e.printStackTrace();
			}
		}
	}

	public static IBuildpathEntry createPackagesContainer(Set names,
			Set autoPackages, IPath containerName) {
		String pkgs = pkgsToString(names);
		String autoPkgs = pkgsToString(autoPackages);
		IBuildpathAttribute attr = DLTKCore.newBuildpathAttribute(
				PACKAGES_ATTR, pkgs);
		IBuildpathAttribute attr2 = DLTKCore.newBuildpathAttribute(
				PACKAGES_AUTO_ATTR, autoPkgs);
		IBuildpathEntry container = DLTKCore.newContainerEntry(containerName,
				BuildpathEntry.NO_ACCESS_RULES, new IBuildpathAttribute[] {
						attr, attr2 }, false/* not exported */);
		return container;
	}

	private static String pkgsToString(Set names) {
		StringBuffer buffer = new StringBuffer();
		Object[] array = names.toArray();
		for (int i = 0; i < array.length; i++) {
			buffer.append((String) array[i]);
			if (i != array.length - 1) {
				buffer.append(SEPARATOR);
			}
		}
		return buffer.toString();
	}
}
