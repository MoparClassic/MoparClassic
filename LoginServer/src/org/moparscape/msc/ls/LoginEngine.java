package org.moparscape.msc.ls;

import java.util.List;
import java.util.TreeMap;

import org.moparscape.msc.ls.model.World;
import org.moparscape.msc.ls.net.FPacket;
import org.moparscape.msc.ls.net.LSPacket;
import org.moparscape.msc.ls.net.PacketQueue;
import org.moparscape.msc.ls.packethandler.PacketHandler;
import org.moparscape.msc.ls.packethandler.PacketHandlerDef;
import org.moparscape.msc.ls.persistence.ConfigManager;

public class LoginEngine extends Thread {
	/**
	 * The mapping of packet IDs to their handler
	 */
	private TreeMap<Integer, PacketHandler> FPacketHandlers = new TreeMap<Integer, PacketHandler>();
	/**
	 * The packet queue to be processed
	 */
	private PacketQueue<FPacket> FPacketQueue;
	/**
	 * The mapping of packet IDs to their handler
	 */
	private TreeMap<Integer, PacketHandler> LSPacketHandlers = new TreeMap<Integer, PacketHandler>();
	/**
	 * The packet queue to be processed
	 */
	private PacketQueue<LSPacket> LSPacketQueue;
	/**
	 * Should we be running?
	 */
	private boolean running = true;
	/**
	 * The main server
	 */
	private Server server;
	/**
	 * The mapping of packet UIDs to their handler
	 */
	private TreeMap<Long, PacketHandler> uniqueHandlers = new TreeMap<Long, PacketHandler>();

	public LoginEngine(Server server) {
		this.server = server;
		LSPacketQueue = new PacketQueue<LSPacket>();
		FPacketQueue = new PacketQueue<FPacket>();
		loadPacketHandlers();
	}

	public PacketQueue<FPacket> getFPacketQueue() {
		return FPacketQueue;
	}

	public PacketQueue<LSPacket> getLSPacketQueue() {
		return LSPacketQueue;
	}

	/**
	 * Loads the packet handling classes from the persistence manager.
	 */
	protected void loadPacketHandlers() {
		PacketHandlerDef[] handlerDefs = ConfigManager.load(
				"LSPacketHandler.json", PacketHandlerDef[].class);
		for (PacketHandlerDef handlerDef : handlerDefs) {
			try {
				String className = handlerDef.getClassName();
				Class<?> c = Class.forName(className);
				if (c != null) {
					PacketHandler handler = (PacketHandler) c.newInstance();
					for (int packetID : handlerDef.getAssociatedPackets()) {
						LSPacketHandlers.put(packetID, handler);
					}
				}
			} catch (Exception e) {
				Server.error(e);
			}
		}
		handlerDefs = ConfigManager.load("FPacketHandler.json",
				PacketHandlerDef[].class);
		for (PacketHandlerDef handlerDef : handlerDefs) {
			try {
				String className = handlerDef.getClassName();
				Class<?> c = Class.forName(className);
				if (c != null) {
					PacketHandler handler = (PacketHandler) c.newInstance();
					for (int packetID : handlerDef.getAssociatedPackets()) {
						FPacketHandlers.put(packetID, handler);
					}
				}
			} catch (Exception e) {
				Server.error(e);
			}
		}
	}

	/**
	 * Processes incoming packets.
	 */
	private void processIncomingPackets() {
		for (LSPacket p : LSPacketQueue.getPackets()) {
			PacketHandler handler;
			if (((handler = uniqueHandlers.get(p.getUID())) != null)
					|| ((handler = LSPacketHandlers.get(p.getID())) != null)) {
				try {
					handler.handlePacket(p, p.getSession());
					uniqueHandlers.remove(p.getUID());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Server.error("Unhandled packet from server: " + p.getID());
			}
		}
		for (FPacket p : FPacketQueue.getPackets()) {
			PacketHandler handler = FPacketHandlers.get(p.getID());
			if (handler != null) {
				try {
					handler.handlePacket(p, p.getSession());
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Server.error("Unhandled packet from frontend: " + p.getID());
			}
		}
	}

	public void processOutgoingPackets() {
		for (World w : server.getWorlds()) {
			List<LSPacket> packets = w.getActionSender().getPackets();
			for (LSPacket packet : packets) {
				w.getSession().write(packet);
			}
			w.getActionSender().clearPackets();
		}
	}

	public void run() {
		System.out.println("LoginEngine now running");
		while (running) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {
			}
			processIncomingPackets();
			processOutgoingPackets();
		}
	}

	public void setHandler(long uID, PacketHandler handler) {
		uniqueHandlers.put(uID, handler);
	}
}