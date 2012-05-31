package org.eclipse.dltk.ui.text.heredoc;

import org.eclipse.dltk.ui.text.ScriptPresentationReconciler;
import org.eclipse.jface.text.presentation.IPresentationDamager;
import org.eclipse.jface.text.presentation.IPresentationRepairer;

/**
 * A slightly modified version of a <code>ScriptPresentationReconciler</code>
 * that knows how to properly handle heredoc presentation reconciliation.
 * 
 * <p>
 * There is no need to use this presentation reconciler if heredoc is not
 * supported by the underlying dynamic language.
 * </p>
 * 
 * @see HereDocPartitionRule
 * @see HereDocEnabledPartitioner
 * @see HereDocEnabledPartitionScanner
 */
public class HereDocEnabledPresentationReconciler extends
		ScriptPresentationReconciler {

	@Override public IPresentationDamager getDamager(String contentType) {
		if (HereDocUtils.isHereDocContent(contentType)) {
			contentType = HereDocUtils.getPartition(contentType);
		}

		return super.getDamager(contentType);
	}

	@Override public IPresentationRepairer getRepairer(String contentType) {
		if (HereDocUtils.isHereDocContent(contentType)) {
			contentType = HereDocUtils.getPartition(contentType);
		}

		return super.getRepairer(contentType);
	}
}