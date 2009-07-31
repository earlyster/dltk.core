package org.eclipse.dltk.launching;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IExecutionEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.osgi.util.NLS;

public class InterpreterSearcher {
	private Set<IFileHandle> searchedDirs;
	private List<IFileHandle> found;
	private List<IInterpreterInstallType> types;

	private String natureId;
	private IInterpreterInstallType[] installTypes;
	private Set<IFileHandle> ignore;

	protected void searchFast(IProgressMonitor monitor,
			IEnvironment environment, int depth) {
		if (monitor.isCanceled()) {
			return;
		}

		// Path variable
		IExecutionEnvironment exeEnv = (IExecutionEnvironment) environment
				.getAdapter(IExecutionEnvironment.class);
		if (exeEnv == null)
			return;

		monitor.subTask(Messages.InterpreterSearcher_0);
		Map<String, String> env = exeEnv.getEnvironmentVariables(true);

		if (env == null) {
			return;
		}
		String path = null;
		for (final String name : env.keySet()) {
			if (name.equalsIgnoreCase("path")) { //$NON-NLS-1$
				path = env.get(name);
				break;
			}
		}
		if (path == null) {
			return;
		}

		// Folder list
		final String separator = environment.getPathsSeparator();

		final List<IPath> folders = new ArrayList<IPath>();
		String[] res = path.split(separator);
		for (int i = 0; i < res.length; i++) {
			folders.add(Path.fromOSString(res[i]));
		}

		monitor.beginTask(Messages.InterpreterSearcher_1, folders.size());
		for (final IPath folder : folders) {
			IFileHandle f = environment.getFile(folder);
			if (f.isDirectory()) {
				search(f, monitor, depth);
			}
			monitor.worked(1);
		}
		monitor.done();
	}

	/**
	 * Searches the specified directory recursively for installed Interpreters,
	 * adding each detected Interpreter to the <code>found</code> list. Any
	 * directories specified in the <code>ignore</code> are not traversed.
	 * 
	 * @param directory
	 * @param found
	 * @param types
	 * @param ignore
	 * @param depth
	 *            deepness of search. -1 if infinite.
	 */
	protected void search(IFileHandle directory, IProgressMonitor monitor,
			int depth) {
		if (depth == 0) {
			return;
		}

		if (monitor.isCanceled()) {
			return;
		}

		if (!searchedDirs.add(directory)) {
			return;
		}

		IFileHandle[] files = directory.getChildren();
		if (files == null) {
			return;
		}

		List<IFileHandle> subDirs = new ArrayList<IFileHandle>();
		for (int i = 0; i < files.length; i++) {
			if (monitor.isCanceled()) {
				return;
			}

			final IFileHandle file = files[i];

			monitor.subTask(NLS.bind(
					Messages.InterpreterSearcher_foundSearching, Integer
							.valueOf(found.size()), file.getCanonicalPath()));

			// Check if file is a symlink
			if (file.isDirectory() && file.isSymlink()) {
				continue;
			}

			if (!ignore.contains(file)) {
				boolean validLocation = false;
				// Take the first Interpreter install type that claims the
				// location as a
				// valid Interpreter install. Interpreter install types should
				// be smart enough to not
				// claim another type's Interpreter, but just in case...
				for (int j = 0; j < installTypes.length; j++) {
					if (monitor.isCanceled()) {
						return;
					}

					final IInterpreterInstallType installType = installTypes[j];
					IStatus status = installType.validatePossiblyName(file);

					if (status.isOK()) {
						found.add(file);
						types.add(installType);
						validLocation = true;
						break;
					}
				}

				if (file.isDirectory() && !validLocation) {
					subDirs.add(file);
				}
			}
		}

		if (!subDirs.isEmpty()) {
			for (IFileHandle subDir : subDirs) {
				search(subDir, monitor, depth - 1);
			}
		}
	}

	public InterpreterSearcher() {
		this.searchedDirs = new HashSet<IFileHandle>();
		this.found = new ArrayList<IFileHandle>();
		this.types = new ArrayList<IInterpreterInstallType>();
	}

	public void search(IEnvironment environment, String natureId,
			Set<IFileHandle> ignore, int depth, IProgressMonitor monitor) {
		if (natureId == null) {
			throw new IllegalArgumentException();
		}

		this.found.clear();
		this.types.clear();
		this.searchedDirs.clear();

		this.natureId = natureId;
		this.installTypes = ScriptRuntime.getInterpreterInstallTypes(natureId);

		this.ignore = ignore == null ? Collections.<IFileHandle> emptySet()
				: ignore;

		searchFast(monitor == null ? new NullProgressMonitor() : monitor,
				environment, depth);
	}

	public boolean hasResults() {
		return !found.isEmpty();
	}

	public IFileHandle[] getFoundFiles() {
		return found.toArray(new IFileHandle[found.size()]);
	}

	public IInterpreterInstallType[] getFoundInstallTypes() {
		return types.toArray(new IInterpreterInstallType[types.size()]);
	}
}
