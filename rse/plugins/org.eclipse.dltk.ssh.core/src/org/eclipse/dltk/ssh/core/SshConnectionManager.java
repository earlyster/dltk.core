package org.eclipse.dltk.ssh.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.dltk.ssh.internal.core.SshConnection;

/**
 * Class is designed to synchronize multiple sftp host access via one sftp
 * connection.
 */
public class SshConnectionManager {
	/**
	 * Contain path of location to connection.
	 */
	private static Map<String, SshConnection> connections = new HashMap<String, SshConnection>();

	/**
	 * Return connection associated with specified location. Location should be
	 * in following mode:
	 * 
	 * Location need to be correct URI persisted string.
	 * 
	 * Should contain user info with user name : password.
	 */
	public static synchronized ISshConnection getConnection(String user_host) {
		if (connections.containsKey(user_host)) {
			return connections.get(user_host);
		}
		int indexOf = user_host.indexOf("@"); //$NON-NLS-1$
		String userName = user_host.substring(0, indexOf);
		String hostName = user_host.substring(indexOf + 1);
		int portIndexOf = hostName.indexOf(":"); //$NON-NLS-1$
		int port = 22;
		if (portIndexOf != -1) {
			port = Integer.parseInt(hostName.substring(portIndexOf + 1));
			hostName = hostName.substring(0, portIndexOf);
		}
		SshConnection connection = new SshConnection(userName, hostName, port);
		connections.put(user_host, connection);
		return connection;
	}

	public static void disconnectAll() {
		Collection<SshConnection> values = connections.values();
		for (ISshConnection connection : values) {
			connection.disconnect();
			connection.setDisabled(60 * 1000 * 1000 * 1000); // Disable forever.
		}
	}
}
