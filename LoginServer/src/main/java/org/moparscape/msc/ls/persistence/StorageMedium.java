package org.moparscape.msc.ls.persistence;

import java.util.List;

import org.moparscape.msc.ls.model.PlayerSave;

public interface StorageMedium {
	public boolean savePlayer(PlayerSave s);
	public void shutdown();
	public void logTrade(long from, long to, int item, long amount, int x,
			int y, int type, long date);
	public void logReport(long user, long reported, byte reason, int x,
			int y, String status);
	public void resetOnlineFlag(int world);
	public void logKill(long user, long killed, byte type);
	public void addFriend(long user, long friend);
	public boolean addFriend_isOnline0(long user, long friend);
	public boolean addFriend_isOnline1(long friend, long user);
	public void removeFriend(long user, long friend);
	public boolean removeFriend_isOnline(long user);
	public void addIgnore(long user, long friend);
	public void removeIgnore(long user, long friend);
	public List<Long> getFriendsOnline(long user);
	public void chatBlock(int on, long user);
	public void privateBlock(int on, long user);
	public List<Long> getPrivateBlockFriendsOnline(long user);
	public void tradeBlock(int on, long user);
	public void duelBlock(int on, long user);
	public boolean playerExists(long user);
	public boolean isBanned(long user);
	public int getGroupID(long user);
	public long getOwner(long user);
	public void setOnlineFlag(int id, long user);
	public boolean ban(boolean setBanned, long user);
	public void logBan(long user, long modhash);
	public void setGameSettings(int idx, boolean on, long user);
	public PlayerSave loadPlayer(long user);
	public void logLogin(long user, String ip);
	public void logIn(String ip, long user);
	public String getPass(long user);
}
