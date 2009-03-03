package org.eclipse.dltk.launching.sourcelookup;

import java.net.URI;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.sourcelookup.AbstractSourceLookupParticipant;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.debug.core.DLTKDebugConstants;
import org.eclipse.dltk.internal.core.DefaultWorkingCopyOwner;
import org.eclipse.dltk.internal.core.ScriptProject;
import org.eclipse.dltk.internal.debug.core.model.ScriptStackFrame;
import org.eclipse.dltk.internal.launching.LaunchConfigurationUtils;

/**
 * This class is used to get source from DBGP remote debugger, if path starts
 * with DBGP scheme.
 * 
 * @author haiodo
 */
public class DBGPSourceLookupParticipant extends
		AbstractSourceLookupParticipant {

	public String getSourceName(Object object) throws CoreException {
		if (object instanceof ScriptStackFrame) {
			final ScriptStackFrame frame = (ScriptStackFrame) object;
			final URI uri = frame.getSourceURI();
			if (DLTKDebugConstants.DBGP_SCHEME
					.equalsIgnoreCase(uri.getScheme())) {
				return uri.getPath();
			}
		}
		return null;
	}

	public Object[] findSourceElements(Object object) throws CoreException {
		if (object instanceof ScriptStackFrame) {
			final ScriptStackFrame frame = (ScriptStackFrame) object;
			final URI uri = frame.getSourceURI();
			if (DLTKDebugConstants.DBGP_SCHEME
					.equalsIgnoreCase(uri.getScheme())) {
				final ILaunchConfiguration launchConfiguration = this
						.getDirector().getLaunchConfiguration();
				final IProject project = LaunchConfigurationUtils
						.getProject(launchConfiguration);
				final ScriptProject scriptProject = (ScriptProject) DLTKCore
						.create(project);
				return new Object[] { new DBGPSourceModule(scriptProject, uri
						.getPath(), DefaultWorkingCopyOwner.PRIMARY, frame) };
			}
		}
		return null;
	}
}
