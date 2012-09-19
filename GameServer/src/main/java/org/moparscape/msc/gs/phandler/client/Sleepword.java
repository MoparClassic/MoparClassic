/**
 * @author Zerratar
 * @date 2008, 7th of june
 */

package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.connection.RSCPacket;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.util.Logger;

public class Sleepword implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		// int pID = ((RSCPacket) p).getID();
		long now = GameEngine.getTime();

		try {
			String sleepword_result = ((RSCPacket) p).readString().trim();
			if (GameEngine.getTime() - player.getLastSleepTime() < 1000)
				return;

			if (player.getWrongWords() >= 10) {
				player.getActionSender().sendLogout();
				Logger.println(player.getWrongWords()
						+ " incorrect sleep words from Player: "
						+ player.getUsername());
				player.destroy(false);
				return;
			}

			if (sleepword_result.equalsIgnoreCase("-null-")) {
				player.getActionSender().sendEnterSleep();
				player.setLastSleepTime(now);
			} else {
				if (!player.isSleeping()) {
					Logger.println(player.getUsername()
							+ " tried waking up while not sleeping, probable bug abuse.");
					return;
				}
				if (sleepword_result.equalsIgnoreCase(player.getSleepword()))
					player.getActionSender().sendWakeUp(true);
				else {
					world.addEntryToSnapshots(new Activity(
							player.getUsername(), player.getUsername()
									+ " failed a sleepword"));
					player.getActionSender().sendIncorrectSleepword();
					player.getActionSender().sendEnterSleep();
					player.setLastSleepTime(now);
					player.setWrongWords();
				}
			}
		} catch (Exception e) {
			if (player.isSleeping()) {
				player.getActionSender().sendIncorrectSleepword();
				player.getActionSender().sendEnterSleep();
			}
		}
	}
}
