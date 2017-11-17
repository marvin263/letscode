package com.syniverse.loopfolder;

import java.io.File;
import java.io.FilenameFilter;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.common.CommUtil;
import com.syniverse.common.MemoryUsage;
import com.syniverse.config.Config;
import com.syniverse.db.DBManipulate;
import com.syniverse.db.DBUtil;
import com.syniverse.db.FeedLogTableBean;
import com.syniverse.info.OriginalFileInfo;
import com.syniverse.io.IOUtil;
import com.syniverse.loader.MoveFileAndBuildOrgnf;
import com.syniverse.loader.SetNameThreadFactory;
import com.syniverse.loader.SubscriberLoader;

public class LoopFolderService {
	private static final Log LOGGER = LogFactory
			.getLog(LoopFolderService.class);
	public static final long initialDelay = 10;
	public static final long Loop_Internal_Second = Config
			.getInt(Config.Key_Poll_Interval_Second);
	private static final int DB_COLUMN_NAME_LENGTH = 60;
	private static final int DB_COLUMN_UPLOADEDBY_LENGTH = 10;
	public static final String SYNIVERSE_ADMIN_BILLING_ID = "99999";
	private Connection conn = null;

	// private final AtomicBoolean loaderRequiredStop;

	public LoopFolderService() {
		// this.loaderRequiredStop = loaderRequiredStop;
	}

	public ExecutorService startLoopFolderService() {
		ScheduledExecutorService loopSVC = Executors
				.newSingleThreadScheduledExecutor(new SetNameThreadFactory(
						SubscriberLoader.ThreadPool_Prefix_LoopFile));
		loopSVC.scheduleWithFixedDelay(new LoopRunnable(), initialDelay,
				Loop_Internal_Second, TimeUnit.SECONDS);
		return loopSVC;
	}

	class LoopRunnable implements Runnable {
		@Override
		public void run() {
			try {
				storeAllAvail();
			} catch (Throwable e) {
				LOGGER.error("Should never happen. Generic exception", e);
			}
		}

		private void storeAllAvail() {
			List<AvailFile> listAll = filterOut();
			// get new connection if required
			if (DBUtil.isValid(conn) == false) {
				DBUtil.closeConnAndStmt(conn, null);
				conn = DBUtil.getNewC();
				// Cannot getValidateConnection()
				if (conn == null) {
					LOGGER.warn("Cannot getValidateConnection(), finish this loop round");
					return;
				}
			}
			for (AvailFile oneAvail : listAll) {
				try {
					storeEachAvail(conn, oneAvail);
				} catch (Throwable e) {
					LOGGER.error(
							"Inserting file error, ignore and continue, file:"
									+ oneAvail.fullPath, e);
				}
			}
		}

		/**
		 * 
		 * Filter files whose length is changing
		 * 
		 * @return
		 */
		private List<AvailFile> filterOut() {
			LOGGER.info("Looping scan&pick files....");
			long begin = System.currentTimeMillis();
			List<AvailFile> listExternal = scanToPickAllFiles(
					SubscriberLoader.getExternalFolder(),
					OriginalFileInfo.FEED_SOURCE_EXTERNAL);
			List<AvailFile> listInternal = scanToPickAllFiles(
					SubscriberLoader.getInternalFolder(),
					OriginalFileInfo.FEED_SOURCE_INTERNAL);

			List<AvailFile> listAll = new ArrayList<AvailFile>();
			listAll.addAll(listExternal);
			listAll.addAll(listInternal);
			int firstScanCount = listAll.size();

			sleep2ObserveGrowth();

			LOGGER.info("Sleep done, check file length again");
			for (int i = 0; i < listAll.size(); i++) {
				AvailFile anAvailf = listAll.get(i);
				File checkAgain = new File(anAvailf.fullPath);
				if (!checkAgain.exists() || !checkAgain.isFile()
						|| checkAgain.length() != anAvailf.length) {
					listAll.remove(anAvailf);
				}
			}
			int secondScanCount = listAll.size();

			Collections.sort(listAll);
			long end = System.currentTimeMillis();
			LOGGER.info(CommUtil
					.format("Looping scan&pick files, cost: [{0}], firstScanCount=[{1}], secondScanCount=[{2}]",
							MemoryUsage.human(end - begin), firstScanCount,
							secondScanCount));
			return listAll;
		}

		/**
		 * Pick all the files based on PickFileFilter even this file is growing
		 * 
		 * @param scanDirectoryPath
		 * @param stayedFolderDesignatedFeedSource
		 * @return
		 */
		private List<AvailFile> scanToPickAllFiles(String scanDirectoryPath,
				String stayedFolderDesignatedFeedSource) {
			// ONLY SCAN, DO NOT MOVE FILES
			List<AvailFile> listAvailFiles = new ArrayList<AvailFile>();
			File[] arrayFiles = new File(scanDirectoryPath)
					.listFiles(new PickFileFilter());
			if (arrayFiles == null || arrayFiles.length == 0) {
				return listAvailFiles;
			}

			for (File afile : arrayFiles) {
				listAvailFiles.add(new AvailFile(afile.getPath(), afile
						.lastModified(), afile.length(),
						stayedFolderDesignatedFeedSource));
			}
			return listAvailFiles;
		}

		private void sleep2ObserveGrowth() {
			try {
				Thread.sleep(10 * 1000);
			} catch (InterruptedException e) {
				LOGGER.error("sleep2ObserveGrowth is interrupted", e);
			}
		}

		private boolean storeEachAvail(Connection conn, AvailFile oneAvail) {
			String fullPath = oneAvail.fullPath;
			File f = new File(fullPath);
			String filename = f.getName();
			String[] arrayBillingFail = fill_BillingID_FailRsn(fullPath,
					filename);
			String billingID = arrayBillingFail[0];
			String failReason = arrayBillingFail[1];

			String listID = DBManipulate.fetchStrListID(conn, billingID,
					DBManipulate.BATCH_TYPE);
			int versionNO = DBManipulate.fetchVersionNum(conn, billingID,
					DBManipulate.BATCH_TYPE);

			String[] arrayTypeUploadedby = deduce_Type_UploadedBy(fullPath,
					filename);
			String type = arrayTypeUploadedby[0];
			String uploadedby = arrayTypeUploadedby[1];

			long current = System.currentTimeMillis();
			long receivedTime = current;
			long updateTime = current;
			long startTime = -1;
			long endTime = -1;
			int totalCount = -1;
			int successCount = 0;
			int failCount = 0;

			FeedLogTableBean flInfo = new FeedLogTableBean();
			flInfo.setBillingID(billingID);
			flInfo.setListID(listID);
			flInfo.setVersionNO(versionNO);
			flInfo.setFeedSource(oneAvail.stayedFolderDesignatedFeedSource);

			flInfo.setType(type);
			flInfo.setFilenameInDB(filename);
			flInfo.setReceiveTime(receivedTime);
			flInfo.setStartTime(startTime);
			flInfo.setEndTime(endTime);
			flInfo.setUpdatedTime(updateTime);
			flInfo.setTotalRow(totalCount);
			flInfo.setSuccessRow(successCount);
			flInfo.setFailRow(failCount);
			flInfo.setUploadedby(uploadedby);
			// looks like good, but we have to check whether file is under
			// correct directory
			if (CommUtil.isEmpty(failReason)) {
				String feedSource_by_Filename = deduceFeedSourceByFilename(filename);
				// under wrong directory
				if (oneAvail.stayedFolderDesignatedFeedSource
						.equalsIgnoreCase(feedSource_by_Filename) == false) {
					LOGGER.error("Bad, filename suffix is not correct: "
							+ fullPath);
					if (oneAvail.stayedFolderDesignatedFeedSource
							.equalsIgnoreCase(OriginalFileInfo.FEED_SOURCE_INTERNAL)) {
						failReason = "Internal Files should have suffix _0 (e.g. xxxx_0, xxxx_0.csv, xxxx_0.zip)";
					} else {
						failReason = "External Files should NOT have suffix _0";
					}
				}
			}
			// We've found error, do not need to check directory
			else {

			}

			// make sure length of filename <= 60, begin
			if (filename.length() > LoopFolderService.DB_COLUMN_NAME_LENGTH) {
				flInfo.setFilenameInDB(filename.substring(0,
						LoopFolderService.DB_COLUMN_NAME_LENGTH));
			}
			// make sure length of filename <= 60, end

			// good
			if (CommUtil.isEmpty(failReason)) {
				return handleNiceFile(flInfo, fullPath);
			}
			// bad, for whatever reason, this file should be failed
			else {
				flInfo.setStartTime(current);
				flInfo.setEndTime(current);
				flInfo.setProcessStatus(FeedLogTableBean.Rejected);
				flInfo.setFailReason(failReason);

				boolean createpickdoneOK_or_backupThenDelete = false;
				boolean insertOK = DBManipulate.insertFeedLog(conn, flInfo);
				if (insertOK) {
					createpickdoneOK_or_backupThenDelete = backupThenDelete(oneAvail);
				}
				return insertOK && createpickdoneOK_or_backupThenDelete;
			}
		}

		public boolean handleNiceFile(FeedLogTableBean flInfo, String fullPath) {
			flInfo.setProcessStatus(FeedLogTableBean.Queued);
			boolean insertOK = DBManipulate.insertFeedLog(conn, flInfo);
			if (insertOK) {
				// This is crucial, Create a file to indicate this has been
				// dealt with
				boolean createpickdoneOK_or_backupThenDelete = IOUtil
						.createNewFile(fullPath
								+ MoveFileAndBuildOrgnf.Suffix_pickdone);
				return insertOK && createpickdoneOK_or_backupThenDelete;
			}
			return false;
		}

		/**
		 * If from external folder, file name should NOT ends with _0
		 * <p>
		 * If from internal folder, file name should ends with _0
		 * <p>
		 * But, if file naming convention DOES NOT accord to the above, we
		 * directly fail this file.
		 * 
		 * After fail it, we need to delete it and back it up
		 * 
		 * @param oneAvail
		 */
		private boolean backupThenDelete(AvailFile oneAvail) {
			String backupDir = SubscriberLoader
					.getBackupDir(oneAvail.stayedFolderDesignatedFeedSource);
			String fullpath = oneAvail.fullPath;
			String filename = new File(fullpath).getName();
			boolean bkupOK = false;
			// zip file
			if (IOUtil.isZip_by_LastestSuffix(filename) == true) {
				bkupOK = IOUtil.renameTo(fullpath, backupDir + "/" + filename);
			}
			// not zip file
			else {
				// zip the original file directly to backupdir location
				bkupOK = IOUtil.zip(fullpath, backupDir + "/" + filename
						+ ".zip");
				// do not need to move the zip file again
				// IOUtil.renameTo(fullpath, backupDir + "/" + filename);
			}
			boolean deleteOK = new File(fullpath).delete();
			LOGGER.info(CommUtil.format(
					"backupThenDelete result. bkupOK={0}, deleteOK={1}",
					bkupOK, deleteOK));
			return bkupOK && deleteOK;
		}

		private String deduceFeedSourceByFilename(String filename) {

			String feedSource = null;
			int firstDot = filename.indexOf(".");
			if (firstDot == -1) {
				if (filename.endsWith("_0")) {
					feedSource = OriginalFileInfo.FEED_SOURCE_INTERNAL;
				} else {
					feedSource = OriginalFileInfo.FEED_SOURCE_EXTERNAL;
				}
			} else {
				if (filename.substring(0, firstDot).endsWith("_0")) {
					feedSource = OriginalFileInfo.FEED_SOURCE_INTERNAL;
				} else {
					feedSource = OriginalFileInfo.FEED_SOURCE_EXTERNAL;
				}

			}
			return feedSource;
		}

		private String[] deduce_Type_UploadedBy(String fullPath, String filename) {
			String strStart = "{";
			String strEnd = "}";
			String[] arrayTypeUploadedby = new String[] { null, null };
			// filename's min length is 7, should here 6 is safe
			String filenameSubtractBillingID_ = filename.substring(6);
			int startPos = filenameSubtractBillingID_.indexOf(strStart);
			// This may be from GUI upload
			if (startPos != -1) {
				int endPos = filenameSubtractBillingID_.indexOf(strEnd);
				if (endPos == -1) {
					arrayTypeUploadedby[0] = DBManipulate.BATCH_TYPE;
					arrayTypeUploadedby[1] = null;
				} else {
					String uploadedby = filenameSubtractBillingID_.substring(
							startPos + 1, endPos);
					// bad. UPLOADED_BY should be <=10
					if (uploadedby.length() > DB_COLUMN_UPLOADEDBY_LENGTH) {
						arrayTypeUploadedby[0] = DBManipulate.BATCH_TYPE;
						arrayTypeUploadedby[1] = null;
					}
					// good
					else {
						arrayTypeUploadedby[0] = "GUI";
						arrayTypeUploadedby[1] = uploadedby;
					}
				}
			}
			// definitely NOT from GUI
			else {
				arrayTypeUploadedby[0] = DBManipulate.BATCH_TYPE;
				arrayTypeUploadedby[1] = null;
			}
			return arrayTypeUploadedby;
		}

		private String[] fill_BillingID_FailRsn(String fullPath, String filename) {
			String[] array = new String[2];
			String failRsn = null;
			// 1. Length of file name should be <=60
			if (filename.length() > DB_COLUMN_NAME_LENGTH) {
				failRsn = CommUtil
						.format("Length of file name({0}, length is {1}) should be less than/equal to {2}",
								filename, filename.length(),
								DB_COLUMN_NAME_LENGTH);
				array[0] = SYNIVERSE_ADMIN_BILLING_ID;
				array[1] = failRsn;
				LOGGER.error("File name is not acceptable: " + failRsn);
				return array;
			}
			int pos = filename.indexOf("_");
			// 2. File name should begin with billing ID
			if (pos == -1) {
				failRsn = CommUtil
						.format("File name({0}) should begin with billing ID (e.g. 12345_20130929135130.csv)",
								filename);
				LOGGER.error("File name is not acceptable: " + failRsn);
				array[0] = SYNIVERSE_ADMIN_BILLING_ID;
				array[1] = failRsn;
				return array;
			}
			// 3. File name should exist in OPERATOR_CONFIG
			else {
				String billingID = filename.substring(0, pos);
				// bad
				if (DBManipulate.isBillingIDExistInOperator(conn, billingID) == false) {
					failRsn = CommUtil
							.format("{0} - Wrong Billing ID in file name({1}). File can't be processed",
									billingID, filename);
					LOGGER.error("File name is not acceptable: " + failRsn);
					array[0] = SYNIVERSE_ADMIN_BILLING_ID;
					array[1] = failRsn;
					return array;
				}
				// good, billingID exists
				else {
					array[0] = billingID;
					array[1] = null;
				}
			}
			return array;
		}

		private final class PickFileFilter implements FilenameFilter {
			public boolean accept(File scanFolder, String filename) {
				// Directory. We do not need this
				if (new File(scanFolder, filename).isDirectory() == true) {
					return false;
				}
				// WON'T pick Suffix_pickdone files
				if (filename.endsWith(MoveFileAndBuildOrgnf.Suffix_pickdone)) {
					return false;
				}
				String lowcaseFilename = filename.toLowerCase();
				// WON'T pick *.tmp or *.temp
				if (lowcaseFilename.endsWith(MoveFileAndBuildOrgnf.Suffix_ignore_tmp)
						|| lowcaseFilename
								.endsWith(MoveFileAndBuildOrgnf.Suffix_ignore_temp)) {
					return false;
				}
				// if "fileName+ MoveFileAndBuildOrgnf.Suffix_pickdone" exist.
				// Ignore fileName
				if (new File(scanFolder, filename
						+ MoveFileAndBuildOrgnf.Suffix_pickdone).exists() == true) {
					return false;
				}
				return true;
			}
		}

		private class AvailFile implements Comparable<AvailFile> {
			public final String fullPath;// Hashcode, eaqual only depends on
											// this
											// one
			public final long lastModified;// Sort purpose. Because for each
											// file
											// require:
											// "First Come, First service"
			public final long length;// Decide whether this file has ever been
										// change
			public final String stayedFolderDesignatedFeedSource;

			public AvailFile(final String fullPath, final long lastModified,
					final long length,
					final String stayedFolderDesignatedFeedSource) {
				this.fullPath = fullPath;
				this.lastModified = lastModified;
				this.length = length;
				this.stayedFolderDesignatedFeedSource = stayedFolderDesignatedFeedSource;
			}

			@Override
			public int hashCode() {
				final int prime = 31;
				int result = 1;
				result = prime * result
						+ ((fullPath == null) ? 0 : fullPath.hashCode());
				return result;
			}

			@Override
			public boolean equals(Object obj) {
				if (this == obj)
					return true;
				if (obj == null)
					return false;
				if (getClass() != obj.getClass())
					return false;
				AvailFile other = (AvailFile) obj;
				if (fullPath == null) {
					if (other.fullPath != null)
						return false;
				} else if (!fullPath.equals(other.fullPath))
					return false;
				return true;
			}

			@Override
			public int compareTo(AvailFile o) {
				return (int) (this.lastModified - o.lastModified);
			}
		}
	}

	public static void main(String[] args) {
	}
}
