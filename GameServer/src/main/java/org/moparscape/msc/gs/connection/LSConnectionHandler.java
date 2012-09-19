package org.moparscape.msc.gs.connection;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.Server;
import org.moparscape.msc.gs.core.LoginConnector;
import org.moparscape.msc.gs.util.Logger;

/**
 * Handles the protocol events fired from MINA.
 */
public class LSConnectionHandler implements IoHandler {
	/**
	 * A reference to the login connector
	 */
	private LoginConnector connector;

	/**
	 * Creates a new connection handler for the given login connector.
	 * 
	 * @param connector
	 *            The connector in use
	 */
	public LSConnectionHandler(LoginConnector connector) {
		this.connector = connector;
	}

	/**
	 * Invoked whenever an exception is thrown by MINA or this IoHandler.
	 * 
	 * @param session
	 *            The associated session
	 * @param cause
	 *            The exception thrown
	 */
	public void exceptionCaught(IoSession session, Throwable cause) {
	}

	/**
	 * Invoked whenever a packet is ready to be added to the queue.
	 * 
	 * @param session
	 *            The IO session on which the packet was received
	 * @param message
	 *            The packet
	 */
	public void messageReceived(IoSession session, Object message) {
		if (session.isClosing()) {
			return;
		}
		LSPacket p = (LSPacket) message;
		connector.getPacketQueue().add(p);
	}

	/**
	 * Invoked whenever a packet is sent.
	 * 
	 * @param session
	 *            The associated session
	 * @param message
	 *            The packet sent
	 */
	public void messageSent(IoSession session, Object message) {
	}

	/**
	 * Invoked whenever an IO session is closed. This must handle unregistering
	 * the disconnecting player from the engine.
	 * 
	 * @param session
	 *            The IO session which has been closed
	 */
	public void sessionClosed(IoSession session) {
		Server server = Instance.getServer();
		if (server != null && server.running()) {
			Logger.error(new Exception("Lost connection the login server!"));
		}
	}

	/**
	 * Invoked whenever an IO session is created.
	 * 
	 * @param session
	 *            The session opened
	 */
	public void sessionCreated(IoSession session) {
		session.getFilterChain().addFirst("protocolFilter",
				new ProtocolCodecFilter(new LSCodecFactory()));
		// Logger.event("Connection to LOGIN_SERVER created");
	}

	/**
	 * Invoked when the idle status of a session changes.
	 * 
	 * @param session
	 *            The session in question
	 * @param status
	 *            The new idle status
	 */
	public void sessionIdle(IoSession session, IdleStatus status) {
	}

	/**
	 * Invoked when a new session is opened.
	 * 
	 * @param session
	 *            The session opened
	 */
	public void sessionOpened(IoSession session) {
	}
}
