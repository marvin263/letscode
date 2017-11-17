package com.syniverse.loader;

import java.io.BufferedReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.common.CommUtil;
import com.syniverse.db.DBManipulate;
import com.syniverse.db.DBUtil;
import com.syniverse.info.EachRowInfo;
import com.syniverse.info.OverviewValidateInfo;
import com.syniverse.info.ProcessingFileInfo;
import com.syniverse.info.SubkeyAndTypeInfo;
import com.syniverse.info.VResultInfo;
import com.syniverse.io.IOUtil;

public class VRunnable implements Runnable {
	private static final Log LOGGER = LogFactory.getLog(VRunnable.class);
	public static final int SEND2DB_THRESHOLD = 5000;
	public static String SQL_CheckExistence = createCheckExistence();

	private final BlockingQueue<EachRowInfo> queueFromValidatorToLoader;
	private final ProcessingFileInfo pfInfo;
	private final VResultInfo vr;
	private final AtomicBoolean forceLoaderCommit;
	private final AtomicBoolean loaderRequiredStop;

	private BufferedReader br;
	private Connection conn;
	private PreparedStatement pstmtCheckExistence = null;

	public VRunnable(BlockingQueue<EachRowInfo> queueFromValidatorToLoader,
			ProcessingFileInfo spfInfo, VResultInfo vr,
			AtomicBoolean forceLoaderCommit, AtomicBoolean loaderRequiredStop) {
		this.queueFromValidatorToLoader = queueFromValidatorToLoader;
		this.pfInfo = spfInfo;
		this.vr = vr;
		this.forceLoaderCommit = forceLoaderCommit;
		this.loaderRequiredStop = loaderRequiredStop;
	}

	@Override
	public void run() {
		try {
			createConnPstmt();
			validatorProduceFileRow();
		} catch (Exception e) {
			vr.setFinalSuccess(false);
			String realRsn = CommUtil
					.format("Generic big exception in producing file rows. file=[{0}], detail=[{1}]",
							pfInfo.filenameInDB, e.getMessage());

			vr.setFailReason(PCSLoader.asmbFailRsn(
					OverviewValidateInfo.FAIL_REASON_common, realRsn));

			LOGGER.error("Should never happen. Generic Exception in VRunnable",
					e);
		} finally {
			forceLoaderCommit.set(true);
			IOUtil.closeReader(br);
			DBUtil.closeConnAndMultiPstmt(conn,
					new PreparedStatement[] { pstmtCheckExistence });
		}
	}

	private void createConnPstmt() {
		try {
			conn = DBUtil.getNewC();
			pstmtCheckExistence = conn.prepareStatement(SQL_CheckExistence);
		} catch (Exception e) {
			LOGGER.error("Error when VRunnable.createConnPstmt", e);
			throw new RuntimeException("Cannot getNewC/CreatePrepareStatement",
					e);
		}
	}

	public void validatorProduceFileRow() {
		long lastProcess = pfInfo.getLastRowProcessed();
		long totalRow = pfInfo.getTotalRow();
		if (lastProcess == totalRow) {
			return;
		}

		String line = null;
		br = IOUtil.createBufferedReader(pfInfo.getDatafileFullPath(), null);
		long processingLine = lastProcess;
		// The first line in the file is: 1
		long fileLineNumberJustRead = 0;

		ArrayList<SubkeyLinenum> goodKeys = new ArrayList<SubkeyLinenum>();
		ArrayList<EachRowInfo> errorErinfos = new ArrayList<EachRowInfo>();
		int currentRoundCount = 0;
		try {
			while ((loaderRequiredStop.get() == false)
					&& (line = br.readLine()) != null) {
				fileLineNumberJustRead++;
				if (fileLineNumberJustRead <= lastProcess) {
					continue;
				}

				if (currentRoundCount == SEND2DB_THRESHOLD) {
					submit_2_Server(errorErinfos, goodKeys);
					currentRoundCount = 0;
					goodKeys.clear();
					errorErinfos.clear();
				}
				// Loader require us stop, so stop
				if (loaderRequiredStop.get() == true) {
					break;
				}
				processingLine++;
				currentRoundCount++;
				String currentSubkey = line.trim();
				String errMsg = FormatValidator.checkFormat(currentSubkey);
				// Format error
				if (!CommUtil.isEmpty(errMsg)) {
					EachRowInfo erInfo = EachRowInfo.creatFormatError(
							currentSubkey, processingLine);
					erInfo.setNotSuccessMsg(errMsg);
					errorErinfos.add(erInfo);
					continue;
				}
				// Since here, subkey format is good
				goodKeys.add(new SubkeyLinenum(processingLine, currentSubkey));
			}

			if (loaderRequiredStop.get() == false && currentRoundCount != 0) {
				submit_2_Server(errorErinfos, goodKeys);
				currentRoundCount = 0;
				goodKeys.clear();
				errorErinfos.clear();
			}

		} catch (Throwable e) {
			vr.setFinalSuccess(false);
			String realRsn = CommUtil
					.format("Generic big exception in producing file rows. splitfile=[{0}], detail=[{1}]",
							pfInfo.filenameInDB, e.getMessage());

			vr.setFailReason(PCSLoader.asmbFailRsn(
					OverviewValidateInfo.FAIL_REASON_common, realRsn));

			LOGGER.error(
					"Should never happen. Generic exception in validatorProduceFileRow",
					e);
		}
	}

	private void submit_2_Server(ArrayList<EachRowInfo> errorErinfos,
			ArrayList<SubkeyLinenum> goodKeys) {
		ArrayList<EachRowInfo> allErinfos = new ArrayList<EachRowInfo>(
				errorErinfos);

		String[] arrayStrkey = new String[goodKeys.size()];
		for (int i = 0; i < goodKeys.size(); i++) {
			arrayStrkey[i] = goodKeys.get(i).subkey;
		}

		SubkeyAndTypeInfo[] arraySubkeyAndType = DBManipulate.constructSKT(
				conn, pstmtCheckExistence, pfInfo.getOwnerOperatorNum(),
				arrayStrkey);

		for (int i = 0; i < arraySubkeyAndType.length; i++) {
			SubkeyAndTypeInfo subkeyAndType = arraySubkeyAndType[i];
			long linenum = goodKeys.get(i).linenum;
			EachRowInfo erInfo = null;
			// 1. no records at all by SUBKEY
			if (subkeyAndType.notExist()) {
				erInfo = EachRowInfo.creatNotInSubscriber(subkeyAndType.subkey,
						linenum);
			}
			// 2. more than 1 records by SUBKEY (more SUBKEY_TYPE)
			else if (subkeyAndType.moreThan1Type()) {
				erInfo = EachRowInfo.creatTooManyTypes(subkeyAndType.subkey,
						linenum);
			}
			// 3. perfect, only one SUBKEY_TYPE by SUBKEY_TYPE
			else if (subkeyAndType.oneType_nice()) {
				erInfo = EachRowInfo
						.creatPerfect(subkeyAndType.subkey, linenum);
				erInfo.setSubkeyType(subkeyAndType.getSubkeytype());
			}
			// never happen
			else {
				erInfo = EachRowInfo
						.creatPerfect(subkeyAndType.subkey, linenum);
				erInfo.setSubkeyType(subkeyAndType.getSubkeytype());
			}

			allErinfos.add(erInfo);
		}

		Collections.sort(allErinfos);

		for (Iterator<EachRowInfo> it = allErinfos.iterator(); it.hasNext();) {
			if (loaderRequiredStop.get() == true) {
				break;
			}
			try {
				queueFromValidatorToLoader.put(it.next());
			} catch (InterruptedException e) {
				LOGGER.info("Validator is interrupted by Loader. let's stop", e);
			}
		}
	}

	private static class SubkeyLinenum {
		public final long linenum;
		public final String subkey;

		public SubkeyLinenum(final long linenum, final String subkey) {
			this.linenum = linenum;
			this.subkey = subkey;
		}
	}

	private static String createCheckExistence() {
		StringBuilder sb = new StringBuilder(
				"select SUBKEY, SUBKEY_TYPE from T_PCS_SUBSCRIBER  where OWNER_OPERATOR_NO=? and SUBKEY in (");
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
}
