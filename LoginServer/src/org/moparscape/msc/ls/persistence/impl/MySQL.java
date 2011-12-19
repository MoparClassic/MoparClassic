package org.moparscape.msc.ls.persistence.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.moparscape.msc.ls.model.BankItem;
import org.moparscape.msc.ls.model.InvItem;
import org.moparscape.msc.ls.model.PlayerSave;
import org.moparscape.msc.ls.net.DatabaseConnection;
import org.moparscape.msc.ls.persistence.StorageMedium;
import org.moparscape.msc.ls.util.Config;
import org.moparscape.msc.ls.util.DataConversions;

class MySQL implements StorageMedium {

	private final DatabaseConnection conn;

	MySQL() {
		conn = new DatabaseConnection();
	}

	@Override
	public boolean savePlayer(PlayerSave s) {
		if (!playerExists(s.getUser())) {
			return false;
		}

		PreparedStatement statement;
		try {
			updateLongs(Statements.save_DeleteBank, s.getUser());

			if (s.getBankCount() > 0) {
				statement = conn.prepareStatement(Statements.save_AddBank);
				int slot = 0;
				for (BankItem item : s.getBankItems()) {
					statement.setLong(1, s.getUser());
					statement.setInt(2, item.getID());
					statement.setInt(3, item.getAmount());
					statement.setInt(4, slot);
					statement.addBatch();
				}
				statement.executeBatch();
				close(statement);
			}

			updateLongs(Statements.save_DeleteInv, s.getUser());

			if (s.getInvCount() > 0) {
				statement = conn.prepareStatement(Statements.save_AddInvItem);
				int slot = 0;
				for (InvItem item : s.getInvItems()) {
					statement.setLong(1, s.getUser());
					statement.setInt(2, item.getID());
					statement.setInt(3, item.getAmount());
					statement.setInt(4, (item.isWielded() ? 1 : 0));
					statement.setInt(5, slot++);
					statement.addBatch();
				}
				statement.executeBatch();
				close(statement);
			}

			updateLongs(Statements.save_DeleteQuests, s.getUser());

			statement = conn.prepareStatement(Statements.save_AddQuest);
			Set<Integer> keys = s.getQuestStages().keySet();
			for (int id : keys) {
				statement.setLong(1, s.getUser());
				statement.setInt(2, id);
				statement.setInt(3, s.getQuestStage(id));
				statement.addBatch();
			}
			statement.executeBatch();
			close(statement);

			statement = conn.prepareStatement(Statements.save_UpdateBasicInfo);
			statement.setInt(1, s.getCombat());
			statement.setInt(2, s.getSkillTotal());
			statement.setInt(3, s.getX());
			statement.setInt(4, s.getY());
			statement.setInt(5, s.getFatigue());
			statement.setInt(6, s.getHairColour());
			statement.setInt(7, s.getTopColour());
			statement.setInt(8, s.getTrouserColour());
			statement.setInt(9, s.getSkinColour());
			statement.setInt(10, s.getHeadSprite());
			statement.setInt(11, s.getBodySprite());
			statement.setInt(12, s.isMale() ? 1 : 0);
			statement.setLong(13, s.getSkullTime());
			statement.setInt(14, s.getCombatStyle());
			statement.setInt(15, s.getQuestPoints());
			statement.setLong(16, s.getUser());
			statement.executeUpdate();
			close(statement);

			String query = "UPDATE `" + Statements.PREFIX + "experience` SET ";
			for (int i = 0; i < 18; i++)
				query += "`exp_" + Config.statArray[i] + "`=" + s.getExp(i)
						+ ",";

			conn.updateQuery(query.substring(0, query.length() - 1)
					+ " WHERE `user`=" + s.getUser());

			query = "UPDATE `" + Statements.PREFIX + "curstats` SET ";
			for (int i = 0; i < 18; i++)
				query += "`cur_" + Config.statArray[i] + "`=" + s.getStat(i)
						+ ",";

			conn.updateQuery(query.substring(0, query.length() - 1)
					+ " WHERE `user`=" + s.getUser());
			updateLongs(Statements.save_SetEventCD, s.getEventCD() / 1000,
					s.getUser());

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public void shutdown() {
		try {
			conn.close();
		} catch (SQLException e) {
			System.out.println("Unable to cleanly close MySQL connection");
		}
	}

	@Override
	public void logTrade(long from, long to, int item, long amount, int x,
			int y, int type, long date) {
		PreparedStatement logTrade = null;
		try {
			logTrade = conn.prepareStatement(Statements.logTrade);

			logTrade.setLong(1, from);
			logTrade.setLong(2, to);
			logTrade.setLong(3, date);
			logTrade.setInt(4, item);
			logTrade.setInt(5, x);
			logTrade.setInt(6, y);
			logTrade.setLong(7, amount);
			logTrade.setInt(8, type);

			logTrade.executeUpdate();
			close(logTrade);
		} catch (SQLException e) {
			if (logTrade != null)
				e.printStackTrace();
			else
				System.out.println("Failed to create prepared statement: "
						+ Statements.logTrade);

		}
	}

	@Override
	public void logReport(long user, long reported, byte reason, int x, int y,
			String status) {
		PreparedStatement logReport = null;
		try {
			logReport = conn.prepareStatement(Statements.logReport);

			logReport.setLong(1, user);
			logReport.setLong(2, reported);
			logReport.setLong(3, System.currentTimeMillis() / 1000);
			logReport.setByte(4, reason);
			logReport.setInt(5, x);
			logReport.setInt(6, y);
			logReport.setString(7, status);

			logReport.executeUpdate();
		} catch (SQLException e) {
			if (logReport != null)
				e.printStackTrace();
			else
				System.out.println("Failed to create prepared statement: "
						+ Statements.logReport);

		}
		close(logReport);
	}

	@Override
	public void logKill(long user, long killed, byte type) {
		PreparedStatement logKill = null;
		try {
			logKill = conn.prepareStatement(Statements.logKill);

			logKill.setLong(1, user);
			logKill.setLong(2, killed);
			logKill.setLong(3, System.currentTimeMillis() / 1000);
			logKill.setByte(4, type);

			logKill.executeUpdate();
		} catch (SQLException e) {
			if (logKill != null)
				e.printStackTrace();
			else
				System.out.println("Failed to create prepared statement: "
						+ Statements.logKill);

		}
		close(logKill);
	}

	@Override
	public void resetOnlineFlag(int world) {
		PreparedStatement resetOnlineFlag = null;
		try {
			resetOnlineFlag = conn.prepareStatement(Statements.resetOnlineFlag);

			resetOnlineFlag.setInt(1, world);

			resetOnlineFlag.executeUpdate();
		} catch (SQLException e) {
			if (resetOnlineFlag != null)
				e.printStackTrace();
			else
				System.out.println("Failed to create prepared statement: "
						+ Statements.resetOnlineFlag);

		}
		close(resetOnlineFlag);
	}

	@Override
	public void addFriend(long user, long friend) {
		updateLongs(Statements.addFriend, user, friend);
	}

	@Override
	public boolean addFriend_isOnline0(long user, long friend) {
		return hasNextFromLongs(Statements.addFriend_isOnline0, user, friend);
	}

	@Override
	public boolean addFriend_isOnline1(long friend, long user) {
		return hasNextFromLongs(Statements.addFriend_isOnline1, friend, user);
	}

	@Override
	public void removeFriend(long user, long friend) {
		updateLongs(Statements.removeFriend, user, friend);
	}

	@Override
	public boolean removeFriend_isOnline(long user) {
		return hasNextFromLongs(Statements.removeFriend_isOnline, user);
	}

	@Override
	public void addIgnore(long user, long friend) {
		updateLongs(Statements.addIgnore, user, friend);
	}

	@Override
	public void removeIgnore(long user, long friend) {
		updateLongs(Statements.removeIgnore, user, friend);
	}

	@Override
	public List<Long> getFriendsOnline(long user) {
		try {
			List<Long> list = longListFromResultSet(
					resultSetFromLongs(Statements.friendsList0, user), "user");
			list.addAll(longListFromResultSet(
					resultSetFromLongs(Statements.friendsList1, user), "user"));
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void chatBlock(int on, long user) {
		updateIntsLongs(Statements.chatBlock, new int[] { on },
				new long[] { user });
	}

	@Override
	public void privateBlock(int on, long user) {
		updateIntsLongs(Statements.privateBlock, new int[] { on },
				new long[] { user });
	}

	@Override
	public List<Long> getPrivateBlockFriendsOnline(long user) {
		try {
			return longListFromResultSet(
					resultSetFromLongs(Statements.privateBlock_online, user,
							user), "user");
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public void tradeBlock(int on, long user) {
		updateIntsLongs(Statements.tradeBlock, new int[] { on },
				new long[] { user });
	}

	@Override
	public void duelBlock(int on, long user) {
		updateIntsLongs(Statements.duelBlock, new int[] { on },
				new long[] { user });
	}

	@Override
	public boolean playerExists(long user) {
		return hasNextFromLongs(Statements.basicInfo, user);
	}

	@Override
	public boolean isBanned(long user) {
		ResultSet res = null;
		try {
			res = resultSetFromLongs(Statements.basicInfo, user);
			res.next();
			return res.getInt("banned") == 1;
		} catch (SQLException e) {
			e.printStackTrace();
			return true;
		} finally {
			close(res);
		}
	}

	@Override
	public int getGroupID(long user) {
		ResultSet res = null;
		try {
			res = resultSetFromLongs(Statements.basicInfo, user);
			res.next();
			return res.getInt("group_id");
		} catch (SQLException e) {
			e.printStackTrace();
			// Normal user = 1
			return 1;
		} finally {
			close(res);
		}
	}

	@Override
	public long getOwner(long user) {
		ResultSet res = null;
		try {
			res = resultSetFromLongs(Statements.basicInfo, user);
			res.next();
			return res.getLong("owner");
		} catch (SQLException e) {
			e.printStackTrace();
			return 0L;
		} finally {
			close(res);
		}
	}

	@Override
	public void setOnlineFlag(int id, long user) {
		updateIntsLongs(Statements.setOnlineFlag, new int[] { id },
				new long[] { user });
	}

	@Override
	public void logBan(long user, long modhash) {
		updateLongs(Statements.logBan, user, modhash,
				(System.currentTimeMillis() / 1000));
	}

	@Override
	public boolean ban(boolean setBanned, long user) {
		updateLongs(Statements.setBanned, (setBanned ? 1 : 0), user);
		return isBanned(user);
	}

	private static final String[] gameSettings = { "cameraauto", "",
			"onemouse", "soundoff", "showroof", "autoscreenshot",
			"combatwindow" };

	@Override
	public void setGameSettings(int idx, boolean on, long user) {
		try {
			conn.updateQuery("UPDATE `" + Statements.PREFIX + "players` SET "
					+ gameSettings[idx] + "=" + (on ? 1 : 0) + " WHERE user='"
					+ user + "'");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public PlayerSave loadPlayer(long user) {
		PlayerSave save = new PlayerSave(user);
		ResultSet result = resultSetFromLongs(Statements.playerData, user);
		try {
			if (!result.next()) {
				return save;
			}

			save.setEventCD(result.getLong("eventcd"));
			save.setOwner(result.getInt("owner"), result.getInt("group_id"),
					result.getLong("sub_expires"));
			save.setMuted(result.getLong("muted"));

			save.setLogin(result.getLong("login_date"),
					DataConversions.IPToLong(result.getString("login_ip")));
			save.setLocation(result.getInt("x"), result.getInt("y"));

			save.setFatigue(result.getInt("fatigue"));
			save.setCombatStyle((byte) result.getInt("combatstyle"));

			save.setPrivacy(result.getInt("block_chat") == 1,
					result.getInt("block_private") == 1,
					result.getInt("block_trade") == 1,
					result.getInt("block_duel") == 1);
			save.setSettings(result.getInt("cameraauto") == 1,
					result.getInt("onemouse") == 1,
					result.getInt("soundoff") == 1,
					result.getInt("showroof") == 1,
					result.getInt("autoscreenshot") == 1,
					result.getInt("combatwindow") == 1);

			save.setAppearance((byte) result.getInt("haircolour"),
					(byte) result.getInt("topcolour"),
					(byte) result.getInt("trousercolour"),
					(byte) result.getInt("skincolour"),
					(byte) result.getInt("headsprite"),
					(byte) result.getInt("bodysprite"),
					result.getInt("male") == 1, result.getInt("skulled"));

			save.setQuestPoints(result.getInt("quest_points"));

			save.setExp(intArrayFromStringArray(Statements.playerExp, "exp_",
					Config.statArray, user));
			save.setCurStats(intArrayFromStringArray(Statements.playerCurExp,
					"cur_", Config.statArray, user));
			
			close(result);
			result = resultSetFromLongs(Statements.playerInvItems, user);
			
			while (result.next()) {
				save.addInvItem(result.getInt("id"), result.getInt("amount"),
						result.getInt("wielded") == 1);
			}

			close(result);
			result = resultSetFromLongs(Statements.playerBankItems, user);
			
			while (result.next()) {
				save.addBankItem(result.getInt("id"), result.getInt("amount"));
			}
			
			close(result);

			save.addFriends(longListFromResultSet(
					resultSetFromLongs(Statements.playerFriends, user),
					"friend"));
			save.addIgnore(longListFromResultSet(
					resultSetFromLongs(Statements.playerIngored, user),
					"ignore"));

			result = resultSetFromLongs(Statements.playerQuests, user);
			while (result.next()) {
				save.setQuestStage(result.getInt("id"), result.getInt("stage"));
			}
			close(result);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return save;
	}

	private void updateLongs(String statement, long... longA) {
		PreparedStatement prepared = null;
		try {
			prepared = conn.prepareStatement(statement);

			for (int i = 1; i <= longA.length; i++) {
				prepared.setLong(i, longA[i - 1]);
			}

			prepared.executeUpdate();
		} catch (SQLException e) {
			if (prepared != null)
				e.printStackTrace();
			else
				System.out.println("Failed to create prepared statement: "
						+ statement);
		}
		close(prepared);
	}

	private void updateIntsLongs(String statement, int[] intA, long[] longA) {
		PreparedStatement prepared = null;
		try {
			prepared = conn.prepareStatement(statement);

			for (int i = 1; i <= intA.length; i++) {
				prepared.setInt(i, intA[i - 1]);
			}
			int offset = intA.length + 1;
			for (int i = 0; i < longA.length; i++) {
				prepared.setLong(i + offset, longA[i]);
			}

			prepared.executeUpdate();
		} catch (SQLException e) {
			if (prepared != null)
				e.printStackTrace();
			else
				System.out.println("Failed to create prepared statement: "
						+ statement);
		}
		close(prepared);
	}

	private int[] intArrayFromStringArray(String statement, String prefix,
			String[] stringArray, long... args) {
		ResultSet result = resultSetFromLongs(statement, args);

		try {
			result.next();
		} catch (SQLException e1) {
			e1.printStackTrace();
			return null;
		}

		int[] data = new int[stringArray.length];

		for (int i = 0; i < data.length; i++) {
			try {
				data[i] = result.getInt(prefix + stringArray[i]);
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			}
		}
		close(result);
		return data;
	}

	private List<Long> longListFromResultSet(ResultSet result, String param)
			throws SQLException {
		List<Long> list = new ArrayList<Long>();

		while (result.next()) {
			list.add(result.getLong(param));
		}
		close(result);
		return list;
	}

	private ResultSet resultSetFromLongs(String statement, long... longA) {
		PreparedStatement prepared = null;
		ResultSet result = null;
		try {
			prepared = conn.prepareStatement(statement);

			for (int i = 1; i <= longA.length; i++) {
				prepared.setLong(i, longA[i - 1]);
			}

			result = prepared.executeQuery();
		} catch (SQLException e) {
			if (prepared != null)
				e.printStackTrace();
			else
				System.out.println("Failed to create prepared statement: "
						+ statement);
		}
		return result;
	}

	private boolean hasNextFromLongs(String statement, long... longA) {
		PreparedStatement prepared = null;
		ResultSet result = null;
		try {
			prepared = conn.prepareStatement(statement);

			for (int i = 1; i <= longA.length; i++) {
				prepared.setLong(i, longA[i - 1]);
			}

			result = prepared.executeQuery();
		} catch (SQLException e) {
			if (prepared != null)
				e.printStackTrace();
			else
				System.out.println("Failed to create prepared statement: "
						+ statement);
		}

		try {
			return result.next();
		} catch (Exception e) {
			return false;
		} finally {
			close(result);
		}
	}

	private void close(ResultSet res) {
		if(res == null) {
			return;
		}
		
		try {
			close(res.getStatement());
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void close(Statement s) {
		if(s == null) {
			return;
		}
		
		try {
			s.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public class Statements {
		private static final String PREFIX = "pk_";
		private static final String logTrade = "INSERT `" + PREFIX
				+ "_tradelog` VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
		private static final String logReport = "INSERT INTO `"
				+ PREFIX
				+ "reports`(`from`, `about`, `time`, `reason`, `x`, `y`, `status`) VALUES(?, ?, ?, ?, ?, ?, ?)";
		private static final String resetOnlineFlag = "UPDATE `" + PREFIX
				+ "players` SET online=0, world=-1 WHERE world=?";
		private static final String logKill = "INSERT INTO `" + PREFIX
				+ "kills`(`user`, `killed`, `time`, `type`) VALUES(?, ?, ?, ?)";
		private static final String addFriend = "INSERT INTO `" + PREFIX
				+ "friends`(`user`, `friend`) VALUES(?, ?)";
		private static final String addFriend_isOnline0 = "SELECT 1 FROM `"
				+ PREFIX
				+ "players` AS p LEFT JOIN `"
				+ PREFIX
				+ "friends` AS f ON f.user=p.user WHERE (p.block_private=0 OR f.friend=?) AND p.user=?";
		private static final String addFriend_isOnline1 = "SELECT 1 FROM `"
				+ PREFIX
				+ "players` AS p LEFT JOIN `"
				+ PREFIX
				+ "friends` AS f ON f.friend=p.user WHERE p.block_private=1 AND f.user=? AND p.user=?";
		private static final String removeFriend = "DELETE FROM `" + PREFIX
				+ "friends` WHERE `user` LIKE ? AND `friend` LIKE ?";
		private static final String removeFriend_isOnline = "SELECT 1 FROM `"
				+ PREFIX + "players` WHERE block_private=1 AND user=?";
		private static final String addIgnore = "INSERT INTO `" + PREFIX
				+ "ignores`(`user`, `ignore`) VALUES(?, ?)";
		private static final String removeIgnore = "DELETE FROM `" + PREFIX
				+ "ignores` WHERE `user` LIKE ? AND `ignore` LIKE ?";
		private static final String friendsList0 = "SELECT p.user FROM `"
				+ PREFIX
				+ "friends` AS f INNER JOIN `"
				+ PREFIX
				+ "players` AS p ON p.user=f.friend WHERE p.block_private=0 AND f.user=?";
		private static final String friendsList1 = "SELECT user FROM `"
				+ PREFIX + "friends` WHERE friend=?";
		private static final String chatBlock = "UPDATE `" + PREFIX
				+ "players` SET block_chat=? WHERE user=?";
		private static final String privateBlock = "UPDATE `" + PREFIX
				+ "players` SET block_private=? WHERE user=?";
		private static final String privateBlock_online = "SELECT user FROM `"
				+ PREFIX
				+ "friends` WHERE friend=? AND user NOT IN (SELECT friend FROM `"
				+ PREFIX + "friends` WHERE user=?)";
		private static final String tradeBlock = "UPDATE `" + PREFIX
				+ "players` SET block_trade=? WHERE user=?";
		private static final String duelBlock = "UPDATE `pk_players` SET block_duel=? WHERE user=?";
		private static final String basicInfo = "SELECT banned, owner, group_id FROM `"
				+ PREFIX + "players` WHERE `user` = ?";
		private static final String setOnlineFlag = "UPDATE `" + PREFIX
				+ "players` SET online=1, world=? WHERE user=?";
		private static final String setBanned = "UPDATE `" + PREFIX
				+ "players` SET `banned`=? WHERE `user` = ?";
		private static final String logBan = "INSERT `" + PREFIX
				+ "banlog` VALUES(?, ?, ?)";
		private static final String playerData = "SELECT * FROM `" + PREFIX
				+ "players` WHERE `user`=?";
		private static final String playerExp = "SELECT * FROM `" + PREFIX
				+ "experience` WHERE `user`=?";
		private static final String playerCurExp = "SELECT * FROM `" + PREFIX
				+ "curstats` WHERE `user`=?";
		private static final String playerInvItems = "SELECT id,amount,wielded FROM `"
				+ PREFIX + "invitems` WHERE `user`=? ORDER BY `slot` ASC";
		private static final String playerBankItems = "SELECT id,amount FROM `"
				+ PREFIX + "bank` WHERE `user`=? ORDER BY `slot` ASC";
		private static final String playerFriends = "SELECT friend FROM `"
				+ PREFIX + "friends` WHERE `user`=?";
		private static final String playerIngored = "SELECT `ignore` FROM `"
				+ PREFIX + "ignores` WHERE `user`=?";
		private static final String playerQuests = "SELECT * FROM `" + PREFIX
				+ "quests` WHERE `user`=?";
		private static final String save_DeleteBank = "DELETE FROM `" + PREFIX
				+ "bank` WHERE `user`=?";
		private static final String save_AddBank = "INSERT INTO `" + PREFIX
				+ "bank`(`user`, `id`, `amount`, `slot`) VALUES(?, ?, ?, ?)";
		private static final String save_DeleteInv = "DELETE FROM `" + PREFIX
				+ "invitems` WHERE `user`=?";
		private static final String save_AddInvItem = "INSERT INTO `"
				+ PREFIX
				+ "invitems`(`user`, `id`, `amount`, `wielded`, `slot`) VALUES(?, ?, ?, ?, ?)";
		private static final String save_UpdateBasicInfo = "UPDATE `"
				+ PREFIX
				+ "players` SET `combat`=?, skill_total=?, `x`=?, `y`=?, `fatigue`=?, `haircolour`=?, `topcolour`=?, `trousercolour`=?, `skincolour`=?, `headsprite`=?, `bodysprite`=?, `male`=?, `skulled`=?, `combatstyle`=?, `quest_points`=? WHERE `user`=?";
		private static final String save_DeleteQuests = "DELETE FROM `"
				+ PREFIX + "quests` WHERE `user`=?";
		private static final String save_AddQuest = "INSERT INTO `" + PREFIX
				+ "quests` (`user`, `id`, `stage`) VALUES(?, ?, ?)";
		private static final String save_SetEventCD = "UPDATE `" + PREFIX
				+ "players` SET eventcd=? WHERE user=?";
		private static final String logLogin = "INSERT INTO `pk_logins`(`user`, `time`, `ip`) VALUES(?, ?, ?)";
		private static final String logIn = "UPDATE `pk_players` SET login_date=?, login_ip=? WHERE user=?";
	}

	@Override
	public void logLogin(long user, String ip) {
		PreparedStatement logLogin = null;
		try {
			logLogin = conn.prepareStatement(Statements.logLogin);

			logLogin.setLong(1, user);
			logLogin.setLong(2, System.currentTimeMillis() / 1000);
			logLogin.setString(3, ip);

			logLogin.executeUpdate();
		} catch (SQLException e) {
			if (logLogin != null)
				e.printStackTrace();
			else
				System.out.println("Failed to create prepared statement: "
						+ Statements.logLogin);

		}
		close(logLogin);
	}

	@Override
	public void logIn(String ip, long user) {
		PreparedStatement login = null;
		try {
			login = conn.prepareStatement(Statements.logIn);

			login.setLong(1, System.currentTimeMillis() / 1000);
			login.setString(2, ip);
			login.setLong(3, user);

			login.executeUpdate();
		} catch (SQLException e) {
			if (login != null)
				e.printStackTrace();
			else
				System.out.println("Failed to create prepared statement: "
						+ Statements.logIn);

		}
		close(login);
	}

	@Override
	public String getPass(long user) {
		ResultSet result = resultSetFromLongs(Statements.playerData, user);
		try {
			if (!result.next()) {
				return null;
			}
			return result.getString("pass");
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(result);
		}
		return null;
	}

}
