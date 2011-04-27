package msc.gs.phandler.client;

import org.apache.mina.common.IoSession;

import msc.gs.Instance;
import msc.gs.builders.ls.PrivacySettingUpdatePacketBuilder;
import msc.gs.connection.LSPacket;
import msc.gs.connection.Packet;
import msc.gs.model.Player;
import msc.gs.model.World;
import msc.gs.phandler.PacketHandler;

public class PrivacySettingHandler implements PacketHandler {
    /**
     * World instance
     */
    public static final World world = Instance.getWorld();
    private PrivacySettingUpdatePacketBuilder builder = new PrivacySettingUpdatePacketBuilder();

    public void handlePacket(Packet p, IoSession session) throws Exception {
	Player player = (Player) session.getAttachment();

	boolean[] newSettings = new boolean[4];
	for (int i = 0; i < 4; i++) {
	    newSettings[i] = p.readByte() == 1;
	}

	builder.setPlayer(player);
	for (int i = 0; i < 4; i++) {
	    builder.setIndex(i);
	    if (newSettings[i] && !player.getPrivacySetting(i)) {
		builder.setOn(true);
	    } else if (!newSettings[i] && player.getPrivacySetting(i)) {
		builder.setOn(false);
	    } else {
		continue;
	    }
	    LSPacket packet = builder.getPacket();
	    if (packet != null) {
		Instance.getServer().getLoginConnector().getSession().write(packet);
	    }
	}

	for (int i = 0; i < 4; i++) {
	    player.setPrivacySetting(i, newSettings[i]);
	}
    }

}
