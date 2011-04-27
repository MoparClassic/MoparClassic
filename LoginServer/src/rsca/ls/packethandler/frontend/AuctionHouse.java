package rsca.ls.packethandler.frontend;

import java.util.ArrayList;
import java.io.DataInput;

import org.apache.mina.common.IoSession;

import rsca.ls.Server;
import rsca.ls.model.World;
import rsca.ls.net.FPacket;
import rsca.ls.net.Packet;
import rsca.ls.packetbuilder.FPacketBuilder;
import rsca.ls.packethandler.PacketHandler;

public class AuctionHouse implements PacketHandler {
    private static final FPacketBuilder builder = new FPacketBuilder();

    public void handlePacket(Packet p, final IoSession session) throws Exception {
	String[] params = ((FPacket) p).getParameters();
	try {
	    final int worldID = Integer.parseInt(params[0]);
	    final long playerhash = Integer.parseInt(params[1]);
	    final int itemID = Integer.parseInt(params[2]);
	    final int buyout = Integer.parseInt(params[3]);
	    System.out.println("Frontend player trying to sell item via auction " + worldID);
	    World world = Server.getServer().getWorld(worldID);
	    if (world == null) {
		throw new Exception("Unknown world");
	    }
	    world.getActionSender().Auction(playerhash, itemID, buyout, new PacketHandler() {
		public void handlePacket(Packet p, IoSession s) throws Exception {
		    builder.setID(150);
		    int isOK = p.readInt();
		    ArrayList<Integer> params = new ArrayList<Integer>();
		    params.add(isOK);
		    builder.setParameters(params.toArray(new String[params.size()]));

		    session.write(builder.toPacket());
		}
	    });
	} catch (Exception e) {
	    builder.setID(0);
	    session.write(builder.toPacket());
	}
    }

}
