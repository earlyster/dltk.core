package org.eclipse.dltk.dbgp;

public interface IDbgpRawListener {
	void dbgpPacketReceived(int sessionId, String content);

	void dbgpPacketSent(int sessionId, String content);
}
