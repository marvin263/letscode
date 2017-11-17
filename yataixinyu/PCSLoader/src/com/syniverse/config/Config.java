package com.syniverse.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.util.HashMap;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import com.syniverse.common.CommUtil;
import com.syniverse.db.DBManipulate;
import com.syniverse.db.DBUtil;
import com.syniverse.io.IOUtil;

public class Config {
	private static final Log LOGGER = LogFactory.getLog(Config.class);

	/* Connection info keys, begin */
	public static final String Connection_driverClassName = "driverClassName";
	public static final String Connection_jdbcURL = "jdbcURL";
	public static final String Connection_username = "username";
	public static final String Connection_password = "password";
	public static final String Connection_testSQL = "testSQL";
	/* Connection info keys, end */

	/* property keys, begin */
	public static final String Key_Poll_Interval_Second = "POLL_INTVL_SCND";
	public static final String Key_Commit_Threshold = "CMT_THRESHOLD";
	public static final String Key_Split_File_Count = "SPLIT_FILE_CNT";
	public static final String Key_Scan_Folder_External = "INBOUND_EXT";
	public static final String Key_Scan_Folder_Internal = "INBOUND_INT";
	public static final String Key_Backup_Dir = "LOAD_BKUP_DIR";
	public static final String Key_Load_Result_Dir = "LOAD_RST_DIR";
	/* property keys, end */

	public static final boolean Use_DB_Config = true;

	public static final String[] ALL_PARA_KEYS = new String[] {
			Key_Poll_Interval_Second,// 1
			Key_Commit_Threshold,// 2
			Key_Split_File_Count,// 3
			Key_Scan_Folder_External,// 4
			Key_Scan_Folder_Internal,// 5
			Key_Backup_Dir, // 6
			Key_Load_Result_Dir // 7
	};

	/**
	 * values of APP_CONFIG.CONFIG_TYPE which Loader needs
	 */
	public static final String[] LOADER_CONFIG_TYPE = new String[] {
			"POLL_FREQUENCE",// 1
			"BATCH_LIMITS",// 2
			"BATCH_FILE_DIR"// 3
	};
	private Properties prop;

	private static Config INSTANCE = new Config();

	public static Config getINSTANCE() {
		return INSTANCE;
	}

	private Config() {
		Properties connProp = getConnectionConfig();

		prop = new Properties();
		prop.putAll(connProp);

		// Configuration stores in database
		if (Use_DB_Config) {
			initializeByDB(connProp);
		}
		// Configuration stores in file
		else {
			initializeByFile();
		}
		correctIfNecessary();
	}

	private Properties getConnectionConfig() {
		String jarFolderSelf = IOUtil.getJarStayFolder(Config.class);
		String jarFolderParent = new File(jarFolderSelf).getParentFile()
				.getAbsolutePath();
		String connCfgFullPath = jarFolderParent
				+ "/conf/ConnectionConfig.properties";
		LOGGER.info("DB configuration file: " + connCfgFullPath);

		Properties connProp = readConfigFile(connCfgFullPath);

		String driverClassName = CommUtil.trimToEmpty(connProp
				.getProperty(Connection_driverClassName));
		connProp.setProperty(Connection_driverClassName, driverClassName);

		String jdbcURL = CommUtil.trimToEmpty(connProp
				.getProperty(Connection_jdbcURL));
		connProp.setProperty(Connection_jdbcURL, jdbcURL);

		String username = CommUtil.trimToEmpty(connProp
				.getProperty(Connection_username));
		connProp.setProperty(Connection_username, username);

		String password = CommUtil.trimToEmpty(connProp
				.getProperty(Connection_password));
		connProp.setProperty(Connection_password, password);

		String testSQL = CommUtil.trimToEmpty(connProp
				.getProperty(Connection_testSQL));
		connProp.setProperty(Connection_testSQL, testSQL);

		return connProp;
	}

	private void initializeByDB(Properties connProp) {
		Connection conn = null;
		try {
			conn = DBUtil.getValidateConnection(
					connProp.getProperty(Config.Connection_driverClassName),
					connProp.getProperty(Config.Connection_jdbcURL),
					connProp.getProperty(Config.Connection_username),
					connProp.getProperty(Config.Connection_password),
					connProp.getProperty(Config.Connection_testSQL));
			HashMap<String, String> map = DBManipulate.selectDBConfiguration(
					ALL_PARA_KEYS, conn, LOADER_CONFIG_TYPE);
			prop.putAll(map);
		} catch (Exception e) {
			LOGGER.error("Error when Config.initializeByDB()", e);
			DBUtil.closeConnAndStmt(conn, null);
		}
	}

	private void initializeByFile() {
		Properties loaderProp = getLoaderConfig();
		prop.putAll(loaderProp);
	}

	private Properties getLoaderConfig() {
		String jarFolderSelf = IOUtil.getJarStayFolder(Config.class);
		String jarFolderParent = new File(jarFolderSelf).getParentFile()
				.getAbsolutePath();
		String loaderCfgFullPath = jarFolderParent
				+ "/conf/LoaderConfig.properties";
		LOGGER.info("Loader configuration file: " + loaderCfgFullPath);
		Properties loaderProp = readConfigFile(loaderCfgFullPath);
		return loaderProp;
	}

	private void correctIfNecessary() {
		// Key_Commit_Threshold
		int cmtThreshold = _getInt(Key_Commit_Threshold);
		if (cmtThreshold <= 0) {
			cmtThreshold = 500;
		}
		if (cmtThreshold >= 5001) {
			cmtThreshold = 5000;
		}
		prop.setProperty(Key_Commit_Threshold, cmtThreshold + "");

		// Key_Split_File_Count
		int splitCount = _getInt(Key_Split_File_Count);
		if (splitCount <= 0) {
			splitCount = 2;
		}
		if (splitCount >= 11) {
			splitCount = 10;
		}
		prop.setProperty(Key_Split_File_Count, splitCount + "");
	}

	private Properties readConfigFile(String cfgFullPath) {
		Properties prop = null;
		try {
			prop = new Properties();
			InputStream fis = getInputStream(cfgFullPath);
			prop.load(fis);
			fis.close();
		} catch (Exception e) {
			LOGGER.error(
					"Error when readConfigFile, configfile=" + cfgFullPath, e);
		}
		return prop;
	}

	private InputStream getInputStream(String configfile) {
		InputStream is = null;
		try {
			is = new FileInputStream(configfile);
		} catch (Exception e) {
			throw new RuntimeException("Cannot find configuration file, file="
					+ configfile);
		}
		return is;
	}

	private String _getString(String key) {
		return CommUtil.trimToEmpty(prop.getProperty(key));
	}

	public static String getString(String key) {
		return getINSTANCE()._getString(key);
	}

	private int _getInt(String key) {
		String strValue = CommUtil.trimToEmpty(prop.getProperty(key));
		int intValue = Integer.parseInt(strValue);
		return intValue;
	}

	public static int getInt(String key) {
		return getINSTANCE()._getInt(key);
	}

	public static void main(String[] args) {
		PropertyConfigurator.configure("conf/log4j.properties");
		Config.getINSTANCE();
		DBUtil.closeConnAndStmt(DBUtil.getNewC(), null);
	}
}
