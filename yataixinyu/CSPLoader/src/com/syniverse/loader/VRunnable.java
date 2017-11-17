package com.syniverse.loader;

import java.io.BufferedReader;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.common.CommUtil;
import com.syniverse.config.Config;
import com.syniverse.info.EachRowInfo;
import com.syniverse.info.OriginalFileInfo;
import com.syniverse.info.OverviewValidateInfo;
import com.syniverse.info.SplitFileInfo;
import com.syniverse.info.VResultInfo;
import com.syniverse.io.IOUtil;
import com.syniverse.rti.csp.validator.ValidateResult;
import com.syniverse.rti.csp.validator.Validator;

public class VRunnable implements Runnable {
	private static final Log LOGGER = LogFactory.getLog(VRunnable.class);
	private final BlockingQueue<EachRowInfo> queueFromValidatorToLoader;
	private final SplitFileInfo spfInfo;
	private final VResultInfo vr;
	private final AtomicBoolean forceLoaderCommit;
	private final AtomicBoolean loaderRequiredStop;
	private BufferedReader br;

	public VRunnable(BlockingQueue<EachRowInfo> queueFromValidatorToLoader,
			SplitFileInfo spfInfo, VResultInfo vr,
			AtomicBoolean forceLoaderCommit, AtomicBoolean loaderRequiredStop) {
		this.queueFromValidatorToLoader = queueFromValidatorToLoader;
		this.spfInfo = spfInfo;
		this.vr = vr;
		this.forceLoaderCommit = forceLoaderCommit;
		this.loaderRequiredStop = loaderRequiredStop;
	}

	@Override
	public void run() {
		try {
			validatorProduceFileRow();
		} catch (Exception e) {
			vr.setFinalSuccess(false);
			String realRsn = CommUtil
					.format("Generic big exception in producing file rows. splitfile=[{0}], detail=[{1}]",
							spfInfo.getSplitfilename(), e.getMessage());

			vr.setFailReason(SubscriberLoader.asmbFailRsn(
					OverviewValidateInfo.FAIL_REASON_common, realRsn));

			LOGGER.error("Should never happen. Generic Exception in VRunnable",
					e);
		} finally {
			IOUtil.closeReader(br);
			forceLoaderCommit.set(true);
		}
	}

	public void validatorProduceFileRow() {
		String line = null;
		int lastProcess = spfInfo.getLastRowProcessed();

		LOGGER.info(CommUtil
				.format("VRunnable begins. lastRowProcessed=[{0}], totalCount=[{1}], successCount=[{2}], failCount=[{3}], file={4}",
						lastProcess, spfInfo.getTotalCount(),
						spfInfo.getSuccessCount(), spfInfo.getFailCount(),
						spfInfo.getSplitfilename()));

		if (lastProcess == spfInfo.getTotalCount()) {
			return;
		}

		Validator vtr = null;
		try {
			String validatorRequiredFileheader = assembleFileheaderWithAction4Validator(SplitFileInfo
					.getOrgnfInfo());
			vtr = new Validator(Config.getString(Config.Connection_jdbcURL),
					Config.getString(Config.Connection_username),
					Config.getString(Config.Connection_password), SplitFileInfo
							.getOrgnfInfo().getBillingID(),
					validatorRequiredFileheader);
		} catch (Throwable e) {
			vr.setFinalSuccess(false);
			String realRsn = CommUtil.format(
					"Cannot create validator. splitfile=[{0}], detail=[{1}]",
					spfInfo.getSplitfilename(), e.getMessage());
			vr.setFailReason(SubscriberLoader.asmbFailRsn(
					OverviewValidateInfo.FAIL_REASON_common, realRsn));

			LOGGER.error("Error when create Validator", e);
			return;
		}

		br = IOUtil.createBufferedReader(spfInfo.getSplitFullPath(), null);
		int processingLine = lastProcess;

		// The first line in the file is: 1
		int fileLineNumberJustRead = 0;
		try {
			while ((loaderRequiredStop.get() == false)
					&& (line = br.readLine()) != null) {
				fileLineNumberJustRead++;
				if (fileLineNumberJustRead <= lastProcess) {
					continue;
				}

				processingLine++;

				ValidateResult validateResult = null;
				try {
					validateResult = vtr.validateRecord(processingLine, line);
				} catch (Throwable e) {
					LOGGER.error("Validator cannot hold this line: " + line, e);
				}

				EachRowInfo erInfo = null;
				if (validateResult == null) {
					LOGGER.error("Create a dummy EachRowInfo quietly and continue...");
					erInfo = EachRowInfo.createDummy(SplitFileInfo
							.getOrgnfInfo().getBillingID(), line,
							processingLine);
				} else if (validateResult.isSuccess() == false) {
					erInfo = EachRowInfo.createFailderInfo(SplitFileInfo
							.getOrgnfInfo().getBillingID(), line,
							processingLine, validateResult);
				}
				// passed validation
				else {
					erInfo = new EachRowInfo(SplitFileInfo.getOrgnfInfo()
							.getBillingID(), line, processingLine,
							validateResult);
				}

				try {
					queueFromValidatorToLoader.put(erInfo);
				} catch (InterruptedException e) {
					LOGGER.info(
							"Validator is interrupted by Loader. let's stop", e);
				}
			}
		} catch (Throwable e) {
			vr.setFinalSuccess(false);
			String realRsn = CommUtil
					.format("Generic big exception in producing file rows. splitfile=[{0}], detail=[{1}]",
							spfInfo.getSplitfilename(), e.getMessage());

			vr.setFailReason(SubscriberLoader.asmbFailRsn(
					OverviewValidateInfo.FAIL_REASON_common, realRsn));

			LOGGER.error(
					"Should never happen. Generic exception in validatorProduceFileRow",
					e);
		}
	}

	private String assembleFileheaderWithAction4Validator(
			OriginalFileInfo orgnfInfo) {
		// external
		if (orgnfInfo.getFeedSource().equals(
				OriginalFileInfo.FEED_SOURCE_EXTERNAL)) {
			return null;
		}
		// internal
		else if (orgnfInfo.getFeedSource().equals(
				OriginalFileInfo.FEED_SOURCE_INTERNAL)) {
			return "Action," + orgnfInfo.getCommaSplitFileheader();
		}
		throw new RuntimeException(
				"Cannot determine file source [external/internal]");
	}
}
