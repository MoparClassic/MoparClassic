package msc.gs.phandler.client;

import org.apache.mina.common.IoSession;

import msc.config.Formulae;
import msc.gs.Instance;
import msc.gs.builders.RSCPacketBuilder;
import msc.gs.connection.Packet;
import msc.gs.model.Player;
import msc.gs.model.World;
import msc.gs.phandler.PacketHandler;
import msc.gs.util.Logger;

public class SessionRequest implements PacketHandler {

	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		Player player = (Player) session.getAttachment();
		if(player.isInitialized()) {
			Logger.println("[WARNING] SessionRequest for already Initialized player!");
			return;
		}
		byte userByte = p.readByte();
		player.setClassName(p.readString().trim());
		long serverKey = Formulae.generateSessionKey(userByte);
		player.setServerKey(serverKey);
		RSCPacketBuilder pb = new RSCPacketBuilder();
		pb.setBare(true);
		pb.addLong(serverKey);
		session.write(pb.toPacket());
	}
}
