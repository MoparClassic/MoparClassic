package rsca.ls;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.TreeMap;

import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoAcceptorConfig;
import org.apache.mina.common.IoHandler;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.apache.mina.transport.socket.nio.SocketSessionConfig;

import rsca.ls.model.PlayerSave;
import rsca.ls.model.World;
import rsca.ls.net.DatabaseConnection;
import rsca.ls.net.FConnectionHandler;
import rsca.ls.net.LSConnectionHandler;
import rsca.ls.util.Config;

public class Server {
    /**
     * Connection to the MySQL database
     */
    public static DatabaseConnection db;
    private static Server server;

    public static void error(Object o) {
	if (o instanceof Exception) {
	    Exception e = (Exception) o;
	    e.printStackTrace();
	    System.exit(1);
	    return;// Adding save data
	}
	System.err.println(o.toString());
    }

    public static Server getServer() {
	if (server == null) {
	    server = new Server();
	}
	return server;
    }

    public static void main(String[] args) throws IOException {
	String configFile = "conf/ls/Conf.xml";
	if (args.length > 0) {
	    File f = new File(args[0]);
	    if (f.exists()) {
		configFile = f.getName();
	    }
	}
	System.out.println("Login Server starting up...");
	Config.initConfig(configFile);
	db = new DatabaseConnection();
	System.out.println("Connected to MySQL");
	Server.getServer();
    }

    /**
     * The login engine
     */
    private LoginEngine engine;
    /**
     * The Server SocketAcceptor
     */
    private IoAcceptor frontendAcceptor;

    private TreeMap<Integer, World> idleWorlds = new TreeMap<Integer, World>();

    /**
     * The Server SocketAcceptor
     */
    private IoAcceptor serverAcceptor;

    private TreeMap<Integer, World> worlds = new TreeMap<Integer, World>();

    private Server() {
	try {
	    engine = new LoginEngine(this);
	    engine.start();
	    serverAcceptor = createListener(Config.LS_IP, Config.LS_PORT, new LSConnectionHandler(engine));
	    frontendAcceptor = createListener(Config.QUERY_IP, Config.QUERY_PORT, new FConnectionHandler(engine));
	} catch (IOException e) {
	    Server.error(e);
	}
    }

    private IoAcceptor createListener(String ip, int port, IoHandler handler) throws IOException {
	IoAcceptor acceptor = new SocketAcceptor();
	IoAcceptorConfig config = new SocketAcceptorConfig();
	config.setDisconnectOnUnbind(true);
	((SocketSessionConfig) config.getSessionConfig()).setReuseAddress(true);
	acceptor.bind(new InetSocketAddress(ip, port), handler, config);
	return acceptor;
    }

    public PlayerSave findSave(long user, World world) {
	PlayerSave save = null;
	// for(World w : getWorlds()) {
	// PlayerSave s = w.getSave(user);
	// if(s != null) {
	// w.unassosiateSave(s);
	// save = s;
	// System.out.println("Found cached save for " +
	// DataConversions.hashToUsername(user));
	// break;
	// }
	// }
	// if(save == null) {
	// System.out.println("No save found for " +
	// DataConversions.hashToUsername(user) + ", loading fresh");
	save = PlayerSave.loadPlayer(user);
	// }
	// world.assosiateSave(save);
	return save;
    }

    public World findWorld(long user) {
	for (World w : getWorlds()) {
	    if (w.hasPlayer(user)) {
		return w;
	    }
	}
	return null;
    }

    public LoginEngine getEngine() {
	return engine;
    }

    public World getIdleWorld(int id) {
	return idleWorlds.get(id);
    }

    public World getWorld(int id) {
	if (id < 0) {
	    return null;
	}
	return worlds.get(id);
    }

    public Collection<World> getWorlds() {
	return worlds.values();
    }

    public boolean isRegistered(World world) {
	return getWorld(world.getID()) != null;
    }

    public void kill() {
	try {
	    serverAcceptor.unbindAll();
	    frontendAcceptor.unbindAll();
	    db.close();
	} catch (Exception e) {
	    Server.error(e);
	}
    }

    public boolean registerWorld(World world) {
	int id = world.getID();
	if (id < 0 || getWorld(id) != null) {
	    return false;
	}
	worlds.put(id, world);
	return true;
    }

    public void setIdle(World world, boolean idle) {
	if (idle) {
	    worlds.remove(world.getID());
	    idleWorlds.put(world.getID(), world);
	} else {
	    idleWorlds.remove(world.getID());
	    worlds.put(world.getID(), world);
	}
    }

    public boolean unregisterWorld(World world) {
	int id = world.getID();
	if (id < 0) {
	    return false;
	}
	if (getWorld(id) != null) {
	    worlds.remove(id);
	    return true;
	}
	if (getIdleWorld(id) != null) {
	    idleWorlds.remove(id);
	    return true;
	}
	return false;
    }
}