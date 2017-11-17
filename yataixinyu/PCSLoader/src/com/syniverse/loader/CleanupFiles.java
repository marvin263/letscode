package com.syniverse.loader;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.common.CommUtil;
import com.syniverse.common.MemoryUsage;
import com.syniverse.info.ProcessingFileInfo;
import com.syniverse.io.IOUtil;

public class CleanupFiles {
	private static final Log LOGGER = LogFactory.getLog(CleanupFiles.class);

	/**
	 * Do three things:
	 * <p>
	 * 1. Merege all the errors/info files to one errors/info file, and put in
	 * under ResultErrorDir/ResultInfoDir
	 * <p>
	 * 2. Backup data file to BackupDir
	 * <p>
	 * 3. Cleanup working directory
	 * 
	 * 
	 * 
	 * @param orgnfInfo
	 * @param listSplitFile
	 */
	public void dealWithFilesAfterLoader(ProcessingFileInfo orgnfInfo) {
		long begin = System.currentTimeMillis();
		LOGGER.info("Begin dealWithFilesAfterLoader " + orgnfInfo);
		try {
			cleanupWorkingDir(orgnfInfo);
		} catch (Throwable e) {
			LOGGER.error("Error when dealWithFilesAfterLoader(), file "
					+ orgnfInfo);
		}

		long end = System.currentTimeMillis();
		LOGGER.info(CommUtil.format(
				"End dealWithFilesAfterLoader, cost: {0}, file{1} ",
				MemoryUsage.human(end - begin), orgnfInfo));
	}

	private void cleanupWorkingDir(ProcessingFileInfo orgnfInfo) {
		if (orgnfInfo != null) {
			if (CommUtil.isEmpty(orgnfInfo.getDatafilename()) == false) {
				deleteIfStartWithBaldname(orgnfInfo.getDatafileFullPath());
			}

			String zipfilename = orgnfInfo.filenameInDB;
			// zip file
			if (CommUtil.isEmpty(zipfilename) == false) {
				String parentFolder = orgnfInfo.getDatafileParentFolder();
				deleteIfStartWithBaldname(parentFolder + "/" + zipfilename);
			}
			// NOT zip file
			else {

			}

		}
	}

	private int deleteIfStartWithBaldname(String fullPath) {
		LOGGER.info("Remove datafile related files in working directory. datafileFullPath:"
				+ fullPath);
		File f = new File(fullPath);
		String baldFilename = IOUtil.eliminate_from_firstDot(f.getName());
		File fParentFolder = f.getParentFile();
		File[] array = fParentFolder.listFiles();
		if (array == null || array.length == 0) {
			return 0;
		}

		int count = 0;
		for (File afile : array) {
			if (afile.isFile()) {
				String somename = afile.getName();
				if (somename.startsWith(baldFilename)) {
					LOGGER.info("Removing " + afile.getAbsolutePath());
					afile.delete();
					count++;
				}
			}
		}

		LOGGER.info("Totally remove count:" + count);
		return count;
	}
}
