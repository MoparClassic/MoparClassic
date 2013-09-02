package org.moparscape.msc.ls.packethandler.gameserver;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;

import org.apache.mina.common.IoSession;
import org.moparscape.msc.ls.Server;
import org.moparscape.msc.ls.net.Packet;
import org.moparscape.msc.ls.packethandler.PacketHandler;
import org.moparscape.msc.ls.util.Config;

public class LogHandler implements PacketHandler {
	private static PrintWriter error;

	private static PrintWriter event;
	private static final SimpleDateFormat formatter = new SimpleDateFormat(
			"HH:mm:ss dd-MM-yy");
	private static PrintWriter mod;
	private static PrintWriter exception;
	static {
		try {
			File parent = new File(Config.LOG_DIR);
			File fevent = new File(Config.LOG_DIR, "event.log");
			File fmod = new File(Config.LOG_DIR, "mod.log");
			File ferr = new File(Config.LOG_DIR, "err.log");
			File ferror = new File(Config.LOG_DIR, "error.log");
			if (!parent.exists()) {
				parent.mkdirs();
			}
			createIfAbsent(fevent);
			createIfAbsent(fmod);
			createIfAbsent(ferr);
			createIfAbsent(ferror);
			event = new PrintWriter(fevent);
			error = new PrintWriter(ferror);
			mod = new PrintWriter(fmod);
			exception = new PrintWriter(ferr);
		} catch (Exception e) {
			Server.error(e);
		}
	}

	private static String getDate() {
		return formatter.format(System.currentTimeMillis());
	}

	private static void createIfAbsent(File f) throws IOException {
		if (!f.exists()) {
			f.createNewFile();
		}
	}

	public void handlePacket(Packet p, IoSession session) throws Exception {
		byte type = p.readByte();
		String message = getDate() + ": " + p.readString();
		switch (type) {
		case 1:
			event.println(message);
			event.flush();
			break;
		case 2:
			error.println(message);
			error.flush();
			break;
		case 3:
			mod.println(message);
			mod.flush();
			break;
		case 4:
			exception.println(message);
			exception.flush();
			break;
		}
	}

}
