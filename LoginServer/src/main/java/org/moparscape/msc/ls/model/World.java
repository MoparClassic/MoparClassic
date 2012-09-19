package org.moparscape.msc.ls.model;

import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.packetbuilder.loginserver.MiscPacketBuilder;
import org.moparscape.msc.ls.util.DataConversions;

public class World {
	private MiscPacketBuilder actionSender = new MiscPacketBuilder();
	private int id = -1;
	private TreeMap<Long, Long> players = new TreeMap<Long, Long>();
	private TreeMap<Long, PlayerSave> saves = new TreeMap<Long, PlayerSave>();
	private IoSession session;

	public World(int id, IoSession session) {
		this.id = id;
		setSession(session);
	}

	public void assosiateSave(PlayerSave save) {
		saves.put(save.getUser(), save);
	}

	public void clearPlayers() {
		for (Entry<Long, Long> player : getPlayers()) {
			long user = player.getKey();
			for (World w : Server.getServer().getWorlds()) {
				w.getActionSender().friendLogout(user);
			}
			System.out.println("Removed "
					+ DataConversions.hashToUsername(user) + " from world "
					+ id);
		}
		players.clear();
	}

	public MiscPacketBuilder getActionSender() {
		return actionSender;
	}

	public Collection<Entry<Long, PlayerSave>> getAssosiatedSaves() {
		return saves.entrySet();
	}

	public int getID() {
		return id;
	}

	public Collection<Entry<Long, Long>> getPlayers() {
		return players.entrySet();
	}

	public PlayerSave getSave(long user) {
		return saves.get(user);
	}

	public IoSession getSession() {
		return session;
	}

	public boolean hasPlayer(long user) {
		return players.containsKey(user);
	}

	public void registerPlayer(long user, String ip) {
		Server server = Server.getServer();
		try {
			long owner = Server.storage.getOwner(user);

			List<Long> friends = Server.storage.getFriendsOnline(user);
			if (friends != null)
				for (long friend : friends) {
					World w = server.findWorld(friend);
					if (w != null) {
						w.getActionSender().friendLogin(friend, user, id);
					}
				}
			Server.storage.logLogin(user, ip);
			Server.storage.logIn(ip, user);
			players.put(user, owner);
			System.out.println("Added " + DataConversions.hashToUsername(user)
					+ " to world " + id);
		} catch (Exception e) {
			Server.error(e);
		}
	}

	public void setSession(IoSession session) {
		this.session = session;
	}

	public void unassosiateSave(PlayerSave save) {
		saves.remove(save.getUser());
	}

	public void unregisterPlayer(long user) {
		for (World w : Server.getServer().getWorlds()) {
			w.getActionSender().friendLogout(user);
		}
		players.remove(user);
		System.out.println("Removed " + DataConversions.hashToUsername(user)
				+ " from world " + id);
		Server.storage.setOnlineFlag(id, user);
	}

}
