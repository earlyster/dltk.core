package org.eclipse.dltk.debug.ui;

import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.PatternMatchEvent;

public class ScriptDebugConsoleTraceTracker extends ScriptDebugConsoleTracker {

	private Pattern pattern;
	private final String patternString;

	public ScriptDebugConsoleTraceTracker() {
		patternString = "\\t*#\\d+ +file:(.*) \\[(\\d+)\\]";
	}

	public ScriptDebugConsoleTraceTracker(String patternString) {
		this.patternString = patternString;
	}

	public void matchFound(PatternMatchEvent event) {
		try {
			int offset = event.getOffset();
			int length = event.getLength();
			ScriptDebuggerConsoleToFileHyperlink link = new ScriptDebuggerConsoleToFileHyperlink(
					console, getPatternInstance());
			console.addHyperlink(link, link.computeOffset(offset, length,
					console), link.computeLength(offset, length, console));

		} catch (BadLocationException e) {
		}
	}

	private Pattern getPatternInstance() {
		if (pattern == null) {
			pattern = Pattern.compile(getPattern(), getCompilerFlags());
		}
		return pattern;
	}

	public String getPattern() {
		return patternString; //$NON-NLS-1$
	}

}
