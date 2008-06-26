package org.eclipse.dltk.debug.ui.breakpoints;

import java.net.URI;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.environment.EnvironmentPathUtils;
import org.eclipse.dltk.debug.core.model.IScriptBreakpoint;
import org.eclipse.dltk.debug.core.model.IScriptLineBreakpoint;
import org.eclipse.dltk.ui.util.SWTFactory;
import org.eclipse.swt.widgets.Composite;

public class ScriptLineBreakpointPropertyPage extends
		ScriptBreakpointPropertyPage {

	protected void createLocationLabels(Composite parent) throws CoreException {
		super.createLocationLabels(parent);
		IScriptLineBreakpoint breakpoint = (IScriptLineBreakpoint) getBreakpoint();

		// Line number
		int lineNumber = breakpoint.getLineNumber();

		SWTFactory.createLabel(parent, BreakpointMessages.LineNumberLabel, 1);
		SWTFactory.createLabel(parent, Integer.toString(lineNumber), 1);
	}

	protected String getBreakpointLocationLabel() {
		final IScriptBreakpoint breakpoint = getBreakpoint();
		if (breakpoint instanceof IScriptLineBreakpoint) {
			final IScriptLineBreakpoint lineBP = (IScriptLineBreakpoint) breakpoint;
			final IResource resource = lineBP.getResource();
			if (resource != null) {
				return BreakpointMessages.ResourceLabel;
			}
		}
		return super.getBreakpointLocationLabel();
	}

	protected String getBreakpointResourceName() throws CoreException {
		final IScriptBreakpoint breakpoint = getBreakpoint();
		if (breakpoint instanceof IScriptLineBreakpoint) {
			final IScriptLineBreakpoint lineBP = (IScriptLineBreakpoint) breakpoint;
			final IResource resource = lineBP.getResource();
			if (resource != null) {
				return resource.getFullPath().toString();
			}
			final IPath path = lineBP.getResourcePath();
			if (path != null) {
				// TODO add environment ONLY for remote ones
				return EnvironmentPathUtils.getLocalPath(path).toString();
			}
			final URI uri = lineBP.getResourceURI();
			return uri.toString();
		}
		return super.getBreakpointResourceName();
	}
}
