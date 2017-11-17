package com.syniverse.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.common.CommUtil;
import com.syniverse.common.MemoryUsage;
import com.syniverse.info.ColumnDescInfo;
import com.syniverse.info.DBColumnsInfo;
import com.syniverse.info.EachRowInfo;
import com.syniverse.info.PK;
import com.syniverse.rti.csp.validator.ActionType;
import com.syniverse.rti.csp.validator.Validator;

public class DBManipulate {
	private static final Log LOGGER = LogFactory.getLog(DBManipulate.class);
	public static final String COMMA = ",";
	public static final String BATCH_TYPE = "BATCH";

	public static Set<PK> put2SetIfExist(Validator vCheckExistence,
			Connection conn, PreparedStatement pstmtCheckExistence,
			List<EachRowInfo> passValidation, String billingID) {
		Set<PK> setExistInDB = new HashSet<PK>();
		if (passValidation.size() == 0) {
			return setExistInDB;
		}
		String[] arraySubkeys = new String[passValidation.size()];
		for (int i = 0; i < passValidation.size(); i++) {
			arraySubkeys[i] = passValidation.get(i).getSubscriberKey();
		}
		try {
			long begin = System.currentTimeMillis();
			boolean[] arrayExist = vCheckExistence.areRecordsExist(conn,
					pstmtCheckExistence, arraySubkeys);
			long end = System.currentTimeMillis();

			LOGGER.info(CommUtil.format(
					"subkeys count [{0}], areRecordsExist cost: {1}",
					passValidation.size(), MemoryUsage.human(end - begin)));

			for (int i = 0; i < arrayExist.length; i++) {
				if (arrayExist[i] == true) {
					setExistInDB.add(PK.create(billingID, arraySubkeys[i]));
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error when put2SetIfExist", e);
			throw new RuntimeException(
					"Could not check passValidation existence", e);
		}
		LOGGER.info(CommUtil.format(
				"subkeys count-->already in DB count [{0}]-->[{1}]",
				passValidation.size(), setExistInDB.size()));
		return setExistInDB;
	}

	/**
	 * Fetch delimiter by billingID&type
	 * 
	 * (at any time, we always only have one delimiter, delimiter has nothing to
	 * do with listID or versionNO)
	 * 
	 * @param conn
	 * @param billingID
	 * @param type
	 * @return
	 */
	public static String fetchDelimiter(Connection conn, String billingID,
			String type) {
		String delimiter = COMMA;
		String sql = "select DELIMITER from attribute_config where billing_id=? and type=?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, billingID);
			pstmt.setString(2, type);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				delimiter = rs.getString("DELIMITER");
			}
		} catch (Exception e) {
			LOGGER.error("Error when fetchDelimiter", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return delimiter;
	}

	public static boolean isBillingIDExistInOperator(Connection conn,
			String billingID) {
		String sql = "SELECT BILLING_ID FROM OPERATOR_CONFIG WHERE BILLING_ID=?";
		PreparedStatement pstmt = null;
		boolean bExist = false;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, billingID);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				bExist = true;
			}
		} catch (Exception e) {
			LOGGER.error("Error when isBillingIDExistInOperator", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return bExist;
	}

	public static String fetchCommaSplitPrimaryKeyColumns(Connection conn) {
		return DBColumnsInfo.BILLING_ID + "," + DBColumnsInfo.SUBSCRIBER_KEY;
	}

	/**
	 * Fetch NEWEST versionNO by billingID&type
	 * 
	 * @param conn
	 * @param billingID
	 * @param type
	 * @return
	 */
	public static int fetchVersionNum(Connection conn, String billingID,
			String type) {
		String sql = "select VERSION_NUM from attribute_config where billing_id=? and type=?";
		LOGGER.info("fetchVersionNum sql:" + sql);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, billingID);
			pstmt.setString(2, type);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				return (int) rs.getLong("VERSION_NUM");
			}
		} catch (Exception e) {
			LOGGER.error("Error when fetchVersionNum", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return -1;
	}

	/**
	 * Fetch NEWEST listID by billingID&type
	 * 
	 * @param conn
	 * @param billingID
	 * @param type
	 * @return
	 */
	public static String fetchStrListID(Connection conn, String billingID,
			String type) {
		String sql = "select LIST_ID from attribute_config where billing_id=? and type=?";
		LOGGER.info("fetchStrListID sql:" + sql);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, billingID);
			pstmt.setString(2, type);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				return rs.getString("LIST_ID");
			}
		} catch (Exception e) {
			LOGGER.error("Error when fetchStrListID", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return null;
	}

	public static List<FeedLogTableBean> selectCandidateFiles(Connection conn) {
		List<FeedLogTableBean> listCandidate = new ArrayList<FeedLogTableBean>();
		String sql = "select LOG_ID, BILLING_ID, LIST_ID, VERSION_NUM,FEED_SOURCE,NAME,FEED_STATUS,TOTAL_ROW_COUNT,PROCESS_SUCCESS_COUNT,PROCESS_FAIL_COUNT from FEED_LOG where FEED_STATUS='Queued' or FEED_STATUS='Processing' order by LOG_ID";
		LOGGER.info("selectCandidateFiles sql:" + sql);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				FeedLogTableBean flInfo = new FeedLogTableBean();
				flInfo.setLogID(rs.getLong("LOG_ID"));
				flInfo.setBillingID(rs.getString("BILLING_ID"));
				flInfo.setListID(rs.getString("LIST_ID"));
				flInfo.setVersionNO(rs.getInt("VERSION_NUM"));
				flInfo.setFeedSource(rs.getString("FEED_SOURCE"));
				flInfo.setFilenameInDB(rs.getString("NAME"));
				flInfo.setProcessStatus(rs.getString("FEED_STATUS"));
				flInfo.setTotalRow((int) rs.getLong("TOTAL_ROW_COUNT"));
				flInfo.setSuccessRow((int) rs.getLong("PROCESS_SUCCESS_COUNT"));
				flInfo.setFailRow((int) rs.getLong("PROCESS_FAIL_COUNT"));
				listCandidate.add(flInfo);
			}
		} catch (Exception e) {
			LOGGER.error("Error when selectCandidateFiles", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return listCandidate;
	}

	public static List<FeedSplitFileTableBean> selectSplitFiles(
			Connection conn, long logID) {
		List<FeedSplitFileTableBean> listCandidate = new ArrayList<FeedSplitFileTableBean>();
		String sql = "select STATUS, LAST_ROW_PROCESSED, SPLIT_FILE_NAME, SPLIT_TOTAL_ROW_COUNT, SPLIT_PROCESS_SUCCESS_COUNT,SPLIT_PROCESS_FAIL_COUNT from FEED_SPLIT_FILE where LOG_ID= ? order by LOG_ID";
		LOGGER.info("selectSplitFiles sql:" + sql);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, logID);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				FeedSplitFileTableBean fsplitInfo = new FeedSplitFileTableBean();
				fsplitInfo.setLogID(logID);
				fsplitInfo.setProcessStatus(rs.getString("STATUS"));
				fsplitInfo.setLastRowProcessed((int) rs
						.getLong("LAST_ROW_PROCESSED"));
				fsplitInfo.setFilename(rs.getString("SPLIT_FILE_NAME"));
				fsplitInfo.setTotalCount((int) rs
						.getLong("SPLIT_TOTAL_ROW_COUNT"));
				fsplitInfo.setSuccessCount((int) rs
						.getLong("SPLIT_PROCESS_SUCCESS_COUNT"));
				fsplitInfo.setFailCount((int) rs
						.getLong("SPLIT_PROCESS_FAIL_COUNT"));

				listCandidate.add(fsplitInfo);
			}
		} catch (Exception e) {
			LOGGER.error("Error when selectSplitFiles", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return listCandidate;
	}

	public static int getSplitFileCount(Connection conn, long logID) {
		String sql = "select count(*) from FEED_SPLIT_FILE where LOG_ID=?";
		LOGGER.info("getSplitFileCount sql:" + sql);
		PreparedStatement pstmt = null;
		int spltCnt = 0;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, logID);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				spltCnt = rs.getInt(1);
			}
		} catch (Exception e) {
			LOGGER.error("Error when getSplitFileCount", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return spltCnt;
	}

	public static boolean isDatefeedEnabled(Connection conn, String billingID) {
		String sql = "select ENABLE_DATAFEED from OPERATOR_CONFIG where BILLING_ID=?";
		LOGGER.info("isDatefeedEnabled sql:" + sql);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, billingID);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String strEnable = rs.getString(1);
				return strEnable.equalsIgnoreCase("Y");
			}
		} catch (Exception e) {
			LOGGER.error("Error when isDatefeedEnabled", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return false;
	}

	/**
	 * Note: For our loader(attribute_config.type==BATCH)
	 * <p>
	 * <b>value of (DB column attribute_list.attr_list) always begins with
	 * "Action"</b>
	 * <p>
	 * Get DB value of (DB column attribute_list.attr_list)
	 * 
	 * 
	 * @param conn
	 * @param type
	 * @param billingID
	 * @return
	 */
	// private static String fetchCommaSplitAttrInDB(Connection conn, String
	// type,
	// String billingID) {
	// String sql = "select attr_list from attribute_list where"
	// +
	// " list_id=(select list_id from attribute_config where billing_id=? and type=?)"
	// +
	// " and version_num=(select version_num from attribute_config where billing_id=? and type=?)";
	// LOGGER.info("fetchCommaSplitAttrListInDB sql:" + sql);
	// String attrList = null;
	// PreparedStatement pstmt = null;
	// try {
	// pstmt = conn.prepareStatement(sql);
	// pstmt.setString(1, billingID);
	// pstmt.setString(2, type);
	// pstmt.setString(3, billingID);
	// pstmt.setString(4, type);
	//
	// ResultSet rs = pstmt.executeQuery();
	// while (rs.next()) {
	// attrList = rs.getString("attr_list");
	// }
	// } catch (Exception e) {
	// LOGGER.error("Error when fetchAttrList", e);
	// } finally {
	// DBUtil.closeStmt(pstmt);
	// }
	// return attrList.toUpperCase();
	// }

	private static String fetchCommaSplitAttrInDB(Connection conn,
			String listID, int versionNO) {
		String sql = "select attr_list from attribute_list where LIST_ID=? and VERSION_NUM=?";
		LOGGER.info("fetchCommaSplitAttrInDB sql:" + sql);
		String attrList = null;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, listID);
			pstmt.setInt(2, versionNO);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				attrList = rs.getString("attr_list");
			}
		} catch (Exception e) {
			LOGGER.error("Error when fetchAttrList", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return attrList.toUpperCase();
	}

	// public static String fetchCommaSplitAttrInDBWithoutAction(Connection
	// conn,
	// String type, String billingID) {
	// String commaSplitAttrListInDB = fetchCommaSplitAttrInDB(conn, type,
	// billingID);
	// return CommUtil.eliminateLeadingAction(commaSplitAttrListInDB);
	// }

	public static String fetchCommaSplitAttrInDBWithoutAction(Connection conn,
			String listID, int versionNO) {
		String commaSplitAttrListInDB = fetchCommaSplitAttrInDB(conn, listID,
				versionNO);
		return CommUtil.eliminateLeadingAction(commaSplitAttrListInDB);
	}

	private static String quoteEachAttr(String commaSplitAttrList) {
		List<String> list = CommUtil.split(commaSplitAttrList, ",");
		int size = list.size();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < size; i++) {
			if (i == (size - 1)) {
				sb.append("'" + list.get(i) + "'");
			} else {
				sb.append("'" + list.get(i) + "',");
			}
		}
		return sb.toString();
	}

	public static HashMap<String, String> fetchDisplayName(Connection conn,
			String commaSplitAttrList, String billingID) {
		HashMap<String, String> map = new HashMap<String, String>();
		String quoteAttrList = quoteEachAttr(commaSplitAttrList);
		LOGGER.info("Original commaSplitAttrList(not include action): "
				+ commaSplitAttrList);
		LOGGER.info("Quoted commaSplitAttrList(not include action): "
				+ quoteAttrList);

		String sql = "select ATTR_COLUMN_NAME, ATTR_DISPLAY_NAME from ATTRIBUTE where BILLING_ID=? and CUSTOM_ATTR='N' and ATTR_COLUMN_NAME in ("
				+ quoteAttrList
				+ ")"
				+ " union "
				+ "select ATTR_COLUMN_NAME, ATTR_DISPLAY_NAME from ATTRIBUTE where BILLING_ID=? and CUSTOM_ATTR='Y' and ACTIVE='Y' and ATTR_COLUMN_NAME in ("
				+ quoteAttrList + ")";

		LOGGER.info("fetchDisplayName sql:" + sql);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, billingID);
			pstmt.setString(2, billingID);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				map.put(rs.getString("ATTR_COLUMN_NAME"),
						rs.getString("ATTR_DISPLAY_NAME"));
			}
		} catch (Exception e) {
			LOGGER.error("Error when fetchDisplayName", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return map;
	}

	public static HashMap<String, String> displayName2AttrName(
			HashMap<String, String> mapAttr2Display) {
		HashMap<String, String> mapDisplay2Attr = new HashMap<String, String>(
				mapAttr2Display.size());
		Iterator<String> it = mapAttr2Display.keySet().iterator();
		while (it.hasNext()) {
			String attr = it.next();
			mapDisplay2Attr.put(mapAttr2Display.get(attr), attr);
		}
		return mapDisplay2Attr;
	}

	public static boolean setValues(PreparedStatement pstmt, EachRowInfo erInfo) {

		try {
			if (erInfo.getAction() == ActionType.INSERT) {
				int allValueCount = DBColumnsInfo.allColumns.size();
				int parameterIndex = 1;
				for (int i = 0; i < allValueCount; i++) {
					Object v = erInfo.allValue.get(i);
					int type = DBColumnsInfo.allTypes.get(i).sqlType;

					pstmt.setObject(parameterIndex, v, type);
					parameterIndex++;
				}
				return true;
			}
			int pkValueCount = erInfo.pkValues.size();
			if (erInfo.getAction() == ActionType.UPDATE) {
				int allValueCount = DBColumnsInfo.allColumns.size();
				int parameterIndex = 1;
				for (int i = 0; i < allValueCount; i++) {
					Object v = erInfo.allValue.get(i);
					int type = DBColumnsInfo.allTypes.get(i).sqlType;
					pstmt.setObject(parameterIndex, v, type);
					parameterIndex++;
				}
				for (int i = 0; i < pkValueCount; i++) {
					Object v = erInfo.pkValues.get(i);
					int type = DBColumnsInfo.pkTypes.get(i).sqlType;

					pstmt.setObject(parameterIndex, v, type);
					parameterIndex++;
				}
				return true;
			}
			if (erInfo.getAction() == ActionType.DELETE) {
				int parameterIndex = 1;
				for (int i = 0; i < pkValueCount; i++) {
					pstmt.setObject(parameterIndex, erInfo.pkValues.get(i),
							DBColumnsInfo.pkTypes.get(i).sqlType);
					parameterIndex++;
				}
				return true;
			}
		} catch (SQLException e) {
			LOGGER.error("Error when setValues. Rethrow this exception", e);
			throw new RuntimeException("Error when setValue", e);
		}
		return false;
	}

	public static boolean insertFeedLog(Connection conn, FeedLogTableBean flInfo) {
		boolean bSuccess = false;
		String sql = "insert into FEED_LOG (LOG_ID,BILLING_ID,LIST_ID,VERSION_NUM,FEED_SOURCE,TYPE,NAME,FEED_DATE,FEED_STATUS,FEED_START_TIME,FEED_END_TIME,UPDATED_TIMESTAMP,TOTAL_ROW_COUNT,PROCESS_SUCCESS_COUNT,PROCESS_FAIL_COUNT,UPLOADED_BY,FAIL_REASON) values (FEED_LOG_SEQ.nextval, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, flInfo.getBillingID());
			pstmt.setString(2, flInfo.getListID());
			pstmt.setInt(3, flInfo.getVersionNO());
			pstmt.setString(4, flInfo.getFeedSource());
			pstmt.setString(5, flInfo.getType());
			pstmt.setString(6, flInfo.getFilenameInDB());
			pstmt.setTimestamp(7, new Timestamp(flInfo.getReceiveTime()));
			pstmt.setString(8, flInfo.getProcessStatus());

			// FEED_START_TIME
			if (flInfo.getStartTime() < 0) {
				pstmt.setNull(9, java.sql.Types.TIMESTAMP);
			} else {
				pstmt.setTimestamp(9, new Timestamp(flInfo.getStartTime()));
			}
			// FEED_END_TIME
			if (flInfo.getEndTime() < 0) {
				pstmt.setNull(10, java.sql.Types.TIMESTAMP);
			} else {
				pstmt.setTimestamp(10, new Timestamp(flInfo.getEndTime()));
			}

			pstmt.setTimestamp(11, new Timestamp(flInfo.getUpdatedTime()));
			pstmt.setLong(12, flInfo.getTotalRow());
			pstmt.setLong(13, flInfo.getSuccessRow());
			pstmt.setLong(14, flInfo.getFailRow());
			pstmt.setString(15, flInfo.getUploadedby());
			// FAIL_REASON
			if (flInfo.getFailReason() == null) {
				pstmt.setNull(16, java.sql.Types.VARCHAR);
			} else {
				pstmt.setString(16, flInfo.getFailReason());
			}
			pstmt.executeUpdate();
			bSuccess = true;
		} catch (Exception e) {
			LOGGER.error("Error when insertFeedLog", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}

		return bSuccess;
	}

	public static boolean increaseSuccessFail(Connection conn,
			long incrSuccess, long incrFail, long logID) {
		boolean bSuccess = false;
		String sql = "update FEED_LOG set PROCESS_SUCCESS_COUNT=PROCESS_SUCCESS_COUNT+?,PROCESS_FAIL_COUNT=PROCESS_FAIL_COUNT+?, UPDATED_TIMESTAMP=sysdate where LOG_ID=?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, incrSuccess);
			pstmt.setLong(2, incrFail);
			pstmt.setLong(3, logID);
			pstmt.executeUpdate();
			bSuccess = true;
		} catch (Exception e) {
			LOGGER.error("Error when increaseSuccessFail", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return bSuccess;
	}

	public static boolean insertFeedSplitLog(Connection conn,
			List<FeedSplitFileTableBean> listFeedSplitInfo) {
		boolean bSuccess = false;
		String sql = "insert into FEED_SPLIT_FILE (LOG_ID,STATUS,LAST_ROW_PROCESSED,SPLIT_FILE_NAME,SPLIT_TOTAL_ROW_COUNT,SPLIT_PROCESS_SUCCESS_COUNT,SPLIT_PROCESS_FAIL_COUNT,UPDATED_TIMESTAMP) values (?,?,?,?,?,?,?,sysdate)";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			for (FeedSplitFileTableBean oneSplitInfo : listFeedSplitInfo) {

				pstmt.setLong(1, oneSplitInfo.getLogID());// LOG_ID
				pstmt.setString(2, oneSplitInfo.getProcessStatus());// STATUS
				pstmt.setLong(3, oneSplitInfo.getLastRowProcessed());// LAST_ROW_PROCESSED
				pstmt.setString(4, oneSplitInfo.getFilename());// SPLIT_FILE_NAME
				pstmt.setLong(5, oneSplitInfo.getTotalCount());// SPLIT_TOTAL_ROW_COUNT
				pstmt.setLong(6, oneSplitInfo.getSuccessCount());// SPLIT_PROCESS_SUCCESS_COUNT
				pstmt.setLong(7, oneSplitInfo.getFailCount());// SPLIT_PROCESS_FAIL_COUNT

				pstmt.addBatch();
			}
			pstmt.executeBatch();
			bSuccess = true;
		} catch (Exception e) {
			LOGGER.error("Error when insertFeedSplitLog", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return bSuccess;
	}

	public static boolean updateLastSucessFail(Connection conn,
			long lastProcessed, long nowSuccess, long nowFail,
			String splitfilename, long logID) {
		boolean bSuccess = false;
		String sql = "update FEED_SPLIT_FILE set LAST_ROW_PROCESSED=?, SPLIT_PROCESS_SUCCESS_COUNT=?, SPLIT_PROCESS_FAIL_COUNT=?, UPDATED_TIMESTAMP=sysdate where LOG_ID=? and SPLIT_FILE_NAME=?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, lastProcessed);
			pstmt.setLong(2, nowSuccess);
			pstmt.setLong(3, nowFail);
			pstmt.setLong(4, logID);
			pstmt.setString(5, splitfilename);
			pstmt.executeUpdate();
			bSuccess = true;
		} catch (Exception e) {
			LOGGER.error("Error when updateLastSucessFail", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return bSuccess;
	}

	public static boolean updateSplitStatus(Connection conn,
			String splitProcessStatus, String splitfilename, long logID) {
		boolean bSuccess = false;
		String sql = "update FEED_SPLIT_FILE set STATUS=?,UPDATED_TIMESTAMP=sysdate where LOG_ID=? and SPLIT_FILE_NAME=?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, splitProcessStatus);
			pstmt.setLong(2, logID);
			pstmt.setString(3, splitfilename);
			pstmt.executeUpdate();
			bSuccess = true;
		} catch (Exception e) {
			LOGGER.error("Error when updateSplitStatus", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return bSuccess;
	}

	public static boolean update(Connection conn, String tableName,
			String[] fields, Object[] fieldValues, String[] keys,
			Object[] keyValues, int[] sqltype) {
		assert fields.length == fieldValues.length;
		assert keys.length == keyValues.length;
		assert (fieldValues.length + keyValues.length) == sqltype.length;

		StringBuilder sb = new StringBuilder("update " + tableName + " set ");
		CommUtil.concat(sb, fields, "=?,", "=?");
		sb.append(" where ");
		CommUtil.concat(sb, keys, "=? and ", "=?");
		LOGGER.info("Update sql: " + sb.toString());

		boolean bSuccess = false;
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sb.toString());
			int parameterIndex = 1;
			for (int i = 0; i < fieldValues.length; i++) {
				pstmt.setObject(parameterIndex, fieldValues[i],
						sqltype[parameterIndex - 1]);
				parameterIndex++;
			}
			for (int i = 0; i < keyValues.length; i++) {
				pstmt.setObject(parameterIndex, keyValues[i],
						sqltype[parameterIndex - 1]);
				parameterIndex++;
			}
			pstmt.executeUpdate();
			bSuccess = true;
		} catch (Exception e) {
			LOGGER.error("Error when update", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return bSuccess;
	}

	public static boolean updateFeedLog(long logID, Connection conn,
			String processStatus, long totalCount) {
		boolean bSuccess = false;
		// update table FEED_LOG.TOTAL_ROW_COUNT
		String sql = "update FEED_LOG set FEED_STATUS=?,TOTAL_ROW_COUNT=?,UPDATED_TIMESTAMP=sysdate where LOG_ID=?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, processStatus);// FEED_STATUS
			pstmt.setLong(2, totalCount);// TOTAL_ROW_COUNT
			pstmt.setLong(3, logID);// LOG_ID
			pstmt.executeUpdate();
			bSuccess = true;
		} catch (Exception e) {
			LOGGER.error("Error when updateFeedLog", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return bSuccess;
	}

	public static boolean updateStartDate(long logID, Connection conn,
			long startTime) {
		boolean bSuccess = false;
		String sql = "update FEED_LOG set FEED_START_TIME=?,UPDATED_TIMESTAMP=sysdate where LOG_ID=?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			// FEED_START_TIME
			if (startTime < 0) {
				pstmt.setNull(1, java.sql.Types.TIMESTAMP);
			} else {
				pstmt.setTimestamp(1, new Timestamp(startTime));
			}
			pstmt.setLong(2, logID);// LOG_ID
			pstmt.executeUpdate();
			bSuccess = true;
		} catch (Exception e) {
			LOGGER.error("Error when updateStartDate", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return bSuccess;
	}

	public static void putColumnDescToList(Connection conn,
			String commaSplitAttrList, List<ColumnDescInfo> listColumnDesc,
			String tableName) {
		StringBuilder sb = new StringBuilder();
		sb.append("select ");
		sb.append(commaSplitAttrList);
		sb.append(" from ");
		sb.append(tableName);
		sb.append(" where 1!=1");

		LOGGER.info("fillColumnDesc sql: " + sb.toString());
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sb.toString());
			ResultSetMetaData rs = pstmt.getMetaData();
			int columnCount = rs.getColumnCount();
			for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
				ColumnDescInfo cdInfo = new ColumnDescInfo(
						rs.getColumnName(columnIndex), // columnName
						rs.getColumnDisplaySize(columnIndex),// displaySize
						rs.getColumnClassName(columnIndex),// fullyQualifiedJavaType
						rs.getColumnType(columnIndex),// sqlType
						rs.getColumnTypeName(columnIndex),// dbSpecificColumnTypeName
						rs.isNullable(columnIndex),// nullable
						rs.getPrecision(columnIndex),// precision
						rs.getScale(columnIndex)// scale
				);
				listColumnDesc.add(cdInfo);
			}
		} catch (Exception e) {
			LOGGER.error("Error when fillTypes", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
	}

	public static List<String> fetchSubscriberColumns(Connection conn) {
		List<String> listColNames = new ArrayList<String>();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn
					.prepareStatement("Select * from SUBSCRIBER where 1!=1");
			ResultSetMetaData rs = pstmt.getMetaData();
			int columnCount = rs.getColumnCount();
			for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
				listColNames.add(rs.getColumnName(columnIndex).toUpperCase());
			}
		} catch (Exception e) {
			LOGGER.error("Error when fetchSubscriberColumns", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return listColNames;
	}

	public static int[] getSuccessFail(Connection conn, long logID) {
		String sql = "select PROCESS_SUCCESS_COUNT, PROCESS_FAIL_COUNT from FEED_LOG where LOG_ID=?";
		LOGGER.info("getSuccessFail sql:" + sql);
		int[] arraySuccessFail = new int[] { 0, 0 };
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, logID);
			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				arraySuccessFail[0] = (int) rs.getLong("PROCESS_SUCCESS_COUNT");
				arraySuccessFail[1] = (int) rs.getLong("PROCESS_FAIL_COUNT");
			}
		} catch (Exception e) {
			LOGGER.error("Error when getSuccessFail", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return arraySuccessFail;
	}

	public static HashMap<String, String> selectDBConfiguration(
			String[] allParaKeys, Connection conn, String[] allLoaderConfigType) {
		HashMap<String, String> map = new HashMap<String, String>();
		StringBuilder sb = new StringBuilder(
				"select KEYID, KEYVALUE from APP_CONFIG where CONFIG_TYPE in('");
		CommUtil.concat(sb, allLoaderConfigType, "','", "')");
		sb.append(" and KEYID in('");
		CommUtil.concat(sb, allParaKeys, "','", "')");

		LOGGER.info("selectDBConfiguration: " + sb.toString());

		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sb.toString());

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				map.put(rs.getString("KEYID"), rs.getString("KEYVALUE"));
			}
		} catch (Exception e) {
			LOGGER.error("Error when selectDBConfiguration", e);
			throw new RuntimeException(
					"Cannot lookup Loader configuration info", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return map;
	}

	public static void main(String[] args) {
		String tableName = "subscriber";
		String[] fields = new String[] { "aaa", "bbb", "ccc" };
		Object[] fieldValues = new Object[] { "av", "bv", "cv" };
		String[] keys = new String[] { "kkk1", "kkk2", "kkk3" };
		Object[] keyValues = new Object[] { "kv1", "kv2", "kv3" };
		int[] sqltype = new int[] { 1, 2, 3, 4, 5, 6 };

		update(null, tableName, fields, fieldValues, keys, keyValues, sqltype);
	}
}
