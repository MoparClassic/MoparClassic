package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.util.Logger;

public class BotHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		try {
			Player player = (Player) session.getAttachment();

			boolean scar = false;
			boolean wpe = false;
			boolean autominer = false;
			if (p.getLength() > 1) {
				boolean windows = p.readByte() == 1;
				if (windows) {
					if (p.readByte() == 1) {
						scar = true;
					}
					if (p.readByte() == 1) {
						autominer = true;
					}
					if (p.readByte() == 1) {
						autominer = true;
					}
					if (p.readByte() == 1) {
						wpe = true;
					}
					/*
					 * for (String s : PlayerLoginHandler.badClients) { if
					 * (s.equalsIgnoreCase(player.getUsername()))
					 * player.badClient = true;
					 * PlayerLoginHandler.badClients.remove(s); break; }
					 */
					for (Player pl : Instance.getWorld().getPlayers()) {
						if (pl.getLastPlayerInfo2() == null)
							continue;
						String s = "Client Statistics for "
								+ player.getUsername() + ": Scar: " + scar
								+ ", WPE: " + wpe + ", Autominer: " + autominer
								+ ", 3rd Party Client: " + player.isBadClient();
						if (pl.getLastPlayerInfo2().equalsIgnoreCase(
								player.getUsername())) {

							s = s.replace("true", "@gre@true@whi@");
							s = s.replace("false", "@red@false@whi@");
							pl.getActionSender().sendAlert(s, false);
							pl.setLastPlayerInfo2(null);
						}
					}
				}
			} else {
				Logger.println(player.getUsername()
						+ " caught on 3rd party client");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
