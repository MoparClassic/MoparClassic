package msc.gs;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoAcceptorConfig;
import org.apache.mina.common.ThreadModel;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.apache.mina.transport.socket.nio.SocketSessionConfig;

import msc.config.Config;
import msc.config.Constants;
import msc.gs.connection.RSCConnectionHandler;
import msc.gs.core.GameEngine;
import msc.gs.core.LoginConnector;
import msc.gs.event.DelayedEvent;
import msc.gs.event.SingleEvent;
import msc.gs.model.World;
import msc.gs.util.Logger;
import msc.irc.IRC;

/**
 * The entry point for RSC server.
 */
public class Server {

    /**
     * World instance
     */
    private static World world = null;

    public static void main(String[] args) throws IOException {
	String configFile = "conf/server/Conf.xml";
	if (args.length > 0) {
	    File f = new File(args[0]);
	    if (f.exists()) {
		configFile = f.getName();
	    }
	}
	

	if (args[2] != null && args[2].equalsIgnoreCase("no"))
	    Constants.IRC.USE_IRC = false;
	
	Constants.GameServer.MEMBER_WORLD = args[1].equalsIgnoreCase("p2p");
	Constants.GameServer.MOTD = "@yel@Welcome to @whi@" + Constants.GameServer.SERVER_NAME + "@yel@ - World @whi@" + (Constants.GameServer.WORLD_NUMBER == 0 ? 2 : Constants.GameServer.WORLD_NUMBER) + " (" + (Constants.GameServer.MEMBER_WORLD ? "P2P" : "F2P") + ")";
	
	world = Instance.getWorld();
	world.wl.loadObjects();

	Config.initConfig(configFile);
	World.initilizeDB();

	Logger.println(Constants.GameServer.SERVER_NAME + " [" + (Constants.GameServer.MEMBER_WORLD ? "P2P" : "F2P") + "] " + "Server starting up...");
	
	new Server();
    }

    public static boolean isMembers() {
	return Constants.GameServer.MEMBER_WORLD;
    }

    /**
     * The SocketAcceptor
     */
    private IoAcceptor acceptor;
    /**
     * The login server connection
     */
    private LoginConnector connector;
    /**
     * The game engine
     */
    private GameEngine engine;
    /**
     * The IRC.
     */
    private IRC irc;

    public IoAcceptor getAcceptor() {
	return acceptor;
    }

    public void setAcceptor(IoAcceptor acceptor) {
	this.acceptor = acceptor;
    }

    public LoginConnector getConnector() {
	return connector;
    }

    public void setConnector(LoginConnector connector) {
	this.connector = connector;
    }

    public IRC getIRC() {
	return irc;
    }

    public void setIRC(IRC irc) {
	this.irc = irc;
    }

    public boolean isRunning() {
	return running;
    }

    public void setRunning(boolean running) {
	this.running = running;
    }

    public DelayedEvent getUpdateEvent() {
	return updateEvent;
    }

    public void setUpdateEvent(DelayedEvent updateEvent) {
	this.updateEvent = updateEvent;
    }

    public static World getWorld() {
	return world;
    }

    public void setEngine(GameEngine engine) {
	this.engine = engine;
    }

    /**
     * Is the server running still?
     */
    private boolean running;

    /**
     * Update event - if the server is shutting down
     */
    private DelayedEvent updateEvent;

    /**
     * Creates a new server instance, which in turn creates a new engine and
     * prepares the server socket to accept connections.
     */
    public Server() {
	running = true;
	world.setServer(this);
	irc = new IRC();
	if (Constants.IRC.USE_IRC) {
	    new Thread(irc).start();
	} else {
	    Logger.println("IRC is disabled");
	}
	try {
	    Instance.getPluginHandler().initPlugins();
	} catch (Exception e) {
	    e.printStackTrace();
	}
	try {
	    connector = new LoginConnector();
	    engine = new GameEngine();
	    engine.start();
	    while (!connector.isRegistered()) {
		Thread.sleep(100);
	    }

	    acceptor = new SocketAcceptor(Runtime.getRuntime().availableProcessors() + 1, Executors.newCachedThreadPool());
	    IoAcceptorConfig config = new SocketAcceptorConfig();
	    config.setDisconnectOnUnbind(true);

	    config.setThreadModel(ThreadModel.MANUAL);
	    SocketSessionConfig ssc = (SocketSessionConfig) config.getSessionConfig();
	    ssc.setSendBufferSize(10000);
	    ssc.setReceiveBufferSize(10000);
	    acceptor.bind(new InetSocketAddress(Config.SERVER_IP, Config.SERVER_PORT), new RSCConnectionHandler(engine), config);

	} catch (Exception e) {
	    Logger.error(e);
	}
    }

    /**
     * Returns the game engine for this server
     */
    public GameEngine getEngine() {
	return engine;
    }

    public LoginConnector getLoginConnector() {
	return connector;
    }

    public boolean isInitialized() {
	return engine != null && connector != null;
    }

    /**
     * Kills the game engine and irc engine
     * 
     * @throws InterruptedException
     */
    public void kill() {
	Logger.print(Constants.GameServer.SERVER_NAME + " shutting down...");
	running = false;
	engine.emptyWorld();
	connector.kill();
	if (Constants.IRC.USE_IRC) {
	    Instance.getIRC().disconnect();
	    Instance.getIRC().dispose();
	}
	System.exit(0);

    }

    public boolean running() {
	return running;
    }

    /**
     * Shutdown the server in 60 seconds
     */
    public boolean shutdownForUpdate() {
	if (updateEvent != null) {
	    return false;
	}
	updateEvent = new SingleEvent(null, 59000) {
	    public void action() {
		kill();
	    }
	};
	Instance.getDelayedEventHandler().add(updateEvent);
	return true;
    }

    /**
     * MS till the server shuts down
     */
    public int timeTillShutdown() {
	if (updateEvent == null) {
	    return -1;
	}
	return updateEvent.timeTillNextRun();
    }

    /**
     * Unbinds the socket acceptor
     */
    public void unbind() {
	try {
	    acceptor.unbindAll();
	} catch (Exception e) {
	}
    }
}
