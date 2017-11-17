package com.syniverse.loader;

import java.io.File;
import java.sql.Connection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.common.CommUtil;
import com.syniverse.common.MemoryUsage;
import com.syniverse.db.DBManipulate;
import com.syniverse.db.FeedLogTableBean;
import com.syniverse.info.OriginalFileInfo;
import com.syniverse.io.IOUtil;

public class MoveFileAndBuildOrgnf {
	public static final String Suffix_movedone = "_movedone";
	public static final String Suffix_unzipdone = "_unzipdone";
	public static final String Suffix_pickdone = "_pickdone";
	public static final String Suffix_ignore_tmp = ".tmp";
	public static final String Suffix_ignore_temp = ".temp";

	private static final Log LOGGER = LogFactory
			.getLog(MoveFileAndBuildOrgnf.class);

	/**
	 * What this method does
	 * 
	 * 1. Move file's from original user uploaded path to working directory
	 * <p>
	 * 2. unzip the file if necessary
	 * 
	 * 3. Construct OriginalFileInfo. NOT set
	 * OriginalFileInfo.originalFileTotalRowCount in this method
	 * 
	 * originalFileTotalRowCount will returned by Splitting file or from
	 * FEED_SPLIT_FILE.SPLIT_TOTAL_ROW_COUNT
	 * 
	 * @param conn
	 * @param flInfoFromDB
	 * @return
	 * @throws RuntimeException
	 */
	public OriginalFileInfo createOrgnfInfoByFeedLog(Connection conn,
			FeedLogTableBean flInfoFromDB) throws RuntimeException {
		long begin = System.currentTimeMillis();
		String orgnFilename = flInfoFromDB.getFilenameInDB();
		LOGGER.info("Begin to create OriginalFileInfo (including move&unzip), orgnFilename:"
				+ orgnFilename);
		String feedSource = flInfoFromDB.getFeedSource();
		String billingID = flInfoFromDB.getBillingID();
		String listID = flInfoFromDB.getListID();
		int versionNO = flInfoFromDB.getVersionNO();

		String workingDir = SubscriberLoader.getWorkingDir();
		String orgnDir = SubscriberLoader.getOriginalFileDir(feedSource);

		String orgnFullPath = orgnDir + "/" + orgnFilename;
		// original file should be moved to working dir.
		// after it's moved to working dir
		// it's full path will be <code>workingdirFullPath</code>
		String workingdirFullPath = workingDir + "/" + orgnFilename;

		String moveDoneFullPath = workingDir + "/" + orgnFilename
				+ Suffix_movedone;
		String unzipDoneFullPath = workingDir + "/" + orgnFilename
				+ Suffix_unzipdone;

		moveIfNecessary(orgnFullPath, workingdirFullPath, moveDoneFullPath);

		String[] arrayZipData = deduceZipAndDatafilename(orgnFilename,
				workingdirFullPath);
		String zipfilename = arrayZipData[0];
		String datafilename = arrayZipData[1];

		unzipIfNecessary(arrayZipData, workingdirFullPath, datafilename,
				unzipDoneFullPath);

		// OK, we always can get dataFile now
		String datafileFullPath = SubscriberLoader.getWorkingDir() + "/"
				+ datafilename;
		String commaSplitFileheader = getCommaSplitFileheader(billingID,
				listID, versionNO, conn, feedSource, datafileFullPath);

		// Delimiter has nothing to do with listID/versionNo
		// At any time, we always only have one delimiter which is stored in
		// attribute_config.DELIMITER
		String delimiter = decideDelimiter(conn, feedSource, billingID);

		OriginalFileInfo orgnfInfo = new OriginalFileInfo();
		orgnfInfo.setLogID(flInfoFromDB.getLogID());
		orgnfInfo.setBillingID(flInfoFromDB.getBillingID());
		orgnfInfo.setFeedSource(flInfoFromDB.getFeedSource());
		orgnfInfo.setDatafilename(datafilename);
		orgnfInfo.setDatafileFullPath(datafileFullPath);
		orgnfInfo.setDatafileParentFolder(new File(datafileFullPath)
				.getParentFile().getAbsolutePath());
		orgnfInfo.setZipfilename(zipfilename);

		// Use listID, verionNO which are fetch from FEED_LOG.LIST_ID,
		// FEED_LOG.VERSION_NUM
		orgnfInfo.setListID(flInfoFromDB.getListID());
		orgnfInfo.setVersionNO(flInfoFromDB.getVersionNO());
		// file header and delimiter
		orgnfInfo.setCommaSplitFileheader(commaSplitFileheader);
		orgnfInfo.setDelimiter(delimiter);
		orgnfInfo.setOriginalFileTotalRowCount(flInfoFromDB.getTotalRow());
		long end = System.currentTimeMillis();
		LOGGER.info(CommUtil
				.format("Create OriginalFileInfo {0}, by FeedLogTableBean {1}, cost:{2}",
						orgnfInfo, flInfoFromDB, MemoryUsage.human(end - begin)));

		return orgnfInfo;
	}

	/**
	 * Note: <b> Delimiter has nothing to do with
	 * attribute_config.LIST_ID/attribute_config.VERSION_NUM<b>
	 * <p>
	 * At any time, we always only have one delimiter which is stored in
	 * attribute_config.DELIMITER
	 * 
	 * 
	 * @param conn
	 * @param feedSource
	 * @param billingID
	 * @return
	 */
	private String decideDelimiter(Connection conn, String feedSource,
			String billingID) {
		String delimiter = null;
		// external
		if (feedSource.equals(OriginalFileInfo.FEED_SOURCE_EXTERNAL)) {
			delimiter = DBManipulate.fetchDelimiter(conn, billingID,
					DBManipulate.BATCH_TYPE);
		}
		// internal
		else if (feedSource.equals(OriginalFileInfo.FEED_SOURCE_INTERNAL)) {
			delimiter = SubscriberLoader.getInternalDelimiter(feedSource,
					billingID);
		}
		return delimiter;
	}

	/**
	 * 1. Move original file to working directory
	 * <p>
	 * 2. CREATE moveDone file (under workingdirFullPath)
	 * <p>
	 * 3. DELETE pickDone file (under orgnFullPath)
	 * 
	 * 
	 * @param orgnFullPath
	 * @param workingdirFullPath
	 * @param moveDoneFullPath
	 * @return
	 */
	private boolean moveIfNecessary(String orgnFullPath,
			String workingdirFullPath, String moveDoneFullPath) {
		LOGGER.info("moveDoneFullPath=" + moveDoneFullPath);
		// Still not move to working dir
		if (new File(moveDoneFullPath).exists() == false) {
			LOGGER.info("_movedone does not exist. moving....");
			boolean mov = _moveOrgn2Workingdir(orgnFullPath, workingdirFullPath);
			return mov;
		}
		// already moved here
		else {
			LOGGER.info("_movedone has already exited. unzip if necessary");
			return true;
		}
	}

	/**
	 * 1. Move original file to working directory
	 * <p>
	 * 2. Create moveDone file (under workingdirFullPath)
	 * <p>
	 * 3. Delete pickDone file (under orgnFullPath)
	 * 
	 * 
	 * @param orgnFullPath
	 * @param workingdirFullPath
	 * @return
	 */
	private boolean _moveOrgn2Workingdir(String orgnFullPath,
			String workingdirFullPath) {
		// 1. Move original file to working directory
		boolean rnm = IOUtil.renameTo(orgnFullPath, workingdirFullPath);
		// 2. Create moveDone file (under workingdirFullPath)
		boolean createtmp = IOUtil.createNewFile(workingdirFullPath
				+ Suffix_movedone);
		// 3. Delete pickDone file (under orgnFullPath)
		new File(orgnFullPath + Suffix_pickdone).delete();
		return rnm && createtmp;
	}

	/**
	 * 1. unzip the file
	 * <p>
	 * 2. create zipDone file
	 * 
	 * If necessary, unzip file
	 * 
	 * 1. unzip the file
	 * <p>
	 * 2. CREATE zipDone file
	 * 
	 * 
	 * @param unzipDoneFullPath
	 * @param zipfullpath
	 * 
	 * @return
	 */
	private boolean unzipIfNecessary(String[] arrayZipData,
			String workingdirFullPath, String extractWhat,
			String unzipDoneFullPath) {
		LOGGER.info("unzipDoneFullPath=" + unzipDoneFullPath);
		String zipfilename = arrayZipData[0];
		// NOT zipped file
		if (CommUtil.isEmpty(zipfilename) == true) {
			// NOT zipped file, we assume it's successful
			return true;
		}
		// zipped file
		else {
			// has NOT been unzipped
			if (new File(unzipDoneFullPath).exists() == false) {
				LOGGER.info("zip file and _unzipdone does not exist. unzip....");
				// 1. unzip the file
				boolean unzip = IOUtil.unzip(workingdirFullPath, extractWhat);
				// 2. create zipDone file
				boolean createtmp = IOUtil.createNewFile(unzipDoneFullPath);
				return unzip && createtmp;
			}
			// Has been unzipped
			else {
				return true;
			}
		}
	}

	private String[] deduceZipAndDatafilename(String orgnFilename,
			String workingdirFullPath) {
		String[] arrayZipData = new String[2];
		// zipfilename
		arrayZipData[0] = zipfilenameORnull(orgnFilename);

		// NOT zipped file
		if (CommUtil.isEmpty(arrayZipData[0]) == true) {
			// NOT zipped file, so, orgnFilename is datafilename
			arrayZipData[1] = orgnFilename;
		}
		// Yes, it's zipped file
		else {
			// let's find out what's the datafilename
			arrayZipData[1] = IOUtil.extractWhat(workingdirFullPath);
		}
		return arrayZipData;
	}

	/**
	 * if <code>orgnFilename</code> is a zip file(ends with .zip, .gz, .gzip),
	 * return the name directly, otherwise, return null(if it's 9999.csv, we
	 * still return null);
	 * 
	 * @param orgnFilename
	 * @return
	 */
	private String zipfilenameORnull(String orgnFilename) {
		boolean isZip = IOUtil.isZip_by_LastestSuffix(orgnFilename);
		if (isZip == true) {
			return orgnFilename;
		} else {
			// Has suffix or not, but it is NOT zip file. NOT zip file
			return null;
		}
	}

	/**
	 * 1. NOT including Action
	 * <p>
	 * 2. column name (NOT display name)
	 * <p>
	 * 3. this is related with <code>listID</code>/<code>versionNO</code>. But
	 * use which <code>listID</code>/<code>versionNO</code>?
	 * 
	 * NOT the newest attribute_config.ATTR_LIST, but the one at the time
	 * Loader's looping service detected this file
	 * 
	 * 
	 * @param billingID
	 * @param listID
	 * @param versionNO
	 * @param conn
	 * @param feedSource
	 * @param datafileFullPath
	 * @return
	 */
	private String getCommaSplitFileheader(String billingID, String listID,
			int versionNO, Connection conn, String feedSource,
			String datafileFullPath) {
		// external
		if (feedSource.equals(OriginalFileInfo.FEED_SOURCE_EXTERNAL)) {
			String commaSplitAttrInDB = DBManipulate
					.fetchCommaSplitAttrInDBWithoutAction(conn, listID,
							versionNO);
			return commaSplitAttrInDB;
		}
		// internal
		else if (feedSource.equals(OriginalFileInfo.FEED_SOURCE_INTERNAL)) {
			if (new File(datafileFullPath).exists() == false) {
				return null;
			}
			String uppercaserFileHeader = IOUtil.readUppercaseFirstLine(
					datafileFullPath, null);
			String delimiter = SubscriberLoader.getInternalDelimiter(
					feedSource, billingID);
			List<String> listFileheaderWithAction = CommUtil.split(
					uppercaserFileHeader, delimiter);
			// We're sure that the first column is always "Action", so just
			// directly remove it
			listFileheaderWithAction.remove(0);
			String commaSplitFileheader = CommUtil.concat(
					listFileheaderWithAction, ",", "");
			return commaSplitFileheader;
		}
		return null;
	}
}
