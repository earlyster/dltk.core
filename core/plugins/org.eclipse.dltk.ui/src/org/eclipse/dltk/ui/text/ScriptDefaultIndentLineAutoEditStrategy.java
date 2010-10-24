/*******************************************************************************
 * Copyright (c) 2010 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.ui.text;

import org.eclipse.dltk.ui.CodeFormatterConstants;
import org.eclipse.dltk.ui.text.util.AutoEditUtils;
import org.eclipse.dltk.ui.text.util.TabStyle;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextUtilities;

public class ScriptDefaultIndentLineAutoEditStrategy extends DefaultIndentLineAutoEditStrategy {
    protected final IPreferenceStore fPreferenceStore;

    public ScriptDefaultIndentLineAutoEditStrategy(IPreferenceStore fPreferenceStore) {
        this.fPreferenceStore = fPreferenceStore;
    }

    protected int getIndentSize() {
        return fPreferenceStore.getInt(CodeFormatterConstants.FORMATTER_INDENTATION_SIZE);
    }

    protected TabStyle getTabStyle() {
        return TabStyle.forName(fPreferenceStore.getString(CodeFormatterConstants.FORMATTER_TAB_CHAR), TabStyle.TAB);
    }

    protected String getIndent() {
        if (getTabStyle() == TabStyle.SPACES) {
            return AutoEditUtils.getNSpaces(getIndentSize());
        } else
            return "\t"; //$NON-NLS-1$
    }

    protected boolean isLineDelimiter(IDocument document, String text) {
        String[] delimiters = document.getLegalLineDelimiters();
        return delimiters != null && TextUtilities.equals(delimiters, text) > -1;
    }
}
