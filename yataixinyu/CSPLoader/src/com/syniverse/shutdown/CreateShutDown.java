package com.syniverse.shutdown;

import java.io.File;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import com.syniverse.common.CommUtil;
import com.syniverse.io.IOUtil;

public class CreateShutDown {
	private static final Log LOGGER = LogFactory.getLog(CreateShutDown.class);

	public CreateShutDown() {
	}

	private void configLogFirst() {
		String jarFolderSelf = IOUtil
				.getJarStayFolder_nologinfo(CreateShutDown.class);
		String jarFolderParent = new File(jarFolderSelf).getParentFile()
				.getAbsolutePath();
		String logDirFullPath = jarFolderParent + "/log";
		IOUtil.ensureFolderExist(logDirFullPath);

		InputStream is = null;
		try {
			is = CreateShutDown.class
					.getResourceAsStream("shutdownlog4j.properties");
			Properties prop = new Properties();
			prop.load(is);
			PropertyConfigurator.configure(prop);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public boolean createShutDownFile() {
		configLogFirst();
		String fullPath = shutdownFullPath();
		LOGGER.info("Shutting down......");
		IOUtil.createNewFile(fullPath);
		LOGGER.info(CommUtil
				.format("Command sent successfully. Loader will shut down within {0} seconds)",
						ShutDownService.SHUTDOWN_DELAY));
		return true;
	}

	public static String shutdownFullPath() {
		String jarFolderSelf = IOUtil
				.getJarStayFolder_nologinfo(CreateShutDown.class);
		String jarFolderParent = new File(jarFolderSelf).getParentFile()
				.getAbsolutePath();
		return jarFolderParent + "/bin/stopTheWorld";
	}

	public static void main(String[] args) {
		new CreateShutDown().createShutDownFile();
	}
}
