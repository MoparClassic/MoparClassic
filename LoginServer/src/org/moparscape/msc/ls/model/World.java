package org.moparscape.msc.ls.model;

import java.util.Collection;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.packetbuilder.gameserver.MiscPacketBuilder;
import org.moparscape.msc.ls.service.FriendsListService;
import org.moparscape.msc.ls.service.UIDTracker;
import org.moparscape.msc.ls.util.Config;
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

	public void registerPlayer(long user, String ip, String UID) {
		try {
			long owner = Server.storage.getOwner(user);
			players.put(user, owner);
			getSave(user).UID = UID;
			UIDTracker.activate(UID);
			Server.storage.logLogin(user, ip);
			Server.storage.logIn(ip, user);
			FriendsListService.logon(user);
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
		FriendsListService.logoff(user);
		UIDTracker.deactivate(getSave(user).UID);
		players.remove(user);
		if (!Config.CACHE_PROFILES) {
			unassosiateSave(getSave(user));
		}
		System.out.println("Removed " + DataConversions.hashToUsername(user)
				+ " from world " + id);
	}

}
