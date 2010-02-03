package org.eclipse.dltk.core;

import org.eclipse.dltk.ast.declarations.ModuleDeclaration;
import org.eclipse.dltk.ast.parser.ASTCacheManager;
import org.eclipse.dltk.ast.parser.IASTCache;
import org.eclipse.dltk.ast.parser.ISourceParser;
import org.eclipse.dltk.ast.parser.ISourceParserConstants;
import org.eclipse.dltk.ast.parser.ISourceParserExtension;
import org.eclipse.dltk.ast.parser.IASTCache.ASTCacheEntry;
import org.eclipse.dltk.compiler.env.CompilerSourceCode;
import org.eclipse.dltk.compiler.problem.IProblemReporter;
import org.eclipse.dltk.compiler.problem.ProblemCollector;
import org.eclipse.dltk.core.ISourceModuleInfoCache.ISourceModuleInfo;
import org.eclipse.dltk.core.RuntimePerformanceMonitor.PerformanceNode;
import org.eclipse.dltk.internal.core.ModelManager;

public class SourceParserUtil {
	private static final String AST = "ast"; //$NON-NLS-1$
	private static final String ERRORS = "errors"; //$NON-NLS-1$

	private static boolean useASTCaching = true;
	private static boolean useASTPersistenceCaching = true;

	public static ModuleDeclaration getModuleDeclaration(ISourceModule module) {
		return getModuleDeclaration(module, null,
				ISourceParserConstants.DEFAULT);
	}

	public static ModuleDeclaration getModuleDeclaration(ISourceModule module,
			IProblemReporter reporter) {
		return getModuleDeclaration(module, reporter,
				ISourceParserConstants.DEFAULT);
	}

	public static ModuleDeclaration getModuleDeclaration(ISourceModule module,
			IProblemReporter reporter, int flags) {
		ISourceModuleInfoCache sourceModuleInfoCache = ModelManager
				.getModelManager().getSourceModuleInfoCache();
		ISourceModuleInfo sourceModuleInfo = sourceModuleInfoCache.get(module);
		return getModuleDeclaration(module, reporter, sourceModuleInfo, flags);
	}

	public static ModuleDeclaration getModuleDeclaration(ISourceModule module,
			IProblemReporter reporter, ISourceModuleInfo mifo) {
		return getModuleDeclaration(module, reporter, mifo,
				ISourceParserConstants.DEFAULT);
	}

	public static ModuleDeclaration getModuleDeclaration(ISourceModule module,
			IProblemReporter reporter, ISourceModuleInfo mifo, int flags) {

		IDLTKLanguageToolkit toolkit;
		toolkit = DLTKLanguageManager.getLanguageToolkit(module);
		if (toolkit == null) {
			return null;
		}
		ModuleDeclaration moduleDeclaration = null;
		PerformanceNode p1 = RuntimePerformanceMonitor.begin();

		final String errorKey;
		final String astKey;
		if (mifo != null && useASTCaching) {
			errorKey = getKey(ERRORS, flags);
			astKey = getKey(AST, flags);
			moduleDeclaration = (ModuleDeclaration) mifo.get(astKey);
			if (moduleDeclaration != null) {
				if (reporter != null) {
					final ProblemCollector collector = (ProblemCollector) mifo
							.get(errorKey);
					if (collector != null) {
						collector.copyTo(reporter);
					}
				}
			}
			if (moduleDeclaration == null && useASTPersistenceCaching) {
				// Try to retrieve information from persistence cache.
				IASTCache[] providers = ASTCacheManager.getProviders(toolkit
						.getNatureId());
				if (providers != null) {
					for (IASTCache provider : providers) {
						ASTCacheEntry restored = provider.restoreModule(module);
						if (restored != null) {
							if (reporter != null) {
								if (restored.problems != null) {
									restored.problems.copyTo(reporter);
								}
							}
							// Store to local cache.
							mifo.put(astKey, restored.module);
							if (restored.problems != null
									&& !restored.problems.isEmpty()) {
								mifo.put(errorKey, restored.problems);
							} else {
								mifo.remove(errorKey);
							}
							moduleDeclaration = restored.module;
							break;
						}
					}
				}
			}
		} else {
			errorKey = null;
			astKey = null;
		}
		p1.done(toolkit.getNatureId(), "Retrive AST from cache", 0);
		if (moduleDeclaration == null) {
			p1.renew();
			ISourceParser sourceParser = DLTKLanguageManager.getSourceParser(
					module.getScriptProject().getProject(), toolkit
							.getNatureId());
			if (sourceParser != null) {
				if (sourceParser instanceof ISourceParserExtension) {
					((ISourceParserExtension) sourceParser).setFlags(flags);
				}
				final ProblemCollector collector = mifo != null ? new ProblemCollector()
						: null;
				try {
					char[] sourceAsCharArray = module.getSourceAsCharArray();
					moduleDeclaration = sourceParser.parse(module.getPath()
							.toString().toCharArray(), sourceAsCharArray,
							collector != null ? collector : reporter);
					if (collector != null && reporter != null) {
						collector.copyTo(reporter);
					}
				} catch (ModelException e) {
					if (DLTKCore.DEBUG) {
						final String msg = Messages.SourceParserUtil_errorRetrievingContent;
						DLTKCore.error(msg, e);
					}
				}
				p1.done(toolkit.getNatureId(), "AST parse time", 0);
				if (moduleDeclaration != null && mifo != null && useASTCaching) {
					mifo.put(astKey, moduleDeclaration);
					if (useASTPersistenceCaching) {
						// Store to persistence cache
						IASTCache[] providers = ASTCacheManager
								.getProviders(toolkit.getNatureId());
						if (providers != null) {
							for (IASTCache provider : providers) {
								provider.storeModule(module, moduleDeclaration,
										collector);
							}
						}
					}
					if (collector != null && !collector.isEmpty()) {
						mifo.put(errorKey, collector);
					} else {
						mifo.remove(errorKey);
					}
				}
			}
		}
		return moduleDeclaration;
	}

	/**
	 * @param baseKey
	 * @param flags
	 * @return
	 */
	private static String getKey(String baseKey, int flags) {
		return flags == 0 ? baseKey : baseKey + flags;
	}

	public static ModuleDeclaration getModuleDeclaration(String filename,
			char[] content, String nature, IProblemReporter reporter,
			ISourceModuleInfo mifo) {
		return getModuleDeclaration(filename, content, nature, reporter, mifo,
				ISourceParserConstants.DEFAULT);
	}

	public static ModuleDeclaration getModuleDeclaration(String filename,
			char[] content, String nature, IProblemReporter reporter,
			ISourceModuleInfo mifo, int flags) {
		ISourceParser sourceParser = DLTKLanguageManager
				.getSourceParser(nature);
		if (sourceParser instanceof ISourceParserExtension) {
			((ISourceParserExtension) sourceParser).setFlags(flags);
		}
		ModuleDeclaration moduleDeclaration = getModuleFromCache(mifo, flags,
				reporter);
		if (moduleDeclaration == null) {
			final ProblemCollector collector = mifo != null ? new ProblemCollector()
					: null;
			moduleDeclaration = sourceParser.parse(filename.toCharArray(),
					content, collector != null ? collector : reporter);
			if (collector != null && reporter != null) {
				collector.copyTo(reporter);
			}
			putModuleToCache(mifo, moduleDeclaration, flags, collector);
		}
		return moduleDeclaration;
	}

	/**
	 * This is for use in parsers.
	 */
	public static ModuleDeclaration getModuleFromCache(ISourceModuleInfo mifo,
			int flags, IProblemReporter reporter) {
		if (mifo != null && useASTCaching) {
			final ModuleDeclaration moduleDeclaration = (ModuleDeclaration) mifo
					.get(getKey(AST, flags));
			if (moduleDeclaration != null && reporter != null) {
				final ProblemCollector collector = (ProblemCollector) mifo
						.get(getKey(ERRORS, flags));
				if (collector != null) {
					collector.copyTo(reporter);
				}
			}
			return moduleDeclaration;
		}
		return null;
	}

	public static void putModuleToCache(ISourceModuleInfo info,
			ModuleDeclaration module, int flags, ProblemCollector collector) {
		if (info != null && useASTCaching) {
			info.put(getKey(AST, flags), module);
			final String errorKey = getKey(ERRORS, flags);
			if (collector != null && !collector.isEmpty()) {
				info.put(errorKey, collector);
			} else {
				info.remove(errorKey);
			}
		}
	}

	public static void parseSourceModule(final ISourceModule module,
			ISourceElementParser parser) {
		PerformanceNode p = RuntimePerformanceMonitor.begin();
		ISourceModuleInfoCache sourceModuleInfoCache = ModelManager
				.getModelManager().getSourceModuleInfoCache();
		ISourceModuleInfo mifo = sourceModuleInfoCache.get(module);
		int len = 0;
		if (module instanceof org.eclipse.dltk.compiler.env.ISourceModule) {
			parser.parseSourceModule(
					(org.eclipse.dltk.compiler.env.ISourceModule) module, mifo);
		} else {
			try {
				String source = module.getSource();
				len = source.length();
				parser.parseSourceModule(new CompilerSourceCode(source), mifo);
			} catch (ModelException ex) {
				final String msg = Messages.SourceParserUtil_errorRetrievingContent;
				DLTKCore.error(msg, ex);
			}
		}
		p.done(DLTKLanguageManager.getLanguageToolkit(module).getNatureId(),
				"Source Element parser", len);
	}

	/**
	 * Performance testing only
	 */
	public static void disableCache() {
		useASTCaching = false;
	}

	public static void enableCache() {
		useASTCaching = true;
	}

	public static void clearCache() {
		ModelManager.getModelManager().getSourceModuleInfoCache().clear();
		ModelManager.getModelManager().getFileCache().clear();
	}
}
