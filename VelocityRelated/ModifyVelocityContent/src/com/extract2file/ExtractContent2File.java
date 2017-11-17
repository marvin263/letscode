package com.extract2file;

import java.sql.Connection;
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

public class ExtractContent2File {

	public static void extract2File(Connection conn) throws Exception {
		ArrayList<SingleTableInfo> list = SingleTableInfo
				.getAllSingleTableInfo();
		long totalCount = 0;
		for (Iterator iterator = list.iterator(); iterator.hasNext();) {
			SingleTableInfo stInfo = (SingleTableInfo) iterator.next();
			System.out.println("\nExport " + stInfo.tableName + " begin......");
			long count = exportOneDatabaseTable(conn, stInfo);
			totalCount += count;
			System.out.println("File count: " + count);
			System.out.println("Export " + stInfo.tableName + " end");
		}
		System.out.println("\nTotal files count: " + totalCount);
		DBUtils.closeConnection(conn);
	}

	private static long exportOneDatabaseTable(Connection conn,
			SingleTableInfo stInfo) throws Exception {
		int count = 0;
		String[] arrayPrimaryKeys = stInfo.getArrayPrimaryKeys();
		String[] arrayVelocityColumns = stInfo.getArrayVelocityColumns();
		Statement stmt = null;
		try {
			stmt = conn.createStatement();
			ResultSet rs = stmt.executeQuery(stInfo.assembleSelectSql());
			while (rs.next()) {
				for (int i = 0; i < arrayVelocityColumns.length; i++) {
					String extractedFileFullPath = assembleFileFullPath(rs,
							arrayPrimaryKeys, arrayVelocityColumns[i],
							stInfo.tableName);
					String fileContent = rs.getString(arrayVelocityColumns[i]);
					FileUtil.write2File(fileContent, extractedFileFullPath,
							null);
					count++;
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DBUtils.closeStatement(stmt, null, null);
		}
		return count;
	}

	/**
	 * The extracted file is just like:
	 * 
	 * E:\Eden\workinghere\resinworkspace\jdbcspring\export_files\EI.
	 * DESCRTEMPLATE\3220--DT_TEMPLATE
	 * 
	 * 1. All files are put into: *workingDirectory*\export_files
	 * 
	 * 2. TableName, for example: EI.DESCRTEMPLATE
	 * 
	 * 3. FileName, format is: p1,p2,p3--columnNameOfVelocityContent, for
	 * example: 3220,3654,8956--DT_TEMPLATE
	 * 
	 * @param rs
	 * @param arrayPrimaryKeys
	 * @param aVelocityColName
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	private static String assembleFileFullPath(ResultSet rs,
			String[] arrayPrimaryKeys, String aVelocityColName, String tableName)
			throws Exception {
		StringBuffer sb = new StringBuffer();
		sb.append(GetConfigFileFolder.getExportedFolder() + "/");
		sb.append(tableName + "/");

		for (int i = 0; i < arrayPrimaryKeys.length; i++) {
			if (i == arrayPrimaryKeys.length - 1) {
				sb.append(rs.getInt(arrayPrimaryKeys[i]));
			} else {
				sb.append(rs.getInt(arrayPrimaryKeys[i]));
				sb.append(GetConfigFileFolder.FILE_NAME_SEPARATOR_COMMA);
			}
		}

		sb.append(GetConfigFileFolder.FILE_NAME_SEPARATOR_TWODASH
				+ aVelocityColName);
		return sb.toString();
	}

	public static void main(String[] args) {
		LoginInfo loginInfo = LoginInfo.getLoginInfo();
		Connection conn = DBUtils.getConnection(loginInfo.driverClassName,
				loginInfo.url, loginInfo.username, loginInfo.password);
		try {
			ExtractContent2File.extract2File(conn);
		} catch (Exception e) {
			e.printStackTrace();
		}
		DBUtils.closeConnection(conn);

	}
}
