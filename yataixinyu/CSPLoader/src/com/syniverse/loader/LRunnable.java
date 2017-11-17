package com.syniverse.loader;

import java.io.BufferedWriter;
import java.io.Writer;
import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.common.CommUtil;
import com.syniverse.common.MemoryUsage;
import com.syniverse.config.Config;
import com.syniverse.db.DBManipulate;
import com.syniverse.db.DBUtil;
import com.syniverse.db.FeedSplitFileTableBean;
import com.syniverse.info.DBColumnsInfo;
import com.syniverse.info.EachRowInfo;
import com.syniverse.info.LResultInfo;
import com.syniverse.info.OverviewValidateInfo;
import com.syniverse.info.PK;
import com.syniverse.info.SplitFileInfo;
import com.syniverse.info.VResultInfo;
import com.syniverse.io.IOUtil;
import com.syniverse.rti.csp.validator.ActionType;
import com.syniverse.rti.csp.validator.Validator;

public class LRunnable implements Runnable {
	private static final Log LOGGER = LogFactory.getLog(LRunnable.class);
	public static final String A_Error = ": Subscriber already exists";
	public static final String U_Error = ": No Subscriber exists to Update";
	public static final String D_Error = ": No Subscriber exists to Delete";

	public static final int COMMIT_THRESHOLD = Config
			.getInt(Config.Key_Commit_Threshold);
	public static final int TOTAL_SWALLOW_SIZE = (int) (COMMIT_THRESHOLD * 1.1);

	private final BlockingQueue<EachRowInfo> queueFromValidatorToLoader;
	private final SplitFileInfo spfInfo;
	private final LResultInfo lr;
	private final AtomicBoolean forceLoaderCommit;
	private final AtomicBoolean loaderRequiredStop;
	private final Future<VResultInfo> futureV;
	private Connection conn;
	private PreparedStatement pstmtA = null;
	private PreparedStatement pstmtU = null;
	private PreparedStatement pstmtD = null;
	private PreparedStatement pstmtCheckExistence = null;
	private Validator vCheckExistence = null;

	/**
	 * .info, store file rows which passed validator already, but something
	 * wrong when insert/update/delete DB
	 */
	private BufferedWriter bwWarning_info = null;
	/**
	 * .errors, store file rows which cannot pass validator
	 * 
	 */
	private BufferedWriter bwFailValidation_errors = null;

	private int successInOneCycle = 0;
	/**
	 * ONLY for log purpose, start from 0
	 */
	private int commitCount = 0;

	public LRunnable(BlockingQueue<EachRowInfo> queueFromValidatorToLoader,
			SplitFileInfo spfInfo, LResultInfo lr,
			AtomicBoolean forceLoaderCommit, AtomicBoolean loaderRequiredStop,
			Future<VResultInfo> futureV) {
		this.queueFromValidatorToLoader = queueFromValidatorToLoader;
		this.spfInfo = spfInfo;
		this.lr = lr;
		this.forceLoaderCommit = forceLoaderCommit;
		this.loaderRequiredStop = loaderRequiredStop;
		this.futureV = futureV;
	}

	private void createConnPstmt() {
		try {
			conn = DBUtil.getNewC();
			conn.setAutoCommit(false);
			pstmtA = conn.prepareStatement(DBColumnsInfo.SQL_A);
			pstmtU = conn.prepareStatement(DBColumnsInfo.SQL_U);
			pstmtD = conn.prepareStatement(DBColumnsInfo.SQL_D);
			pstmtCheckExistence = conn
					.prepareStatement(DBColumnsInfo.SQL_CHECK_EXISTENCE);
		} catch (Exception e) {
			LOGGER.error("Error when createConnPstmt", e);
			throw new RuntimeException("Cannot getNewC/CreatePrepareStatement",
					e);
		}
	}

	private void createVCheckExistence() {
		try {
			vCheckExistence = new Validator(SplitFileInfo.getOrgnfInfo()
					.getBillingID());
		} catch (Exception e) {
			LOGGER.error("Error when createVCheckExistence", e);
			throw new RuntimeException(
					"Cannot create Validator for checking existence", e);
		}
	}

	private void createBufferedWriter() {
		String workingDirWarning = spfInfo.getSplitFullPath()
				+ SubscriberLoader.Suffix_Result_File_Info;
		String workingDirError = spfInfo.getSplitFullPath()
				+ SubscriberLoader.Suffix_Result_File_Error;
		LOGGER.info(CommUtil.format(
				"workingDirWarning={0}, workingDirError={1}",
				workingDirWarning, workingDirError));

		bwWarning_info = IOUtil.createBufferedWriter(workingDirWarning, null,
				true);
		bwFailValidation_errors = IOUtil.createBufferedWriter(workingDirError,
				null, true);
	}

	@Override
	public void run() {
		long begin = System.currentTimeMillis();
		try {
			createBufferedWriter();
			createConnPstmt();
			createVCheckExistence();
			loaderConsumeFileRow();
		} catch (Throwable e) {
			lr.setFinalSuccess(false);
			// set FEED_SPLIT_FILE.STATUS
			DBManipulate.updateSplitStatus(conn,
					FeedSplitFileTableBean.Exception, spfInfo
							.getSplitfilename(), SplitFileInfo.getOrgnfInfo()
							.getLogID());
			String realRsn = CommUtil
					.format("Generic big exception in consuming file rows. splitfile=[{0}], detail=[{1}]",
							spfInfo.getSplitfilename(), e.getMessage());
			lr.setFailReason(SubscriberLoader.asmbFailRsn(
					OverviewValidateInfo.FAIL_REASON_common, realRsn));

			LOGGER.error(CommUtil.format(
					"commitCount={0}, Generic Exception happens", mark()), e);
		} finally {
			forceLoaderCommit.set(true);
			loaderRequiredStop.set(true);
			futureV.cancel(true);
			IOUtil.closeWriter(new Writer[] { bwWarning_info,
					bwFailValidation_errors });
			DBUtil.closeConnAndMultiPstmt(conn, new PreparedStatement[] {
					pstmtA, pstmtU, pstmtD, pstmtCheckExistence });
			long end = System.currentTimeMillis();

			try {
				LOGGER.info(CommUtil
						.format("Marvin LRunnable/Dennis checking existence: [{0}]/[{1}] , file: {2}",
								MemoryUsage.human(end - begin),
								vCheckExistence.getTimeForRecordsExist(),
								spfInfo.getSplitfilename()));
			} catch (Throwable e) {
				LOGGER.error("Error when log time cost. should never happen", e);
			}
		}
	}

	public void loaderConsumeFileRow() {
		BlockingQueue<EachRowInfo> communicateQueue = queueFromValidatorToLoader;
		int passedValidationCount = 0;
		int swallowCount = 0;
		int commitThreshold = COMMIT_THRESHOLD;
		int swallowThreshold = TOTAL_SWALLOW_SIZE;

		LOGGER.info(CommUtil.format("filename=[{0}], COMMIT_THRESHOLD=[{1}]",
				spfInfo.getSplitfilename(), commitThreshold));

		LOGGER.info(CommUtil.format("filename=[{0}], TOTAL_SWALLOW_SIZE=[{1}]",
				spfInfo.getSplitfilename(), swallowThreshold));

		List<EachRowInfo> failValidation = new ArrayList<EachRowInfo>();
		List<EachRowInfo> passValidation = new ArrayList<EachRowInfo>(
				commitThreshold);

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

			spfInfo.lastProcessRowIncre1();
			swallowCount++;
			if (erInfo.isPassed() == true) {
				passedValidationCount++;
				passValidation.add(erInfo);
			}
			// fail validation
			else {
				failValidation.add(erInfo);
			}

			// hit commitThreshold
			if ((passedValidationCount == commitThreshold)
					|| (swallowCount == swallowThreshold)) {
				commitDBandWriteErrorFile(passValidation, failValidation,
						swallowCount);
				passValidation.clear();
				failValidation.clear();
				// reset passedValidationCount, totalCount
				passedValidationCount = 0;
				swallowCount = 0;
			}

		}// end_while

		// even passedValidationCount is 0, we still need to execute this,
		// because we have to write error file
		commitDBandWriteErrorFile(passValidation, failValidation, swallowCount);
		passValidation.clear();
		failValidation.clear();
		// reset passedValidationCount, totalCount
		passedValidationCount = 0;
		swallowCount = 0;
		// Since we arrive here, set FEED_SPLIT_FILE.STATUS to Completed
		DBManipulate.updateSplitStatus(conn, FeedSplitFileTableBean.Completed,
				spfInfo.getSplitfilename(), SplitFileInfo.getOrgnfInfo()
						.getLogID());
	}

	private void commitDBandWriteErrorFile(List<EachRowInfo> passValidation,
			List<EachRowInfo> failValidation, int swallowCount) {
		successInOneCycle = 0;
		LOGGER.info(CommUtil
				.format("commitCount={0} begins, swallowCount={1}, passValidation.size()={2}, failValidation.size()={3}",
						mark(), swallowCount, passValidation.size(),
						failValidation.size()));
		/*
		 * Block means what: When iterate passValidation, if successive
		 * element's actions are the same, we call these element as Block
		 * 
		 * Block will be executed by pstmt.executeBatch();
		 * 
		 * Once pstmt.executeBatch() fails, we'll roll back all the changes. and
		 * then
		 * 
		 * execute alreadySuccessBlock by batch, then execute all the left
		 * elements in passValidation one by one
		 */

		// After a "block batch execute" successful, add to this list
		List<List<EachRowInfo>> alreadySuccessBlock = new ArrayList<List<EachRowInfo>>();
		// current block
		List<EachRowInfo> listCurrentBlock = new ArrayList<EachRowInfo>();

		List<List<EachRowInfo>> listAUD = new ArrayList<List<EachRowInfo>>(3);
		listAUD.add(new ArrayList<EachRowInfo>());// A
		listAUD.add(new ArrayList<EachRowInfo>());// U
		listAUD.add(new ArrayList<EachRowInfo>());// D

		List<EachRowInfo> listAError = new ArrayList<EachRowInfo>();
		List<EachRowInfo> listUError = new ArrayList<EachRowInfo>();
		List<EachRowInfo> listDError = new ArrayList<EachRowInfo>();

		Set<PK> setAlreadExist = DBManipulate.put2SetIfExist(vCheckExistence,
				conn, pstmtCheckExistence, passValidation,
				SplitFileInfo.orgnfInfo.getBillingID());

		long begin = System.currentTimeMillis();
		ActionType lastActioin = null;
		boolean batchExceptionHappened = false;
		// NOT included in last success block
		int lastSuccessBlockEndIndex_exclusive = 0;
		for (int i = 0; i < passValidation.size(); i++) {
			EachRowInfo erInfo = passValidation.get(i);
			boolean actionAllowed = checkActionAllow(listAUD, setAlreadExist,
					erInfo);
			if (actionAllowed == false) {
				continue;
			}
			ActionType currentAction = erInfo.getAction();
			// action is the same with previous one. add to batch
			if (lastActioin == null || lastActioin == currentAction) {
				addRow2BlockAndBatch(listCurrentBlock, erInfo);
				lastActioin = currentAction;
			}
			// action has changed
			else {
				LOGGER.info(CommUtil
						.format("Action changed. last={0}, current={1}, countInBatch={2}",
								lastActioin, currentAction,
								listCurrentBlock.size()));
				long acBegin = System.currentTimeMillis();
				// commit the LAST block
				int exeCount = _doExecuteBatch(assignPstmt(lastActioin));
				if (exeCount != -1) {
					LOGGER.info("good, _doExecuteBatch works good");
					alreadySuccessBlock.add(new ArrayList<EachRowInfo>(
							listCurrentBlock));
					successInOneCycle += exeCount;
					addErrorToEach(listAUD, listAError, listUError, listDError);

					clearErrorRecord(listAUD);
					listCurrentBlock.clear();

					// PLEASE NOTE HERE: whenever action changes, we commit the
					// batch, but don't forget that we've NOT do anything to
					// current <code>erInfo</code>
					lastSuccessBlockEndIndex_exclusive = i;
					addRow2BlockAndBatch(listCurrentBlock, erInfo);
					// action has changed
					lastActioin = currentAction;
				} else {
					// listCurrentBlock commit error. and of course,
					// <code>erInfo</code> HAS NOT been consumed yet
					LOGGER.info("bad, _doExecuteBatch encounters exception. rollback everying. Reexecute all rows in this commit cycle one by one");
					rollbackDatabase();
					batchExceptionHappened = true;
					break;
				}
				LOGGER.info(CommUtil
						.format("Action changed. cost: {0}",
								MemoryUsage.human(System.currentTimeMillis()
										- acBegin)));
			}
		}
		// the tail block has never been executed. let's commit it here
		if (batchExceptionHappened == false) {
			batchExceptionHappened = executeTailAfterPreviousSuccessiveBlockAllSuccess(
					listAUD, listAError, listUError, listDError, lastActioin,
					alreadySuccessBlock, listCurrentBlock);
		}

		// Everything goes so good. Now we've executed all the elements in batch
		if (batchExceptionHappened == false) {
		}
		// HOHO, we encounter exception when execute in batch
		else {
			LOGGER.info("hoho, encounter exception during batch operation. reExecuteAll_IfBatchFailed begins");
			reExecuteAll_IfBatchFailed(lastSuccessBlockEndIndex_exclusive,
					passValidation, alreadySuccessBlock, listAError,
					listUError, listDError, passValidation);
		}

		spfInfo.setSuccessCount(spfInfo.getSuccessCount() + successInOneCycle);
		int totalFailCount = failValidation.size()
				+ (passValidation.size() - successInOneCycle);
		spfInfo.setFailCount(spfInfo.getFailCount() + totalFailCount);

		List<EachRowInfo> listPassValidationButErrorWhenDBOperation = new ArrayList<EachRowInfo>();
		listPassValidationButErrorWhenDBOperation.addAll(listAError);
		listPassValidationButErrorWhenDBOperation.addAll(listUError);
		listPassValidationButErrorWhenDBOperation.addAll(listDError);

		commitAndWrite(failValidation,
				listPassValidationButErrorWhenDBOperation, successInOneCycle,
				totalFailCount);

		LOGGER.info(CommUtil
				.format("cost(not include check existence)={0}, swallowCount={1}, successCount={2}, failCount={3}, commitCount={4}",
						MemoryUsage.human(System.currentTimeMillis() - begin),
						swallowCount, successInOneCycle, totalFailCount, mark()));
		commitCount++;
	}

	private boolean commitAndWrite(List<EachRowInfo> failValidation,
			List<EachRowInfo> listPassValidationButErrorWhenDBOperation,
			int incrSuccess, int incrFail) {
		// 1. Set success/fail count in FEED_SPLIT_FILE
		DBManipulate.updateLastSucessFail(conn, spfInfo.getLastRowProcessed(),
				spfInfo.getSuccessCount(), spfInfo.getFailCount(), spfInfo
						.getSplitfilename(), SplitFileInfo.getOrgnfInfo()
						.getLogID());
		// 2. Increase success/fail count in FEED_LOG
		DBManipulate.increaseSuccessFail(conn, incrSuccess, incrFail,
				SplitFileInfo.getOrgnfInfo().getLogID());
		// 3. OK. We've updated Subscriber, as well as FEED_SPLIT_FILE,
		// FEED_LOG. Let's commit
		DBUtil.commit(conn);
		// 4. Write error/info files
		HandleErrorRow.writeFailedValidation_error(bwFailValidation_errors,
				failValidation);
		HandleErrorRow.writeWarning_info(bwWarning_info,
				listPassValidationButErrorWhenDBOperation);
		return true;
	}

	/**
	 * Do two things
	 * <p>
	 * 1. If Action now allowed, add this <code>erInfo</code> into listError
	 * <p>
	 * 2. If allowed, add/remove <code>erInfo</code> into/from
	 * <code>setAlreadyExist</code>
	 * 
	 * PLEASE NOTE: we'll may add/remove erInfo into/from setAlreadyExist
	 * 
	 * @param listAError
	 * @param listUError
	 * @param listDError
	 * @param setAlreadExist
	 * @param erInfo
	 * @return
	 */
	private boolean checkActionAllow(List<EachRowInfo> listAError,
			List<EachRowInfo> listUError, List<EachRowInfo> listDError,
			Set<PK> setAlreadExist, EachRowInfo erInfo) {
		ActionType currentAction = erInfo.getAction();
		PK pk = PK.create(erInfo);
		// already in DB or (it's in passValidation AND has just been
		// addBatch/executed)
		if (setAlreadExist.contains(pk)) {
			if (currentAction == ActionType.INSERT) {
				erInfo.setValidationMsg(erInfo.getSubkeySource() + A_Error);
				listAError.add(erInfo);
				return false;
			} else if (currentAction == ActionType.UPDATE) {
				// nice, we can update
				return true;
			} else if (currentAction == ActionType.DELETE) {
				setAlreadExist.remove(pk);
				// nice, we can delete
				return true;
			}
		}
		// NOT exist yet
		else {
			if (currentAction == ActionType.INSERT) {
				setAlreadExist.add(pk);
				return true;
			} else if (currentAction == ActionType.UPDATE) {
				erInfo.setValidationMsg(erInfo.getSubkeySource() + U_Error);
				listUError.add(erInfo);
				return false;
			} else if (currentAction == ActionType.DELETE) {
				erInfo.setValidationMsg(erInfo.getSubkeySource() + D_Error);
				listDError.add(erInfo);
				return false;
			}
		}
		return false;
	}

	private boolean executeTailAfterPreviousSuccessiveBlockAllSuccess(
			List<List<EachRowInfo>> listAUD, List<EachRowInfo> listAError,
			List<EachRowInfo> listUError, List<EachRowInfo> listDError,
			ActionType lastActioin,
			List<List<EachRowInfo>> alreadySuccessBlock,
			List<EachRowInfo> listCurrentBlock) {
		long begin = System.currentTimeMillis();
		LOGGER.info(CommUtil
				.format("executeTailAfterPreviousSuccessiveBlockAllSuccess begins. countInBatch={0}",
						listCurrentBlock.size()));
		// all the elements in passValidation have been touched. Now, all the
		// elements not be executed are all in listCurrentBlock, and they have
		// the same action
		boolean batchExceptionHappened = false;

		if (listCurrentBlock.size() == 0) {
			LOGGER.info("executeTailAfterPreviousSuccessiveBlockAllSuccess-->listCurrentBlock.size()==0");
			alreadySuccessBlock
					.add(new ArrayList<EachRowInfo>(listCurrentBlock));

			addErrorToEach(listAUD, listAError, listUError, listDError);

			clearErrorRecord(listAUD);
			listCurrentBlock.clear();
		} else {
			// let's execute the TAIL block
			int exeCount = _doExecuteBatch(assignPstmt(lastActioin));
			if (exeCount != -1) {
				LOGGER.info("good, executeTailAfterPreviousSuccessiveBlockAllSuccess-->_doExecuteBatch works good");
				alreadySuccessBlock.add(new ArrayList<EachRowInfo>(
						listCurrentBlock));
				successInOneCycle += exeCount;

				addErrorToEach(listAUD, listAError, listUError, listDError);

				clearErrorRecord(listAUD);
				listCurrentBlock.clear();
			} else {
				// all the left elements is in listCurrentBlock, and they have
				// the same action
				LOGGER.info("bad, executeTailAfterPreviousSuccessiveBlockAllSuccess-->_doExecuteBatch encounters exception. Reexecute all rows in this batch one by one");
				rollbackDatabase();
				batchExceptionHappened = true;
			}
		}
		LOGGER.info(CommUtil
				.format("executeTailAfterPreviousSuccessiveBlockAllSuccess ends. cost: {0}",
						MemoryUsage.human(System.currentTimeMillis() - begin)));
		return batchExceptionHappened;
	}

	private void addRow2BlockAndBatch(List<EachRowInfo> listCurrentBlock,
			EachRowInfo erInfo) {
		listCurrentBlock.add(erInfo);
		DBManipulate.setValues(assignPstmt(erInfo.getAction()), erInfo);
		DBUtil.addBatch(assignPstmt(erInfo.getAction()));
	}

	private void reExecuteAll_IfBatchFailed(
			int lastSuccessBlockEndIndex_exclusive,
			List<EachRowInfo> passValidation,
			List<List<EachRowInfo>> alreadySuccessBlock,
			List<EachRowInfo> listAError, List<EachRowInfo> listUError,
			List<EachRowInfo> listDError, List<EachRowInfo> listToBeExecuted) {
		long begin = System.currentTimeMillis();
		LOGGER.info("reExecuteAll_IfBatchFailed begins");
		// We've roll back everything, execute passValidation from very
		// beginning

		// 1. Execute all successive successful block
		boolean redoSuccessBlock = redoAllSuccessBlockAfterRollback(alreadySuccessBlock);

		// 2. Execute all the left elements in passValidation
		int fromIndex = -1;
		int length = -1;
		// Good, the original successful batch all success again.
		if (redoSuccessBlock == true) {
			fromIndex = lastSuccessBlockEndIndex_exclusive;
			length = passValidation.size() - lastSuccessBlockEndIndex_exclusive;
		}
		// bad, batch fails. we have to execute all the elements in
		// passValidation one by one
		else {
			// we should regenerate error msg, failCount
			successInOneCycle = 0;
			listAError.clear();
			listUError.clear();
			listDError.clear();

			fromIndex = 0;
			length = passValidation.size();
		}
		executeFromLastSuccessBlock(listAError, listUError, listDError,
				passValidation, fromIndex, length);
		LOGGER.info(CommUtil.format(
				"reExecuteAll_IfBatchFailed ends. cost: {0}",
				MemoryUsage.human(System.currentTimeMillis() - begin)));
	}

	private void rollbackDatabase() {
		DBUtil.rollback(conn);
	}

	private void executeOneByOne(List<EachRowInfo> listAError,
			List<EachRowInfo> listUError, List<EachRowInfo> listDError,
			List<EachRowInfo> passValidation, int fromIndex, int length) {
		LOGGER.info("executeOneByOne, rows to be executed:" + length);
		// we must refetch setAlreadyExist, because we may already have executed
		// some batches successfully
		List<EachRowInfo> listTMP = new ArrayList<EachRowInfo>(length);
		for (int i = fromIndex; i < (fromIndex + length); i++) {
			listTMP.add(passValidation.get(i));
		}
		Set<PK> setAlreadExist = DBManipulate.put2SetIfExist(vCheckExistence,
				conn, pstmtCheckExistence, listTMP,
				SplitFileInfo.orgnfInfo.getBillingID());

		for (int i = fromIndex; i < (fromIndex + length); i++) {
			EachRowInfo erInfo = passValidation.get(i);
			ActionType action = erInfo.getAction();
			if (checkActionAllow(listAError, listUError, listDError,
					setAlreadExist, erInfo) == false) {
				continue;
			}
			PreparedStatement pstmt = assignPstmt(action);
			DBManipulate.setValues(pstmt, erInfo);
			try {
				pstmt.executeUpdate();
				successInOneCycle++;
			} catch (Exception e) {
				// checkActionAllowed() has add/remove values into/from
				// setAlreadyExist
				// since it fails, let's do the opposite operation
				restoreSetAfterFail(setAlreadExist, erInfo);
				LOGGER.error(
						CommUtil.format(
								"executeOneByOne exception, ingore and continue next. [billingID={0}, subscriberKey={1}]",
								erInfo.billingID, erInfo.getSubscriberKey()), e);

				erInfo.setValidationMsg("Unknow error: " + e.getMessage());
				add2Error(listAError, listUError, listDError, erInfo);
			}
		}
	}

	private void restoreSetAfterFail(Set<PK> setAlreadExist, EachRowInfo erInfo) {
		PK pk = PK.create(erInfo);
		switch (erInfo.getAction()) {
		case DELETE:
			setAlreadExist.add(pk);
			break;
		case UPDATE:
			// do nothing
			break;
		case INSERT:
			setAlreadExist.remove(pk);
			break;
		default:
		}
	}

	/**
	 * The successful blocks are in succession in passValidation (note: maybe
	 * some elements are removed by checkAllowed())
	 * 
	 * These blocks should be executed successfully, so we don't need to
	 * regenerate listA/U/D error message or regenerate failCount
	 * 
	 * 
	 * But, there may be possibility (en, this may happen) that after we roll
	 * back before, somebody inserts data from GUI, so, this may cause the
	 * original successful batch fail
	 * 
	 * If batch fails, let's roll back again batch fails
	 * 
	 * @param alreadySuccessBlock
	 * 
	 * 
	 * @return
	 */
	private boolean redoAllSuccessBlockAfterRollback(
			List<List<EachRowInfo>> alreadySuccessBlock) {
		long begin = System.currentTimeMillis();
		int totalBlockCount = alreadySuccessBlock.size();
		LOGGER.info("redoAllSuccessBlockAfterRollback begins, totalBlockCount="
				+ alreadySuccessBlock.size());

		int processingIndex = 0;
		for (List<EachRowInfo> listBlock : alreadySuccessBlock) {
			if (listBlock.size() == 0) {
				continue;
			}
			PreparedStatement pstmt = assignPstmt(listBlock.get(0).getAction());
			for (EachRowInfo erInfo : listBlock) {
				DBManipulate.setValues(pstmt, erInfo);
				DBUtil.addBatch(pstmt);
			}
			int exeCount = _doExecuteBatch(pstmt);

			LOGGER.info(CommUtil
					.format("redoAllSuccessBlockAfterRollback, processing {0}/{1}, batchSuccessCount={3} ",
							processingIndex, totalBlockCount, exeCount));

			if (exeCount != -1) {
				// good, batch successes
			} else {
				// bad, batch fails
				rollbackDatabase();
				return false;
			}

		}
		LOGGER.info(CommUtil.format(
				"redoAllSuccessBlockAfterRollback ends. cost: {0}",
				MemoryUsage.human(System.currentTimeMillis() - begin)));
		return true;
	}

	private void executeFromLastSuccessBlock(List<EachRowInfo> listAError,
			List<EachRowInfo> listUError, List<EachRowInfo> listDError,
			List<EachRowInfo> passValidation, int fromIndex, int length) {
		long begin = System.currentTimeMillis();

		LOGGER.info("executeFromLastSuccessBlock begins, rows to be executed:"
				+ length);
		executeOneByOne(listAError, listUError, listDError, passValidation,
				fromIndex, length);
		LOGGER.info(CommUtil.format(
				"executeFromLastSuccessBlock ends, cost: {0}",
				MemoryUsage.human(System.currentTimeMillis() - begin)));
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

	private boolean checkActionAllow(List<List<EachRowInfo>> listAUD,
			Set<PK> setAlreadExist, EachRowInfo erInfo) {
		List<EachRowInfo> listAError = listAUD.get(0);
		List<EachRowInfo> listUError = listAUD.get(1);
		List<EachRowInfo> listDError = listAUD.get(2);
		return checkActionAllow(listAError, listUError, listDError,
				setAlreadExist, erInfo);
	}

	private void clearErrorRecord(List<List<EachRowInfo>> listAUD) {
		for (List<EachRowInfo> list : listAUD) {
			list.clear();
		}
	}

	private void addErrorToEach(List<List<EachRowInfo>> listAUD,
			List<EachRowInfo> listAError, List<EachRowInfo> listUError,
			List<EachRowInfo> listDError) {
		listAError.addAll(listAUD.get(0));
		listUError.addAll(listAUD.get(1));
		listDError.addAll(listAUD.get(2));
	}

	private void add2Error(List<EachRowInfo> listAError,
			List<EachRowInfo> listUError, List<EachRowInfo> listDError,
			EachRowInfo erInfo) {
		switch (erInfo.getAction()) {
		case INSERT:
			listAError.add(erInfo);
			break;
		case UPDATE:
			listUError.add(erInfo);
			break;
		case DELETE:
			listDError.add(erInfo);
			break;
		default:
			break;
		}
	}

	private PreparedStatement assignPstmt(ActionType action) {
		switch (action) {
		case DELETE:
			return pstmtD;
		case UPDATE:
			return pstmtU;
		case INSERT:
			return pstmtA;
		}
		return null;
	}

	private String mark() {
		return "[" + commitCount + ", " + spfInfo.getSplitfilename() + "]";
	}
}
