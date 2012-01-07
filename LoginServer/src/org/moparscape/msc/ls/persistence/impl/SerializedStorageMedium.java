package org.moparscape.msc.ls.persistence.impl;

import java.io.File;


/**
 * @author xEnt
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;

import org.moparscape.msc.ls.model.PlayerSave;
import org.moparscape.msc.ls.persistence.StorageMedium;
import org.moparscape.msc.ls.util.Config;
import org.moparscape.msc.ls.util.DataConversions;

public class SerializedStorageMedium implements StorageMedium {

	ObjectOutputStream oos;
	private static final String baseDir = "player_data";
	
	static {
		File f = new File(baseDir);
		if(!f.exists()) {
			f.mkdir();
		}
	}

	@Override
	public boolean savePlayer(PlayerSave s) {
		try {
			File f = new File(baseDir + File.separator + s.getUsername());
			if(!f.exists())
				f.createNewFile();
			oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(s);
		} catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (oos != null) {
					oos.flush();
					oos.close();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		return false;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void logTrade(long from, long to, int item, long amount, int x,
			int y, int type, long date) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logReport(long user, long reported, byte reason, int x, int y,
			String status) {
		// TODO Auto-generated method stub

	}

	@Override
	public void resetOnlineFlag(int world) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logKill(long user, long killed, byte type) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addFriend(long user, long friend) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean addFriend_isOnline0(long user, long friend) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addFriend_isOnline1(long friend, long user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void removeFriend(long user, long friend) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean removeFriend_isOnline(long user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void addIgnore(long user, long friend) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeIgnore(long user, long friend) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Long> getFriendsOnline(long user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void chatBlock(int on, long user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void privateBlock(int on, long user) {
		// TODO Auto-generated method stub

	}

	@Override
	public List<Long> getPrivateBlockFriendsOnline(long user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void tradeBlock(int on, long user) {
		// TODO Auto-generated method stub

	}

	@Override
	public void duelBlock(int on, long user) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean playerExists(long user) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isBanned(long user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getGroupID(long user) {
		// TODO Auto-generated method stub
		return 11;
	}

	private long ownerId = 0;

	@Override
	public long getOwner(long user) {
		return ownerId++;
	}

	@Override
	public void setOnlineFlag(int id, long user) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean ban(boolean setBanned, long user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void logBan(long user, long modhash) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setGameSettings(int idx, boolean on, long user) {
		// TODO Auto-generated method stub

	}

	@Override
	public PlayerSave loadPlayer(long user) {

		PlayerSave ps = getPlayerData(user);
	
		if (ps == null) // new char
		{
			PlayerSave save = new PlayerSave(user);
			save.setLocation(213, 452);
			save.setAppearance((byte) 2, (byte) 8, (byte) 14, (byte) 0, (byte) 1,
					(byte) 2, true, 0l);

			int[] exp = new int[Config.statArray.length];
			Arrays.fill(exp, 0);
			int[] stats = exp.clone();

			exp[3] = 1154;
			save.setExp(exp);
			stats[3] = 10;
			save.setCurStats(stats);
			return save;
		} else {
			return ps;
		}	

	}



	@Override
	public void logLogin(long user, String ip) {
		// TODO Auto-generated method stub

	}

	@Override
	public void logIn(String ip, long user) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getPass(long user) {		
		return "";
	}
	
	
	public PlayerSave getPlayerData(long user) {
		File userr = new File(baseDir + File.separator + DataConversions.hashToUsername(user));
		if (!userr.exists() )
		{
			return null;
		}

		try {
			FileInputStream fis = new FileInputStream(userr);

			ObjectInputStream ois = new ObjectInputStream(fis);
			PlayerSave ps = (PlayerSave)ois.readObject();
			return ps;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
