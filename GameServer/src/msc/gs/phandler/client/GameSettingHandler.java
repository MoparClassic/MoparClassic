package msc.gs.phandler.client;

import org.apache.mina.common.IoSession;

import msc.gs.Instance;
import msc.gs.builders.ls.GameSettingUpdatePacketBuilder;
import msc.gs.connection.LSPacket;
import msc.gs.connection.Packet;
import msc.gs.model.Player;
import msc.gs.model.World;
import msc.gs.phandler.PacketHandler;

public class GameSettingHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = Instance.getWorld();
    private GameSettingUpdatePacketBuilder builder = new GameSettingUpdatePacketBuilder();

    public void handlePacket(Packet p, IoSession session) throws Exception {
	Player player = (Player) session.getAttachment();
	int idx = (int) p.readByte();
	if (idx < 0 || idx > 6) {
	    player.setSuspiciousPlayer(true);
	    return;
	}
	boolean on = p.readByte() == 1;
	player.setGameSetting(idx, on);

	builder.setPlayer(player);
	builder.setIndex(idx);
	builder.setOn(on);

	LSPacket packet = builder.getPacket();
	if (packet != null) {
	    Instance.getServer().getLoginConnector().getSession().write(packet);
	}
    }

}
