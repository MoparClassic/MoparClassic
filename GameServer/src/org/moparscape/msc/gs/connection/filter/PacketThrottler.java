package org.moparscape.msc.gs.connection.filter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.jcip.annotations.ThreadSafe;

import org.apache.mina.common.IoFilterAdapter;
import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.alert.AlertHandler;
import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.event.DelayedEvent;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.util.annotation.Singleton;

/**
 * 
 * This filter will disconnect players that have exceeded the packet per second
 * threshold, and if configured, send an alert.
 * 
 * @author CodeForFame
 * 
 */
@ThreadSafe
@Singleton
public class PacketThrottler extends IoFilterAdapter {

	// ///////////////////////////////////////
	// /////// Singleton Boilerplate /////////
	// ///////////////////////////////////////

	private static final PacketThrottler instance = new PacketThrottler();

	public static PacketThrottler getInstance() {
		return instance;
	}

	// ///////////////////////////////////////
	// ///// Singleton Boilerplate End ///////
	// ///////////////////////////////////////

	/**
	 * The map of username hashes to packet per second.
	 */
	private Map<Long, Integer> playerToPacketCount = new ConcurrentHashMap<Long, Integer>();

	private PacketThrottler() {
		// Clears the count every second, so we can check the packets per
		// second.
		Instance.getDelayedEventHandler().add(new DelayedEvent(null, 1000) {

			@Override
			public void run() {
				playerToPacketCount.clear();
			}

		});
	}

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session,
			Object message) {

		Player player = (Player) session.getAttachment();
		if (session.isClosing() || player.destroyed()) {
			return;
		}

		int count = incrementAndGet(player.getUsernameHash());

		if (count > Config.PACKET_PER_SECOND_THRESHOLD) {

			if (Config.PACKET_PER_SECOND_ALERT) {
				// If the player is initialized, then use the username,
				// otherwise use the IP.
				String s = (player.isInitialized() ? player.getUsername()
						: player.getCurrentIP())
						+ " has exceeded the packet per second threshold";
				// Sends an alert with a priority of 2.
				AlertHandler.sendAlert(s, 2);
			}

			// Destroys the user and discards the packet.
			player.destroy(true);
			return;
		}

		nextFilter.messageReceived(session, message);
	}

	/**
	 * Increments and returns the current count.
	 * 
	 * @param hash
	 *            - The hash of the player.
	 */
	private int incrementAndGet(long hash) {
		final int count;

		// Even though operations are atomic, there are multiple, therefore it
		// needs to be synchronized.
		synchronized (playerToPacketCount) {

			// If it is null, default to 0
			Integer i = playerToPacketCount.get(hash);
			if (i == null) {
				count = 1;
			} else {
				count = i + 1;
			}

			// Update/Create entry
			playerToPacketCount.put(hash, count);
		}

		return count;
	}
}
