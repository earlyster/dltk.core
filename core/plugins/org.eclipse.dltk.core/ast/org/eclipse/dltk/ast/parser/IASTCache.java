package org.eclipse.dltk.ast.parser;

import org.eclipse.dltk.compiler.problem.ProblemCollector;
import org.eclipse.dltk.core.ISourceModule;

public interface IASTCache {
	public static class ASTCacheEntry {
		public IModuleDeclaration module;
		public ProblemCollector problems;
	}

	ASTCacheEntry restoreModule(ISourceModule module);

	void storeModule(ISourceModule module,
			IModuleDeclaration moduleDeclaration, ProblemCollector problems);
}
