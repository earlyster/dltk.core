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
package org.eclipse.dltk.internal.core.structure;

import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.search.indexing.core.AbstractProjectIndexer;
import org.eclipse.dltk.internal.core.search.DLTKSearchDocument;

public class StructureProjectIndexer extends AbstractProjectIndexer {
	public void doIndexing(DLTKSearchDocument document, ISourceModule module) {
		if (disabledNatures.contains(DLTKLanguageManager.getLanguageToolkit(
				module).getNatureId())) {
			return;
		}
		new StructureIndexer(document, module).indexDocument();
	}
}
