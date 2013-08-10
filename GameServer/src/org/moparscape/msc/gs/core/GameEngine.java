package org.moparscape.msc.gs.core;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.TreeMap;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.connection.PacketQueue;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.connection.filter.IPBanManager;
import org.moparscape.msc.gs.event.DelayedEvent;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.landscape.ActiveTile;
import org.moparscape.msc.gs.model.snapshot.Snapshot;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.phandler.PacketHandlerDef;
import org.moparscape.msc.gs.util.Logger;

/**
 * The central motor of the game. This class is responsible for the primary
 * operation of the entire game.
 */
public final class GameEngine extends Thread {

	/**
	 * World instance
	 */
	private static final World world = Instance.getWorld();

	/**
	 * Responsible for updating all connected clients
	 */
	private ClientUpdater clientUpdater = new ClientUpdater();
	/**
	 * Handles delayed events rather than events to be ran every iteration
	 */
	private DelayedEventHandler eventHandler = new DelayedEventHandler();
	/**
	 * When the update loop was last ran, required for throttle
	 */
	private long lastSentClientUpdate = GameEngine.getTime();
	private long lastSentClientUpdateFast = GameEngine.getTime();
	private long lastCleanedChatlogs = 0;
	private int lastCleanedChatlogsOutput = 0;
	/**
	 * The mapping of packet IDs to their handler
	 */
	private TreeMap<Integer, PacketHandler> packetHandlers = new TreeMap<Integer, PacketHandler>();
	/**
	 * The packet queue to be processed
	 */
	private PacketQueue<RSCPacket> packetQueue;
	/**
	 * Whether the engine's thread is running
	 */
	private boolean running = true;
	private static volatile long time = 0;

	/**
	 * Only use this method when you need the actual time.
	 */
	public static long getTimestamp() {
		return System.currentTimeMillis();
	}

	/**
	 * Use this instead of System.currentTimeIllis when getting elapsed time, as
	 * each call does a system call, and potentially a hardware poll...<br>
	 * Also, you don't generally need the time to be updated more often than
	 * each part in the main loop.
	 * 
	 * @return The current time.
	 */
	public static long getTime() {
		return time;
	}

	/**
	 * Constructs a new game engine with an empty packet queue.
	 */
	public GameEngine() {
		packetQueue = new PacketQueue<RSCPacket>();
		try {
			loadPacketHandlers();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
		redirectSystemStreams();
	}

	public void emptyWorld() {
		for (Player p : world.getPlayers()) {
			p.save();
			p.getActionSender().sendLogout();
		}
		Instance.getServer().getLoginConnector().getActionSender()
				.saveProfiles();
	}

	/**
	 * Ban dummy packet flood private Map<InetAddress, Long> clients; private
	 * Map<InetAddress, Integer> counts; private Map<InetAddress, Integer>
	 * written; public void flagSession(IoSession session) { InetAddress addr =
	 * getAddress(session); String ip = addr.toString(); ip =
	 * ip.replaceAll("/",""); long now = System.currentTimeMillis(); int c = 0;
	 * if(counts.containsKey(addr) && clients.containsKey(addr)) { try { c =
	 * counts.get(addr); } catch(Exception e) { Logging.debug("Error: " + e); }
	 * if(c >= 10) { if(!written.containsKey(addr)) { try {
	 * Logging.debug("Dummy packet flooder IP: " + ip); BufferedWriter bf2 = new
	 * BufferedWriter(new FileWriter("dummy.log", true));
	 * bf2.write("sudo /sbin/route add " + addr.getHostAddress() +
	 * " gw 127.0.0.1"); bf2.newLine(); bf2.close(); written.put(addr, 1); }
	 * catch(Exception e) { System.err.println(e);} } } } if
	 * (clients.containsKey(addr)) { long lastConnTime = clients.get(addr); if
	 * (now - lastConnTime < 2000) { if(!counts.containsKey(addr)) {
	 * counts.put(addr, 0); } else c = counts.get(addr) + 1; counts.put(addr,
	 * c); } else { clients.put(addr, now); } } else { clients.put(addr, now); }
	 * }
	 * 
	 * private InetAddress getAddress(IoSession io) { return
	 * ((InetSocketAddress) io.getRemoteAddress()).getAddress(); }
	 */
	/**
	 * Returns the current packet queue.
	 * 
	 * @return A <code>PacketQueue</code>
	 */
	public PacketQueue<RSCPacket> getPacketQueue() {
		return packetQueue;
	}

	public void kill() {
		Logger.println("Terminating GameEngine");
		running = false;
	}

	/**
	 * Loads the packet handling classes from the persistence manager.
	 * 
	 * @throws Exception
	 */
	protected void loadPacketHandlers() throws Exception {
		PacketHandlerDef[] handlerDefs = Instance.dataStore()
				.loadPacketHandlerDefs();
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

	private void processClients() {
		clientUpdater.sendQueuedPackets();
		long now = GameEngine.getTime();
		if (now - lastSentClientUpdate >= 600) {
			lastSentClientUpdate = now;
			clientUpdater.doMajor();
		}
		if (now - lastSentClientUpdateFast >= 104) {
			lastSentClientUpdateFast = now;
			clientUpdater.doMinor();
		}
	}

	private long lastEventTick;

	private void processEvents() {
		if (getTime() - lastEventTick >= 100) {
			eventHandler.doEvents();
			lastEventTick = getTime();
		}
	}

	public DelayedEventHandler getEventHandler() {
		return eventHandler;
	}

	/**
	 * Redirects system err
	 */
	public static void redirectSystemStreams() {
		OutputStream out = new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				String line = String.valueOf((char) b);
				Logger.systemerr(line);
			}

			@Override
			public void write(byte[] b, int off, int len) throws IOException {
				String line = new String(b, off, len);
				Logger.systemerr(line);
			}

			@Override
			public void write(byte[] b) throws IOException {
				write(b, 0, b.length);
			}
		};
		System.setErr(new PrintStream(out, true));
	}

	private void processIncomingPackets() {
		for (RSCPacket p : packetQueue.getPackets()) {
			IoSession session = p.getSession();
			Player player = (Player) session.getAttachment();
			if (player.getUsername() == null && p.getID() != 32
					&& p.getID() != 77 && p.getID() != 0) {
				final String ip = player.getCurrentIP();
				IPBanManager.throttle(ip);
				continue;
			}
			PacketHandler handler = packetHandlers.get(p.getID());
			player.ping();
			if (handler != null) {
				try {
					handler.handlePacket(p, session);
					try {
						if (p.getID() != 5) {
							// String s = "[PACKET] " +
							// session.getRemoteAddress().toString().replace("/",
							// "") + " : " + p.getID()+
							// " ["+handler.getClass().toString()+"]" + " : "+
							// player.getUsername() + " : ";
							// for(Byte b : p.getData())
							// s += b;
							// Logger.println(s);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}

				} catch (Exception e) {
					String s;
					StringWriter sw = new StringWriter();
					PrintWriter pw = new PrintWriter(sw, true);
					e.printStackTrace(pw);
					pw.flush();
					sw.flush();
					s = sw.toString();
					Logger.error("Exception with p[" + p.getID() + "] from "
							+ player.getUsername() + " ["
							+ player.getCurrentIP() + "]: " + s);
					player.getActionSender().sendLogout();
					player.destroy(false);
				}
			} else {
				Logger.error("Unhandled packet from " + player.getCurrentIP()
						+ ": " + p.getID() + "len: " + p.getLength());
			}
		}
	}

	public void processLoginServer() {
		LoginConnector connector = Instance.getServer().getLoginConnector();
		if (connector != null) {
			connector.processIncomingPackets();
			connector.sendQueuedPackets();
		}
	}

	/**
	 * The thread execution process.
	 */
	public void run() {
		Logger.println("GameEngine now running");
		time = System.nanoTime() / 1000000000;

		eventHandler
				.add(new DelayedEvent(null, Config.GARBAGE_COLLECT_INTERVAL) { // Ran
																				// every
					// 50*2
					// minutes
					@Override
					public void run() {
						new Thread(new Runnable() {
							public void run() {
								garbageCollect();
							}
						}).start();
					}
				});
		eventHandler.add(new DelayedEvent(null, Config.SAVE_INTERVAL) {
			public void run() {
				long now = GameEngine.getTime();
				for (Player p : world.getPlayers()) {
					if (now - p.getLastSaveTime() >= Config.SAVE_INTERVAL) {
						p.save();
						p.setLastSaveTime(now);
					}
				}
				Instance.getServer().getLoginConnector().getActionSender()
						.saveProfiles();
			}
		});
		while (running) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException ie) {
			}
			long deltaTime = updateTime();
			processLoginServer();
			if ((deltaTime = getDeltaTime()) >= 1000)
				Logger.println("processLoginServer is taking longer than it should, exactly "
						+ deltaTime + "ms");
			processIncomingPackets();
			if ((deltaTime = getDeltaTime()) >= 1000)
				Logger.println("processIncomingPackets is taking longer than it should, exactly "
						+ deltaTime + "ms");
			processEvents();
			if ((deltaTime = getDeltaTime()) >= 1000)
				Logger.println("processEvents is taking longer than it should, exactly "
						+ deltaTime + "ms");
			processClients();
			if ((deltaTime = getDeltaTime()) >= 1000)
				Logger.println("processClients is taking longer than it should, exactly "
						+ deltaTime + "ms");
			cleanSnapshotDeque();
			if ((deltaTime = getDeltaTime()) >= 1000)
				Logger.println("processSnapshotDeque is taking longer than it should, exactly "
						+ deltaTime + "ms");
		}
	}

	public long getDeltaTime() {
		long t1 = time;
		return updateTime() - t1;
	}

	public long updateTime() {
		return time = System.nanoTime() / 1000000;
	}

	/**
	 * Cleans snapshots of entries over 60 seconds old (executed every second)
	 */
	public void cleanSnapshotDeque() {
		long curTime = GameEngine.getTimestamp(); // We need to compare
													// timestamps
		if (curTime - lastCleanedChatlogs > 1000) { // Every second
			lastCleanedChatlogs = curTime;
			lastCleanedChatlogsOutput++;
			if (lastCleanedChatlogsOutput > 60 * 5) {
				Logger.println("----------------------------------------------");
				Logger.println(world.getSnapshots().size() + " items on deque");
			}
			Iterator<Snapshot> i = world.getSnapshots().descendingIterator();
			Snapshot s = null;
			while (i.hasNext()) {
				s = i.next();
				if (curTime - s.getTimestamp() > 60000) {
					i.remove();
					s = null;
				} else {
					s = null;
				}
			}
			i = null;
			if (lastCleanedChatlogsOutput > 60 * 5) {
				Logger.println(world.getSnapshots().size()
						+ " items on deque AFTER CLEANUP");
				Logger.println("----------------------------------------------");
				lastCleanedChatlogsOutput = 0;
			}
		}
	}

	/**
	 * Cleans garbage (Tilecleanup)
	 */
	public synchronized void garbageCollect() {
		long startTime = getTime();
		int curMemory = (int) (Runtime.getRuntime().totalMemory() - Runtime
				.getRuntime().freeMemory()) / 1000;
		for (int i = 0; i < Instance.getWorld().tiles.length; i++) {
			for (int in = 0; in < Instance.getWorld().tiles[i].length; in++) {
				ActiveTile tile = Instance.getWorld().tiles[i][in];
				if (tile != null) {
					if (!tile.hasGameObject() && !tile.hasItems()
							&& !tile.hasNpcs() && !tile.hasPlayers()) {
						Instance.getWorld().tiles[i][in] = null;
					}
				}
			}
		}
		Runtime.getRuntime().gc();
		int newMemory = (int) (Runtime.getRuntime().totalMemory() - Runtime
				.getRuntime().freeMemory()) / 1000;
		Logger.println("GARBAGE COLLECT | Executing Memory Cleanup");
		Logger.println("GARBAGE COLLECT | Memory before: " + curMemory + "kb"
				+ " Memory after: " + newMemory + " (Freed: "
				+ (curMemory - newMemory) + "kb)");
		Logger.println("GARBAGE COLLECT | Cleanup took "
				+ (updateTime() - startTime) + "ms");
	}
}
