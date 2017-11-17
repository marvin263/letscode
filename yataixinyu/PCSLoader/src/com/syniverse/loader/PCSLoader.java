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
import com.syniverse.db.DBManipulate;
import com.syniverse.db.DBUtil;
import com.syniverse.db.FileTableBean;
import com.syniverse.info.AbstractFileInfo;
import com.syniverse.info.EachRowInfo;
import com.syniverse.info.FutureVL;
import com.syniverse.info.LResultInfo;
import com.syniverse.info.OverviewValidateInfo;
import com.syniverse.info.ProcessingFileInfo;
import com.syniverse.info.ResultVL;
import com.syniverse.info.VResultInfo;
import com.syniverse.io.IOUtil;
import com.syniverse.shutdown.ShutDownService;

public class PCSLoader {
	private static final Log LOGGER = LogFactory.getLog(PCSLoader.class);
	static {
		configLogFirst();
	}
	public static final long Sleep_If_No_Files = 20 * 1000;
	public final static int V2L_CAPACITY = (int) (LRunnable.COMMIT_THRESHOLD * 1.1);

	// private static final String PCSLoadResultDir = Config
	// .getString(Config.Key_Load_Result_Dir);
	private static final String PCSLoadResultDir = "D:/TestPCSLoader/PCSLoaderResultDir";

	public static final String Suffix_Result_File_Error = ".errors";
	public static final String Suffix_Result_File_Dups = ".dups";

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
				.getJarStayFolder_nologinfo(PCSLoader.class);
		String jarFolderParent = new File(jarFolderSelf).getParentFile()
				.getAbsolutePath();

		String logDirFullPath = jarFolderParent + "/log";
		IOUtil.ensureFolderExist(logDirFullPath);

		String log4jFullPath = jarFolderParent + "/conf/log4j.properties";

		PropertyConfigurator.configure(log4jFullPath);

		LOGGER.info("log4j.properties file: " + log4jFullPath);
	}

	public PCSLoader() {
	}

	private void prepare() {
		startService();
	}

	private void startService() {
		validatorSvc = Executors
				.newSingleThreadExecutor(new SetNameThreadFactory(
						ThreadPool_Prefix_Validator));
		loaderSvc = Executors.newSingleThreadExecutor(new SetNameThreadFactory(
				ThreadPool_Prefix_Loader));
		new ShutDownService().startShutDownService();
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
			List<FileTableBean> listCandidate = fetchFilesFromDB();
			int fileCount = listCandidate.size();
			LOGGER.info(CommUtil.format("round [{0}], fileCount [{1}]", round,
					fileCount));
			round++;
			if (fileCount == 0) {
				sleepWhenNoFiles();
				continue;
			}
			// OK, we've gotten files from DB. Let's construct
			// ProcessingFileInfo
			for (FileTableBean ftb : listCandidate) {
				ProcessingFileInfo pfInfo = null;
				try {
					long begin = System.currentTimeMillis();
					// Cannot get connection. let jump out for. Do not consume
					// <code>listCandidate</code> any more
					if (reassignIfInvalid() == false) {
						LOGGER.warn("Cannot getValidateConnection(), jump out, do not consume listCandidate");
						break;
					}
					// Change status to processing
					DBManipulate.updateStartDate(ftb, globalConn, begin);
					DBManipulate.updateProcessStatus(ftb, globalConn,
							FileTableBean.Processing);

					pfInfo = bofin.createOrgnfInfoByFeedLog(globalConn, ftb);

					List<String> listFailReason = nofileOrZipIssueFailReason(pfInfo);

					if (!listFailReason.isEmpty()) {
						copeWithNodatafile(pfInfo, ftb, listFailReason);
						continue;
					}

					loadEachFile(globalConn, ftb, pfInfo);

					long end = System.currentTimeMillis();
					LOGGER.info(CommUtil.format(
							"rowCount: {0}, totally cost: {1}, filename: {2}",
							ftb.getTotalRow(), MemoryUsage.human(end - begin),
							ftb.filenameInDB));

				} catch (Throwable e) {
					LOGGER.error(
							"Should never happen. Generic exception in processLoadEndlessly(), file: "
									+ ftb, e);

					// Ensure we've updated FEED_LOG.FEED_STATUS, so that we'll
					// not process this file again and again
					List<String> listFailReason = new ArrayList<String>();
					String realRsn = CommUtil
							.format("Generic big exception in tackling original file. original=[{0}], detail=[{1}]",
									ftb.filenameInDB, e.getMessage());
					listFailReason.add(PCSLoader.asmbFailRsn(
							OverviewValidateInfo.FAIL_REASON_common, realRsn));

					updateFeedStatus(globalConn, pfInfo,
							FileTableBean.Exception, listFailReason);
				} finally {
					listResultVL.clear();
					listFutureVL.clear();
				}
			}
		}
	}

	private void copeWithNodatafile(ProcessingFileInfo orgnfInfo,
			FileTableBean oneFltb, List<String> listFailReason) {
		clearupAndUpdateStatus(globalConn, orgnfInfo, FileTableBean.Exception,
				listFailReason);
	}

	private List<String> nofileOrZipIssueFailReason(ProcessingFileInfo pfInfo) {
		List<String> listFailReason = new ArrayList<String>(1);
		String failRsn = null;
		File orgnFile = new File(pfInfo.getDatafileParentFolder() + "/"
				+ pfInfo.filenameInDB);
		if (!orgnFile.exists() || !orgnFile.isFile()) {
			failRsn = "File uploaded is missing. Please contact Syniverse Administrator";
			listFailReason.add(failRsn);
			return listFailReason;
		}

		// zip file
		if (IOUtil.isZip_by_LastestSuffix(pfInfo.filenameInDB) == true) {
			String zipFullPath = pfInfo.getDatafileParentFolder() + "/"
					+ pfInfo.filenameInDB;
			int containedCount = IOUtil.datafileCount(zipFullPath);

			if (containedCount == -1) {
				failRsn = "Cannot unzip file, it might be a corrupted zip file";
				listFailReason.add(failRsn);
				return listFailReason;
			}
			if (containedCount == 0 || containedCount > 1) {
				failRsn = CommUtil
						.format("Only one file is allowed inside zip file (now {0} contains {1} data files)",
								pfInfo.filenameInDB, containedCount);
				listFailReason.add(failRsn);
				return listFailReason;
			}
		}
		// NOT zip
		else {
			// do nothing
		}

		File datafile = new File(pfInfo.getDatafileFullPath());
		if (CommUtil.isEmpty(pfInfo.getDatafilename()) == true
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
	private List<FileTableBean> fetchFilesFromDB() {
		reassignIfInvalid();
		List<FileTableBean> listCandidate = null;
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
	 * @param pfInfo
	 * @return
	 */
	private OverviewValidateInfo overviewValidateB4Split(Connection conn,
			ProcessingFileInfo pfInfo) {
		// OverViewValidate first before split
		OverViewValidate ov = new OverViewValidate(conn, pfInfo);
		OverviewValidateInfo ovvInfo = ov.validate();
		LOGGER.info("Overview validating: " + pfInfo);
		LOGGER.info("Overview validating result: " + ovvInfo);
		return ovvInfo;
	}

	private void loadEachFile(Connection conn, FileTableBean ftb,
			ProcessingFileInfo orgnfInfo) {
		long begin = System.currentTimeMillis();
		LOGGER.info("Begin loadEachFile() split&load file: " + orgnfInfo);

		String processStatus = FileTableBean.Rejected;
		List<String> listFailReason = new ArrayList<String>();

		try {
			OverviewValidateInfo ovvInfo = overviewValidateB4Split(conn,
					orgnfInfo);
			// overview validation fails.
			if (ovvInfo.isPassed() == false) {
				listFailReason.add(ovvInfo.getFailReason());
				processStatus = FileTableBean.Rejected;
				LOGGER.info(CommUtil
						.format("Cannot pass overview validation, failreason=[{0}], file=[{1}]",
								ovvInfo.getFailReason(), orgnfInfo));
				return;
			}

			// OverviewValidate OK, let's split it

			sumitValidatorLoaderTasks(orgnfInfo);

			boolean bAllSuccess = waitForResult(listFailReason, ftb, orgnfInfo,
					listFutureVL, listResultVL);
			processStatus = bAllSuccess ? FileTableBean.Completed
					: FileTableBean.Exception;

		} catch (Throwable e) {
			processStatus = FileTableBean.Exception;

			String realRsn = CommUtil
					.format("Generic big exception in loading original file. original=[{0}], detail=[{1}]",
							orgnfInfo.filenameInDB, e.getMessage());

			listFailReason.add(PCSLoader.asmbFailRsn(
					OverviewValidateInfo.FAIL_REASON_common, realRsn));

			LOGGER.error(
					"Should never happen. loadEachFile() catches Generic exception. Update processStatus to FileTableBean.Exception"
							+ orgnfInfo, e);
		} finally {
			clearupAndUpdateStatus(conn, orgnfInfo, processStatus,
					listFailReason);
		}
		long end = System.currentTimeMillis();
		LOGGER.info(CommUtil.format(
				"End loadEachFile() split&load, cost: {0}, file: {1}",
				MemoryUsage.human(end - begin), orgnfInfo));
	}

	private void clearupAndUpdateStatus(Connection conn,
			ProcessingFileInfo ftb, String processStatus,
			List<String> listFailReason) {
		// Encounter exception or not, we both need do two things
		// 1. cleanup files(
		// c). delete all files in working dir generated during
		// execution
		// 2. update FEED_LOG status
		// 1. deal with files
		new CleanupFiles().dealWithFilesAfterLoader(ftb);
		// OK, finally, we've finished loading a file(encounter
		// exception or
		// not)
		// let's update DB
		updateFeedStatus(conn, ftb, processStatus, listFailReason);
	}

	private void updateFeedStatus(Connection conn, ProcessingFileInfo ftb,
			String processStatus, List<String> listFailReason) {
		// update FEED_LOG. do not need to update FEED_SPLIT_FILE. they've been
		// updated in LRunnable
		try {
			String orgnFailRsn = CommUtil.concat(listFailReason, "----", "");
			String tuncFailRsn = CommUtil
					.truncateToSize(orgnFailRsn, 512, true);

			long current = System.currentTimeMillis();
			String tableName = "FEED_LOG";

			String[] fields = new String[] { "FILE_STATUS", "FAIL_REASON",
					"END_TIMESTAMP", "UPDATED_TIMESTAMP" };
			Object[] fieldValues = new Object[] { processStatus, tuncFailRsn,
					new Timestamp(current), new Timestamp(current) };

			String[] keys = null;
			Object[] keyValues = null;

			int[] sqltype = null;

			if (ftb.isUpload()) {
				tableName = "T_PCS_FILE_LOG";
				keys = new String[] { "LOG_ID", "RETRY_COUNT" };
				keyValues = new Object[] { Long.valueOf(ftb.getLogID()),
						Integer.valueOf(ftb.getCurRetryCount()) };
				sqltype = new int[] { Types.VARCHAR, Types.VARCHAR,
						Types.TIMESTAMP, Types.TIMESTAMP, Types.NUMERIC,
						Types.NUMERIC };
			} else {
				tableName = "T_PCS_REF_RANGE";
				keys = new String[] { "RANGE_ID" };
				keyValues = new Object[] { Long.valueOf(ftb.getRangeID()) };
				sqltype = new int[] { Types.VARCHAR, Types.VARCHAR,
						Types.TIMESTAMP, Types.TIMESTAMP, Types.NUMERIC };
			}

			DBManipulate.update(conn, tableName, fields, fieldValues, keys,
					keyValues, sqltype);
		} catch (Throwable e) {
			LOGGER.error("Error when updateFeedStatus", e);
		}
	}

	private boolean waitForResult(List<String> listFailReason,
			FileTableBean ftb, ProcessingFileInfo orgnfInfo,
			List<FutureVL> listF, List<ResultVL> listR) {
		long begin = System.currentTimeMillis();
		LOGGER.info("Begin waitForResult. ProcessingFileInfo:" + orgnfInfo);
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
		LOGGER.info("Ends waitForResult. ProcessingFileInfo:" + orgnfInfo);
		LOGGER.info(CommUtil.format(
				"rowCount: {0}, AUD database cost: {1}, filename: {2}",
				ftb.getTotalRow(), MemoryUsage.human(end - begin),
				orgnfInfo.getDatafilename()));

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

	private void sumitValidatorLoaderTasks(ProcessingFileInfo pfInfo) {

		BlockingQueue<EachRowInfo> queueFromValidatorToLoader = new ArrayBlockingQueue<EachRowInfo>(
				V2L_CAPACITY);
		AtomicBoolean forceLoaderCommit = new AtomicBoolean(false);
		AtomicBoolean loaderRequiredStop = new AtomicBoolean(false);

		VResultInfo vr = new VResultInfo(pfInfo.filenameInDB);
		VRunnable vrunnable = new VRunnable(queueFromValidatorToLoader, pfInfo,
				vr, forceLoaderCommit, loaderRequiredStop);

		Future<VResultInfo> futureV = validatorSvc.submit(vrunnable, vr);

		LResultInfo lr = new LResultInfo(pfInfo.filenameInDB);
		LRunnable lrunnable = new LRunnable(queueFromValidatorToLoader, pfInfo,
				lr, forceLoaderCommit, loaderRequiredStop, futureV);

		Future<LResultInfo> futureL = loaderSvc.submit(lrunnable, lr);

		listFutureVL.add(new FutureVL(futureV, futureL));
		listResultVL.add(new ResultVL(vr, lr));

	}

	public static String asmbFailRsn(String commRsn, String realRsn) {
		return commRsn + OverviewValidateInfo.DELIMITER_fail_reason + realRsn;
	}

	public static String getWorkingDir() {
		if (!CommUtil.isEmpty(Working_Dir)) {
			return Working_Dir;
		}
		String jarFolderSelf = IOUtil.getJarStayFolder(PCSLoader.class);
		String jarFolderParent = new File(jarFolderSelf).getParentFile()
				.getAbsolutePath();
		String wd = jarFolderParent + "/" + "workingdir";
		IOUtil.ensureFolderExist(wd);
		Working_Dir = wd;
		LOGGER.info("Working directory is: " + Working_Dir);
		return Working_Dir;
	}

	public static String getPCSLoadResultDir(AbstractFileInfo afi) {
		String folder = null;
		if (afi.isUpload()) {
			folder = PCSLoader.PCSLoadResultDir + "/upload_" + afi.getLogID();
		} else {
			folder = PCSLoader.PCSLoadResultDir + "/range_" + afi.getRangeID();
		}
		IOUtil.ensureFolderExist(folder);
		return folder;
	}

	public static String decideRangeFilename(String rangeLower,
			String rangeUpper) {
		return rangeLower + "_" + rangeUpper + ".dat";
	}

	public static String decideResultFilename_Bold(AbstractFileInfo afi) {
		String nameWithoutSuffix = IOUtil
				.eliminate_from_firstDot(afi.filenameInDB);
		if (afi.isUpload()) {
			return nameWithoutSuffix + "_" + afi.getCurRetryCount();
		} else {
			return nameWithoutSuffix;
		}
	}

	public static void main(String[] args) {
		PCSLoader sl = new PCSLoader();
		sl.launchMain();
	}
}
