package org.eclipse.dltk.core.internal.rse;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.environment.EnvironmentManager;
import org.eclipse.dltk.core.environment.IEnvironment;
import org.eclipse.dltk.core.environment.IFileHandle;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.IConnectorService;
import org.eclipse.rse.internal.efs.RSEFileSystem;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFileSubSystem;

@SuppressWarnings("restriction")
public class RSEEnvironment implements IEnvironment, IAdaptable {
	private IRemoteFileSubSystem fs;
	private IHost host;
	private static Map<IRemoteFileSubSystem, Boolean> tryToConnect = new HashMap<IRemoteFileSubSystem, Boolean>();

	public RSEEnvironment(IRemoteFileSubSystem fs) {
		this.fs = fs;
		this.host = fs.getConnectorService().getHost();
	}

	/*
	 * @see org.eclipse.dltk.core.environment.IEnvironment#isLocal()
	 */
	public boolean isLocal() {
		return false;
	}

	public IFileHandle getFile(IPath path) {
		if (Path.EMPTY.equals(path)) {
			throw new RuntimeException(
					Messages.RSEEnvironment_EmptyFileNameError);
		}
		return new RSEFileHandle(this, getURIFor(host, path.toString()));
	}

	public String getId() {
		return RSEEnvironmentProvider.RSE_ENVIRONMENT_PREFIX
				+ host.getAliasName();
	}

	public String getSeparator() {
		return fs.getSeparator();
	}

	public char getSeparatorChar() {
		return fs.getSeparatorChar();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RSEEnvironment) {
			RSEEnvironment other = (RSEEnvironment) obj;
			return getId().equals(other.getId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	public String getName() {
		return host.getName() + Messages.RSEEnvironment_EnvironmentNameSuffix;
	}

	public IHost getHost() {
		return host;
	}

	@SuppressWarnings("unchecked")
	public Object getAdapter(Class adapter) {
		return Platform.getAdapterManager()
				.loadAdapter(this, adapter.getName());
	}

	public URI getURI(IPath location) {
		return getURIFor(host, location.toString());
	}

	public String convertPathToString(IPath path) {
		if (host.getSystemType().isWindows()) {
			return path.toString().replace('/', '\\');
		} else {
			return path.toString();
		}
	}

	public IFileHandle getFile(URI locationURI) {
		if (RSEEnvironmentProvider.RSE_SCHEME.equalsIgnoreCase(locationURI
				.getScheme())
				&& locationURI.getHost().equals(host.getHostName())) {
			return new RSEFileHandle(this, locationURI);
		} else {
			final URI[] resolved = EnvironmentManager.resolve(locationURI);
			for (int i = 0; i < resolved.length; ++i) {
				final URI newLocation = resolved[i];
				if (RSEEnvironmentProvider.RSE_SCHEME
						.equalsIgnoreCase(newLocation.getScheme())
						&& newLocation.getHost().equals(host.getHostName())) {
					return new RSEFileHandle(this, newLocation);
				}
			}
			return null;
		}
	}

	public String getPathsSeparator() {
		return Character.toString(getPathsSeparatorChar());
	}

	public char getPathsSeparatorChar() {
		return host.getSystemType().isWindows() ? ';' : ':';
	}

	public String getCanonicalPath(IPath path) {
		return convertPathToString(path);
	}

	public static URI getURIFor(IHost host, String path) {
		return RSEFileSystem.getURIFor(host.getHostName(), path);
	}

	/**
	 * @since 2.0
	 */
	public boolean isConnected() {
		IConnectorService[] services = host.getConnectorServices();
		int connected = 0;
		for (IConnectorService service : services) {
			if (service.isConnected()) {
				connected++;
			}
		}
		return connected == services.length;
	}

	/**
	 * @since 2.0
	 */
	public boolean connect() {
		if (isConnected()) {
			return true;
		}
		// TODO: Add synchronize section only if we are not calling from UI
		// thread. Need to create UI adapter service to do it.
		// synchronized (this.host) {
		connectUnsafe();
		// }
		return isConnected();
	}

	private void connectUnsafe() {
		boolean tryToConnect = isTryToConnect();
		if (tryToConnect) {
			IConnectorService[] services = host.getConnectorServices();
			for (IConnectorService service : services) {
				if (!service.isConnected()) {
					try {
						service.connect(new NullProgressMonitor());
					} catch (Exception e) {
						if (!(e instanceof OperationCanceledException)) {
							DLTKRSEPlugin.log(e);
						}
					}
				}
			}
			setTryToConnect(false);
		}
	}

	/**
	 * @since 2.0
	 */
	public boolean isTryToConnect() {
		boolean tryToConnect = true;
		if (RSEEnvironment.tryToConnect.containsKey(fs)) {
			tryToConnect = RSEEnvironment.tryToConnect.get(fs).booleanValue();
		}
		return tryToConnect;
	}

	/**
	 * @since 2.0
	 */
	public void setTryToConnect(boolean value) {
		RSEEnvironment.tryToConnect.put(fs, Boolean.valueOf(value));
	}

}
