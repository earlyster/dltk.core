/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 
 *******************************************************************************/
package org.eclipse.dltk.internal.core;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.ElementChangedEvent;
import org.eclipse.dltk.core.IElementChangedListener;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IModelElementDelta;
import org.eclipse.dltk.core.IModelElementVisitor;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ISourceModuleInfoCache;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.internal.core.util.LRUCache;

/**
 * Used to cache some source module information. All information related to
 * source module are removed, then source module are changed.
 * 
 * @author haiodo
 */
public class SourceModuleInfoCache extends OverflowingLRUCache implements
		ISourceModuleInfoCache {
	static long allAccess = 0;
	static long miss = 0;
	static long closes = 0;

	public SourceModuleInfoCache() {
		super(ModelCache.DEFAULT_ROOT_SIZE * 50);
		setLoadFactor(0.90);
	}

	@Override
	protected boolean close(LRUCacheEntry entry) {
		++closes;
		return true;
	}

	public void start() {
		DLTKCore.addElementChangedListener(changedListener);
	}

	public void stop() {
		DLTKCore.removeElementChangedListener(changedListener);
	}

	private final ISourceModuleInfo cacheGet(ISourceModule module) {
		allAccess++;
		@SuppressWarnings("unchecked")
		final SoftReference<ISourceModuleInfo> ref = (SoftReference<ISourceModuleInfo>) super
				.get(module);
		return ref != null ? (ISourceModuleInfo) ref.get() : null;
	}

	public synchronized ISourceModuleInfo get(ISourceModule module) {
		if (DLTKCore.VERBOSE) {
			System.out.println("Filling ratio:" + fillingRatio()); //$NON-NLS-1$
		}
		ISourceModuleInfo info = cacheGet(module);
		if (info == null) {
			miss++;
			info = new SourceModuleInfo();
			put(module, new SoftReference<ISourceModuleInfo>(info));
			return info;
		}
		// this.cache.printStats();
		if (DLTKCore.PERFOMANCE) {
			System.out.println("SourceModuleInfoCache: access:" + allAccess //$NON-NLS-1$
					+ " ok:" + (100.0f * (allAccess - miss) / allAccess) //$NON-NLS-1$
					+ "% closes:" + closes); //$NON-NLS-1$
			System.out.println("Filling ratio:" + fillingRatio()); //$NON-NLS-1$
		}
		return info;
	}

	private IElementChangedListener changedListener = new IElementChangedListener() {
		public void elementChanged(ElementChangedEvent event) {
			IModelElementDelta delta = event.getDelta();
			processDelta(delta);
		}

		private void processDelta(IModelElementDelta delta) {
			IModelElement element = delta.getElement();
			if (delta.getKind() == IModelElementDelta.REMOVED
					|| delta.getKind() == IModelElementDelta.CHANGED) {
				if (element.getElementType() == IModelElement.SOURCE_MODULE) {
					if (delta.getKind() == IModelElementDelta.REMOVED
							|| isContentChanged(delta)
					// || isWorkingCopy(delta)
					) {
						if (DEBUG) {
							System.out
									.println("[Cache] remove: kind=" + delta.getKind() + " flags=" + Integer.toHexString(delta.getFlags()) + " elementName=" + delta.getElement().getElementName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
						SourceModuleInfoCache.this
								.remove((ISourceModule) element);
					} else if (DEBUG) {
						System.out
								.println("[Cache] skip delta: kind=" + delta.getKind() + " flags=" + Integer.toHexString(delta.getFlags()) + " elementName=" + delta.getElement().getElementName()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
				}
				if (element.getElementType() == ISourceModule.PROJECT_FRAGMENT) {
					if (delta.getAffectedChildren().length == 0) {
						IProjectFragment fragment = (IProjectFragment) element;
						try {
							fragment.accept(new IModelElementVisitor() {
								public boolean visit(IModelElement element) {
									if (element.getElementType() == ISourceModule.SOURCE_MODULE) {
										SourceModuleInfoCache.this
												.remove((ISourceModule) element);
										return false;
									}
									return true;
								}
							});
						} catch (ModelException e) {
							if (DLTKCore.DEBUG) {
								e.printStackTrace();
							}
						}
					}

				}
			}
			if ((delta.getFlags() & IModelElementDelta.F_CHILDREN) != 0) {
				IModelElementDelta[] affectedChildren = delta
						.getAffectedChildren();
				for (int i = 0; i < affectedChildren.length; i++) {
					IModelElementDelta child = affectedChildren[i];
					processDelta(child);
				}
			}
		}

		private final boolean isContentChanged(IModelElementDelta delta) {
			return (delta.getFlags() & (IModelElementDelta.F_CONTENT | IModelElementDelta.F_FINE_GRAINED)) == IModelElementDelta.F_CONTENT;
		}

		private final boolean isWorkingCopy(IModelElementDelta delta) {
			return (delta.getFlags() & IModelElementDelta.F_PRIMARY_WORKING_COPY) != 0;
		}
	};

	private static class SourceModuleInfo implements ISourceModuleInfo {
		private Map<Object, Object> map;

		public synchronized Object get(Object key) {
			if (map == null) {
				return null;
			}
			return map.get(key);
		}

		public synchronized void put(Object key, Object value) {
			if (map == null) {
				map = new HashMap<Object, Object>();
			}
			map.put(key, value);
		}

		public synchronized void remove(Object key) {
			if (map != null) {
				map.remove(key);
			}
		}

		public synchronized boolean isEmpty() {
			return this.map == null || this.map.isEmpty();
		}
	}

	public synchronized void remove(ISourceModule element) {
		if (DEBUG) {
			System.out.println("[Cache] remove " + element.getElementName()); //$NON-NLS-1$
		}
		super.remove(element);
	}

	private static final boolean DEBUG = false;

	public synchronized void clear() {
		flush();
	}

	@Override
	protected LRUCache newInstance(int size, int overflow) {
		throw new UnsupportedOperationException();
	}
}
