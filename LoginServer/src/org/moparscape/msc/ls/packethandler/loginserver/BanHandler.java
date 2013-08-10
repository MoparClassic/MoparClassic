package org.moparscape.msc.ls.packethandler.loginserver;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.model.World;
import org.moparscape.msc.ls.net.LSPacket;
import org.moparscape.msc.ls.net.Packet;
import org.moparscape.msc.ls.packetbuilder.loginserver.ReplyPacketBuilder;
import org.moparscape.msc.ls.packethandler.PacketHandler;
import org.moparscape.msc.ls.util.DataConversions;

public class BanHandler implements PacketHandler {
	private ReplyPacketBuilder builder = new ReplyPacketBuilder();

	public void handlePacket(Packet p, IoSession session) throws Exception {
		final long uID = ((LSPacket) p).getUID();
		boolean setBanned = ((LSPacket) p).getID() == 4;
		long user = p.readLong();
		long modhash = p.readLong();

		if (!Server.storage.playerExists(user)) {
			builder.setSuccess(false);
			builder.setReply("There is not an account by that username");
		} else if (setBanned && Server.storage.getGroupID(user) < 3) {
			builder.setSuccess(false);
			builder.setReply("You cannot ban a (p)mod or admin!");
		} else if (Server.storage.ban(setBanned, user)) {
			builder.setSuccess(false);
			builder.setReply("There is not an account by that username");
		} else {
			World w = Server.getServer().findWorld(user);
			if (w != null) {
				w.getActionSender().logoutUser(user);
			}
			if (setBanned)
				Server.storage.logBan(user, modhash);
			builder.setSuccess(true);
			builder.setReply(DataConversions.hashToUsername(user)
					+ " has been " + (setBanned ? "banned" : "unbanned"));
		}
		builder.setUID(uID);

		LSPacket temp = builder.getPacket();
		if (temp != null) {
			session.write(temp);
		}

	}

}
