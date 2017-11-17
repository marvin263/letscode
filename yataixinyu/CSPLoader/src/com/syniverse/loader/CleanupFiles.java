package com.syniverse.loader;

import java.io.File;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.common.CommUtil;
import com.syniverse.common.MemoryUsage;
import com.syniverse.info.OriginalFileInfo;
import com.syniverse.info.SplitFileInfo;
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
	public void dealWithFilesAfterLoader(OriginalFileInfo orgnfInfo,
			List<SplitFileInfo> listSplitFile) {
		long begin = System.currentTimeMillis();
		LOGGER.info("Begin dealWithFilesAfterLoader " + orgnfInfo);
		try {
			mergeResultFileToResultDir(orgnfInfo, listSplitFile);
			backupDatafile(orgnfInfo);
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

	private void mergeResultFileToResultDir(OriginalFileInfo orgnfInfo,
			List<SplitFileInfo> listSplitFile) {
		if (CommUtil.isEmpty(orgnfInfo.getDatafilename()) == true
				|| listSplitFile == null || listSplitFile.size() == 0) {
			return;
		}
		long begin = System.currentTimeMillis();
		LOGGER.info("Begin mergeResultFileToResultDir " + orgnfInfo);
		String[] errorSrcFullPath = new String[listSplitFile.size()];
		String[] infoSrcFullPath = new String[listSplitFile.size()];

		for (int i = 0; i < listSplitFile.size(); i++) {
			SplitFileInfo spf = listSplitFile.get(i);
			errorSrcFullPath[i] = spf.getSplitFullPath()
					+ SubscriberLoader.Suffix_Result_File_Error;
			infoSrcFullPath[i] = spf.getSplitFullPath()
					+ SubscriberLoader.Suffix_Result_File_Info;

		}
		String errorinfofilename = deduceErrorInfoFilename(orgnfInfo);

		String errRstFullPath = SubscriberLoader.getErrorFileDir(orgnfInfo
				.getFeedSource())
				+ "/"
				+ errorinfofilename
				+ SubscriberLoader.Suffix_Result_File_Error;
		IOUtil.mergeTo(errRstFullPath, errorSrcFullPath);
		String infoRstFullPath = SubscriberLoader.getInfoFileDir(orgnfInfo
				.getFeedSource())
				+ "/"
				+ errorinfofilename
				+ SubscriberLoader.Suffix_Result_File_Info;
		IOUtil.mergeTo(infoRstFullPath, infoSrcFullPath);

		long end = System.currentTimeMillis();
		LOGGER.info(CommUtil.format(
				"End mergeResultFileToResultDir, cost: {0}, file{1} ",
				MemoryUsage.human(end - begin), orgnfInfo));
	}

	/**
	 * 99999_20130925141410-->99999_20130925141410
	 * 
	 * 99999_20130925141410.zip-->99999_20130925141410
	 * 
	 * 99999_20130925141410.csv-->99999_20130925141410
	 * 
	 * 99999_20130925141410.csv.zip-->99999_20130925141410
	 * 
	 * @param orgnfInfo
	 * @return
	 */
	private String deduceErrorInfoFilename(OriginalFileInfo orgnfInfo) {
		String zipfilename = orgnfInfo.getZipfilename();
		String datafilename = orgnfInfo.getDatafilename();
		// NOT zipped file
		if (CommUtil.isEmpty(zipfilename) == true) {
			// NOT zipped file
			return IOUtil.eliminate_from_firstDot(datafilename);
		}
		// zipped file
		else {
			// if it DOES be zipped file, we use the zip name, NOT the
			// "extractedWhat", otherwise, GUI cannot fetch error/info files
			return IOUtil.eliminate_from_firstDot(zipfilename);
		}
	}

	private void backupDatafile(OriginalFileInfo orgnfInfo) {
		long begin = System.currentTimeMillis();
		LOGGER.info("Begin backupDatafile " + orgnfInfo);
		String backupDir = SubscriberLoader.getBackupDir(orgnfInfo
				.getFeedSource());
		// original file is NOT zipped. We should zip it ourselves when backup
		if (CommUtil.isEmpty(orgnfInfo.getZipfilename()) == true) {
			String baldDatafilename = IOUtil.eliminate_from_firstDot(orgnfInfo.getDatafilename());
			String zipfilename = baldDatafilename + ".zip";
			String zipFullPath = orgnfInfo.getDatafileParentFolder() + "/"
					+ zipfilename;

			IOUtil.zip(orgnfInfo.getDatafileFullPath(), zipFullPath);

			IOUtil.renameTo(zipFullPath, backupDir + "/" + zipfilename);
		}
		// original file is zipped. Just move it to backupdir
		else {
			String zipFullPath = orgnfInfo.getDatafileParentFolder() + "/"
					+ orgnfInfo.getZipfilename();
			IOUtil.renameTo(zipFullPath,
					backupDir + "/" + orgnfInfo.getZipfilename());
		}
		long end = System.currentTimeMillis();
		LOGGER.info(CommUtil.format("End backupDatafile, cost: {0}, file{1} ",
				MemoryUsage.human(end - begin), orgnfInfo));
	}

	private void cleanupWorkingDir(OriginalFileInfo orgnfInfo) {
		if (orgnfInfo != null) {
			if (CommUtil.isEmpty(orgnfInfo.getDatafilename()) == false) {
				deleteIfStartWithBaldname(orgnfInfo.getDatafileFullPath());
			}

			String zipfilename = orgnfInfo.getZipfilename();
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
