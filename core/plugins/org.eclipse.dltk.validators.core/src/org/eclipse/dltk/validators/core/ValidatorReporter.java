package org.eclipse.dltk.validators.core;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

import org.eclipse.dltk.compiler.CharOperation;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceRange;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.builder.ISourceLineTracker;
import org.eclipse.dltk.utils.TextUtils;

import java.util.HashMap;
import java.util.Map;

/**
 */
public class ValidatorReporter implements IValidatorReporter {

	private Map<ISourceModule, ISourceLineTracker> lineTrackers = 
			new HashMap<ISourceModule, ISourceLineTracker>();

	private String markerId;
	private boolean underline;

	public ValidatorReporter(String markerId, boolean underline) {
		this.markerId = markerId;
		this.underline = underline;
	}

	public ValidatorReporter(String markerId) {
		this(markerId, true);
	}

	public IMarker report(ISourceModule module, IValidatorProblem problem)
			throws CoreException {
		IMarker marker = report(module.getResource(), problem);

		if (marker != null && underline) {
			ISourceLineTracker lineTracker = getLineTracker(module);

			int lineNo = problem.getLineNumber();
			ISourceRange range = lineTracker.getLineInformation(lineNo - 1);

			String source = module.getSource();
			ISourceRange adjusted = TextUtils.trimWhitespace(source, range);

			marker.setAttribute(IMarker.CHAR_START, adjusted.getOffset());
			marker.setAttribute(IMarker.CHAR_END, adjusted.getOffset()
					+ adjusted.getLength());
		}

		return marker;
	}

	public IMarker report(IResource resource, IValidatorProblem problem)
			throws CoreException {
		if (problem == null) {
			return null;
		}

		return createMarker(resource, problem);
	}

	protected ISourceLineTracker getLineTracker(ISourceModule module) {
		ISourceLineTracker lineTracker = lineTrackers.get(module);
		if (lineTracker == null) {
			char[] source;
			try {
				source = module.getSourceAsCharArray();
				if (source == null) {
					source = CharOperation.NO_CHAR;
				}
			} catch (ModelException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}

				source = CharOperation.NO_CHAR;
			}

			lineTracker = TextUtils.createLineTracker(source);
			lineTrackers.put(module, lineTracker);
		}

		return lineTracker;
	}

	private IMarker createMarker(IResource resource, IValidatorProblem problem)
			throws CoreException {

		IMarker marker = resource.createMarker(markerId);

		marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
		marker.setAttribute(IMarker.LINE_NUMBER, problem.getLineNumber());
		marker.setAttribute(IMarker.SEVERITY, getSeverity(problem));

		Map<String, Object> attributes = problem.getAttributes();
		for (String key : attributes.keySet()) {
			marker.setAttribute(key, attributes.get(key));
		}

		return marker;
	}

	private int getSeverity(IValidatorProblem problem) {
		int severity = IMarker.SEVERITY_INFO;

		if (problem.isWarning()) {
			severity = IMarker.SEVERITY_WARNING;
		} else if (problem.isError()) {
			severity = IMarker.SEVERITY_ERROR;
		}

		return severity;
	}

}
