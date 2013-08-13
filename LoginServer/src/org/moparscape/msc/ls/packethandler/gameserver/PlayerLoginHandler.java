package org.moparscape.msc.ls.packethandler.gameserver;

import java.util.ArrayList;
import java.util.Map.Entry;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.auth.Auth;
import org.moparscape.msc.ls.auth.impl.AuthFactory;
import org.moparscape.msc.ls.model.PlayerSave;
import org.moparscape.msc.ls.model.World;
import org.moparscape.msc.ls.net.LSPacket;
import org.moparscape.msc.ls.net.Packet;
import org.moparscape.msc.ls.packetbuilder.gameserver.PlayerLoginPacketBuilder;
import org.moparscape.msc.ls.packethandler.PacketHandler;
import org.moparscape.msc.ls.service.UIDTracker;
import org.moparscape.msc.ls.util.Config;
import org.moparscape.msc.ls.util.DataConversions;

public class PlayerLoginHandler implements PacketHandler {
	public static ArrayList<String> badClients = new ArrayList<String>();
	private PlayerLoginPacketBuilder builder = new PlayerLoginPacketBuilder();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		final long uID = ((LSPacket) p).getUID();
		World world = (World) session.getAttachment();
		long user = p.readLong();
		String ip = DataConversions.IPToString(p.readLong());
		byte[] pass = p.readBytes(p.readInt());
		String UID = p.readString();
		byte loginCode = validatePlayer(user, pass, ip, UID);

		builder.setUID(uID);
		if (loginCode == 0 || loginCode == 1 || loginCode == 99) {
			try {
				badClients.add(DataConversions.hashToUsername(user));
				System.out.println("UID: " + UID + " Player: "
						+ DataConversions.hashToUsername(user));
			} catch (Exception e) {
				System.out.println("Exception in UID printer :"
						+ e.getMessage());
			}

			builder.setPlayer(Server.getServer().findSave(user, world),
					loginCode);
			world.registerPlayer(user, ip, UID);
		} else {
			builder.setPlayer(null, loginCode);
		}

		LSPacket packet = builder.getPacket();
		if (packet != null) {
			session.write(packet);
		}
	}

	private static Auth auth;

	static {
		try {
			auth = AuthFactory.create(Config.AUTH_CLASS);
			System.out.println("Authentication Scheme: "
					+ auth.getClass().getSimpleName());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private byte validatePlayer(long user, byte[] pass, String ip, String UID) {
		Server server = Server.getServer();
		byte returnVal = 0;

		if (UIDTracker.isActive(UID)) {
			return 8;
		}

		if (!Server.storage.playerExists(user)) {
			PlayerSave p = Server.storage.loadPlayer(user);
			p.pass = pass;
			Server.storage.savePlayer(p);
		}

		if (!auth.validate(user, pass, new StringBuilder())) {
			return 2;
		}

		if (Server.storage.isBanned(user)) {
			System.out.println("Banned player: "
					+ DataConversions.hashToUsername(user)
					+ " trying to login.");
			return 6;
		}

		if (Server.storage.getGroupID(user) >= 5) {
			returnVal = 99;
		}

		long owner = Server.storage.getOwner(user);
		for (World w : server.getWorlds()) {
			for (Entry<Long, Long> player : w.getPlayers()) {
				if (player.getKey() == user) {
					return 3;
				}
				if (player.getValue() == owner) {
					return 9;
				}
			}
			if (w.hasPlayer(user)) {
				return 3;
			}
		}
		return returnVal;
	}
}