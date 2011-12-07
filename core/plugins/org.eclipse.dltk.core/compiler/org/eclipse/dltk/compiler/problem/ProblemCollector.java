/*******************************************************************************
 * Copyright (c) 2008 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.compiler.problem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.compiler.task.ITaskReporter;
import org.eclipse.dltk.core.IScriptModelMarker;
import org.eclipse.dltk.internal.core.util.Util;

public class ProblemCollector extends AbstractProblemReporter implements
		ITaskReporter {

	protected final List<IProblem> problems = new ArrayList<IProblem>();

	public void reset() {
		problems.clear();
	}

	public void reportProblem(IProblem problem) {
		if (!problems.contains(problem)) {
			// FIXME (alex) duplicates happen because of AST caching
			problems.add(problem);
		}
	}

	public void reportTask(String message, int lineNumber, int priority,
			int charStart, int charEnd) {
		reportProblem(new TaskInfo(message, lineNumber, priority, charStart,
				charEnd));
	}

	/**
	 * @return
	 */
	public boolean isEmpty() {
		return problems.isEmpty();
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		if (ITaskReporter.class.equals(adapter)
				|| IProblemReporter.class.equals(adapter)) {
			return this;
		}
		return super.getAdapter(adapter);
	}

	/**
	 * @return
	 */
	public boolean hasErrors() {
		if (!problems.isEmpty()) {
			for (final IProblem problem : problems) {
				if (problem.isError()) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return problems.toString();
	}

	protected static class TaskInfo extends CategorizedProblem {

		private final String message;
		private final int charStart;
		private final int lineNumber;
		private final int charEnd;
		private final int priority;

		/**
		 * @param message
		 * @param lineNumber
		 * @param charEnd
		 * @param charStart
		 */
		public TaskInfo(String message, int lineNumber, int priority,
				int charStart, int charEnd) {
			this.message = message;
			this.lineNumber = lineNumber;
			this.priority = priority;
			this.charEnd = charEnd;
			this.charStart = charStart;
		}

		public int getCategoryID() {
			return 0;
		}

		public boolean isTask() {
			return true;
		}

		public String getMarkerType() {
			return DefaultProblem.MARKER_TYPE_TASK;
		}

		public String[] getArguments() {
			return null;
		}

		public IProblemIdentifier getID() {
			return IProblem.Task;
		}

		public String getMessage() {
			return message;
		}

		public String getOriginatingFileName() {
			return null;
		}

		public int getSourceEnd() {
			return charEnd;
		}

		public int getSourceLineNumber() {
			return lineNumber;
		}

		public int getSourceStart() {
			return charStart;
		}

		public int getPriority() {
			return priority;
		}

		public void setSeverity(ProblemSeverity severity) {
			// unsupported
		}

		public boolean isError() {
			return false;
		}

		public boolean isWarning() {
			return false;
		}

		public void setSourceEnd(int sourceEnd) {
			// unsupported
		}

		public void setSourceLineNumber(int lineNumber) {
			// unsupported
		}

		public void setSourceStart(int sourceStart) {
			// unsupported
		}

		@Override
		public String toString() {
			final StringBuffer sb = new StringBuffer();
			sb.append("Task"); //$NON-NLS-1$
			sb.append(' ');
			sb.append(lineNumber);
			sb.append('[');
			sb.append(charStart);
			sb.append(".."); //$NON-NLS-1$
			sb.append(charEnd);
			sb.append(']');
			sb.append(':');
			if (this.message != null) {
				sb.append(this.message);
			}
			return sb.toString();
		}

	}

	/**
	 * @param destination
	 */
	public void copyTo(IProblemReporter destination) {
		for (final IProblem problem : problems) {
			destination.reportProblem(problem);
		}
	}

	public List<IProblem> getProblems() {
		return Collections.unmodifiableList(problems);
	}

	/**
	 * @return
	 */
	public List<IProblem> getErrors() {
		final List<IProblem> result = new ArrayList<IProblem>();
		for (final IProblem problem : problems) {
			if (problem.isError()) {
				result.add(problem);
			}
		}
		return result;
	}

	/**
	 * @param resource
	 * @param problemFactory
	 * @throws CoreException
	 * @since 3.0
	 */
	public void createMarkers(IResource resource, IProblemFactory problemFactory)
			throws CoreException {
		createMarkers(resource, problemFactory,
				IProblemSeverityTranslator.IDENTITY);
	}

	/**
	 * @param resource
	 * @param problemFactory
	 * @param translator
	 * @throws CoreException
	 * @since 4.0
	 */
	public void createMarkers(IResource resource,
			IProblemFactory problemFactory,
			IProblemSeverityTranslator translator) throws CoreException {
		for (final IProblem problem : problems) {
			ProblemSeverity severity = problem.getSeverity();
			if (!problem.isTask()) {
				severity = translator.getSeverity(problem.getID(), severity);
				if (severity == null || severity == ProblemSeverity.IGNORE) {
					continue;
				}
			}
			final IMarker m = problemFactory.createMarker(resource, problem);
			if (problem.getSourceLineNumber() >= 0) {
				m.setAttribute(IMarker.LINE_NUMBER,
						problem.getSourceLineNumber() + 1);
			}
			m.setAttribute(IMarker.MESSAGE, problem.getMessage());
			if (problem.getSourceStart() >= 0) {
				m.setAttribute(IMarker.CHAR_START, problem.getSourceStart());
			}
			if (problem.getSourceEnd() >= 0) {
				m.setAttribute(IMarker.CHAR_END, problem.getSourceEnd());
			}
			if (!problem.isTask()) {
				m.setAttribute(IMarker.SEVERITY, severity.value);
			} else {
				m.setAttribute(IMarker.USER_EDITABLE, Boolean.FALSE);
				if (problem instanceof TaskInfo) {
					m.setAttribute(IMarker.PRIORITY,
							((TaskInfo) problem).getPriority());
				}
			}
			if (problem.getID() != null) {
				m.setAttribute(IScriptModelMarker.ID,
						DefaultProblemIdentifier.encode(problem.getID()));
			}
			final String[] arguments = problem.getArguments();
			if (arguments != null && arguments.length != 0) {
				m.setAttribute(IScriptModelMarker.ARGUMENTS,
						Util.getProblemArgumentsForMarker(arguments));
			}
		}
	}
}
