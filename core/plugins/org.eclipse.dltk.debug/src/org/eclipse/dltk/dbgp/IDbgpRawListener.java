package org.eclipse.dltk.dbgp;

public interface IDbgpRawListener {
	void dbgpPacketReceived(int sessionId, IDbgpPacket content);

	void dbgpPacketSent(int sessionId, IDbgpPacket content);
}
