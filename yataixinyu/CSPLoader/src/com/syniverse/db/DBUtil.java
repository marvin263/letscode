package com.syniverse.db;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.common.MemoryUsage;
import com.syniverse.config.Config;
import com.syniverse.shutdown.ShutDownService;

public class DBUtil {
	private static final Log LOGGER = LogFactory.getLog(DBUtil.class);

	public static void closeStmt(Statement stmt) {
		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (Throwable e) {
			LOGGER.error("Error when close Statement", e);
		}
	}

	public static void closeConn(Connection conn) {
		try {
			if (conn != null) {
				conn.close();
			}
		} catch (Throwable e) {
			LOGGER.error("Error when close Statement", e);
		} finally {
			if (conn != null) {
				ShutDownService.removeClosed(conn);
			}
		}
	}

	public static void closeConnAndMultiPstmt(Connection conn,
			PreparedStatement[] arrayPstmt) {
		if (arrayPstmt != null) {
			for (PreparedStatement pstmt : arrayPstmt) {
				closeStmt(pstmt);
			}
		}
		closeConn(conn);
	}

	public static void closeConnAndStmt(Connection conn, Statement stmt) {
		closeStmt(stmt);
		closeConn(conn);
	}

	public static void rollback(Connection conn) {
		try {
			conn.rollback();
		} catch (Throwable e) {
			LOGGER.error("Error when rollback", e);
			throw new RuntimeException("Connection rollback exception", e);
		}
	}

	public static boolean commit(Connection conn) {
		try {
			conn.commit();
			return true;
		} catch (Throwable e) {
			LOGGER.error("Error when commit", e);
			throw new RuntimeException("Connection commit exception", e);
		}
	}

	public static void addBatch(PreparedStatement pstmt) {
		try {
			pstmt.addBatch();
		} catch (Throwable e) {
			LOGGER.error("Error when addBatch", e);
			throw new RuntimeException("PreparedStatement addBatch exception",
					e);
		}
	}

	private static Connection getConnection(String driverClassName,
			String jdbcURL, String username, String password) {
		try {
			Class.forName(driverClassName);

			Connection conn = DriverManager.getConnection(jdbcURL, username,
					password);

			return conn;
		} catch (Exception e) {
			LOGGER.error("Fatal error, DBUtil.getConnection()", e);
		}
		return null;
	}

	/**
	 * Try 3 times at most to get a new Connection. If success, return
	 * connection. If fail, JVM exit
	 * 
	 * @return
	 */
	public static Connection getNewC() {
		return getValidateConnection(
				Config.getString(Config.Connection_driverClassName),
				Config.getString(Config.Connection_jdbcURL),
				Config.getString(Config.Connection_username),
				Config.getString(Config.Connection_password),
				Config.getString(Config.Connection_testSQL));
	}

	/**
	 * Try 3 times at most to get a new Connection. If success, return
	 * connection. If fail, return null
	 * 
	 * @return
	 */
	public static Connection getValidateConnection(String driverClassName,
			String jdbcURL, String username, String password, String testSql) {
		long begin = System.currentTimeMillis();
		Connection conn = null;
		// start from 0
		int whichRound = 0;
		while (whichRound < 3) {
			LOGGER.info("getValidateConnection round: " + whichRound);
			conn = tryGetConnection(driverClassName, jdbcURL, username,
					password, testSql);
			if (conn != null) {
				break;
			}
			whichRound++;
		}
		if (conn == null) {
			LOGGER.fatal("Fatal error, getValidateConnection() fails. round: "
					+ (whichRound - 1)
					+ ", getValidateConnection() finally fails");
			// System.exit(1);
			return null;
		}
		long end = System.currentTimeMillis();
		LOGGER.info("getValidateConnection successfully, round: " + whichRound
				+ ", costs in milliseconds: " + MemoryUsage.human(end - begin));

		ShutDownService.addNew(conn);
		return conn;
	}

	private static Connection tryGetConnection(String driverClassName,
			String jdbcURL, String username, String password, String testSql) {
		Connection conn = DBUtil.getConnection(driverClassName, jdbcURL,
				username, password);

		boolean connOK = isValid(conn, testSql);
		return connOK == true ? conn : null;

	}

	private static boolean isValid(Connection conn, String testSql) {
		// PLEASE always check null
		if (conn == null) {
			return false;
		}
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(testSql);
			while (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			LOGGER.error("Fatal error, isValid() exception. ", e);
			// When fatal error, try close both
			DBUtil.closeConnAndStmt(conn, stmt);
		} finally {
			// In either case (good or bad), close stmt only
			DBUtil.closeStmt(stmt);
		}
		return false;
	}

	public static boolean isValid(Connection conn) {
		return isValid(conn, Config.getString(Config.Connection_testSQL));
	}

	public static void testResultSetMetaData(String[] args) throws Exception {
		Connection conn = getNewC();
		String sql = "select BILLING_ID, SUBSCRIBER_KEY, SUBSCRIBER_NAME, INTERNAL_SCORING, SERVICE_START_DATE, VOICE_SERVICE, CREATED_TIMESTAMP, BILL_CYCLE_END_DAY  from subscriber where 1!=1";
		Statement stmt = conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		ResultSetMetaData rsm = rs.getMetaData();
		int cnt = rsm.getColumnCount();
		for (int i = 0; i < cnt; i++) {
			StringBuilder sb = new StringBuilder();
			String name = rsm.getColumnName(i + 1);
			sb.append(", name=" + name);
			String label = rsm.getColumnLabel(i + 1);
			sb.append(", label=" + label);
			String classname = rsm.getColumnClassName(i + 1);
			sb.append(", fully-qualified java classname=" + classname);
			int coltype = rsm.getColumnType(i + 1);
			sb.append(", java sql int coltype=" + coltype);
			String typename = rsm.getColumnTypeName(i + 1);
			sb.append(", db-specified typename=" + typename);
			int colDisplaySize = rsm.getColumnDisplaySize(i + 1);
			sb.append(", colDisplaySize=" + colDisplaySize);
			int precision = rsm.getPrecision(i + 1);
			sb.append(", precision=" + precision);
			int scale = rsm.getScale(i + 1);
			sb.append(", scale=" + scale);

			System.out.println(sb.toString());
		}
		DBUtil.closeConnAndStmt(conn, null);
	}

	public static void main(String[] args) throws Exception {
		Connection conn = DBUtil.getNewC();
		conn.setAutoCommit(false);
		String sql = "insert into SUBSCRIBER(BILLING_ID,SUBSCRIBER_KEY,TECHNOLOGY_TYPE,IMSI,MIN,MDN,MSISDN,INTERNAL_SCORING,DO_NOT_SOLICIT,DO_NOT_SOLICIT_DATE) values(?,?,?,?,?,?,?,?,?,?)";
		Object[] array = { null, "106", "302780000251216", "GSM",
				"302780000251216", "", "", "80000251216", new Integer(98), "Y",
				null };
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			for (int i = 1; i <= 7; i++) {
				pstmt.setObject(i, array[i], 12);
			}

			pstmt.setObject(8, new Integer(98), 2);
			pstmt.setObject(9, "Y", 1);
			pstmt.setObject(10, null, 93);
			pstmt.addBatch();
			int[] aa = pstmt.executeBatch();

		} catch (BatchUpdateException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			DBUtil.closeConnAndStmt(conn, pstmt);
		}

	}
}
