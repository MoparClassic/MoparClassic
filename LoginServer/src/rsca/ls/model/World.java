package rsca.ls.model;

import java.sql.ResultSet;
import java.util.Collection;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.mina.common.IoSession;

import rsca.ls.Server;
import rsca.ls.packetbuilder.loginserver.MiscPacketBuilder;
import rsca.ls.util.DataConversions;

public class World {
    private MiscPacketBuilder actionSender = new MiscPacketBuilder();
    private int id = -1;
    private TreeMap<Long, Integer> players = new TreeMap<Long, Integer>();
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
	for (Entry<Long, Integer> player : getPlayers()) {
	    long user = player.getKey();
	    for (World w : Server.getServer().getWorlds()) {
		w.getActionSender().friendLogout(user);
	    }
	    System.out.println("Removed " + DataConversions.hashToUsername(user) + " from world " + id);
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

    public Collection<Entry<Long, Integer>> getPlayers() {
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
	ResultSet result;
	try {
	    result = Server.db.getQuery("SELECT owner, block_private FROM `pk_players` WHERE `user`='" + user + "'");
	    if (!result.next()) {
		return;
	    }
	    int owner = result.getInt("owner");
	    boolean blockPrivate = result.getInt("block_private") == 1;

	    result = Server.db.getQuery("SELECT user FROM `pk_friends` WHERE `friend`='" + user + "'" + (blockPrivate ? " AND user IN (SELECT friend FROM `pk_friends` WHERE `user`='" + user + "')" : ""));
	    while (result.next()) {
		long friend = result.getLong("user");
		World w = server.findWorld(friend);
		if (w != null) {
		    w.getActionSender().friendLogin(friend, user, id);
		}
	    }
	    long now = (int) (System.currentTimeMillis() / 1000);
	    Server.db.updateQuery("INSERT INTO `pk_logins`(`user`, `time`, `ip`) VALUES('" + user + "', '" + now + "', '" + ip + "')");
	    Server.db.updateQuery("UPDATE `pk_players` SET login_date=" + now + ", login_ip='" + ip + "' WHERE user='" + user + "'");

	    players.put(user, owner);
	    System.out.println("Added " + DataConversions.hashToUsername(user) + " to world " + id);
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
	System.out.println("Removed " + DataConversions.hashToUsername(user) + " from world " + id);

	try {
	    Server.db.updateQuery("UPDATE `pk_players` SET online=0 WHERE user='" + user + "'");
	} catch (Exception e) {
	    Server.error(e);
	}

    }

}
