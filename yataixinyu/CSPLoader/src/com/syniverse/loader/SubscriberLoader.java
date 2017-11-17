package com.syniverse.loader;

import java.io.File;
import java.sql.Connection;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;

import com.syniverse.common.CommUtil;
import com.syniverse.common.MemoryUsage;
import com.syniverse.config.Config;
import com.syniverse.db.DBManipulate;
import com.syniverse.db.DBUtil;
import com.syniverse.db.FeedLogTableBean;
import com.syniverse.info.DBColumnsInfo;
import com.syniverse.info.EachRowInfo;
import com.syniverse.info.FutureVL;
import com.syniverse.info.LResultInfo;
import com.syniverse.info.OriginalFileInfo;
import com.syniverse.info.OverviewValidateInfo;
import com.syniverse.info.ResultVL;
import com.syniverse.info.SplitFileInfo;
import com.syniverse.info.VResultInfo;
import com.syniverse.io.IOUtil;
import com.syniverse.loopfolder.LoopFolderService;
import com.syniverse.shutdown.ShutDownService;

public class SubscriberLoader {
	private static final Log LOGGER = LogFactory.getLog(SubscriberLoader.class);
	static {
		configLogFirst();
	}
	public static final long Sleep_If_No_Files = 20 * 1000;
	public final static int V2L_CAPACITY = (int) (Config
			.getInt(Config.Key_Commit_Threshold) * 1.1);

	private static final String SCAN_FOLDER_external = Config
			.getString(Config.Key_Scan_Folder_External);
	private static final String SCAN_FOLDER_internal = Config
			.getString(Config.Key_Scan_Folder_Internal);
	private static final String BackupDir = Config
			.getString(Config.Key_Backup_Dir);
	private static final String LoadResultDir = Config
			.getString(Config.Key_Load_Result_Dir);

	public static final int Split_File_Count = Config
			.getInt(Config.Key_Split_File_Count);

	public static final String Suffix_Result_File_Error = ".errors";
	public static final String Suffix_Result_File_Info = ".info";

	public static final String ThreadPool_Prefix_Loader = "Loader";
	public static final String ThreadPool_Prefix_Validator = "Validator";
	public static final String ThreadPool_Prefix_LoopFile = "LoopFile";
	public static final String ThreadPool_Prefix_Shutdown = "Shutdown";

	private static String Working_Dir = null;

	private List<FutureVL> listFutureVL = new ArrayList<FutureVL>();
	private List<ResultVL> listResultVL = new ArrayList<ResultVL>();

	private ExecutorService validatorSvc;
	private ExecutorService loaderSvc;
	// ONLY for log purpose, start from 0
	private long round = 0;
	private Connection globalConn;

	private static void configLogFirst() {
		String jarFolderSelf = IOUtil
				.getJarStayFolder_nologinfo(SubscriberLoader.class);
		String jarFolderParent = new File(jarFolderSelf).getParentFile()
				.getAbsolutePath();

		String logDirFullPath = jarFolderParent + "/log";
		IOUtil.ensureFolderExist(logDirFullPath);

		String log4jFullPath = jarFolderParent + "/conf/log4j.properties";

		PropertyConfigurator.configure(log4jFullPath);

		LOGGER.info("log4j.properties file: " + log4jFullPath);
	}

	public SubscriberLoader() {
	}

	private void prepare() {
		startService();
	}

	private void startService() {
		ExecutorService loopFolderSvc = new LoopFolderService()
				.startLoopFolderService();
		validatorSvc = Executors.newFixedThreadPool(
				SubscriberLoader.Split_File_Count, new SetNameThreadFactory(
						ThreadPool_Prefix_Validator));
		loaderSvc = Executors.newFixedThreadPool(
				SubscriberLoader.Split_File_Count, new SetNameThreadFactory(
						ThreadPool_Prefix_Loader));
		ExecutorService shutdownSvc = new ShutDownService()
				.startShutDownService();

	}

	public void launchMain() {
		prepare();
		processLoadEndlessly();
	}

	private void processLoadEndlessly() {
		MoveFileAndBuildOrgnf bofin = new MoveFileAndBuildOrgnf();
		globalConn = DBUtil.getNewC();
		while (true) {
			LOGGER.info("Starting a new round");
			List<FeedLogTableBean> listCandidate = fetchFilesFromDB();
			int fileCount = listCandidate.size();
			LOGGER.info(CommUtil.format("round [{0}], fileCount [{1}]", round,
					fileCount));
			round++;
			if (fileCount == 0) {
				sleepWhenNoFiles();
				continue;
			}
			// OK, we've gotten files from DB. Let's construct OriginalFileInfo
			for (FeedLogTableBean oneFltb : listCandidate) {
				OriginalFileInfo orgnfInfo = null;
				try {
					long begin = System.currentTimeMillis();
					// Cannot get connection. let jump out for. Do not consume
					// <code>listCandidate</code> any more
					if (reassignIfInvalid() == false) {
						LOGGER.warn("Cannot getValidateConnection(), jump out, do not consume listCandidate");
						break;
					}
					DBManipulate.updateStartDate(oneFltb.getLogID(),
							globalConn, begin);
					orgnfInfo = bofin.createOrgnfInfoByFeedLog(globalConn,
							oneFltb);
					List<String> listFailReason = nofileOrZipIssueFailReason(
							orgnfInfo, oneFltb);
					if (!listFailReason.isEmpty()) {
						copeWithNodatafile(orgnfInfo, oneFltb, listFailReason);
						continue;
					}
					SplitFileInfo.setOrgnfInfo(orgnfInfo);

					loadEachFile(globalConn, orgnfInfo);

					long end = System.currentTimeMillis();
					LOGGER.info(CommUtil.format(
							"rowCount: {0}, totally cost: {1}, filename: {2}",
							orgnfInfo.getOriginalFileTotalRowCount(),
							MemoryUsage.human(end - begin),
							oneFltb.getFilenameInDB()));

				} catch (Throwable e) {
					LOGGER.error(
							"Should never happen. Generic exception in processLoadEndlessly(), file: "
									+ oneFltb, e);

					// Ensure we've updated FEED_LOG.FEED_STATUS, so that we'll
					// not process this file again and again
					List<String> listFailReason = new ArrayList<String>();
					String realRsn = CommUtil
							.format("Generic big exception in tackling original file. original=[{0}], detail=[{1}]",
									oneFltb.getFilenameInDB(), e.getMessage());
					listFailReason.add(SubscriberLoader.asmbFailRsn(
							OverviewValidateInfo.FAIL_REASON_common, realRsn));

					updateFeedStatus(globalConn, oneFltb.getLogID(),
							FeedLogTableBean.Exception, listFailReason);
				} finally {
					listResultVL.clear();
					listFutureVL.clear();
					SplitFileInfo.setOrgnfInfo(null);
				}
			}
		}
	}

	private void copeWithNodatafile(OriginalFileInfo orgnfInfo,
			FeedLogTableBean oneFltb, List<String> listFailReason) {
		LOGGER.warn(CommUtil
				.format("copeWithNodatafile(). original=[{0}], datafile should be=[{1}]. ingore and continue...",
						oneFltb.getFilenameInDB(),
						orgnfInfo.getDatafileFullPath()));
		clearupAndUpdateStatus(globalConn, orgnfInfo,
				FeedLogTableBean.Exception, null, listFailReason);
	}

	private List<String> nofileOrZipIssueFailReason(OriginalFileInfo orgnfInfo,
			FeedLogTableBean oneFltb) {
		List<String> listFailReason = new ArrayList<String>(1);
		String failRsn = null;
		File orgnFile = new File(orgnfInfo.getDatafileParentFolder() + "/"
				+ oneFltb.getFilenameInDB());
		if (!orgnFile.exists() || !orgnFile.isFile()) {
			failRsn = "File uploaded is missing. Please contact Syniverse Administrator";
			listFailReason.add(failRsn);
			return listFailReason;
		}

		// zip file
		if (IOUtil.isZip_by_LastestSuffix(oneFltb.getFilenameInDB()) == true) {
			String zipFullPath = orgnfInfo.getDatafileParentFolder() + "/"
					+ orgnfInfo.getZipfilename();
			int containedCount = IOUtil.datafileCount(zipFullPath);

			if (containedCount == -1) {
				failRsn = "Cannot unzip file, it might be a corrupted zip file";
				listFailReason.add(failRsn);
				return listFailReason;
			}
			if (containedCount == 0 || containedCount > 1) {
				failRsn = CommUtil
						.format("Only one file is allowed inside zip file (now {0} contains {1} data files)",
								oneFltb.getFilenameInDB(), containedCount);
				listFailReason.add(failRsn);
				return listFailReason;
			}
		}
		// NOT zip
		else {
		}

		File datafile = new File(orgnfInfo.getDatafileFullPath());
		if (CommUtil.isEmpty(orgnfInfo.getDatafilename()) == true
				|| !datafile.exists() || !datafile.isFile()) {
			failRsn = "Data file is missing. Please contact Syniverse Administrator";
			listFailReason.add(failRsn);
			return listFailReason;
		}
		return listFailReason;
	}

	/**
	 * true: get good connection; false: cannot get connection
	 * 
	 * @return
	 */
	private boolean reassignIfInvalid() {
		// Just in case the connection is invalidate
		if (DBUtil.isValid(globalConn) == false) {
			DBUtil.closeConnAndStmt(globalConn, null);
			LOGGER.info("reassignIfInvalid(), reassigning invalid Connection");
			globalConn = DBUtil.getNewC();
			return (globalConn != null);
		} else {
			// good, Connection is valid. do nothing
			return true;
		}
	}

	private void sleepWhenNoFiles() {
		LOGGER.info("No file found. So, sleep milliseconds="
				+ Sleep_If_No_Files);
		try {
			Thread.sleep(Sleep_If_No_Files);
		} catch (InterruptedException e) {
			LOGGER.info("sleepWhenNoFiles interrupted. ingore sliently. "
					+ Sleep_If_No_Files);
		}
	}

	/**
	 * Always Create a connection to fetch data, after done close this
	 * connection
	 * 
	 * @return
	 */
	private List<FeedLogTableBean> fetchFilesFromDB() {
		reassignIfInvalid();
		List<FeedLogTableBean> listCandidate = null;
		try {
			listCandidate = DBManipulate.selectCandidateFiles(globalConn);
		} catch (Exception e) {
			LOGGER.error("Error when fetchFilesFromDB", e);
		}
		LOGGER.info("fetchFilesFromDB end. file count=" + listCandidate.size());
		return listCandidate;
	}

	/**
	 * true:-->we can continue
	 * 
	 * false-->reject this file
	 * 
	 * 
	 * @param conn
	 * @param orgnfInfo
	 * @return
	 */
	private OverviewValidateInfo overviewValidateB4Split(Connection conn,
			OriginalFileInfo orgnfInfo) {
		// OverViewValidate first before split
		OverViewValidate ov = new OverViewValidate(conn, orgnfInfo);
		OverviewValidateInfo ovvInfo = ov.validate();
		LOGGER.info("Overview validating: " + orgnfInfo);
		LOGGER.info("Overview validating result: " + ovvInfo);
		return ovvInfo;
	}

	private void loadEachFile(Connection conn, OriginalFileInfo orgnfInfo) {
		long begin = System.currentTimeMillis();
		LOGGER.info("Begin loadEachFile() split&load file: " + orgnfInfo);

		String processStatus = FeedLogTableBean.Rejected;
		List<SplitFileInfo> listSplitFile = null;
		List<String> listFailReason = new ArrayList<String>();

		try {
			OverviewValidateInfo ovvInfo = overviewValidateB4Split(conn,
					orgnfInfo);
			// overview validation fails.
			if (ovvInfo.isPassed() == false) {
				listFailReason.add(ovvInfo.getFailReason());
				processStatus = FeedLogTableBean.Rejected;
				LOGGER.info(CommUtil
						.format("Cannot pass overview validation, failreason=[{0}], file=[{1}]",
								ovvInfo.getFailReason(), orgnfInfo));
				return;
			}

			// OverviewValidate OK, let's split it
			listSplitFile = new BuildSplitFileInfo().createSplitFileInfo(conn,
					orgnfInfo);
			// whatever reason, we cannot get split files. Reject this file
			if (listSplitFile == null || listSplitFile.size() == 0) {
				processStatus = FeedLogTableBean.Rejected;
				String realRsn = "Split file failed";

				listFailReason.add(SubscriberLoader.asmbFailRsn(
						OverviewValidateInfo.FAIL_REASON_common, realRsn));

				LOGGER.info("Whatever reason, we just cannot get split files. Reject it. "
						+ orgnfInfo);
				return;
			}

			// Since we're here, file has been split successfully
			setupColumnNameAndColumnType(conn, orgnfInfo);

			sumitValidatorLoaderTasks(listSplitFile);

			boolean bAllSuccess = waitForResult(listFailReason, orgnfInfo,
					listFutureVL, listResultVL);
			processStatus = bAllSuccess ? FeedLogTableBean.Completed
					: FeedLogTableBean.Exception;

		} catch (Throwable e) {
			processStatus = FeedLogTableBean.Exception;
			String orgnfilename = CommUtil.isEmpty(orgnfInfo.getZipfilename()) ? orgnfInfo
					.getDatafilename() : orgnfInfo.getZipfilename();

			String realRsn = CommUtil
					.format("Generic big exception in loading original file. original=[{0}], detail=[{1}]",
							orgnfilename, e.getMessage());

			listFailReason.add(SubscriberLoader.asmbFailRsn(
					OverviewValidateInfo.FAIL_REASON_common, realRsn));

			LOGGER.error(
					"Should never happen. loadEachFile() catches Generic exception. Update processStatus to FeedLogTableBean.Exception"
							+ orgnfInfo, e);
		} finally {
			clearupAndUpdateStatus(conn, orgnfInfo, processStatus,
					listSplitFile, listFailReason);
		}
		long end = System.currentTimeMillis();
		LOGGER.info(CommUtil.format(
				"End loadEachFile() split&load, cost: {0}, file: {1}",
				MemoryUsage.human(end - begin), orgnfInfo));
	}

	private void clearupAndUpdateStatus(Connection conn,
			OriginalFileInfo orgnfInfo, String processStatus,
			List<SplitFileInfo> listSplitFile, List<String> listFailReason) {
		// Encounter exception or not, we both need do two things
		// 1. cleanup files(
		// a). merge error/info file
		// b). backup files
		// c). delete all files in working dir generated during
		// execution
		// 2. update FEED_LOG status
		// 1. deal with files
		new CleanupFiles().dealWithFilesAfterLoader(orgnfInfo, listSplitFile);
		// OK, finally, we've finished loading a file(encounter
		// exception or
		// not)
		// let's update DB
		updateFeedStatus(conn, orgnfInfo.getLogID(), processStatus,
				listFailReason);
	}

	private void setupColumnNameAndColumnType(Connection conn,
			OriginalFileInfo orgnfInfo) {
		LOGGER.info("Begin setupColumnNameAndColumnType() " + orgnfInfo);
		// external
		if (orgnfInfo.getFeedSource().equals(
				OriginalFileInfo.FEED_SOURCE_EXTERNAL)) {
			String commaSplitAttrInDB = orgnfInfo.getCommaSplitFileheader();

			String commaSplitAllColumn = CommUtil.appendToTailIfNotExist(
					commaSplitAttrInDB, DBColumnsInfo.Impose_If_Not_Provide);

			String commaSplitPK = DBManipulate
					.fetchCommaSplitPrimaryKeyColumns(conn);

			DBColumnsInfo.initialize(conn, commaSplitAllColumn, commaSplitPK);
		}
		// internal
		else if (orgnfInfo.getFeedSource().equals(
				OriginalFileInfo.FEED_SOURCE_INTERNAL)) {
			String commaSplitFileheader = orgnfInfo.getCommaSplitFileheader();

			String commaSplitAllColumn = CommUtil.appendToTailIfNotExist(
					commaSplitFileheader, DBColumnsInfo.Impose_If_Not_Provide);

			String commaSplitPK = DBManipulate
					.fetchCommaSplitPrimaryKeyColumns(conn);

			DBColumnsInfo.initialize(conn, commaSplitAllColumn, commaSplitPK);
		}
	}

	private void updateFeedStatus(Connection conn, long logID,
			String processStatus, List<String> listFailReason) {
		// update FEED_LOG. do not need to update FEED_SPLIT_FILE. they've been
		// updated in LRunnable
		try {
			String orgnFailRsn = CommUtil.concat(listFailReason, "----", "");
			String tuncFailRsn = CommUtil
					.truncateToSize(orgnFailRsn, 512, true);

			long current = System.currentTimeMillis();
			String tableName = "FEED_LOG";

			String[] fields = new String[] { "FEED_STATUS", "FAIL_REASON",
					"FEED_END_TIME", "UPDATED_TIMESTAMP" };
			Object[] fieldValues = new Object[] { processStatus, tuncFailRsn,
					new Timestamp(current), new Timestamp(current) };

			String[] keys = new String[] { "LOG_ID" };
			Object[] keyValues = new Object[] { new Long(logID) };

			int[] sqltype = new int[] { Types.VARCHAR, Types.VARCHAR,
					Types.TIMESTAMP, Types.TIMESTAMP, Types.NUMERIC };

			DBManipulate.update(conn, tableName, fields, fieldValues, keys,
					keyValues, sqltype);
		} catch (Throwable e) {
			LOGGER.error("Error when updateFeedStatus", e);
		}
	}

	private boolean waitForResult(List<String> listFailReason,
			OriginalFileInfo orgnfInfo, List<FutureVL> listF,
			List<ResultVL> listR) {
		long begin = System.currentTimeMillis();
		LOGGER.info("Begin waitForResult. OriginalFileInfo:" + orgnfInfo);
		boolean bValidateOK = true;
		boolean bLoaderOK = true;
		for (int i = 0; i < listF.size(); i++) {
			FutureVL futureVL = listF.get(i);
			Future<VResultInfo> fv = futureVL.futureV;
			Future<LResultInfo> fl = futureVL.futureL;

			// blocks until result is returned, begin
			VResultInfo vr = listR.get(i).vr;
			try {
				vr = fv.get();
				LOGGER.info("Has attained VResultInfo:" + vr);
			} catch (Exception e) {
				LOGGER.error("Exception while waiting VResultInfo:" + vr, e);
			}
			addIfNotEmpty(vr, listFailReason);
			bValidateOK = vr.isFinalSuccess() && bValidateOK;

			LResultInfo lr = listR.get(i).lr;
			try {
				lr = fl.get();
				LOGGER.info("Has attained LResultInfo:" + lr);
			} catch (Exception e) {
				LOGGER.error("Exception while waiting LResultInfo:" + lr, e);
			}
			addIfNotEmpty(lr, listFailReason);
			bLoaderOK = lr.isFinalSuccess() && bLoaderOK;
			// blocks until result is returned, end
		}
		long end = System.currentTimeMillis();
		LOGGER.info("Ends waitForResult. OriginalFileInfo:" + orgnfInfo);
		LOGGER.info(CommUtil.format(
				"rowCount: {0}, AUD database cost: {1}, filename: {2}",
				orgnfInfo.getOriginalFileTotalRowCount(),
				MemoryUsage.human(end - begin), orgnfInfo.getDatafilename()));

		return bValidateOK && bLoaderOK;
	}

	private void addIfNotEmpty(VResultInfo vr, List<String> listFailReason) {
		if (CommUtil.isEmpty(vr.getFailReason()) == false) {
			listFailReason.add(vr.getFailReason());
		}
	}

	private void addIfNotEmpty(LResultInfo lr, List<String> listFailReason) {
		if (CommUtil.isEmpty(lr.getFailReason()) == false) {
			listFailReason.add(lr.getFailReason());
		}
	}

	private void sumitValidatorLoaderTasks(List<SplitFileInfo> listSplit) {
		LOGGER.info("Begin sumitValidatorLoaderTasks(), split file count:"
				+ listSplit.size());
		for (SplitFileInfo spfInfo : listSplit) {
			BlockingQueue<EachRowInfo> queueFromValidatorToLoader = new ArrayBlockingQueue<EachRowInfo>(
					V2L_CAPACITY);
			AtomicBoolean forceLoaderCommit = new AtomicBoolean(false);
			AtomicBoolean loaderRequiredStop = new AtomicBoolean(false);

			VResultInfo vr = new VResultInfo(spfInfo.getSplitfilename());
			VRunnable vrunnable = new VRunnable(queueFromValidatorToLoader,
					spfInfo, vr, forceLoaderCommit, loaderRequiredStop);

			Future<VResultInfo> futureV = validatorSvc.submit(vrunnable, vr);

			LResultInfo lr = new LResultInfo(spfInfo.getSplitfilename());
			LRunnable lrunnable = new LRunnable(queueFromValidatorToLoader,
					spfInfo, lr, forceLoaderCommit, loaderRequiredStop, futureV);

			Future<LResultInfo> futureL = loaderSvc.submit(lrunnable, lr);

			listFutureVL.add(new FutureVL(futureV, futureL));
			listResultVL.add(new ResultVL(vr, lr));
		}
	}

	public static String asmbFailRsn(String commRsn, String realRsn) {
		return commRsn + OverviewValidateInfo.DELIMITER_fail_reason + realRsn;
	}

	public static String getExternalFolder() {
		String folder = SubscriberLoader.SCAN_FOLDER_external;
		IOUtil.ensureFolderExist(folder);
		return folder;
	}

	public static String getInternalFolder() {
		String folder = SubscriberLoader.SCAN_FOLDER_internal;
		IOUtil.ensureFolderExist(folder);
		return folder;
	}

	public static String getWorkingDir() {
		if (!CommUtil.isEmpty(Working_Dir)) {
			return Working_Dir;
		}
		String jarFolderSelf = IOUtil.getJarStayFolder(SubscriberLoader.class);
		String jarFolderParent = new File(jarFolderSelf).getParentFile()
				.getAbsolutePath();
		String wd = jarFolderParent + "/" + "workingdir";
		IOUtil.ensureFolderExist(wd);
		Working_Dir = wd;
		LOGGER.info("Working directory is: " + Working_Dir);
		return Working_Dir;
	}

	public static String getBackupDir(String feedSource) {
		String dir = SubscriberLoader.BackupDir;
		// external
		if (feedSource.equals(OriginalFileInfo.FEED_SOURCE_EXTERNAL)) {
			dir = SubscriberLoader.BackupDir + "/" + "external";
		}
		// internal
		else if (feedSource.equals(OriginalFileInfo.FEED_SOURCE_INTERNAL)) {
			dir = SubscriberLoader.BackupDir + "/" + "internal";
		}
		IOUtil.ensureFolderExist(dir);
		return dir;
	}

	public static String getOriginalFileDir(String feedSource) {
		String dir = SCAN_FOLDER_external;
		if (OriginalFileInfo.FEED_SOURCE_EXTERNAL.equals(feedSource)) {
			dir = SCAN_FOLDER_external;
		} else if (OriginalFileInfo.FEED_SOURCE_INTERNAL.equals(feedSource)) {
			dir = SCAN_FOLDER_internal;
		}
		IOUtil.ensureFolderExist(dir);
		return dir;
	}

	private static String _getLoadResultDir(String feedSource) {
		String dir = SubscriberLoader.LoadResultDir;
		// external
		if (feedSource.equals(OriginalFileInfo.FEED_SOURCE_EXTERNAL)) {
			dir = SubscriberLoader.LoadResultDir + "/" + "external";
		}
		// internal
		else if (feedSource.equals(OriginalFileInfo.FEED_SOURCE_INTERNAL)) {
			dir = SubscriberLoader.LoadResultDir + "/" + "internal";
		}
		return dir;
	}

	public static String getErrorFileDir(String feedSource) {
		String folder = _getLoadResultDir(feedSource) + "/" + "errorfiles";
		IOUtil.ensureFolderExist(folder);
		return folder;
	}

	public static String getInfoFileDir(String feedSource) {
		String folder = _getLoadResultDir(feedSource) + "/" + "infofiles";
		IOUtil.ensureFolderExist(folder);
		return folder;
	}

	public static String getRetryFileDir(String feedSource) {
		String folder = _getLoadResultDir(feedSource) + "/" + "retryfiles";
		IOUtil.ensureFolderExist(folder);
		return folder;
	}

	public static String getInternalDelimiter(String feedSource,
			String billingID) {
		return ",";
	}

	public static void main(String[] args) {
		SubscriberLoader sl = new SubscriberLoader();
		sl.launchMain();
	}
}
