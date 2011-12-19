package org.moparscape.msc.gs;

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
import org.moparscape.msc.config.Config;
import org.moparscape.msc.gs.connection.RSCConnectionHandler;
import org.moparscape.msc.gs.connection.filter.ConnectionFilter;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.core.LoginConnector;
import org.moparscape.msc.gs.event.DelayedEvent;
import org.moparscape.msc.gs.event.SingleEvent;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.util.Logger;

/**
 * The entry point for RSC server.
 */
public class Server {

	/**
	 * World instance
	 */
	private static World world = null;

	public static void main(String[] args) throws IOException {
		String configFile = "conf" + File.separator + "world.xml";
		if (args.length > 0) {
			File f = new File(args[0]);
			if (f.exists()) {
				configFile = f.getName();
			} else {
				System.out.println("Config not found: " + f.getCanonicalPath());
				displayConfigDefaulting(configFile);
			}
		} else {
			System.out.println("No config file specified.");
			displayConfigDefaulting(configFile);
		}


		Config.initConfig(configFile);
		world = Instance.getWorld();
		try {
			world.wl.loadObjects();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		World.initilizeDB();

		Logger.println(Config.SERVER_NAME + " ["
				+ (Config.members ? "P2P" : "F2P") + "] "
				+ "Server starting up...");

		server = new Server();
	}

	private static Server server;

	public static boolean isMembers() {
		return Config.members;
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

			acceptor = new SocketAcceptor(Runtime.getRuntime()
					.availableProcessors() + 1, Executors.newCachedThreadPool());
			acceptor.getFilterChain().addFirst("connectionfilter",
					new ConnectionFilter());
			IoAcceptorConfig config = new SocketAcceptorConfig();
			config.setDisconnectOnUnbind(true);

			config.setThreadModel(ThreadModel.MANUAL);
			SocketSessionConfig ssc = (SocketSessionConfig) config
					.getSessionConfig();
			ssc.setSendBufferSize(10000);
			ssc.setReceiveBufferSize(10000);
			acceptor.bind(new InetSocketAddress(Config.SERVER_IP,
					Config.SERVER_PORT), new RSCConnectionHandler(engine),
					config);
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					acceptor.unbindAll();
				}
			});
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
	 * Kills the game engine
	 * 
	 * @throws InterruptedException
	 */
	public void kill() {
		Logger.print(Config.SERVER_NAME + " shutting down...");
		running = false;
		engine.emptyWorld();
		connector.kill();
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

	public static Server getServer() {
		return server;
	}
	
	private static void displayConfigDefaulting(String file) {
		System.out.println("Defaulting to use " + file);
	}
}
