package org.eclipse.dltk.launching.sourcelookup;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.core.DefaultWorkingCopyOwner;
import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.dltk.internal.debug.core.model.ScriptStackFrame;
import org.eclipse.dltk.internal.launching.IPathEquality;
import org.eclipse.dltk.internal.launching.LaunchConfigurationUtils;
import org.eclipse.dltk.internal.launching.PathEqualityUtils;

public class ScriptSourceLookupParticipant extends
		AbstractSourceLookupParticipant {

	private final class ExternalSourceModuleFinder implements
			IModelElementVisitor {

		private final IPath fileFullPath;

		private ExternalSourceModuleFinder(IPath fileFullPath) {
			this.fileFullPath = fileFullPath;
		}

		private final IPathEquality pathEquality = PathEqualityUtils
				.getInstance();

		private final ISourceModule[] result = new ISourceModule[1];

		public boolean visit(IModelElement element) {
			if (element.getElementType() == IModelElement.PROJECT_FRAGMENT) {
				IProjectFragment fragment = (IProjectFragment) element;
				if (!fragment.isExternal()) {
					return false;
				}
			}
			if (element.getElementType() == IModelElement.SOURCE_MODULE) {
				ISourceModule module = (ISourceModule) element;
				if (pathEquality.equals(fileFullPath, module.getPath())) {
					result[0] = module;
				}
				return false;
			}
			return true;
		}

		public boolean isFound() {
			return result[0] != null;
		}

		public Object[] getResult() {
			return result;
		}
	}

	public String getSourceName(Object object) throws CoreException {
		ScriptStackFrame frame = (ScriptStackFrame) object;

		String path = frame.getFileName().getPath();
		if (path.length() == 0) {
			return null;
		}
		// if (Platform.getOS().equals(Platform.OS_WIN32)) {
		// path = path.substring(1);
		// }

		String root = getProjectRoot();

		// strip off the project root
		if (path.indexOf(root) != -1) {
			return path.substring(root.length() + 1);
		}

		IFile[] files = getWorkspaceRoot().findFilesForLocation(new Path(path));

		IProject project = LaunchConfigurationUtils.getProject(getDirector()
				.getLaunchConfiguration());
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

	private IEnvironment getEnvironment() {
		IProject project = LaunchConfigurationUtils.getProject(getDirector()
				.getLaunchConfiguration());
		IScriptProject scriptProject = DLTKCore.create(project);
		return EnvironmentManager.getEnvironment(scriptProject);
	}

	protected String getProjectRoot() throws CoreException {
		IProject project = LaunchConfigurationUtils.getProject(getDirector()
				.getLaunchConfiguration());
		return project.getLocationURI().getPath();
	}

	public Object[] findSourceElements(Object object) throws CoreException {
		Object[] elements = super.findSourceElements(object);
		if (elements.length > 0) {
			return elements;
		}
		ILaunchConfiguration launchConfiguration = this.getDirector()
				.getLaunchConfiguration();

		IProject project = LaunchConfigurationUtils
				.getProject(launchConfiguration);
		ScriptProject scriptProject = (ScriptProject) DLTKCore.create(project);

		ScriptStackFrame frame = (ScriptStackFrame) object;
		final String path = frame.getFileName().getPath();
		if (path == null || path.length() == 0) {
			return new Object[0];
		}
		final IFileHandle file = getEnvironment().getFile(new Path(path));
		if (file.exists()) {
			// Try to open external source module.
			final ExternalSourceModuleFinder finder = new ExternalSourceModuleFinder(
					file.getFullPath());
			scriptProject.accept(finder);
			if (finder.isFound()) {
				return finder.getResult();
			}
			final IFile[] workspaceFiles = getWorkspaceRoot()
					.findFilesForLocationURI(file.toURI());
			if (workspaceFiles.length != 0 && workspaceFiles[0].exists()) {
				return workspaceFiles;
			}
		}
		return new Object[] { new DBGPSourceModule(scriptProject, frame
				.getFileName().getPath(), DefaultWorkingCopyOwner.PRIMARY,
				frame) };
	}

	private static IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}
}
