package org.moparscape.msc.gs.db.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;

import org.moparscape.msc.gs.Instance;
import org.moparscape.msc.gs.core.GameEngine;
import org.moparscape.msc.gs.db.ReportHandler;
import org.moparscape.msc.gs.model.Player;
import org.moparscape.msc.gs.model.World;
import org.moparscape.msc.gs.model.snapshot.Activity;
import org.moparscape.msc.gs.model.snapshot.Chatlog;
import org.moparscape.msc.gs.model.snapshot.Snapshot;
import org.moparscape.msc.gs.util.Logger;

class MySQLReportHandler implements ReportHandler {

	MySQLReportHandler() {
	}

	/**
	 * Inserts a new row into "msc2_reports" table
	 */
	private PreparedStatement insertNewReportRow;

	private PreparedStatement insertNewDupeDataRow;

	/**
	 * Initilizes prepared statements, if failed, shuts down the server.
	 */
	public void initilizePreparedStatements(DBConnection db) {
		try {
			insertNewReportRow = db
					.getConnection()
					.prepareStatement(
							"INSERT INTO `pk_reports` (`from`, `about`, `time`, `reason`, `snapshot_from`,`snapshot_about`,`chatlogs`, `from_x`, `from_y`, `about_x`, `about_y`) VALUES(?,?,?,?,?,?,?,?,?,?,?)");
			insertNewDupeDataRow = db
					.getConnection()
					.prepareStatement(
							"INSERT INTO `dupe_data` (`user`, `userhash`, `string`, `time`) VALUES(?,?,?,?);");

		} catch (SQLException e) {
			e.printStackTrace();
			Logger.error(e);
		}
	}

	/**
	 * Submits a report to the database
	 * 
	 * Chatlogs, snapshots, locations etc are generated inside this method.
	 * 
	 * @param from
	 *            Players usernameHash (who reported)
	 * @param about
	 *            Players usernameHash (who got reported)
	 * @param reason
	 *            What type of a report is this
	 */
	@Override
	public synchronized void submitReport(long from, long about, byte reason,
			Player from2) {

		long time = GameEngine.getTime() / 1000;
		String f = org.moparscape.msc.gs.tools.DataConversions
				.hashToUsername(from);
		String a = org.moparscape.msc.gs.tools.DataConversions
				.hashToUsername(about);

		Player about2 = World.getWorld().getPlayer(about);
		int player2X;
		int player2Y;
		if (about2 == null) {
			player2X = 0;
			player2Y = 0;
		} else {
			player2X = about2.getX();
			player2Y = about2.getY();
		}
		StringBuilder snapshot_from = new StringBuilder();
		StringBuilder snapshot_about = new StringBuilder();

		StringBuilder chatlog = new StringBuilder();
		Iterator<Snapshot> i = Instance.getWorld().getSnapshots()
				.descendingIterator();
		while (i.hasNext()) {
			Snapshot s = i.next();
			if (s instanceof Chatlog) {
				Chatlog cl = (Chatlog) s;
				if (cl.getRecievers().contains(a) || cl.getOwner().equals(a)) {
					chatlog.append((cl.getTimestamp() / 1000) + " <"
							+ cl.getOwner() + "> " + cl.getMessage() + "\n");
				}
			} else if (s instanceof Activity) {
				Activity ac = (Activity) s;
				if (ac.getOwner().equals(f)) {
					snapshot_from.append((ac.getTimestamp() / 1000) + " "
							+ ac.getActivity() + "\n");
				} else if (ac.getOwner().equals(a)) {
					snapshot_about.append((ac.getTimestamp() / 1000) + " "
							+ ac.getActivity() + "\n");
				}
			}
		}
		try {
			insertNewReportRow.setLong(1, from);
			insertNewReportRow.setLong(2, about);
			insertNewReportRow.setLong(3, time);
			insertNewReportRow.setInt(4, reason);
			insertNewReportRow.setString(5, snapshot_from.toString());
			insertNewReportRow.setString(6, snapshot_about.toString());
			insertNewReportRow.setString(7, chatlog.toString());
			insertNewReportRow.setInt(8, from2.getX());
			insertNewReportRow.setInt(9, from2.getY());
			insertNewReportRow.setInt(10, player2X);
			insertNewReportRow.setInt(11, player2Y);
			insertNewReportRow.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.error(e);
		}
	}

	@Override
	public synchronized void submitDupeData(String username, Long hash) {
		StringBuilder data = new StringBuilder();
		Iterator<Snapshot> i = Instance.getWorld().getSnapshots()
				.descendingIterator();
		while (i.hasNext()) {
			Snapshot s = i.next();
			if (s instanceof Chatlog) {
				Chatlog cl = (Chatlog) s;
				if (cl.getRecievers().contains(username)
						|| cl.getOwner().equals(username)) {
					data.append((cl.getTimestamp() / 1000) + " <"
							+ cl.getOwner() + "> " + cl.getMessage() + "\n");
				}
			} else if (s instanceof Activity) {
				Activity ac = (Activity) s;
				if (ac.getOwner().equals(username)) {
					data.append((ac.getTimestamp() / 1000) + " "
							+ ac.getActivity() + "\n");
				}
			}
		}
		try {
			insertNewDupeDataRow.setString(1, username);
			insertNewDupeDataRow.setLong(2, hash);
			insertNewDupeDataRow.setString(3, data.toString());
			insertNewDupeDataRow.setLong(4, GameEngine.getTime());
			insertNewDupeDataRow.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			Logger.error(e);
		}
	}
}
