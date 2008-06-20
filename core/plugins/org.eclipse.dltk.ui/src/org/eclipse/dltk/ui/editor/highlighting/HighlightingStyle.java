package org.eclipse.dltk.ui.editor.highlighting;

import org.eclipse.jface.text.TextAttribute;

/**
 * Highlighting Style.
 */
public class HighlightingStyle {

	/** Text attribute */
	private TextAttribute fTextAttribute;
	/** Enabled state */
	private boolean fIsEnabled;
	private SemanticHighlighting semanticHighlighting;

	/**
	 * Initialize with the given text attribute.
	 * 
	 * @param textAttribute
	 *            The text attribute
	 * @param isEnabled
	 *            the enabled state
	 * @param semanticHighlighting
	 */
	public HighlightingStyle(TextAttribute textAttribute, boolean isEnabled,
			SemanticHighlighting semanticHighlighting) {
		setTextAttribute(textAttribute);
		setEnabled(isEnabled);
		this.semanticHighlighting = semanticHighlighting;
	}

	/**
	 * @return Returns the text attribute.
	 */
	public TextAttribute getTextAttribute() {
		return fTextAttribute;
	}

	/**
	 * @param textAttribute
	 *            The background to set.
	 */
	public void setTextAttribute(TextAttribute textAttribute) {
		fTextAttribute = textAttribute;
	}

	/**
	 * @return the enabled state
	 */
	public boolean isEnabled() {
		return fIsEnabled;
	}

	/**
	 * @param isEnabled
	 *            the new enabled state
	 */
	public void setEnabled(boolean isEnabled) {
		fIsEnabled = isEnabled;
	}

	public SemanticHighlighting getSemaHighlighting() {
		return this.semanticHighlighting;
	}
}
