package org.eclipse.dltk.compiler.problem;

public interface IProblemSeverityTranslator {

	ProblemSeverity getSeverity(IProblemIdentifier problemId,
			ProblemSeverity defaultServerity);

	/**
	 * Implementation of {@link IProblemSeverityTranslator} which always returns
	 * the default value.
	 */
	static IProblemSeverityTranslator IDENTITY = new IProblemSeverityTranslator() {
		public ProblemSeverity getSeverity(IProblemIdentifier problemId,
				ProblemSeverity defaultSeverity) {
			if (defaultSeverity == null
					|| defaultSeverity == ProblemSeverity.DEFAULT) {
				return ProblemSeverity.WARNING;
			} else {
				return defaultSeverity;
			}
		}
	};

}
