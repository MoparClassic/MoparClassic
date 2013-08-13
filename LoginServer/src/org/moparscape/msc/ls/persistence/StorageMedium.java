package org.moparscape.msc.ls.persistence;

import org.moparscape.msc.ls.model.PlayerSave;

public interface StorageMedium {
	public boolean savePlayer(PlayerSave s);

	public void shutdown();

	public void logTrade(long from, long to, int item, long amount, int x,
			int y, int type, long date);

	public void logReport(long user, long reported, byte reason, int x, int y,
			String status);

	public void logKill(long user, long killed, byte type);

	public void addFriend(long user, long friend);

	public void removeFriend(long user, long friend);

	public void addIgnore(long user, long friend);

	public void removeIgnore(long user, long friend);

	public boolean playerExists(long user);

	public boolean isBanned(long user);

	public int getGroupID(long user);

	public long getOwner(long user);

	public boolean ban(boolean setBanned, long user);

	public void logBan(long user, long modhash);

	public PlayerSave loadPlayer(long user);

	public void logLogin(long user, String ip);

	public void logIn(String ip, long user);

	public byte[] getPass(long user);
}
