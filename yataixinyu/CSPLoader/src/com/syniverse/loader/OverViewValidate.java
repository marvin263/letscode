package com.syniverse.loader;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.common.CommUtil;
import com.syniverse.db.DBManipulate;
import com.syniverse.info.OriginalFileInfo;
import com.syniverse.info.OverviewValidateInfo;
import com.syniverse.io.IOUtil;

public class OverViewValidate {
	private static final Log LOGGER = LogFactory.getLog(OverViewValidate.class);
	public static final String FIRST_FILE_COLUMN = "ACTION";
	private final Connection conn;
	private final OriginalFileInfo orgnfInfo;

	public OverViewValidate(Connection conn, OriginalFileInfo orgnfInfo) {
		this.conn = conn;
		this.orgnfInfo = orgnfInfo;
	}

	public OverviewValidateInfo validate() {
		OverviewValidateInfo ovfi = new OverviewValidateInfo();
		ovfi.setPassed(false);
		if (stop4DatafeedFlag(conn, orgnfInfo) == true) {
			ovfi.setFailReason(OverviewValidateInfo.FAIL_REASON_datafeed_disabled);
			ovfi.setSuggestion(OverviewValidateInfo.SUGGESTION_datafeed_disabled);
			return ovfi;
		}
		String uppercaserFileHeader = IOUtil.readUppercaseFirstLine(
				orgnfInfo.getDatafileFullPath(), null);
		if (uppercaserFileHeader == null) {
			ovfi.setFailReason(OverviewValidateInfo.FAIL_REASON_lack_file_header);
			ovfi.setSuggestion(OverviewValidateInfo.SUGGESTION_lack_file_header);
			return ovfi;
		}
		if (isActionFlagExist(uppercaserFileHeader) == false) {
			ovfi.setFailReason(OverviewValidateInfo.FAIL_REASON_lack_action_flag);
			ovfi.setSuggestion(OverviewValidateInfo.SUGGESTION_lack_action_flag);
			return ovfi;
		}

		String realFailRsn = isAllProvidedColAllowed(orgnfInfo.getBillingID(),
				uppercaserFileHeader, orgnfInfo.getDelimiter());
		if (CommUtil.isEmpty(realFailRsn) == false) {
			ovfi.setFailReason(SubscriberLoader
					.asmbFailRsn(
							OverviewValidateInfo.FAIL_REASON_file_header_not_match_dbconfig,
							realFailRsn));
			ovfi.setSuggestion(OverviewValidateInfo.SUGGESTION_file_header_not_match_dbconfig);
			return ovfi;
		}

		ovfi.setPassed(true);
		ovfi.setFailReason(null);
		ovfi.setSuggestion(null);
		return ovfi;
	}

	private boolean stop4DatafeedFlag(Connection conn,
			OriginalFileInfo orgnfInfo) {
		int splitCount = DBManipulate.getSplitFileCount(conn,
				orgnfInfo.getLogID());
		// already in processing. Finish it regardless of datafeed is Y or N
		if (splitCount > 0) {
			// NOT stop
			return false;
		}
		// NOT in processing. check datafeed flag
		if (DBManipulate.isDatefeedEnabled(conn, orgnfInfo.getBillingID()) == false) {
			// datafeed disabled. Should stop
			return true;
		} else {
			// datafeed enabled. NOT stop
			return false;
		}
	}

	private boolean isActionFlagExist(String firstLine) {
		// firstLine is empty
		if (CommUtil.isEmpty(firstLine)) {
			return false;
		}
		if (firstLine.trim().toUpperCase().startsWith(FIRST_FILE_COLUMN)) {
			return true;
		}
		return false;
	}

	private String isAllProvidedColAllowed(String billingID,
			String uppercaseFirstFileRow,
			String delimiter_ShouldUse_deduced_from_DB) {
		// File header delimiter is wrong
		if (uppercaseFirstFileRow.indexOf(delimiter_ShouldUse_deduced_from_DB) == -1) {
			String realRsn = CommUtil
					.format("File header delimiter is wrong. Fileheader={0}, but correct delimiter should be={1}",
							uppercaseFirstFileRow,
							delimiter_ShouldUse_deduced_from_DB);
			LOGGER.error(realRsn);
			return realRsn;
		}
		// First element is action, let's remove it
		List<String> listFileheaderWithoutAction = CommUtil.split(
				uppercaseFirstFileRow, delimiter_ShouldUse_deduced_from_DB);
		listFileheaderWithoutAction.remove(0);
		LOGGER.info("Headers from file (no action): "
				+ listFileheaderWithoutAction);
		// external
		if (orgnfInfo.getFeedSource().equals(
				OriginalFileInfo.FEED_SOURCE_EXTERNAL)) {

			String commaSplitAttrList = orgnfInfo.getCommaSplitFileheader();
			LOGGER.info("Column names from ATTRIBUTE_LIST.ATTR_LIST (no action): "
					+ commaSplitAttrList);

			List<String> dbColHeader = CommUtil.split(commaSplitAttrList, ",");

			if (listFileheaderWithoutAction.size() != dbColHeader.size()) {
				String realRsn = CommUtil
						.format("File header is not consistent with ATTRIBUTE_LIST.ATTR_LIST. Fileheader={0}, ATTRIBUTE_LIST.ATTR_LIST={1}",
								listFileheaderWithoutAction.toString(),
								dbColHeader.toString());
				LOGGER.error(realRsn);
				return realRsn;
			}

			HashMap<String, String> mapColumn2Display = DBManipulate
					.fetchDisplayName(conn, commaSplitAttrList, billingID);

			for (int i = 0; i < dbColHeader.size(); i++) {
				String dbDisplay = mapColumn2Display.get(dbColHeader.get(i));
				String fileDisplay = listFileheaderWithoutAction.get(i);
				if (!fileDisplay.equalsIgnoreCase(dbDisplay)) {
					String realRsn = CommUtil
							.format("Display name is wrong. Fileheader display name={0}, db display name={1}",
									fileDisplay, dbDisplay);
					LOGGER.error(realRsn);
					return realRsn;
				}
			}
		}
		// internal
		else if (orgnfInfo.getFeedSource().equals(
				OriginalFileInfo.FEED_SOURCE_INTERNAL)) {
			List<String> listAllColum = DBManipulate
					.fetchSubscriberColumns(conn);
			if (listAllColum.containsAll(listFileheaderWithoutAction) == false) {
				String realRsn = CommUtil
						.format("Internal file header should be all from db column name, but there are some ones cannot be found in db column name. Fileheader={0}",
								listFileheaderWithoutAction.toString());
				LOGGER.error(realRsn);
				return realRsn;
			}
		}
		return null;
	}

	/**
	 * 
	 * No matter shrinking or growing, we can handle both. But just keep this
	 * method
	 * 
	 * @param orgnfInfo
	 * @return
	 */
	private boolean configuredCountShrink(OriginalFileInfo orgnfInfo) {
		int alreadySplitToNumber = DBManipulate.getSplitFileCount(conn,
				orgnfInfo.getLogID());
		int configuredSplitCount = SubscriberLoader.Split_File_Count;
		// Has not yet been split, we can always handle this. Good
		if (alreadySplitToNumber == 0) {
			return true;
		}
		// alreadySplitToNumber is not 0, and it less than (equal to)
		if (alreadySplitToNumber <= configuredSplitCount) {
			return true;
		}
		// (alreadySplitToNumber > configuredSplitCount).
		// At this time, we have more already split files. Fail
		else {
			return false;
		}
	}

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("adfdf");
		list.add("b");
		list.add("d");
		System.out.println(list.toString());
	}
}
