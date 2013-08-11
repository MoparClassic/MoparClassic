package org.moparscape.msc.ls.packethandler.gameserver;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.net.LSPacket;
import org.moparscape.msc.ls.net.Packet;
import org.moparscape.msc.ls.packetbuilder.gameserver.ReplyPacketBuilder;
import org.moparscape.msc.ls.packethandler.PacketHandler;

public class SaveProfilesRequestHandler implements PacketHandler {
	private ReplyPacketBuilder builder = new ReplyPacketBuilder();

	public void handlePacket(Packet p, final IoSession session)
			throws Exception {
		final long uID = ((LSPacket) p).getUID();
		/**
		 * try {
		 * //Runtime.getRuntime().exec("/home/org.moparscape.msc/unblock"); }
		 * catch (Exception err) { System.out.println(err); }
		 **/

		boolean success = true;
		// Iterator iterator = world.getAssosiatedSaves().iterator();
		// while(iterator.hasNext()) {
		// PlayerSave profile = ((Entry<Long,
		// PlayerSave>)iterator.next()).getValue();
		// profile.save();
		// iterator.remove();
		// }

		builder.setUID(uID);
		builder.setSuccess(success);

		LSPacket packet = builder.getPacket();
		if (packet != null) {
			session.write(packet);
		}
	}

}
