package org.moparscape.msc.gs.connection;

import java.net.InetSocketAddress;

import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandler;
import org.apache.mina.common.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.moparscape.msc.config.Constants;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.util.Logger;

/**
 * Handles the protocol events fired from MINA.
 */
public class RSCConnectionHandler implements IoHandler {
	/**
	 * World instance
	 */

	/*
	 * 
	 * private int attackers = 0;
	 * 
	 * private Map<InetAddress, Long> clients; private Set<InetAddress>
	 * connectedAddresses; private Map<InetAddress, Integer> counts; private
	 * long lastAttack = 0;
	 */
	/**
	 * A reference to the game engine's packet queue
	 */

	private PacketQueue<RSCPacket> packets;

	/**
	 * Creates a new connection handler for the given engine.
	 * 
	 * @param engine
	 *            The engine in use
	 */
	public RSCConnectionHandler(GameEngine engine) {
		packets = (PacketQueue<RSCPacket>) engine.getPacketQueue();
		/*
		 * clients = Collections.synchronizedMap(new HashMap<InetAddress,
		 * Long>()); counts = Collections.synchronizedMap(new
		 * HashMap<InetAddress, Integer>()); written =
		 * Collections.synchronizedMap(new HashMap<InetAddress, Integer>());
		 * connectedAddresses = new HashSet<InetAddress>();
		 */
	}

	/*
	 * public void connectionOk(IoSession io) { counts.remove(getAddress(io)); }
	 * 
	 * public void delayClient(IoSession session, int delay) { long d =
	 * System.currentTimeMillis() - delay; clients.put(getAddress(session), d);
	 * }
	 */
	/**
	 * Invoked whenever an exception is thrown by MINA or this IoHandler.
	 * 
	 * @param session
	 *            The associated session
	 * @param cause
	 *            The exception thrown
	 */
	public void exceptionCaught(IoSession session, Throwable cause) {
		Player p = (Player) session.getAttachment();
		// if(p.getUsername().equalsIgnoreCase("xent")) {

		// }
		if (p != null)
			p.getActionSender().sendLogout();
		session.close();
		/*
		 * Logging.debug("---MINA Error from: " + p.getUsername() + " -------");
		 * cause.printStackTrace();Logging.debug(
		 * "------------------------------------------------------------");
		 */
		cause.printStackTrace();
	}

	/**
	 * Method responsible for deciding if a connection is OK to continue
	 * 
	 * @param session
	 *            The new session that will be verified
	 * @return True if the session meets the criteria, otherwise false (if
	 *         false, adds to a IPSec filter & writes IP to log.
	 */
	/*
	 * public boolean isConnectionOk(IoSession session) { final InetAddress addr
	 * = getAddress(session); final String ip = addr.toString().replaceAll("/",
	 * ""); // ip = ip.replaceAll("/",""); long now =
	 * System.currentTimeMillis(); int c = 0; if (counts.containsKey(addr) &&
	 * clients.containsKey(addr)) { try { c = counts.get(addr); } catch
	 * (Exception e) { Logging.debug("Error: " + e); } if (c >= 5) { if
	 * (!written.containsKey(addr)) { attackers++; // if(now - lastAttack >
	 * 60000) // { // for(Player p : world.getPlayers()) // { // p.save(); //
	 * p.getActionSender().sendLogout(); // } //
	 * Instance.getServer().getLoginConnector
	 * ().getActionSender().saveProfiles(); // } try { /*
	 * Logging.debug("ATTACKER IP: " + ip); BufferedWriter bf2 = new
	 * BufferedWriter(new FileWriter( "ddos.log", true));
	 * bf2.write("sudo /sbin/route add " + addr.getHostAddress() +
	 * " gw 127.0.0.1"); bf2.newLine(); bf2.close(); / written.put(addr, 1);
	 * Instance.getDelayedEventHandler().add(new DelayedEvent(null, 3600000) {
	 * public void run() { written.remove(addr); counts.remove(addr); try {
	 * Runtime.getRuntime().exec("sudo /sbin/route delete " + ip); } catch
	 * (Exception err) { Logging.debug(err); } } }); try {
	 * Runtime.getRuntime().exec("sudo /sbin/route add " + ip +
	 * " gw 127.0.0.1"); } catch (Exception err) { Logging.debug(err); }
	 * 
	 * // try { Runtime.getRuntime().exec(ip + ".bat"); } // catch (Exception
	 * err) { Logging.debug(err); } lastAttack = now; } catch (Exception e) {
	 * System.err.println(e); } } return false; } } if
	 * (clients.containsKey(addr)) { long lastConnTime = clients.get(addr); if
	 * (now - lastConnTime < 2000) { if (!counts.containsKey(addr))
	 * counts.put(addr, 0); else c = counts.get(addr) + 1; counts.put(addr, c);
	 * return false; } else { clients.put(addr, now); if
	 * (counts.containsKey(addr)) counts.remove(addr); return true; } } else {
	 * clients.put(addr, now); return true; } }
	 */
	/**
	 * Invoked whenever a packet is ready to be added to the queue.
	 * 
	 * @param session
	 *            The IO session on which the packet was received
	 * @param message
	 *            The packet
	 */
	public void messageReceived(IoSession session, Object message) {
		Player player = (Player) session.getAttachment();
		if (session.isClosing() || player.destroyed()) {
			return;
		}
		RSCPacket p = (RSCPacket) message;

		/*
		 * if(p.getID() == 57 || p.getID() == 73 || p.getID() == 40 || p.getID()
		 * == 51 || p.getID() == 128 || p.getID() == 206 || p.getID() == 71 ||
		 * p.getID() == 55)
		 */

		if (p.getID() == 55)
			player.addInterval();

		player.addPacket(p);
		packets.add(p);
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
		Player player = (Player) session.getAttachment();
		if (!player.destroyed()) {
			player.destroy(false);
		}
	}

	public void sessionCreated(IoSession session) {
		/*
		 * if (!isConnectionOk(session)) { session.close(); return; } else {
		 * connectionOk(session); }
		 */
		session.getFilterChain().addFirst("protocolFilter",
				new ProtocolCodecFilter(new RSCCodecFactory()));
		// Logger.event("Connection from: " +
		// ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress());
		Logger.println("Connection from: "
				+ ((InetSocketAddress) session.getRemoteAddress()).getAddress()
						.getHostAddress());
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
		Player player = (Player) session.getAttachment();
		if (!player.destroyed()) {
			player.destroy(false);
		}
		session.close();
	}

	/**
	 * Invoked when a new session is opened.
	 * 
	 * @param session
	 *            The session opened
	 */
	public void sessionOpened(IoSession session) {
		Constants.GameServer.ACCEPTED_CONNECTIONS++;
		session.setAttachment(new Player(session));
		session.setIdleTime(IdleStatus.BOTH_IDLE, 30);
		session.setWriteTimeout(30);
	}
}
