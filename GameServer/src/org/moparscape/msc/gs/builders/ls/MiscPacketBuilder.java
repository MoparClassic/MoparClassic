package org.moparscape.msc.gs.builders.ls;

import java.util.ArrayList;
import java.util.List;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.config.Config;
import org.moparscape.msc.gs.connection.LSPacket;
import org.moparscape.msc.gs.connection.Packet;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.core.LoginConnector;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.Point;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.phandler.PacketHandler;
import org.moparscape.msc.gs.phandler.ls.PlayerLogin;
import org.moparscape.msc.gs.tools.DataConversions;
import org.moparscape.msc.gs.util.EntityList;
import org.moparscape.msc.gs.util.Logger;

public class MiscPacketBuilder {
	/**
	 * Connector instance
	 */
	private final LoginConnector connector;
	/**
	 * List of packets waiting to be sent to the user
	 */
	private List<LSPacket> packets = new ArrayList<LSPacket>();
	/**
	 * World instance
	 */
	private World world = Instance.getWorld();

	public MiscPacketBuilder(LoginConnector connector) {
		this.connector = connector;
	}

	public void addFriend(long user, long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(11);
		s.addLong(user);
		s.addLong(friend);
		packets.add(s.toPacket());
	}

	public void addIgnore(long user, long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(13);
		s.addLong(user);
		s.addLong(friend);
		packets.add(s.toPacket());
	}

	public void banPlayer(final Player mod, final long user, final boolean ban) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(ban ? 4 : 5);
		s.addLong(user);
		final long modhash = DataConversions.usernameToHash(mod.getUsername());
		s.addLong(modhash);
		s.setHandler(connector, new PacketHandler() {
			public void handlePacket(Packet p, IoSession session)
					throws Exception {
				if (p.readByte() == 1) {
					Logger.mod(mod.getUsername() + " "
							+ (ban ? "banned" : "unbanned") + " "
							+ DataConversions.hashToUsername(user));
				}
				mod.getActionSender().sendMessage(p.readString());
			}
		});
		packets.add(s.toPacket());
	}

	/**
	 * Clears old packets that have already been sent
	 */
	public void clearPackets() {
		packets.clear();
	}

	/**
	 * Gets a List of new packets since the last update
	 */
	public List<LSPacket> getPackets() {
		return packets;
	}

	public void logAction(String message, int type) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(32);
		s.addByte((byte) type);
		s.addBytes(message.getBytes());
		packets.add(s.toPacket());
	}

	public void logKill(long user, long killed, boolean stake) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(3);
		s.addLong(user);
		s.addLong(killed);
		s.addByte((byte) (stake ? 2 : 1));
		packets.add(s.toPacket());
	}

	public void playerLogin(Player player) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(31);
		s.setHandler(connector, new PlayerLogin(player));
		s.addLong(player.getUsernameHash());
		s.addLong(DataConversions.IPToLong(player.getCurrentIP()));
		s.addInt(player.getPassword().length);
		s.addBytes(player.getPassword());
		s.addBytes(player.getClassName().getBytes());
		packets.add(s.toPacket());
	}

	public void playerLogout(long user) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(30);
		s.addLong(user);
		packets.add(s.toPacket());
	}

	/**
	 * Tells the login server we are registered and lists players connected
	 * (should be 0 at startup)
	 */
	public void registerWorld() {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(1);
		s.setHandler(connector, new PacketHandler() {
			public void handlePacket(Packet p, IoSession session)
					throws Exception {
				connector.setRegistered(p.readByte() == 1);
			}
		});
		s.addShort(Config.WORLD_ID);
		EntityList<Player> players = world.getPlayers();
		s.addShort(players.size());
		for (Player player : players) {
			s.addLong(player.getUsernameHash());
			s.addLong(DataConversions.IPToLong(player.getCurrentIP()));
		}
		packets.add(s.toPacket());
	}

	public void removeFriend(long user, long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(12);
		s.addLong(user);
		s.addLong(friend);
		packets.add(s.toPacket());
	}

	public void removeIgnore(long user, long friend) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(14);
		s.addLong(user);
		s.addLong(friend);
		packets.add(s.toPacket());
	}

	public void reportUser(long user, long reported, byte reason) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(15);
		s.addLong(user);
		s.addLong(reported);
		s.addByte(reason);
		packets.add(s.toPacket());
	}

	public void requestPlayerInfo(final Player mod, final long user) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(8);
		s.addLong(user);
		s.setHandler(connector, new PacketHandler() {
			public void handlePacket(Packet p, IoSession session)
					throws Exception {
				if (p.readByte() == 1) {
					Logger.mod(mod.getUsername() + " requested information on "
							+ DataConversions.hashToUsername(user));

					int world = p.readShort();
					Point location = Point.location(p.readShort(),
							p.readShort());
					long loginDate = p.readLong();
					int lastMoved = (int) ((GameEngine.getTime() - p.readLong()) / 1000);
					boolean chatBlock = p.readByte() == 1;
					int fatigue = p.readShort();
					String state = p.readString();

					mod.getActionSender().sendAlert(
							"@whi@" + DataConversions.hashToUsername(user)
									+ " is currently on world @or1@" + world
									+ "@whi@ at @or1@" + location.toString()
									+ "@whi@ (@or1@"
									+ location.getDescription()
									+ "@whi@). State is @or1@" + state
									+ "@whi@. Logged in @or1@"
									+ DataConversions.timeSince(loginDate)
									+ "@whi@ ago. Last moved @or1@" + lastMoved
									+ " secs @whi@ ago. Chat block is @or1@"
									+ (chatBlock ? "on" : "off")
									+ "@whi@. Fatigue is at @or1@" + fatigue
									+ "@whi@.", false);
				} else {
					mod.getActionSender()
							.sendMessage(
									"Invalid player, maybe they aren't currently online?");
				}
			}
		});
		packets.add(s.toPacket());
	}

	public void saveProfiles() {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(9);
		s.setHandler(connector, new PacketHandler() {
			public void handlePacket(Packet p, IoSession session)
					throws Exception {
				if (p.readByte() != 1) {
					Logger.error("Error saving all profiles!");
				}
			}
		});
		packets.add(s.toPacket());
	}

	public void sendPM(long user, long friend, boolean avoidBlock,
			byte[] message) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(10);
		s.addLong(user);
		s.addLong(friend);
		s.addByte((byte) (avoidBlock ? 1 : 0));
		s.addBytes(message);
		packets.add(s.toPacket());
	}

	public void tradeLog(final long from, final long to, final int item,
			final long amount, final int x, final int y, final int type) {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(33);
		s.addLong(from);
		s.addLong(to);
		s.addInt(item);
		s.addLong(amount);
		s.addInt(x);
		s.addInt(y);
		s.addInt(type);
		s.addLong((GameEngine.getTimestamp() / 1000));
		packets.add(s.toPacket());
	}

	public void unregisterWorld() {
		LSPacketBuilder s = new LSPacketBuilder();
		s.setID(2);
		s.setHandler(connector, new PacketHandler() {
			public void handlePacket(Packet p, IoSession session)
					throws Exception {
				session.close().join();
				Instance.getServer().unbind();
				Instance.getServer().getEngine().kill();
			}
		});
		packets.add(s.toPacket());
	}
}
