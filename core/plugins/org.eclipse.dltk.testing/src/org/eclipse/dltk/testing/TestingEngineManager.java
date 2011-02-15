package org.eclipse.dltk.testing;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.internal.testing.util.NumberUtils;
import org.eclipse.dltk.utils.NatureExtensionManager;

public final class TestingEngineManager extends
		NatureExtensionManager<ITestingEngine> {

	private static final String EXTENSION_POINT = DLTKTestingPlugin.PLUGIN_ID
			+ ".engine"; //$NON-NLS-1$

	private static class Descriptor {

		final IConfigurationElement element;
		final int priority;

		/**
		 * @param confElement
		 * @param priority
		 */
		public Descriptor(IConfigurationElement confElement, int priority) {
			this.element = confElement;
			this.priority = priority;
		}

	}

	private TestingEngineManager() {
		super(EXTENSION_POINT, ITestingEngine.class);
	}

	private static final String PRIORITY_ATTR = "priority"; //$NON-NLS-1$

	protected Object createDescriptor(IConfigurationElement confElement) {
		final String strPriority = confElement.getAttribute(PRIORITY_ATTR);
		int priority = NumberUtils.toInt(strPriority);
		return new Descriptor(confElement, priority);
	}

	private final Comparator<Object> descriptorComparator = new Comparator<Object>() {

		public int compare(Object o1, Object o2) {
			Descriptor descriptor1 = (Descriptor) o1;
			Descriptor descriptor2 = (Descriptor) o2;
			return descriptor1.priority - descriptor2.priority;
		}

	};

	protected void initializeDescriptors(List<Object> descriptors) {
		Collections.sort(descriptors, descriptorComparator);
	}

	protected Object createInstanceByDescriptor(Object descriptor)
			throws CoreException {
		Descriptor engineDescriptor = (Descriptor) descriptor;
		return super.createInstanceByDescriptor(engineDescriptor.element);
	}

	protected ITestingEngine[] createEmptyResult() {
		return new ITestingEngine[0];
	}

	private static TestingEngineManager instance = null;

	private static synchronized TestingEngineManager getInstance() {
		if (instance == null) {
			instance = new TestingEngineManager();
		}
		return instance;
	}

	public static ITestingEngine[] getEngines(String natureId) {
		return getInstance().getInstances(natureId);
	}

	/**
	 * Returns the {@link ITestingEngine} with the specified engineId or
	 * <code>null</code>.
	 * 
	 * @param engineId
	 * @return
	 */
	public static ITestingEngine getEngine(String engineId) {
		if (engineId != null) {
			final ITestingEngine[] engines = getInstance().getAllInstances();
			for (int i = 0; i < engines.length; ++i) {
				final ITestingEngine engine = engines[i];
				if (engineId.equals(engine.getId())) {
					return engine;
				}
			}
		}
		return null;
	}

	public static TestingEngineDetectResult detect(ITestingEngine[] engines,
			ISourceModule module) {
		IStatus infoStatus = null;
		ITestingEngine infoEngine = null;
		for (int i = 0; i < engines.length; i++) {
			final ITestingEngine engine = engines[i];
			final IStatus status = engine.validateSourceModule(module);
			if (status != null) {
				if (status.isOK()) {
					return new TestingEngineDetectResult(engine, status);
				} else if (status.getSeverity() == IStatus.INFO
						&& infoStatus == null) {
					infoStatus = status;
					infoEngine = engine;
				}
			}
		}
		if (infoEngine != null) {
			return new TestingEngineDetectResult(infoEngine, infoStatus);
		} else {
			return null;
		}
	}

}
