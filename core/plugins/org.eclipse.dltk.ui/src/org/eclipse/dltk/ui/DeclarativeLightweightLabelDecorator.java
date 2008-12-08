package org.eclipse.dltk.ui;

import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;

public class DeclarativeLightweightLabelDecorator extends LabelProvider
		implements ILightweightLabelDecorator, IExecutableExtension {
	private String bundleId;
	private String iconLocation;

	private ImageDescriptor descriptor;

	public void decorate(Object element, IDecoration decoration) {
		if (descriptor == null) {
			URL url = FileLocator.find(Platform.getBundle(bundleId), new Path(
					iconLocation), null);
			if (url == null) {
				return;
			}
			descriptor = ImageDescriptor.createFromURL(url);
		}
		decoration.addOverlay(descriptor);
	}

	public void setInitializationData(IConfigurationElement config,
			String propertyName, Object data) throws CoreException {
		bundleId = config.getContributor().getName();
		iconLocation = (String) data;
	}
}
