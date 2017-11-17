package com.syniverse.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.common.CommUtil;
import com.syniverse.info.AbstractFileInfo;
import com.syniverse.info.EachRowInfo;
import com.syniverse.info.PK;
import com.syniverse.info.SubkeyAndTypeInfo;

public class DBManipulate {
	private static final Log LOGGER = LogFactory.getLog(DBManipulate.class);
	public static final String Updated_by = "Updated_by";

	/**
	 * Get existing records by <code>passValidation</code> and
	 * <code>groupID</code>
	 * 
	 * @param conn
	 * @param pstmt
	 * @param passValidation
	 * @param groupID
	 * @return
	 */
	public static Set<PK> put2SetIfExist(Connection conn,
			PreparedStatement pstmt, List<EachRowInfo> passValidation,
			long groupID) {
		Map<Integer, ArrayList<String>> mapType2Subkey = new HashMap<Integer, ArrayList<String>>();
		for (EachRowInfo erInfo : passValidation) {
			Integer subkeyType = Integer.valueOf(erInfo.getSubkeyType());
			ArrayList<String> listSubkey = mapType2Subkey.get(subkeyType);
			if (listSubkey == null) {
				listSubkey = new ArrayList<String>();
				mapType2Subkey.put(subkeyType, listSubkey);
			}
			listSubkey.add(erInfo.subkey);
		}

		Set<PK> keys = new HashSet<PK>();
		Iterator<Entry<Integer, ArrayList<String>>> it = mapType2Subkey
				.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<Integer, ArrayList<String>> entry = (Map.Entry<Integer, ArrayList<String>>) it
					.next();
			Integer subkeyType = entry.getKey();
			ArrayList<String> listSubkey = entry.getValue();

			String[] arraySubkey = listSubkey.toArray(new String[0]);
			keys.addAll(put2SetIfExist(groupID, conn, pstmt,
					subkeyType.intValue(), arraySubkey));
		}
		return keys;
	}

	public static Set<PK> put2SetIfExist(long groupID, Connection conn,
			PreparedStatement pstmt, int subkeyType, String[] arraySubkey) {
		int keysLength = arraySubkey.length;
		int batchSize = 1000;
		int remainder = keysLength % batchSize;
		int loopCount = (keysLength / batchSize) + 1;

		Set<PK> keys = new HashSet<PK>();

		Statement stmt = null;
		try {
			pstmt.setLong(1, groupID);
			pstmt.setInt(2, subkeyType);
			int firstInIndex = 3;
			// last in-parameter index, inclusive
			int lastInIndex = firstInIndex + (batchSize - 1);
			ResultSet rs = null;

			for (int curLoop = 0; curLoop < loopCount; curLoop++) {
				int fromOfCurloop = curLoop * batchSize;
				// do by PreparedStatement
				if (curLoop < loopCount - 1) {
					for (int inIndex = firstInIndex, subkeyIndex = fromOfCurloop; inIndex <= lastInIndex; inIndex++, subkeyIndex++) {
						pstmt.setString(inIndex, arraySubkey[subkeyIndex]);
					}

					rs = pstmt.executeQuery();
				}
				// last loop should be done by Statement
				else {
					// all has been done by PreparedStatement.
					// Do not need Statement
					if (remainder == 0) {
						rs = null;
					} else {
						String[] arrayLeft = new String[0];
						System.arraycopy(arraySubkey, fromOfCurloop, arrayLeft,
								0, (keysLength - fromOfCurloop));
						StringBuilder sb = new StringBuilder(
								"select SUBKEY from T_PCS_XREF_GRP_SUB ");
						sb.append(" where GROUP_NO=" + groupID);
						sb.append(" and SUBKEY_TYPE=" + subkeyType);
						sb.append(" and SUBKEY in (");
						CommUtil.concat(sb, arraySubkey, ",", ")");

						stmt = conn.createStatement();
						rs = stmt.executeQuery(sb.toString());
					}
				}
				if (rs != null) {
					while (rs.next()) {
						keys.add(PK.create(groupID, subkeyType, rs.getString(1)));
					}
				}

				DBUtil.closeRS(rs);
			}
		} catch (Exception e) {
			LOGGER.error("Error when put2SetIfExist", e);
			throw new RuntimeException(
					"Could not check passValidation existence", e);
		} finally {
			DBUtil.closeStmt(stmt);
		}
		return keys;
	}

	// key: subkey, value: listTypes-->subType, subType
	private static Map<String, ArrayList<Integer>> put2MapIfExist(
			Connection conn, PreparedStatement pstmt, int ownerOperatorNO,
			String[] arraySubkey) {
		int keysLength = arraySubkey.length;
		int batchSize = 1000;
		int remainder = keysLength % batchSize;
		int loopCount = (keysLength / batchSize) + 1;

		Map<String, ArrayList<Integer>> mapKey2Types = new HashMap<String, ArrayList<Integer>>();
		Statement stmt = null;
		try {
			pstmt.setInt(1, ownerOperatorNO);
			int firstInIndex = 2;
			// last in-parameter index, inclusive
			int lastInIndex = firstInIndex + (batchSize - 1);
			ResultSet rs = null;
			for (int curLoop = 0; curLoop < loopCount; curLoop++) {
				int fromOfCurloop = curLoop * batchSize;
				// do by PreparedStatement
				if (curLoop < loopCount - 1) {
					for (int inIndex = firstInIndex, subkeyIndex = fromOfCurloop; inIndex <= lastInIndex; inIndex++, subkeyIndex++) {
						pstmt.setString(inIndex, arraySubkey[subkeyIndex]);
					}
					rs = pstmt.executeQuery();
				}
				// last loop should be done by Statement
				else {
					// all has been done by PreparedStatement.
					// Do not need Statement
					if (remainder == 0) {
						rs = null;
					} else {
						String[] arrayLeft = new String[0];
						System.arraycopy(arraySubkey, fromOfCurloop, arrayLeft,
								0, (keysLength - fromOfCurloop));
						StringBuilder sb = new StringBuilder(
								"select SUBKEY, SUBKEY_TYPE from T_PCS_SUBSCRIBER ");
						sb.append(" where OWNER_OPERATOR_NO=" + ownerOperatorNO);
						sb.append(" and SUBKEY in (");
						CommUtil.concat(sb, arraySubkey, ",", ")");

						stmt = conn.createStatement();
						rs = stmt.executeQuery(sb.toString());
					}
				}
				if (rs != null) {
					while (rs.next()) {
						String subkey = rs.getString(1);
						Integer SubkeyType = Integer.valueOf(rs.getInt(2));
						SubkeyAndTypeInfo skt = new SubkeyAndTypeInfo(subkey);
						// already existed
						if (mapKey2Types.containsKey(subkey)) {
							mapKey2Types.get(subkey).add(SubkeyType);
						} else {
							ArrayList<Integer> listTypes = new ArrayList<Integer>();
							listTypes.add(SubkeyType);
							mapKey2Types.put(subkey, listTypes);
						}
					}
					DBUtil.closeRS(rs);
				}
			}
		} catch (Exception e) {
			LOGGER.error("Error when put2SetIfExist", e);
			throw new RuntimeException(
					"Could not check passValidation existence", e);
		} finally {
			DBUtil.closeStmt(stmt);
		}
		return mapKey2Types;
	}

	public static SubkeyAndTypeInfo[] constructSKT(Connection conn,
			PreparedStatement pstmt, int ownerOperatorNO, String[] arraySubkey) {
		Map<String, ArrayList<Integer>> mapKey2Types = put2MapIfExist(conn,
				pstmt, ownerOperatorNO, arraySubkey);
		SubkeyAndTypeInfo[] arraySubkeyAndType = new SubkeyAndTypeInfo[arraySubkey.length];
		for (int i = 0; i < arraySubkey.length; i++) {
			String aSubkey = arraySubkey[i];
			SubkeyAndTypeInfo skt = new SubkeyAndTypeInfo(aSubkey);
			arraySubkeyAndType[i] = skt;
			if (mapKey2Types.containsKey(aSubkey)) {
				skt.listSubkeytype.addAll(mapKey2Types.get(aSubkey));
			} else {

			}
		}
		return arraySubkeyAndType;
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

	public static List<FileTableBean> selectCandidateFiles(Connection conn) {
		List<FileTableBean> listUpload = selectCandidateFiles_upload(conn);
		List<FileTableBean> listRange = selectCandidateFiles_range(conn);
		// let's handle range files first
		listRange.addAll(listUpload);
		return listRange;
	}

	private static List<FileTableBean> selectCandidateFiles_upload(
			Connection conn) {
		List<FileTableBean> listCandidate = new ArrayList<FileTableBean>();
		String sql = "SELECT LOG_ID, RETRY_COUNT, GROUP_NO, OWNER_OPERATOR_NO, NAME, TOTAL_ROW_COUNT, PROCESS_SUCCESS_COUNT, PROCESS_FAIL_COUNT, DUPLICATE_COUNT FROM T_PCS_FILE_LOG WHERE FILE_STATUS='Queued' OR FILE_STATUS='Processing' order by LOG_ID ASC, RETRY_COUNT ASC";
		LOGGER.info("selectCandidateFiles sql:" + sql);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				FileTableBean ftb = new FileTableBean(rs.getString("NAME"));

				ftb.setLogID(rs.getLong("LOG_ID"));
				ftb.setCurRetryCount(rs.getInt("RETRY_COUNT"));
				ftb.setGroupID(rs.getLong("GROUP_NO"));
				ftb.setOwnerOperatorNum(rs.getInt("OWNER_OPERATOR_NO"));
				ftb.setTotalRow(rs.getLong("TOTAL_ROW_COUNT"));
				ftb.setSuccessRow(rs.getLong("PROCESS_SUCCESS_COUNT"));
				ftb.setFailRow(rs.getLong("PROCESS_FAIL_COUNT"));
				ftb.setDupsRow(rs.getLong("DUPLICATE_COUNT"));

				listCandidate.add(ftb);
			}
		} catch (Exception e) {
			LOGGER.error("Error when selectCandidateFiles", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return listCandidate;
	}

	private static List<FileTableBean> selectCandidateFiles_range(
			Connection conn) {
		List<FileTableBean> listCandidate = new ArrayList<FileTableBean>();
		String sql = "SELECT RANGE_ID, GROUP_NO, LOWER_RANGE, UPPER_RANGE, OWNER_OPERATOR_NO, TOTAL_ROW_COUNT, PROCESS_SUCCESS_COUNT, PROCESS_FAIL_COUNT, DUPLICATE_COUNT FROM T_PCS_FILE_LOG WHERE FILE_STATUS='Queued' OR FILE_STATUS='Processing' order by LOG_ID ASC";
		LOGGER.info("selectCandidateFiles sql:" + sql);
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);

			ResultSet rs = pstmt.executeQuery();
			while (rs.next()) {
				String rangeLower = rs.getString("LOWER_RANGE");
				String rangeUpper = rs.getString("UPPER_RANGE");
				String filenameInDB = com.syniverse.loader.PCSLoader
						.decideRangeFilename(rangeLower, rangeUpper);

				FileTableBean ftb = new FileTableBean(filenameInDB);
				ftb.setRangeID(rs.getLong("RANGE_ID"));
				ftb.setGroupID(rs.getLong("GROUP_NO"));
				ftb.setRangeLower(rangeLower);
				ftb.setRangeUpper(rangeUpper);

				ftb.setOwnerOperatorNum(rs.getInt("OWNER_OPERATOR_NO"));
				ftb.setTotalRow(rs.getLong("TOTAL_ROW_COUNT"));
				ftb.setSuccessRow(rs.getLong("PROCESS_SUCCESS_COUNT"));
				ftb.setFailRow(rs.getLong("PROCESS_FAIL_COUNT"));
				ftb.setDupsRow(rs.getLong("DUPLICATE_COUNT"));

				listCandidate.add(ftb);
			}
		} catch (Exception e) {
			LOGGER.error("Error when selectCandidateFiles", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return listCandidate;
	}

	

	// public static String fetchCommaSplitAttrInDBWithoutAction(Connection
	// conn,
	// String type, String billingID) {
	// String commaSplitAttrListInDB = fetchCommaSplitAttrInDB(conn, type,
	// billingID);
	// return CommUtil.eliminateLeadingAction(commaSplitAttrListInDB);
	// }

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

	public static boolean setValues(PreparedStatement pstmt,
			EachRowInfo erInfo, long groupID) {
		try {
			pstmt.setLong(1, groupID);// GROUP_NO NUMBER
			pstmt.setString(2, erInfo.subkey);// SUBKEY VARCHAR2(40 CHAR)
			pstmt.setInt(3, erInfo.getSubkeyType());// SUBKEY_TYPE NUMBER(3,0)
		} catch (SQLException e) {
			LOGGER.error("Error when setValues. Rethrow this exception", e);
			throw new RuntimeException("Error when setValue", e);
		}
		return true;
	}

	public static boolean increaseSuccessFailDups(AbstractFileInfo afi,
			Connection conn, long incrSuccess, long incrFail, long incrDups) {
		boolean bSuccess = false;
		PreparedStatement pstmt = null;
		try {
			if (afi.isUpload()) {
				String sql = "update T_PCS_FILE_LOG set PROCESS_SUCCESS_COUNT=PROCESS_SUCCESS_COUNT+?,PROCESS_FAIL_COUNT=PROCESS_FAIL_COUNT+?,DUPLICATE_COUNT=DUPLICATE_COUNT+?,UPDATED_TIMESTAMP=sysdate where LOG_ID=? and RETRY_COUNT=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setLong(4, afi.getLogID());
				pstmt.setInt(5, afi.getCurRetryCount());
			} else {
				String sql = "update T_PCS_REF_RANGE set PROCESS_SUCCESS_COUNT=PROCESS_SUCCESS_COUNT+?,PROCESS_FAIL_COUNT=PROCESS_FAIL_COUNT+?,DUPLICATE_COUNT=DUPLICATE_COUNT+?,UPDATED_TIMESTAMP=sysdate where RANGE_ID=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setLong(4, afi.getRangeID());
			}

			pstmt.setLong(1, incrSuccess);// PROCESS_SUCCESS_COUNT
			pstmt.setLong(2, incrFail);// PROCESS_FAIL_COUNT
			pstmt.setLong(3, incrDups);// DUPLICATE_COUNT

			pstmt.executeUpdate();
			bSuccess = true;
		} catch (Exception e) {
			LOGGER.error("Error when increaseSuccessFailDups", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return bSuccess;
	}

	public static boolean updateTotalRow(AbstractFileInfo afi, Connection conn,
			long totalRow) {
		if (!afi.isUpload()) {
			return true;
		}
		// OK, this is a uploaded file
		boolean bSuccess = false;
		String sql = "update T_PCS_FILE_LOG set TOTAL_ROW_COUNT=?,UPDATED_TIMESTAMP=sysdate where LOG_ID=? and RETRY_COUNT=?";
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setLong(1, totalRow);
			pstmt.setLong(2, afi.getLogID());
			pstmt.setInt(3, afi.getCurRetryCount());
			pstmt.executeUpdate();
			bSuccess = true;
		} catch (Exception e) {
			LOGGER.error("Error when updateTotalRow", e);
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

	public static boolean updateProcessStatus(AbstractFileInfo ftb, Connection conn,
			String processStatus) {
		boolean bSuccess = false;
		PreparedStatement pstmt = null;
		try {
			if (ftb.isUpload()) {
				String sql = "update T_PCS_FILE_LOG set FILE_STATUS=?,UPDATED_TIMESTAMP=sysdate where LOG_ID=? and RETRY_COUNT=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setLong(2, ftb.getLogID());// LOG_ID
				pstmt.setLong(3, ftb.getCurRetryCount());// RETRY_COUNT
			} else {
				String sql = "update T_PCS_REF_RANGE set FILE_STATUS=?,UPDATED_TIMESTAMP=sysdate where RANGE_ID=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setLong(2, ftb.getRangeID()); // RANGE_ID
			}

			// FEED_START_TIME
			pstmt.setString(1, processStatus);
			pstmt.executeUpdate();
			bSuccess = true;
		} catch (Exception e) {
			LOGGER.error("Error when updateStatus", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return bSuccess;
	}

	public static boolean updateStartDate(FileTableBean ftb, Connection conn,
			long startTime) {
		boolean bSuccess = false;
		PreparedStatement pstmt = null;
		try {
			if (ftb.isUpload()) {
				String sql = "update T_PCS_FILE_LOG set START_TIMESTAMP=?,UPDATED_TIMESTAMP=sysdate where LOG_ID=? and RETRY_COUNT=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setLong(2, ftb.getLogID());// LOG_ID
				pstmt.setLong(3, ftb.getCurRetryCount());// RETRY_COUNT
			} else {
				String sql = "update T_PCS_REF_RANGE set START_TIMESTAMP=?,UPDATED_TIMESTAMP=sysdate where RANGE_ID=?";
				pstmt = conn.prepareStatement(sql);
				pstmt.setLong(2, ftb.getRangeID()); // RANGE_ID
			}

			// FEED_START_TIME
			if (startTime < 0) {
				pstmt.setNull(1, java.sql.Types.TIMESTAMP);
			} else {
				pstmt.setTimestamp(1, new Timestamp(startTime));
			}
			pstmt.executeUpdate();
			bSuccess = true;
		} catch (Exception e) {
			LOGGER.error("Error when updateStartDate", e);
		} finally {
			DBUtil.closeStmt(pstmt);
		}
		return bSuccess;
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
