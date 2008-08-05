package org.eclipse.dltk.console.ui;

import java.util.StringTokenizer;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * @author ssanders
 */
public class AnsiColorHelper {

	public static interface IAnsiColorHandler {

		public void handleText(int start, String content, boolean isInput,
				boolean isError) throws BadLocationException;

		public void processingComplete(int start, int length);

	}

	public static final Color COLOR_BLACK = new Color(Display.getCurrent(), 0,
			0, 0);
	public static final Color COLOR_BLUE = new Color(Display.getCurrent(), 0,
			0, 255);
	public static final Color COLOR_CYAN = new Color(Display.getCurrent(), 0,
			255, 255);
	public static final Color COLOR_GREEN = new Color(Display.getCurrent(), 0,
			255, 0);
	public static final Color COLOR_MAGENTA = new Color(Display.getCurrent(),
			255, 0, 255);
	public static final Color COLOR_RED = new Color(Display.getCurrent(), 255,
			0, 0);
	public static final Color COLOR_WHITE = new Color(Display.getCurrent(),
			255, 255, 255);
	public static final Color COLOR_YELLOW = new Color(Display.getCurrent(),
			255, 255, 0);

	private static final StyleRange DEFAULT_ERROR = new StyleRange(-1, -1,
			COLOR_RED, null, SWT.BOLD);
	private static final StyleRange DEFAULT_OUTPUT = new StyleRange(-1, -1,
			COLOR_BLUE, null);

	private StyleRange defaultOutput;
	private StyleRange defaultError;

	private boolean enabled = true;
	private boolean bright;
	private boolean dim;
	private boolean underscore;
	private boolean blink;
	private boolean reverse;
	private boolean hidden;
	private Color foreground;
	private Color background;

	public AnsiColorHelper(StyleRange defaultOutput, StyleRange defaultError) {
		this.defaultOutput = ((defaultOutput != null) ? defaultOutput
				: DEFAULT_OUTPUT);
		this.defaultError = ((defaultError != null) ? defaultError
				: DEFAULT_ERROR);
	}

	public AnsiColorHelper() {
		this(DEFAULT_OUTPUT, DEFAULT_ERROR);
	}

	public void reset() {
		bright = false;
		dim = false;
		underscore = false;
		blink = false;
		reverse = false;
		hidden = false;
		foreground = null;
		background = null;
	}

	public StyleRange resolveStyleRange(int start, int length, boolean isError) {
		StyleRange styleRange;

		Color curForeground = foreground;
		Color curBackground = background;
		int curFontStyle = 0;
		if (bright == true) {
			curFontStyle = (curFontStyle | SWT.BOLD);
		}
		if (dim == true) {
			curFontStyle = (curFontStyle | SWT.ITALIC);
		}

		if (curForeground == null) {
			StyleRange curDefault = ((isError == true) ? defaultError
					: defaultOutput);

			curForeground = curDefault.foreground;
			curBackground = curDefault.background;
			if (curFontStyle == 0) {
				curFontStyle = curDefault.fontStyle;
			}
		}

		if (hidden == true) {
			curForeground = curBackground;
		} else if (reverse == true) {
			Color oldForeground = curForeground;
			curForeground = curBackground;
			curBackground = oldForeground;
		}

		styleRange = new StyleRange(start, length, curForeground,
				curBackground, curFontStyle);
		styleRange.underline = underscore;
		styleRange.strikeout = blink;

		return styleRange;
	}

	private void decodeAnsi(String ansiCode) {
		StringTokenizer tokenizer = new StringTokenizer(ansiCode, ";"); //$NON-NLS-1$
		String token;
		while (tokenizer.hasMoreTokens() == true) {
			token = tokenizer.nextToken();

			if ("0".equals(token) == true) { //$NON-NLS-1$
				reset();
			} else if ("1".equals(token) == true) { //$NON-NLS-1$
				bright = true;
			} else if ("2".equals(token) == true) { //$NON-NLS-1$
				dim = true;
			} else if ("4".equals(token) == true) { //$NON-NLS-1$
				underscore = true;
			} else if ("5".equals(token) == true) { //$NON-NLS-1$
				blink = true;
			} else if ("7".equals(token) == true) { //$NON-NLS-1$
				reverse = true;
			} else if ("8".equals(token) == true) { //$NON-NLS-1$
				hidden = true;
			} else if ("30".equals(token) == true) { //$NON-NLS-1$
				foreground = COLOR_BLACK;
			} else if ("31".equals(token) == true) { //$NON-NLS-1$
				foreground = COLOR_RED;
			} else if ("32".equals(token) == true) { //$NON-NLS-1$
				foreground = COLOR_GREEN;
			} else if ("33".equals(token) == true) { //$NON-NLS-1$
				foreground = COLOR_YELLOW;
			} else if ("34".equals(token) == true) { //$NON-NLS-1$
				foreground = COLOR_BLUE;
			} else if ("35".equals(token) == true) { //$NON-NLS-1$
				foreground = COLOR_MAGENTA;
			} else if ("36".equals(token) == true) { //$NON-NLS-1$
				foreground = COLOR_CYAN;
			} else if ("37".equals(token) == true) { //$NON-NLS-1$
				foreground = COLOR_WHITE;
			} else if ("40".equals(token) == true) { //$NON-NLS-1$
				background = COLOR_BLACK;
			} else if ("41".equals(token) == true) { //$NON-NLS-1$
				background = COLOR_RED;
			} else if ("42".equals(token) == true) { //$NON-NLS-1$
				background = COLOR_GREEN;
			} else if ("43".equals(token) == true) { //$NON-NLS-1$
				background = COLOR_YELLOW;
			} else if ("44".equals(token) == true) { //$NON-NLS-1$
				background = COLOR_BLUE;
			} else if ("45".equals(token) == true) { //$NON-NLS-1$
				background = COLOR_MAGENTA;
			} else if ("46".equals(token) == true) { //$NON-NLS-1$
				background = COLOR_CYAN;
			} else if ("47".equals(token) == true) { //$NON-NLS-1$
				background = COLOR_WHITE;
			}
		}
	}

	public void processText(int originalOffset, String content,
			boolean isInput, boolean isError, IAnsiColorHandler handler)
			throws BadLocationException {
		int start = 0;
		int adjust = 0;

		if (enabled == true) {
			int ansiCnt;
			boolean isAnsi;
			char curChar;
			String ansiCode;
			String curContent;

			for (int cnt = 0, max = content.length(); cnt < max; cnt++) {
				if (content.charAt(cnt) == '[') {
					ansiCnt = (cnt + 1);
					isAnsi = false;
					while (ansiCnt < max) {
						curChar = content.charAt(ansiCnt);
						if ((curChar == ';')
								|| ((curChar >= '0') && (curChar <= '9'))) {
							ansiCnt++;
						} else if (curChar == 'm') {
							isAnsi = true;
							break;
						} else {
							isAnsi = false;
							break;
						}
					}

					if (isAnsi == true) {
						curContent = content.substring(start, cnt);
						handler.handleText((originalOffset + start - adjust),
								curContent, isInput, isError);

						ansiCode = content.substring((cnt + 1), ansiCnt);
						decodeAnsi(ansiCode);
						adjust += ((ansiCnt - cnt) + 1);

						cnt = ansiCnt;
						start = (ansiCnt + 1);
					}
				}
			}
		}

		if (start < content.length()) {
			String overflowContent = ((start == 0) ? content : content
					.substring(start));
			handler.handleText((originalOffset + start - adjust),
					overflowContent, isInput, isError);
		}

		handler.processingComplete(originalOffset, (content.length() - adjust));
	}

	public void disableWhile(Runnable runnable) {
		boolean oldEnabled = enabled;
		enabled = false;
		try {
			runnable.run();
		} finally {
			enabled = oldEnabled;
		}
	}

}
