/*******************************************************************************
 * Copyright (c) 2006, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.dltk.ui.editor.saveparticipant;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.ui.IDLTKStatusConstants;
import org.eclipse.dltk.ui.DLTKUIPlugin;
import org.eclipse.dltk.utils.PriorityNatureExtensionManager;
import org.eclipse.jface.text.IRegion;
import org.eclipse.osgi.util.NLS;

/**
 * A registry for save participants. This registry manages
 * {@link SaveParticipantDescriptor}s and keeps track of enabled save
 * participants.
 * <p>
 * Save participants can be enabled and disabled on the Java &gt; Editor &gt;
 * Save Participants preference page. Enabled save participants are notified
 * through a call to
 * {@link IPostSaveListener#saved(org.eclipse.jdt.core.ICompilationUnit, IRegion[], org.eclipse.core.runtime.IProgressMonitor)}
 * whenever the {@link CompilationUnitDocumentProvider} saves a compilation unit
 * that is in the workspace.
 * </p>
 * <p>
 * An instance of this registry can be received through a call to
 * {@link JavaPlugin#getSaveParticipantRegistry()}.
 * </p>
 * 
 * @since 3.0
 */
public final class SaveParticipantRegistry extends
		PriorityNatureExtensionManager<IPostSaveListener> {

	private static final IPostSaveListener[] EMPTY_ARRAY = new IPostSaveListener[0];

	/**
	 * Creates a new instance.
	 */
	public SaveParticipantRegistry() {
		super(DLTKUIPlugin.PLUGIN_ID + ".saveParticipants",
				IPostSaveListener.class);
	}

	@Override
	protected boolean isValidElement(IConfigurationElement element) {
		return "saveParticipant".equals(element.getName());
	}

	/**
	 * Returns an array of <code>IPostSaveListener</code> which are enabled in
	 * the given context.
	 * 
	 * @param context
	 *            the context from which to retrieve the settings from, not null
	 * @return the current enabled post save listeners according to the
	 *         preferences
	 */
	public IPostSaveListener[] getEnabledPostSaveListeners(ISourceModule module) {
		ArrayList<IPostSaveListener> result = null;
		final IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(module);
		if (toolkit != null) {
			for (IPostSaveListener descriptor : getInstances(toolkit
					.getNatureId())) {
				if (descriptor.isEnabled(module)) {
					if (result == null) {
						result = new ArrayList<IPostSaveListener>();
					}
					result.add(descriptor);
				}
			}
		}
		if (result == null) {
			return EMPTY_ARRAY;
		} else {
			return result.toArray(new IPostSaveListener[result.size()]);
		}
	}

	@Override
	protected IPostSaveListener[] createEmptyResult() {
		return EMPTY_ARRAY;
	}

	/**
	 * Tells whether one of the active post save listeners needs to be informed
	 * about the changed region in this save cycle.
	 * 
	 * @param unit
	 *            the unit which is about to be saved
	 * @return true if the change regions need do be determined
	 * @throws CoreException
	 *             if something went wrong
	 */
	public static boolean isChangedRegionsRequired(final ISourceModule unit,
			IPostSaveListener[] listeners) throws CoreException {
		String message = SaveParticipantMessages.SaveParticipantRegistry_needsChangedRegionFailed;
		final MultiStatus errorStatus = new MultiStatus(DLTKUIPlugin.PLUGIN_ID,
				IDLTKStatusConstants.EDITOR_CHANGED_REGION_CALCULATION,
				message, null);

		try {
			final boolean result[] = new boolean[] { false };
			for (int i = 0; i < listeners.length; i++) {
				final IPostSaveListener listener = listeners[i];
				SafeRunner.run(new ISafeRunnable() {

					public void run() throws Exception {
						if (listener.needsChangedRegions(unit))
							result[0] = true;
					}

					public void handleException(Throwable ex) {
						String msg = NLS
								.bind("The save participant ''{0}'' caused an exception.", listener.getId()); //$NON-NLS-1$
						DLTKUIPlugin
								.log(new Status(
										IStatus.ERROR,
										DLTKUIPlugin.PLUGIN_ID,
										IDLTKStatusConstants.EDITOR_POST_SAVE_NOTIFICATION,
										msg, ex));

						final String participantName = listener.getName();
						msg = NLS
								.bind(SaveParticipantMessages.SaveParticipantRegistry_needsChangedRegionCausedException,
										participantName, ex.toString());
						errorStatus
								.add(new Status(
										IStatus.ERROR,
										DLTKUIPlugin.PLUGIN_ID,
										IDLTKStatusConstants.EDITOR_CHANGED_REGION_CALCULATION,
										msg, null));
					}

				});
				if (result[0])
					return true;
			}
		} finally {
			if (!errorStatus.isOK())
				throw new CoreException(errorStatus);
		}

		return false;
	}

}
