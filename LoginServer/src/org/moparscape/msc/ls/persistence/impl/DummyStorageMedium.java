package org.moparscape.msc.ls.persistence.impl;

import java.util.Arrays;
import java.util.List;

import org.moparscape.msc.ls.model.PlayerSave;
import org.moparscape.msc.ls.persistence.StorageMedium;
import org.moparscape.msc.ls.util.Config;

class DummyStorageMedium implements StorageMedium {

	@Override
	public boolean savePlayer(PlayerSave s) {

		return true;
	}
	
	@Override
	public String getPass(long user) {
		return "";
	}

	@Override
	public void shutdown() {

	}

	@Override
	public void logTrade(long from, long to, int item, long amount, int x,
			int y, int type, long date) {

	}

	@Override
	public void logReport(long user, long reported, byte reason, int x, int y,
			String status) {

	}

	@Override
	public void resetOnlineFlag(int world) {

	}

	@Override
	public void logKill(long user, long killed, byte type) {

	}

	@Override
	public void addFriend(long user, long friend) {

	}

	@Override
	public boolean addFriend_isOnline0(long user, long friend) {

		return false;
	}

	@Override
	public boolean addFriend_isOnline1(long friend, long user) {

		return false;
	}

	@Override
	public void removeFriend(long user, long friend) {

	}

	@Override
	public boolean removeFriend_isOnline(long user) {

		return false;
	}

	@Override
	public void addIgnore(long user, long friend) {

	}

	@Override
	public void removeIgnore(long user, long friend) {

	}

	@Override
	public List<Long> getFriendsOnline(long user) {

		return null;
	}

	@Override
	public void chatBlock(int on, long user) {

	}

	@Override
	public void privateBlock(int on, long user) {

	}

	@Override
	public List<Long> getPrivateBlockFriendsOnline(long user) {

		return null;
	}

	@Override
	public void tradeBlock(int on, long user) {

	}

	@Override
	public void duelBlock(int on, long user) {

	}

	@Override
	public boolean playerExists(long user) {

		return true;
	}

	@Override
	public boolean isBanned(long user) {
		return false;
	}

	@Override
	public int getGroupID(long user) {
		// Dev mode
		return 11;
	}

	private long ownerId = 0;

	@Override
	public long getOwner(long user) {
		return ownerId++;
	}

	@Override
	public void setOnlineFlag(int id, long user) {

	}

	@Override
	public boolean ban(boolean setBanned, long user) {
		return false;
	}

	@Override
	public void logBan(long user, long modhash) {

	}

	@Override
	public void setGameSettings(int idx, boolean on, long user) {

	}

	@Override
	public PlayerSave loadPlayer(long user) {
		PlayerSave save = new PlayerSave(user);
		save.setLocation(213, 452);
		save.setAppearance((byte) 2, (byte) 8, (byte) 14, (byte) 0, (byte) 1,
				(byte) 2, true, 0l);

		int[] exp = new int[Config.statArray.length];
		Arrays.fill(exp, 1);
		int[] stats = exp.clone();
		
		exp[3] = 1200;
		save.setExp(exp);
		stats[3] = 10;
		save.setCurStats(stats);
		return save;
	}

	@Override
	public void logLogin(long user, String ip) {

	}

	@Override
	public void logIn(String ip, long user) {

	}

}
