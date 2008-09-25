/*******************************************************************************
 * Copyright (c) 2000, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.validators.internal.core;

import org.eclipse.osgi.util.NLS;

public class ValidatorMessages extends NLS {
	private static final String BUNDLE_NAME = ValidatorMessages.class.getName();
	public static String ValidatorDefinitionsContainer_failedToLoadValidatorFromXml;
	public static String ValidatorDefinitionsContainer_unknownValidatorType;
	public static String ValidatorRuntime_badFormat;
	public static String ValidatorBuilder_buildingModules;
	public static String ValidatorBuilder_buildModuleSubTask;
	public static String ValidatorBuilder_buildExternalModuleSubTask;
	public static String ValidatorBuilder_clearingResourceMarkers;
	public static String ValidatorBuilder_errorDeleteResourceMarkers;
	public static String ValidatorBuilder_finalizeBuild;
	public static String ValidatorBuilder_InitializeBuilders;
	public static String ValidatorBuilder_unknownError;
	public static String ValidatorsCore_exception;
	

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, ValidatorMessages.class);
	}
}
