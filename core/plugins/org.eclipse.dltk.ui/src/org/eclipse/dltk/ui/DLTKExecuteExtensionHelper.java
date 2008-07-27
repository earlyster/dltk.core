package org.eclipse.dltk.ui;

import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.dltk.core.DLTKLanguageManager;
import org.eclipse.dltk.core.IDLTKLanguageToolkit;

public class DLTKExecuteExtensionHelper {

	public static String getNatureId(IConfigurationElement config,
			String propertyName, Object data) {
		if (data instanceof String) {
			return (String) data;
		}

		if (data instanceof Map) {
			return (String) ((Map) data).get("nature"); //$NON-NLS-1$
		}

		throw new RuntimeException(
				Messages.DLTKExecuteExtensionHelper_natureAttributeMustBeSpecifiedAndCorrect);
	}

	public static IDLTKLanguageToolkit getLanguageToolkit(
			IConfigurationElement config, String propertyName, Object data) {
		String nature = getNatureId(config, propertyName, data);
		IDLTKLanguageToolkit toolkit = DLTKLanguageManager
				.getLanguageToolkit(nature);

		if (toolkit == null) {
			throw new RuntimeException(
					Messages.DLTKExecuteExtensionHelper_natureAttributeMustBeSpecifiedAndCorrect);
		}
		return toolkit;
	}
}
