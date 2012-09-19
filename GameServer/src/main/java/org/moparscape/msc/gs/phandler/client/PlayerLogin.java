package org.moparscape.msc.gs.phandler.client;

import java.net.InetSocketAddress;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.config.Config;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.builders.RSCPacketBuilder;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.util.Logger;
import org.moparscape.msc.gs.util.RSA;

public class PlayerLogin implements PacketHandler {

	/**
	 * World instance
	 */
	public static final World world = Instance.getWorld();

	public void handlePacket(Packet p1, IoSession session) throws Exception {

		Player player = (Player) session.getAttachment();
		final String ip = ((InetSocketAddress) session.getRemoteAddress())
				.getAddress().toString().replaceAll("/", "");

		byte loginCode;
		try {

			byte[] data = RSA.decrypt(p1.getData());
			Packet p = new Packet(session, data);

			boolean reconnecting = (p.readByte() == 1);

			int clientVersion = p.readInt();

			if (Config.SERVER_VERSION != clientVersion) {
				Logger.println("ip: " + ip + " | clientversion: "
						+ clientVersion + " : " + Config.SERVER_VERSION);
			}

			int[] sessionKeys = new int[4];
			for (int key = 0; key < sessionKeys.length; key++) {
				sessionKeys[key] = p.readInt();
			}
			String username = "";
			String password = "";

			username = p.readString(20).trim();
			password = p.readString(20).trim();

			if (world.countPlayers() >= Config.MAX_PLAYERS) {
				loginCode = 10;
			} else if (clientVersion < Config.SERVER_VERSION) {
				loginCode = 4;
			} else if (!player.setSessionKeys(sessionKeys)) {
				loginCode = 5;
			} else {
				player.load(username, password, 0, reconnecting);
				if (clientVersion < Config.SERVER_VERSION) {
					player.clientWarn(true);
				}
				return;
			}
		} catch (Exception e) {
			System.err.println("Login exception with: " + ip);
			e.printStackTrace();
			loginCode = 4;
		}

		RSCPacketBuilder pb = new RSCPacketBuilder();
		pb.setBare(true);
		pb.addByte((byte) loginCode);
		session.write(pb.toPacket());
		player.destroy(true);
	}
}
