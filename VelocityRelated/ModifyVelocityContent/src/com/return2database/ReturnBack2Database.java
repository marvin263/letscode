package com.return2database;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;

import com.configfile.GetConfigFileFolder;
import com.configfile.LoginInfo;
import com.configfile.SingleTableInfo;
import com.database.DBUtils;
import com.io.FileUtil;

public class ReturnBack2Database {
	public static ArrayList<String> listUpdatedFiles = new ArrayList<String>();

	public static void returnBack2Database(Connection conn) {
		listUpdatedFiles.clear();
		File exportedFolder = new File(GetConfigFileFolder.getExportedFolder());
		if (!exportedFolder.exists()) {
			return;
		}
		File[] arrayTableNames = exportedFolder.listFiles();
		if (arrayTableNames == null) {
			return;
		}
		// Read all tables needing to be searched into this list
		ArrayList<SingleTableInfo> list = SingleTableInfo
				.getAllSingleTableInfo();
		for (int i = 0; i < arrayTableNames.length; i++) {
			// E:\Eden\workinghere\resinworkspace\jdbcspring\export_files\EI.DESCRTEMPLATE
			File fileTableName = arrayTableNames[i];
			SingleTableInfo stInfo = chooseSingleTableInfo(fileTableName, list);
			if (stInfo == null) {
				continue;
			}
			handleOneTableName(conn, fileTableName, stInfo);
		}
		System.out.println("\nTotal updated files: " + listUpdatedFiles.size());
	}

	private static SingleTableInfo chooseSingleTableInfo(File fileTableName,
			ArrayList<SingleTableInfo> list) {
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			SingleTableInfo aStInfo = (SingleTableInfo) iterator.next();
			if (aStInfo.tableName.equals(fileTableName.getName())) {
				return aStInfo;
			}
		}
		return null;// never happen
	}

	private static void handleOneTableName(Connection conn, File fileTableName,
			SingleTableInfo stInfo) {
		System.out.println("\nImport Analysing " + fileTableName.getName()
				+ " begin......");
		File[] arrayDataFiles = fileTableName.listFiles();
		if (arrayDataFiles == null) {
			System.out.println("Import Analysing " + fileTableName.getName()
					+ " end");
			return;
		}
		for (int i = 0; i < arrayDataFiles.length; i++) {
			File dataFile = arrayDataFiles[i];
			String dataFileName = dataFile.getName();

			int posTowSlash = dataFileName
					.indexOf(GetConfigFileFolder.FILE_NAME_SEPARATOR_TWODASH);

			String velocityColName = dataFileName.substring(posTowSlash
					+ GetConfigFileFolder.FILE_NAME_SEPARATOR_TWODASH.length());

			String primaryColValues = dataFileName.substring(0, posTowSlash);

			String[] arrayPrimaryColValues = primaryColValues
					.split(GetConfigFileFolder.FILE_NAME_SEPARATOR_COMMA);

			handleOneDataFile(dataFile, conn, velocityColName,
					arrayPrimaryColValues, stInfo);
		}
		System.out.println("Import Analysing " + fileTableName.getName()
				+ " end");
	}

	private static String handleOneDataFile(File dataFile, Connection conn,
			String velocityColName, String[] arrayPrimaryColValues,
			SingleTableInfo stInfo) {
		String fromFile = getFileContentByFileName(dataFile);

		String fromDatabase = getDatabaseContentByFileName(conn,
				velocityColName, arrayPrimaryColValues, stInfo);

		if (shouldUpdateDatabase(fromFile, fromDatabase) == true) {
			String fullPath = dataFile.getAbsolutePath();
			listUpdatedFiles.add(fullPath);
			System.out.println(fullPath
					+ " has been changed, updating into database");
			updateOneDatabaseRow(conn, velocityColName, arrayPrimaryColValues,
					stInfo);
		}
		return null;
	}

	private static String getFileContentByFileName(File fileTableName) {
		String strContent = FileUtil.readFromFile(
				fileTableName.getAbsolutePath(), null);
		return strContent;
	}

	private static String getDatabaseContentByFileName(Connection conn,
			String velocityColName, String[] arrayPrimaryColValues,
			SingleTableInfo stInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append("select " + velocityColName + " from " + stInfo.tableName
				+ " where ");

		String[] arrayPrimaryKeys = stInfo.getArrayPrimaryKeys();
		for (int i = 0; i < arrayPrimaryKeys.length; i++) {
			sb.append(arrayPrimaryKeys[i]);
			sb.append("=");
			sb.append(arrayPrimaryColValues[i]);
			if (i == arrayPrimaryKeys.length - 1) {
			} else {
				sb.append(" and ");
			}
		}

		String strContent = null;
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(sb.toString());
			if (rs.next()) {
				strContent = rs.getString(velocityColName);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeStatement(stmt, null, null);
		}
		return strContent;
	}

	private static String updateOneDatabaseRow(Connection conn,
			String velocityColName, String[] arrayPrimaryColValues,
			SingleTableInfo stInfo) {
		StringBuffer sb = new StringBuffer();
		sb.append("update " + stInfo.tableName + " set " + velocityColName
				+ "= ? where ");

		String[] arrayPrimaryKeys = stInfo.getArrayPrimaryKeys();
		for (int i = 0; i < arrayPrimaryKeys.length; i++) {
			sb.append(arrayPrimaryKeys[i]);
			sb.append("=");
			sb.append("?");
			if (i == arrayPrimaryKeys.length - 1) {
			} else {
				sb.append(" and ");
			}
		}

		String strContent = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, strContent);
			for (int i = 0; i < arrayPrimaryColValues.length; i++) {
				pstmt.setInt(2 + i, Integer.parseInt(arrayPrimaryColValues[i]));
			}
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeStatement(pstmt, null, null);
		}
		return strContent;
	}

	private static boolean shouldUpdateDatabase(String fromFile,
			String fromDatabase) {
		if (fromFile == null || fromDatabase == null) {
			return false;
		}
		if (fromFile.trim().equals("") || fromDatabase.trim().equals("")) {
			return false;
		}
		if (fromFile.trim().equals(fromDatabase.trim())) {
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		LoginInfo loginInfo = LoginInfo.getLoginInfo();
		Connection conn = DBUtils.getConnection(loginInfo.driverClassName,
				loginInfo.url, loginInfo.username, loginInfo.password);
		returnBack2Database(conn);
		DBUtils.closeConnection(conn);

	}
}
