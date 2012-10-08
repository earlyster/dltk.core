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
package org.eclipse.dltk.testing;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.dltk.testing.model.ITestElement;

/**
 * This interface is obtained as adapter from {@link ITestRunnerUI}.
 */
public interface ITestElementResolver extends IAdaptable {

	TestElementResolution resolveElement(ITestElement element);

}
