package org.moparscape.msc.gs.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.moparscape.msc.config.Config;
import org.moparscape.msc.gs.util.Logger;

/**
 * Used to interact with the database.
 */
public class DBConnection {

	/**
	 * The database connection in use
	 */
	private Connection con;
	/**
	 * A statement for running queries on
	 */
	private Statement statement;
	/**
	 * Report2Database handleing logic
	 */
	private static ReportHandlerQueries reportDB = new ReportHandlerQueries();

	/**
	 * Getter for reportDB
	 * 
	 * @return reportDB
	 */
	public static ReportHandlerQueries getReport() {
		return reportDB;
	}

	static {
		testForDriver();
	}

	/**
	 * Tests we have a mysql Driver
	 */
	private static void testForDriver() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException cnfe) {
			cnfe.printStackTrace();
		}
	}

	/**
	 * Instantiates a new database connection
	 */
	public DBConnection() {
		if (!createConnection()) {
			new Exception("Unable to connect to MySQL").printStackTrace();
			System.exit(1);
		} else {
			Logger.println("Database connection achieved.");
		}
	}

	public void initilizePreparedStatements(DBConnection db) {
		reportDB.initilizePreparedStatements(db);
	}

	public boolean createConnection() {
		try {
			con = DriverManager.getConnection("jdbc:mysql://"
					+ Config.MYSQL_HOST + "/" + Config.MYSQL_DB,
					Config.MYSQL_USER, Config.MYSQL_PASS);
			statement = con.createStatement();
			statement.setEscapeProcessing(true);
			return isConnected();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean isConnected() {
		try {
			statement.executeQuery("SELECT CURRENT_DATE");
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public ResultSet getQuery(String q) throws SQLException {
		try {
			return statement.executeQuery(q);
		} catch (SQLException e) {
			if (!isConnected() && createConnection()) {
				return getQuery(q);
			}
			throw new SQLException(e.getMessage() + ": '" + q + "'",
					e.getSQLState(), e.getErrorCode());
		}
	}

	public Connection getConnection() {
		return con;
	}

	/*
	 * public synchronized int updateUserStats(String user, int messages, int
	 * modes, int kicks, int kicked, long lastTimeSpoken, int joins, int parts,
	 * String randomstring, int moderatedchan) throws SQLException {
	 * existingRecord.setString(1, user); existingRecord.execute();
	 * if(existingRecord.getResultSet().next()) { // Existing
	 * existingUserQuery.setInt(1, messages); existingUserQuery.setInt(2,
	 * modes); existingUserQuery.setInt(3, kicks); existingUserQuery.setInt(4,
	 * kicked); existingUserQuery.setLong(5, lastTimeSpoken);
	 * existingUserQuery.setInt(6, joins); existingUserQuery.setInt(7, parts);
	 * existingUserQuery.setString(8, randomstring); existingUserQuery.setInt(9,
	 * moderatedchan); existingUserQuery.setString(10, user); return
	 * existingUserQuery.executeUpdate(); } else { // New record
	 * newUserQuery.setString(1, user); newUserQuery.setInt(2, messages);
	 * newUserQuery.setInt(3, modes); newUserQuery.setInt(4, kicks);
	 * newUserQuery.setInt(5, kicked); newUserQuery.setLong(6, lastTimeSpoken);
	 * newUserQuery.setInt(7, joins); newUserQuery.setInt(8, parts);
	 * newUserQuery.setString(9, randomstring); newUserQuery.setInt(10,
	 * moderatedchan); return newUserQuery.executeUpdate(); }
	 * 
	 * }
	 */

	/**
	 * Closes the database conection.
	 * 
	 * @throws SQLException
	 *             if there was an error when closing the connection
	 */
	public void close() throws SQLException {
		con.close();
		con = null;
	}
}
