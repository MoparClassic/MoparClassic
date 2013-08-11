package org.moparscape.msc.ls.packethandler.gameserver;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.model.World;
import org.moparscape.msc.ls.net.Packet;
import org.moparscape.msc.ls.packethandler.PacketHandler;

public class GameSettingHandler implements PacketHandler {

	public void handlePacket(Packet p, IoSession session) throws Exception {
		World world = (World) session.getAttachment();
		long user = p.readLong();
		boolean on = p.readByte() == 1;
		int idx = (int) p.readByte();
		Server.getServer().findSave(user, world).setGameSetting(idx, on);
	}

}
