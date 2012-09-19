package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.phandler.PacketHandler;

public class PlayerAppearanceIDHandler implements PacketHandler {
	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		int mobCount = p.readShort();
		int[] indicies = new int[mobCount];
		int[] appearanceIDs = new int[mobCount];
		for (int x = 0; x < mobCount; x++) {
			indicies[x] = p.readShort();
			appearanceIDs[x] = p.readShort();
		}
		Player player = (Player) session.getAttachment();
		player.addPlayersAppearanceIDs(indicies, appearanceIDs);
	}

}
