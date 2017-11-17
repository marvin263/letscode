package com.syniverse.loader;

import java.io.BufferedWriter;
import java.io.Writer;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.common.CommUtil;
import com.syniverse.db.DBManipulate;
import com.syniverse.db.DBUtil;
import com.syniverse.db.FileTableBean;
import com.syniverse.info.EachRowInfo;
import com.syniverse.info.LResultInfo;
import com.syniverse.info.OverviewValidateInfo;
import com.syniverse.info.PK;
import com.syniverse.info.ProcessingFileInfo;
import com.syniverse.info.VResultInfo;
import com.syniverse.io.IOUtil;

public class LRunnable implements Runnable {
	private static final Log LOGGER = LogFactory.getLog(LRunnable.class);

	public static final int COMMIT_THRESHOLD = 1500;
	public static final int TOTAL_SWALLOW_SIZE = (int) (COMMIT_THRESHOLD * 1.1);

	public static String SQL_A = createInsert();
	public static String SQL_grpsub_existence = createCheckExistence();

	private final BlockingQueue<EachRowInfo> queueFromValidatorToLoader;
	private final ProcessingFileInfo spfInfo;
	private final LResultInfo lr;
	private final AtomicBoolean forceLoaderCommit;
	private final AtomicBoolean loaderRequiredStop;
	private final Future<VResultInfo> futureV;

	private Connection conn;
	private PreparedStatement pstmtA = null;
	private PreparedStatement pstmtGrpsubkExistence = null;
	/**
	 * .info, store file rows which passed validator already, but something
	 * wrong when insert/update/delete DB
	 */
	private BufferedWriter bw_dups = null;
	/**
	 * .errors, store file rows which cannot pass validator
	 * 
	 */
	private BufferedWriter bw_errors = null;

	/**
	 * ONLY for log purpose, start from 0
	 */
	private int commitCount = 0;
	private final long groupID;

	private static String createInsert() {
		String insertSql = "Insert into T_PCS_XREF_GRP_SUB (GROUP_NO,SUBKEY,SUBKEY_TYPE,INSERT_TIMESTAMP) values (?,?,?,sysdate)";
		return insertSql;
	}

	private static String createCheckExistence() {
		StringBuilder sb = new StringBuilder(
				"select SUBKEY from T_PCS_XREF_GRP_SUB where GROUP_NO=? and SUBKEY_TYPE=? and SUBKEY in (");
		int total = 1000;
		for (int i = 0; i < total; i++) {
			if (i == (total - 1)) {
				sb.append("?)");
			} else {
				sb.append("?,");
			}
		}
		return sb.toString();
	}

	public LRunnable(BlockingQueue<EachRowInfo> queueFromValidatorToLoader,
			ProcessingFileInfo spfInfo, LResultInfo lr,
			AtomicBoolean forceLoaderCommit, AtomicBoolean loaderRequiredStop,
			Future<VResultInfo> futureV) {
		this.queueFromValidatorToLoader = queueFromValidatorToLoader;
		this.spfInfo = spfInfo;
		this.lr = lr;
		this.forceLoaderCommit = forceLoaderCommit;
		this.loaderRequiredStop = loaderRequiredStop;
		this.futureV = futureV;
		groupID = spfInfo.getGroupID();
	}

	private void createConnPstmt() {
		try {
			conn = DBUtil.getNewC();
			conn.setAutoCommit(false);
			pstmtA = conn.prepareStatement(SQL_A);
			pstmtGrpsubkExistence = conn.prepareStatement(SQL_grpsub_existence);
		} catch (Exception e) {
			LOGGER.error("Error when createConnPstmt", e);
			throw new RuntimeException("Cannot getNewC/CreatePrepareStatement",
					e);
		}
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
	 * 99999_20130925141410.csv.zip-->99999_20130925141410-->
	 * upload/range_logID/99999_20130925141410_x.errors,
	 * upload/range_logID/99999_20130925141410_x.dups
	 * 
	 */
	private void createBufferedWriter() {
		try {
			String dupname = PCSLoader.decideResultFilename_Bold(spfInfo)
					+ PCSLoader.Suffix_Result_File_Dups;
			String fullDups = PCSLoader.getPCSLoadResultDir(spfInfo) + "/"
					+ dupname;

			String errorname = PCSLoader.decideResultFilename_Bold(spfInfo)
					+ PCSLoader.Suffix_Result_File_Error;
			String fullError = PCSLoader.getPCSLoadResultDir(spfInfo) + "/"
					+ errorname;

			bw_dups = IOUtil.createBufferedWriter(fullDups, null, true);
			bw_errors = IOUtil.createBufferedWriter(fullError, null, true);
		} catch (Exception e) {
			LOGGER.error("Error when createBufferedWriter", e);
			throw new RuntimeException("Cannot createBufferedWriter", e);
		}
	}

	@Override
	public void run() {
		long begin = System.currentTimeMillis();
		try {
			createBufferedWriter();
			createConnPstmt();
			loaderConsumeFileRow();
		} catch (Throwable e) {
			lr.setFinalSuccess(false);
			DBManipulate.updateProcessStatus(spfInfo, conn, FileTableBean.Exception);
			String realRsn = CommUtil
					.format("Generic big exception in consuming file rows. splitfile=[{0}], detail=[{1}]",
							spfInfo.filenameInDB, e.getMessage());
			lr.setFailReason(PCSLoader.asmbFailRsn(
					OverviewValidateInfo.FAIL_REASON_common, realRsn));

			LOGGER.error(CommUtil.format(
					"commitCount={0}, Generic Exception happens", mark()), e);
		} finally {
			forceLoaderCommit.set(true);
			loaderRequiredStop.set(true);
			futureV.cancel(true);
			IOUtil.closeWriter(new Writer[] { bw_dups, bw_errors });
			DBUtil.closeConnAndMultiPstmt(conn, new PreparedStatement[] {
					pstmtA, pstmtGrpsubkExistence });
			long end = System.currentTimeMillis();
		}
	}

	public void loaderConsumeFileRow() {
		BlockingQueue<EachRowInfo> communicateQueue = queueFromValidatorToLoader;
		int passedValidationCount = 0;
		int swallowCount = 0;
		int commitThreshold = COMMIT_THRESHOLD;
		int swallowThreshold = TOTAL_SWALLOW_SIZE;

		ListHolder hder = new ListHolder();
		while ( /*
				 * response to interruption
				 */
		(Thread.currentThread().isInterrupted() == false)// response to
				/*
				 * when count of rows passing validation hits commitThreshold,
				 * commit to DB
				 */
				&& passedValidationCount < commitThreshold
				/*
				 * prevent that we swallow so many rows that OutMemoryError
				 * happens
				 */
				&& swallowCount < swallowThreshold
				/*
				 * consider forceLoaderCommit
				 */
				&& (
				/*
				 * validator is still producing file rows
				 */
				forceLoaderCommit.get() == false ||
				/*
				 * after validator has done producing file rows, we should
				 * consume all of them
				 */
				(forceLoaderCommit.get() == true && communicateQueue.size() > 0))) {
			EachRowInfo erInfo = null;
			try {
				erInfo = communicateQueue.poll(50, TimeUnit.MILLISECONDS);
			} catch (Exception e) {
				LOGGER.info(
						"Loader interrupted. Ingore sliently and continue.... now communicateQueue.size()="
								+ communicateQueue.size(), e);
				continue;
			}

			if (erInfo == null) {
				continue;
			}

			swallowCount++;
			if (erInfo.yesPerfect == true) {
				passedValidationCount++;
				hder.passValidation.add(erInfo);
			}
			// VRunnable told us: this one fails
			else {
				assignErrors(hder, erInfo);
			}

			// hit commitThreshold
			if ((passedValidationCount == commitThreshold)
					|| (swallowCount == swallowThreshold)) {
				commitDBandWriteErrorFile(hder, swallowCount);
				// reset passedValidationCount, totalCount
				passedValidationCount = 0;
				swallowCount = 0;
				hder.clear();
			}

		}// end_while

		// even passedValidationCount is 0, we still need to execute this,
		// because we have to write error file
		commitDBandWriteErrorFile(hder, swallowCount);
		// reset passedValidationCount, totalCount
		passedValidationCount = 0;
		swallowCount = 0;
		hder.clear();
		// Since we arrive here, set FEED_SPLIT_FILE.STATUS to Completed
		DBManipulate.updateProcessStatus(spfInfo, conn, FileTableBean.Completed);
	}

	private void assignErrors(ListHolder hder, EachRowInfo erInfo) {
		// 1. subkey format is error
		if (erInfo.formatError) {
			hder.formatError.add(erInfo);
		}
		// 2. subkey NOT in T_PCS_SUBSCRIBER
		else if (erInfo.notInSubscriber) {
			hder.notInSubscriber.add(erInfo);
		}
		// 3. find too many subkeyType for subkey
		else if (erInfo.tooManyTypes) {
			hder.tooManyTypes.add(erInfo);
		}
	}

	private void commitDBandWriteErrorFile(ListHolder hder, int swallowCount) {
		Set<PK> setAlreadExist = DBManipulate.put2SetIfExist(conn,
				pstmtGrpsubkExistence, hder.passValidation, groupID);
		boolean bNeedCommit = false;
		for (int i = 0; i < hder.passValidation.size(); i++) {
			EachRowInfo erInfo = hder.passValidation.get(i);
			// duplicated
			if (setAlreadExist
					.contains(PK.create(spfInfo.getGroupID(), erInfo))) {
				hder.duplicate.add(erInfo);
				continue;
			}
			bNeedCommit = true;
			// set values
			DBManipulate.setValues(pstmtA, erInfo, groupID);
			DBUtil.addBatch(pstmtA);
		}
		if (bNeedCommit == true) {
			int exeCount = _doExecuteBatch(pstmtA);
			boolean batchExceptionHappened = false;
			if (exeCount != -1) {
				LOGGER.info("good, _doExecuteBatch works good");
				hder.alreadySuccessCount = hder.alreadySuccessCount + exeCount;
			} else {
				LOGGER.info("bad, _doExecuteBatch encounters exception. rollback everying. Reexecute all rows in this commit cycle one by one");
				hder.alreadySuccessCount = 0;
				hder.duplicate.clear();
				rollbackDatabase();
				batchExceptionHappened = true;
			}

			// good, batch successfully
			if (batchExceptionHappened == false) {
			}
			// HOHO, we encounter exception when execute in batch
			else {
				reExecuteAll_IfBatchFailed(groupID, hder);
			}
		}
		commitAndWrite(hder, swallowCount);
	}

	private boolean commitAndWrite(ListHolder hder, int swallowCount) {
		long incrDups = hder.duplicate.size();

		int cntFormatError = hder.formatError.size();
		int cntNotInSubscriber = hder.notInSubscriber.size();
		int cntTooManyTypes = hder.tooManyTypes.size();
		int cntInsertError = hder.dbInsertError.size();
		long incrFail = cntFormatError + cntNotInSubscriber + cntTooManyTypes
				+ cntInsertError;

		long incrSuccess = hder.alreadySuccessCount;
		assert swallowCount == incrSuccess + incrFail + incrDups;

		// 1. Increase success/fail/dups count
		DBManipulate.increaseSuccessFailDups(spfInfo, conn, incrSuccess,
				incrFail, incrDups);
		// 2. OK. We've updated T_PCS_XREF_GRP_SUB, as well as
		// T_PCS_FILE_LOG|T_PCS_REF_RANGE
		// Let's commit
		DBUtil.commit(conn);
		// 3. DB update done, let's write result files
		writeResultFiles(hder, incrFail);

		hder.clear();
		return true;
	}

	private void writeResultFiles(ListHolder hder, long incrFail) {
		List<EachRowInfo> fails = new ArrayList<EachRowInfo>((int) incrFail);
		fails.addAll(hder.formatError);
		fails.addAll(hder.notInSubscriber);
		fails.addAll(hder.tooManyTypes);
		fails.addAll(hder.dbInsertError);
		// sort by EachRowInfo.lineNumberInOrgnFile
		Collections.sort(fails);
		HandleErrorRow.write_errors(bw_errors, fails);

		List<EachRowInfo> dups = new ArrayList<EachRowInfo>(hder.duplicate);
		// sort by EachRowInfo.lineNumberInOrgnFile
		Collections.sort(dups);
		HandleErrorRow.write_dups(bw_dups, dups);
	}

	private void reExecuteAll_IfBatchFailed(long groupID, ListHolder hder) {
		executeOneByOne(groupID, hder);
	}

	private void rollbackDatabase() {
		DBUtil.rollback(conn);
	}

	private void executeOneByOne(long groupID, ListHolder hder) {
		Set<PK> setAlreadExist = DBManipulate.put2SetIfExist(conn,
				pstmtGrpsubkExistence, hder.passValidation, groupID);
		int size = hder.passValidation.size();
		for (int i = 0; i < size; i++) {
			EachRowInfo erInfo = hder.passValidation.get(i);
			// duplicated
			if (setAlreadExist.contains(PK.create(groupID, erInfo))) {
				hder.duplicate.add(erInfo);
				continue;
			}
			DBManipulate.setValues(pstmtA, erInfo, groupID);
			try {
				pstmtA.executeUpdate();
				setAlreadExist.add(PK.create(groupID, erInfo));
				hder.alreadySuccessCount++;
			} catch (Exception e) {
				erInfo.setNotSuccessMsg("Unknow error: " + e.getMessage());
				hder.dbInsertError.add(erInfo);
			}
		}
	}

	private int _doExecuteBatch(PreparedStatement lastPstmt) {
		try {
			int[] success = lastPstmt.executeBatch();
			return success.length;
		} catch (BatchUpdateException e) {
			LOGGER.error(
					"BatchUpdateException when doExecuteBatch, will reexecute each row in this batch again",
					e);
			return -1;
		} catch (SQLException e) {
			LOGGER.error(
					"SQLException when doExecuteBatch, will reexecute each row in this batch again",
					e);
			return -1;
		} catch (Throwable e) {
			LOGGER.error(
					"Exception when doExecuteBatch, will reexecute each row in this batch again",
					e);
			return -1;
		}
	}

	private String mark() {
		return "[" + commitCount + ", " + spfInfo.filenameInDB + "]";
	}

	static class ListHolder {
		public long alreadySuccessCount;
		/**
		 * subkey is lexically illegal. (NOT check database)
		 */
		public final List<EachRowInfo> formatError = new ArrayList<EachRowInfo>();
		/**
		 * cannot find any record by subkey
		 */
		public final List<EachRowInfo> notInSubscriber = new ArrayList<EachRowInfo>();
		/**
		 * find more than one records by subkey
		 */
		public final List<EachRowInfo> tooManyTypes = new ArrayList<EachRowInfo>();
		/**
		 * 
		 * Has checked: format, whether in T_PCS_SUBSCRIBER
		 * 
		 * NOT check whether in T_PCS_XREF_GRP_SUB
		 */
		public final List<EachRowInfo> passValidation = new ArrayList<EachRowInfo>();
		/**
		 * (GROUP_NO, SUBKEY, SUBKEY_TYPE) duplicated in T_PCS_XREF_GRP_SUB
		 */
		public final List<EachRowInfo> duplicate = new ArrayList<EachRowInfo>();
		/**
		 * everything is good, but error when insert into T_PCS_XREF_GRP_SUB
		 */
		public final List<EachRowInfo> dbInsertError = new ArrayList<EachRowInfo>();

		public ListHolder() {
			alreadySuccessCount = 0;
		}

		public void clear() {
			alreadySuccessCount = 0;
			formatError.clear();
			notInSubscriber.clear();
			tooManyTypes.clear();
			passValidation.clear();
			duplicate.clear();
			dbInsertError.clear();
		}
	}
}
