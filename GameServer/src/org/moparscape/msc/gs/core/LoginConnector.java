package org.moparscape.msc.gs.core;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.TreeMap;

import org.apache.mina.common.ConnectFuture;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.transport.socket.nio.SocketConnector;
import org.apache.mina.transport.socket.nio.SocketConnectorConfig;
import org.apache.mina.transport.socket.nio.SocketSessionConfig;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.builders.ls.MiscPacketBuilder;
import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.connection.LSConnectionHandler;
import org.moparscape.msc.gs.connection.LSPacket;
import org.moparscape.msc.gs.connection.PacketQueue;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.phandler.PacketHandlerDef;
import org.moparscape.msc.gs.util.Logger;

public class LoginConnector {
	/**
	 * A packet builder
	 */
	private MiscPacketBuilder actionSender = new MiscPacketBuilder(this);
	/**
	 * Connection attempts
	 */
	private int connectionAttempts = 0;
	/**
	 * Connection Handler
	 */
	private IoHandler connectionHandler = new LSConnectionHandler(this);
	/**
	 * The mapping of packet IDs to their handler
	 */
	private TreeMap<Integer, PacketHandler> packetHandlers = new TreeMap<Integer, PacketHandler>();
	/**
	 * The packet queue to be processed
	 */
	private PacketQueue<LSPacket> packetQueue;
	/**
	 * World registered
	 */
	private boolean registered = false;
	/**
	 * Should we be running?
	 */
	private boolean running = true;
	/**
	 * IoSession
	 */
	private IoSession session;
	/**
	 * The mapping of packet UIDs to their handler
	 */
	private TreeMap<Long, PacketHandler> uniqueHandlers = new TreeMap<Long, PacketHandler>();

	public int getConnectionAttempts() {
		return connectionAttempts;
	}

	public void setConnectionAttempts(int connectionAttempts) {
		this.connectionAttempts = connectionAttempts;
	}

	public IoHandler getConnectionHandler() {
		return connectionHandler;
	}

	public void setConnectionHandler(IoHandler connectionHandler) {
		this.connectionHandler = connectionHandler;
	}

	public TreeMap<Integer, PacketHandler> getPacketHandlers() {
		return packetHandlers;
	}

	public void setPacketHandlers(TreeMap<Integer, PacketHandler> packetHandlers) {
		this.packetHandlers = packetHandlers;
	}

	public boolean isRunning() {
		return running;
	}

	public void setRunning(boolean running) {
		this.running = running;
	}

	public TreeMap<Long, PacketHandler> getUniqueHandlers() {
		return uniqueHandlers;
	}

	public void setUniqueHandlers(TreeMap<Long, PacketHandler> uniqueHandlers) {
		this.uniqueHandlers = uniqueHandlers;
	}

	public void setActionSender(MiscPacketBuilder actionSender) {
		this.actionSender = actionSender;
	}

	public void setPacketQueue(PacketQueue<LSPacket> packetQueue) {
		this.packetQueue = packetQueue;
	}

	public void setSession(IoSession session) {
		this.session = session;
	}

	public LoginConnector() {
		packetQueue = new PacketQueue<LSPacket>();
		try {
			loadPacketHandlers();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		reconnect();
	}

	public MiscPacketBuilder getActionSender() {
		return actionSender;
	}

	public PacketQueue<LSPacket> getPacketQueue() {
		return packetQueue;
	}

	public IoSession getSession() {
		return session;
	}

	public boolean isRegistered() {
		return registered;
	}

	public void kill() {
		running = false;
		Logger.print("Unregistering world (" + Config.WORLD_ID + ") with LS");
		actionSender.unregisterWorld();
	}

	private void loadPacketHandlers() throws Exception {
		PacketHandlerDef[] handlerDefs = Instance.dataStore()
				.loadLSPacketHandlerDefs();
		for (PacketHandlerDef handlerDef : handlerDefs) {
			try {
				String className = handlerDef.getClassName();
				Class<?> c = Class.forName(className);
				if (c != null) {
					PacketHandler handler = (PacketHandler) c.newInstance();
					for (int packetID : handlerDef.getAssociatedPackets()) {
						packetHandlers.put(packetID, handler);
					}
				}
			} catch (Exception e) {
				Logger.error(e);
			}
		}
	}

	public void processIncomingPackets() {
		for (LSPacket p : packetQueue.getPackets()) {
			PacketHandler handler;
			if (((handler = uniqueHandlers.get(p.getUID())) != null)
					|| ((handler = packetHandlers.get(p.getID())) != null)) {
				try {
					handler.handlePacket(p, session);
					uniqueHandlers.remove(p.getUID());
				} catch (Exception e) {
					Logger.error("Exception with p[" + p.getID()
							+ "] from LOGIN_SERVER: " + e.getMessage());
				}
			} else {
				Logger.error("Unhandled packet from LS: " + p.getID());
			}
		}
	}

	public boolean reconnect() {
		try {
			Logger.println("Attempting to connect to LS");
			SocketConnector conn = new SocketConnector();
			SocketConnectorConfig config = new SocketConnectorConfig();
			((SocketSessionConfig) config.getSessionConfig())
					.setKeepAlive(true);
			((SocketSessionConfig) config.getSessionConfig())
					.setTcpNoDelay(true);
			ConnectFuture future = conn.connect(new InetSocketAddress(
					Config.LS_IP, Config.LS_PORT), connectionHandler, config);
			future.join(3000);
			if (future.isConnected()) {
				session = future.getSession();
				Logger.println("Registering world (" + Config.WORLD_ID
						+ ") with LS");
				actionSender.registerWorld();
				connectionAttempts = 0;
				return true;
			}
			if (connectionAttempts++ >= 100) {
				Logger.println("Unable to connect to LS, giving up after "
						+ connectionAttempts + " tries");
				System.exit(1);
				return false;
			} else {
				// Add a delay so it doesn't instantly get to 100
				Thread.sleep(1000);
			}
			return reconnect();

		} catch (Exception e) {
			Logger.println("Error connecting to LS: " + e.getMessage());
			return false;
		}
	}

	public boolean running() {
		return running;
	}

	public synchronized void sendQueuedPackets() {
		try {
			List<LSPacket> packets = actionSender.getPackets();
			for (LSPacket packet : packets) {
				session.write(packet);
			}
			actionSender.clearPackets();
		} catch (Exception e) {
			Logger.println("Stack processInc: ");
			e.printStackTrace();
		}
	}

	public void setHandler(long uID, PacketHandler handler) {
		uniqueHandlers.put(uID, handler);
	}

	public void setRegistered(boolean registered) {
		if (registered) {
			this.registered = true;
			Logger.println("World successfully registered with LS");
		} else {
			Logger.error(new Exception("Error registering world"));
		}
	}

}
