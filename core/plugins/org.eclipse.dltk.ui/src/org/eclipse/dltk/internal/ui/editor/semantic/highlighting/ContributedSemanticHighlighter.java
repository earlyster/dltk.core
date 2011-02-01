/*******************************************************************************
 * Copyright (c) 2011 xored software, Inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     xored software, Inc. - initial API and Implementation (Alex Panchenko)
 *******************************************************************************/
package org.eclipse.dltk.internal.ui.editor.semantic.highlighting;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.compiler.env.IModuleSource;
import org.eclipse.dltk.internal.ui.coloring.AbstractColoringPreferenceRequestor;
import org.eclipse.dltk.ui.coloring.ColoringPreferences;
import org.eclipse.dltk.ui.coloring.EnablementStyle;
import org.eclipse.dltk.ui.coloring.FontStyle;
import org.eclipse.dltk.ui.coloring.IColoringPreferenceKey;
import org.eclipse.dltk.ui.coloring.IColoringPreferenceProvider;
import org.eclipse.dltk.ui.editor.highlighting.AbortSemanticHighlightingException;
import org.eclipse.dltk.ui.editor.highlighting.AbstractSemanticHighlighter;
import org.eclipse.dltk.ui.editor.highlighting.ISemanticHighlighter;
import org.eclipse.dltk.ui.editor.highlighting.ISemanticHighlighterExtension;
import org.eclipse.dltk.ui.editor.highlighting.SemanticHighlighting;
import org.eclipse.swt.graphics.RGB;

public class ContributedSemanticHighlighter extends AbstractSemanticHighlighter {

	private final String natureId;
	private final ISemanticHighlighter[] highlighters;

	public ContributedSemanticHighlighter(String natureId,
			ISemanticHighlighter[] highlighters) {
		this.natureId = natureId;
		this.highlighters = highlighters;
	}

	@Override
	protected boolean doHighlighting(IModuleSource code) throws Exception {
		for (ISemanticHighlighter highlighter : highlighters) {
			try {
				highlighter.process(code, this);
			} catch (AbortSemanticHighlightingException e) {
				return false;
			}
		}
		return true;
	}

	private boolean initialized = false;
	private final Map<String, SemanticHighlighting> highlightings = new HashMap<String, SemanticHighlighting>();

	private static class StdSemanticHighlighting extends SemanticHighlighting {

		final IColoringPreferenceKey key;
		final EnablementStyle enablementStyle;

		public StdSemanticHighlighting(IColoringPreferenceKey key,
				EnablementStyle enablementStyle) {
			this.key = key;
			this.enablementStyle = enablementStyle;
		}

		@Override
		public String getPreferenceKey() {
			return key.getColorKey();
		}

		@Override
		public boolean isSemanticOnly() {
			return enablementStyle != EnablementStyle.ALWAYS_ON;
		}

	}

	private static class SemanticHighlightingCollector extends
			AbstractColoringPreferenceRequestor {

		final Map<String, SemanticHighlighting> map = new HashMap<String, SemanticHighlighting>();

		public void addPreference(IColoringPreferenceKey key, String name,
				RGB color, EnablementStyle enablementStyle,
				FontStyle... fontStyles) {
			map.put(key.getColorKey(), new StdSemanticHighlighting(key,
					enablementStyle));
		}

	}

	public SemanticHighlighting[] getSemanticHighlightings() {
		if (!initialized) {
			initialized = true;
			for (ISemanticHighlighter highlighter : highlighters) {
				if (highlighter instanceof ISemanticHighlighterExtension) {
					final SemanticHighlighting[] hls = ((ISemanticHighlighterExtension) highlighter)
							.getSemanticHighlightings();
					if (hls != null) {
						for (SemanticHighlighting hl : hls) {
							highlightings.put(hl.getPreferenceKey(), hl);
						}
					}
				}
			}
			Map<String, SemanticHighlighting> all = null;
			for (ISemanticHighlighter highlighter : highlighters) {
				final String[] hls = highlighter.getHighlightingKeys();
				if (hls != null) {
					for (String hl : hls) {
						if (!highlightings.containsKey(hl)) {
							if (all == null) {
								final SemanticHighlightingCollector collector = new SemanticHighlightingCollector();
								final IColoringPreferenceProvider[] providers = ColoringPreferences
										.getProviders(natureId);
								if (providers != null) {
									for (IColoringPreferenceProvider provider : providers) {
										provider.providePreferences(collector);
									}
								}
								all = collector.map;
							}
							final SemanticHighlighting h = all.get(hl);
							if (h != null) {
								highlightings.put(hl, h);
							}
						}
					}
				}
			}
		}
		return highlightings.values().toArray(
				new SemanticHighlighting[highlightings.size()]);
	}

}
