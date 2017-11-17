package com.syniverse.split;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.common.CommUtil;
import com.syniverse.common.MemoryUsage;
import com.syniverse.db.FeedSplitFileTableBean;
import com.syniverse.info.OriginalFileInfo;
import com.syniverse.info.SplitFileInfo;
import com.syniverse.loader.SubscriberLoader;

public class DoSplit {
	private static final Log LOGGER = LogFactory.getLog(SubscriberLoader.class);

	public static List<SplitFileInfo> splitOriginalFile(
			OriginalFileInfo orgnfInfo, int count) {
		long begin = System.currentTimeMillis();
		LOGGER.info(CommUtil.format("Splitting begins--{0}", orgnfInfo));

		List<SplitFileInfo> listSplitFile = new ArrayList<SplitFileInfo>(count);
		try {
			FileSplitter fs = new FileSplitter("Action,"
					+ orgnfInfo.getCommaSplitFileheader(), ",");
			SplitResult spResult = fs.splitFile(
					orgnfInfo.getDatafileFullPath(),
					SubscriberLoader.Split_File_Count);
			orgnfInfo.setOriginalFileTotalRowCount(spResult.getTotalRowCount());

			String[] smallFileNames = spResult.getSmallFileNames();
			int[] smallFileRowNumbers = spResult.getSmallFileRowCounts();

			for (int i = 0; i < smallFileNames.length; i++) {
				String splitfilename = smallFileNames[i];
				SplitFileInfo spfInfo = new SplitFileInfo();
				spfInfo.setSplitfilename(splitfilename);
				spfInfo.setLastRowProcessed(0);

				spfInfo.setTotalCount(smallFileRowNumbers[i]);
				spfInfo.setSuccessCount(0);
				spfInfo.setFailCount(0);
				// We're going to use the split file. Set their status all to
				// Processing
				spfInfo.setProcessStatus(FeedSplitFileTableBean.Processing);

				listSplitFile.add(spfInfo);
			}
		} catch (Exception e) {
			LOGGER.error(CommUtil.format(
					"Splitting exception. Reject this file--{0}", orgnfInfo), e);
			listSplitFile = null;
		}
		LOGGER.info(CommUtil.format("Splitting ends, cost:{0}--{1}",
				MemoryUsage.human(System.currentTimeMillis() - begin),
				orgnfInfo));
		return listSplitFile;
	}
}
