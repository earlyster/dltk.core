package org.eclipse.dltk.launching.sourcelookup;

import java.net.URI;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.eclipse.dltk.debug.core.DLTKDebugConstants;
import org.eclipse.dltk.internal.core.DefaultWorkingCopyOwner;
import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.dltk.internal.debug.core.model.ScriptStackFrame;
import org.eclipse.dltk.internal.launching.LaunchConfigurationUtils;

public class ScriptSourceLookupParticipant extends
		AbstractSourceLookupParticipant {

	public String getSourceName(Object object) throws CoreException {
		if (object instanceof ScriptStackFrame) {
			final ScriptStackFrame frame = (ScriptStackFrame) object;
			final URI uri = frame.getSourceURI();
			if (DLTKDebugConstants.UNKNOWN_SCHEME.equalsIgnoreCase(uri
					.getScheme())) {
				return null;
			}

			String path = uri.getPath();
			if (path.length() == 0) {
				return null;
			}
			// if (Platform.getOS().equals(Platform.OS_WIN32)) {
			// path = path.substring(1);
			// }

			String root = getProjectRoot();

			// strip off the project root
			if (path.startsWith(root) && path.charAt(root.length()) == '/') {
				return path.substring(root.length() + 1);
			}

			IFile[] files = getWorkspaceRoot().findFilesForLocation(
					new Path(path));

			IProject project = LaunchConfigurationUtils
					.getProject(getDirector().getLaunchConfiguration());
			for (int i = 0; i < files.length; i++) {
				IFile file = files[i];
				if (file.exists()) {
					if (file.getProject().equals(project)) {
						return file.getProjectRelativePath().toString();
					}
				}
			}
			return path;
		}
		return null;
	}

	protected String getProjectRoot() throws CoreException {
		IProject project = LaunchConfigurationUtils.getProject(getDirector()
				.getLaunchConfiguration());
		return project.getLocationURI().getPath();
	}

	public Object[] findSourceElements(Object object) throws CoreException {
		final Object[] elements = super.findSourceElements(object);
		if (elements != null && elements.length > 0) {
			return elements;
		}
		if (object instanceof ScriptStackFrame) {
			ScriptStackFrame frame = (ScriptStackFrame) object;
			final URI uri = frame.getSourceURI();
			if (DLTKDebugConstants.UNKNOWN_SCHEME.equalsIgnoreCase(uri
					.getScheme())) {
				return null;
			}
			final String path = uri.getPath();
			if (path == null || path.length() == 0) {
				return null;
			}
			final Path pathObj = new Path(path);
			if (pathObj.isEmpty()) {
				return null;
			}
			ILaunchConfiguration launchConfiguration = this.getDirector()
					.getLaunchConfiguration();

			IProject project = LaunchConfigurationUtils
					.getProject(launchConfiguration);
			final ProjectSourceLookup lookup = new ProjectSourceLookup(
					project);
			final IProjectLookupResult result = lookup.find(pathObj);
			if (result != null) {
				return result.toArray();
			}
			return new Object[] { new DBGPSourceModule((ScriptProject) lookup
					.getScriptProject(), path, DefaultWorkingCopyOwner.PRIMARY,
					frame) };
		}
		return null;
	}

	private static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
}
