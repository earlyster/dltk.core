package org.eclipse.dltk.internal.ui;

import org.eclipse.jface.internal.text.html.BrowserInput;
import org.eclipse.jface.text.DefaultInformationControl;


/**
 * Provides input for a {@link BrowserInformationControl}.
 *
 * @since 3.4
 */
public abstract class BrowserInformationControlInput extends BrowserInput {

	/**
	 * Returns the leading image width.
	 *
	 * @return the size of the leading image, by default <code>0</code> is returned
	 * @since 3.4
	 */
	public int getLeadingImageWidth() {
		return 0;
	}

	/**
	 * Creates the next browser input with the given input as previous one.
	 *
	 * @param previous the previous input or <code>null</code> if none
	 */
	public BrowserInformationControlInput(BrowserInformationControlInput previous) {
		super(previous);
	}

	/**
	 * @return the HTML contents
	 */
	public abstract String getHtml();

	/**
	 * Returns the HTML from {@link #getHtml()}.
	 * This is a fallback mode for platforms where the {@link BrowserInformationControl}
	 * is not available and this input is passed to a {@link DefaultInformationControl}.
	 *
	 * @return {@link #getHtml()}
	 */
	public String toString() {
		return getHtml();
	}
}
