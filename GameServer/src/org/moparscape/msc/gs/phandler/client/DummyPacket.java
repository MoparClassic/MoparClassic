package org.moparscape.msc.gs.phandler.client;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.builders.RSCPacketBuilder;
import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.phandler.PacketHandler;

public class DummyPacket implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		try {
			if (p.getLength() > 2) { // 1 for byte, 2 for short
				@SuppressWarnings("unused")
				byte b = p.readByte();
				int clientVersion = p.readShort();
				if (clientVersion > Config.SERVER_VERSION - 5
						&& clientVersion < Config.SERVER_VERSION + 1) {
					RSCPacketBuilder pb = new RSCPacketBuilder();
					pb.setBare(true);
					pb.addByte((byte) 4); // client update
					session.write(pb.toPacket());
					Player player = (Player) session.getAttachment();
					player.destroy(true);
				}
			}
		} catch (Exception e) {

		}
	}
}
