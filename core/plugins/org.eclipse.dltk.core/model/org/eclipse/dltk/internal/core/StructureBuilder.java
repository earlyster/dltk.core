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
package org.eclipse.dltk.internal.core;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.compiler.task.ITaskReporter;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.builder.IBuildContext;
import org.eclipse.dltk.core.builder.IBuildParticipant;
import org.eclipse.dltk.core.builder.IBuildParticipantExtension;
import org.eclipse.dltk.core.builder.IBuildParticipantPredicate;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.dltk.internal.core.builder.AbstractBuildContext;
import org.eclipse.dltk.internal.core.builder.BuildParticipantManager;

class StructureBuilder {

	private static class ReconcileBuildContext extends AbstractBuildContext {

		final AccumulatingProblemReporter reporter;

		/**
		 * @param module
		 */
		protected ReconcileBuildContext(ISourceModule module,
				AccumulatingProblemReporter reporter) {
			super(module, IBuildContext.RECONCILE_BUILD);
			this.reporter = reporter;
		}

		/*
		 * @see org.eclipse.dltk.core.builder.IBuildContext#getFileHandle()
		 */
		public IFileHandle getFileHandle() {
			return null;
		}

		public IProblemReporter getProblemReporter() {
			return reporter;
		}

		public ITaskReporter getTaskReporter() {
			return reporter;
		}

	}

	static void build(String natureId, ISourceModule module,
			AccumulatingProblemReporter reporter) {
		final NullProgressMonitor monitor = new NullProgressMonitor();
		final IScriptProject project = module.getScriptProject();
		final IBuildParticipant[] participants = beginBuild(natureId, project);
		if (participants.length == 0) {
			return;
		}
		final IBuildParticipantPredicate[] predicates = BuildParticipantManager
				.getPredicates(project, natureId);
		final ReconcileBuildContext context = new ReconcileBuildContext(module,
				reporter);
		try {
			OUTER: for (int k = 0; k < participants.length; ++k) {
				final IBuildParticipant participant = participants[k];
				for (IBuildParticipantPredicate predicate : predicates) {
					if (!predicate.apply(participant, context))
						continue OUTER;
				}
				participant.build(context);
			}
		} catch (CoreException e) {
			DLTKCore.error("error", e); //$NON-NLS-1$
		}
		for (int j = 0; j < participants.length; ++j) {
			final IBuildParticipant participant = participants[j];
			if (participant instanceof IBuildParticipantExtension) {
				((IBuildParticipantExtension) participant).endBuild(monitor);
			}
		}
	}

	private static IBuildParticipant[] beginBuild(String natureId,
			final IScriptProject project) {
		final IBuildParticipant[] participants = BuildParticipantManager
				.getBuildParticipants(project, natureId);
		int count = 0;
		for (int j = 0; j < participants.length; ++j) {
			final IBuildParticipant participant = participants[j];
			final boolean useParticipant;
			if (participant instanceof IBuildParticipantExtension) {
				useParticipant = ((IBuildParticipantExtension) participant)
						.beginBuild(IBuildContext.RECONCILE_BUILD);
			} else {
				useParticipant = true;
			}
			if (useParticipant) {
				if (count < j) {
					participants[count] = participants[j];
				}
				++count;
			}
		}
		return BuildParticipantManager.copyFirst(participants, count);
	}

}
