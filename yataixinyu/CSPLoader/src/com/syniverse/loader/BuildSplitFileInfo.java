package com.syniverse.loader;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.db.DBManipulate;
import com.syniverse.db.FeedLogTableBean;
import com.syniverse.db.FeedSplitFileTableBean;
import com.syniverse.info.OriginalFileInfo;
import com.syniverse.info.SplitFileInfo;
import com.syniverse.split.DoSplit;

public class BuildSplitFileInfo {

	private static final Log LOGGER = LogFactory
			.getLog(BuildSplitFileInfo.class);

	public List<SplitFileInfo> createSplitFileInfo(Connection conn,
			OriginalFileInfo orgnfInfo) {
		List<FeedSplitFileTableBean> sptbInDB = DBManipulate.selectSplitFiles(
				conn, orgnfInfo.getLogID());
		List<SplitFileInfo> listSplitFile = new ArrayList<SplitFileInfo>();
		// Please note: finding records for logID in FEED_SPLIT_FILE means: file
		// has been already split successfully
		// This is a new file. Never been split
		if (sptbInDB.size() == 0) {
			return buildByFile(conn, orgnfInfo, listSplitFile);
		}
		// already been split
		else {
			buildByDB(orgnfInfo, sptbInDB, listSplitFile);
		}
		return listSplitFile;
	}

	private List<SplitFileInfo> buildByFile(Connection conn,
			OriginalFileInfo orgnfInfo, List<SplitFileInfo> listSplitFile) {
		List<SplitFileInfo> splitResult = DoSplit.splitOriginalFile(orgnfInfo,
				SubscriberLoader.Split_File_Count);

		// Bad, Split exception. Just return the empty listSplitFile.
		// Do not update FEED_LOG here, that will be done in
		// SubscriberLoader#loadEachFile()
		if (splitResult == null) {
			return listSplitFile;
		}
		// Good, split OK
		else {
			listSplitFile.addAll(splitResult);
			// a). Update FEED_LOG.FEED_STATUS to "Processing"
			// b). Insert all the split file into FEED_SPLIT_FILE
			String processStatus = FeedLogTableBean.Processing;
			DBManipulate.updateFeedLog(orgnfInfo.getLogID(), conn,
					processStatus, orgnfInfo.getOriginalFileTotalRowCount());
			DBManipulate.insertFeedSplitLog(conn, spfInfo2sptb(listSplitFile));
		}
		return listSplitFile;
	}

	private List<FeedSplitFileTableBean> spfInfo2sptb(
			List<SplitFileInfo> listSplitInfo) {
		List<FeedSplitFileTableBean> sptb = new ArrayList<FeedSplitFileTableBean>();
		for (SplitFileInfo eachSplitFileInfo : listSplitInfo) {
			sptb.add(spfInfo2sptb(eachSplitFileInfo));
		}
		return sptb;
	}

	// SplitFileInfo---->FeedSplitFileTableBean
	public static FeedSplitFileTableBean spfInfo2sptb(SplitFileInfo esplit) {
		FeedSplitFileTableBean sptbInfo = new FeedSplitFileTableBean();

		sptbInfo.setLogID(SplitFileInfo.getOrgnfInfo().getLogID());
		sptbInfo.setFilename(esplit.getSplitfilename());
		sptbInfo.setLastRowProcessed(esplit.getLastRowProcessed());

		sptbInfo.setTotalCount(esplit.getTotalCount());
		sptbInfo.setSuccessCount(esplit.getSuccessCount());
		sptbInfo.setFailCount(esplit.getFailCount());

		sptbInfo.setProcessStatus(esplit.getProcessStatus());

		return sptbInfo;
	}

	private void buildByDB(OriginalFileInfo orgnfInfo,
			List<FeedSplitFileTableBean> sptbInDB,
			List<SplitFileInfo> listSplitFile) {
		// FeedSplitFileTableBean---->SplitFileInfo
		for (FeedSplitFileTableBean onesptb : sptbInDB) {
			SplitFileInfo spfInfo = new SplitFileInfo();

			spfInfo.setSplitfilename(onesptb.getFilename());
			spfInfo.setLastRowProcessed(onesptb.getLastRowProcessed());

			spfInfo.setTotalCount(onesptb.getTotalCount());
			spfInfo.setSuccessCount(onesptb.getSuccessCount());
			spfInfo.setFailCount(onesptb.getFailCount());

			spfInfo.setProcessStatus(onesptb.getProcessStatus());

			listSplitFile.add(spfInfo);
		}
	}

}
