package com.syniverse.info;

import java.io.Serializable;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.common.CommUtil;
import com.syniverse.db.DBManipulate;

public class DBColumnsInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	private static final Log LOGGER = LogFactory.getLog(DBColumnsInfo.class);

	public static final String TABLE_NAME = "SUBSCRIBER";
	public static String SQL_A = null;
	public static String SQL_U = null;
	public static String SQL_D = null;
	public static String SQL_CHECK_EXISTENCE = null;

	/**
	 * For external:
	 * <p>
	 * ColumnDescInfo of (each column from ATTRIBUTE_LIST.ATTR_LIST,
	 * Impose_If_Not_Provide)
	 * <p>
	 * For internal:
	 * <p>
	 * ColumnDescInfo of (file header(which is also SUBSCRIBER
	 * columnname),Impose_If_Not_Provide)
	 * 
	 * If file header(which is also SUBSCRIBER columnname) has aleardy contains
	 * BILLING_ID, SUBSCRIBER_KEY, when append columns from
	 * Impose_If_Not_Provide, we'll ignore this column
	 * 
	 * 
	 * Please Note:
	 * 
	 * 1. NOT include action column
	 * <p>
	 * 2. Tailing ones are always ColumnDescInfo of (Impose_If_Not_Provide)
	 * 
	 */
	public static final List<ColumnDescInfo> allTypes = new ArrayList<ColumnDescInfo>();
	/**
	 * ColumnDescInfo of (BILLING_ID, SUBSCRIBER_KEY)
	 */
	public static final List<ColumnDescInfo> pkTypes = new ArrayList<ColumnDescInfo>();

	/**
	 * For external:
	 * <p>
	 * each column from ATTRIBUTE_LIST.ATTR_LIST,Impose_If_Not_Provide
	 * <p>
	 * For internal:
	 * <p>
	 * file header(which is also SUBSCRIBER columnname),Impose_If_Not_Provide
	 * 
	 * If file header(which is also SUBSCRIBER columnname) has aleardy contains
	 * BILLING_ID, SUBSCRIBER_KEY, when append columns from
	 * Impose_If_Not_Provide, we'll ignore this column
	 * 
	 * Please Note:
	 * 
	 * 1. NOT include action column
	 * <p>
	 * 2. Tailing ones are always BILLING_ID, SUBSCRIBER_KEY
	 * 
	 */
	public static final List<String> allColumns = new ArrayList<String>();

	/**
	 * BILLING_ID, SUBSCRIBER_KEY
	 */
	public static final List<String> pkColumns = new ArrayList<String>();

	public static final String BILLING_ID = "BILLING_ID";
	// GSM: IMSI, CDMA: MIN-->MDN
	public static final String SUBSCRIBER_KEY = "SUBSCRIBER_KEY";
	public static final String SUBSCRIBER_NUM = "SUBSCRIBER_NUM";
	public static final String TECHNOLOGY_TYPE = "TECHNOLOGY_TYPE";
	public static final String IMSI = "IMSI";// GSM
	public static final String MSISDN = "MSISDN";// GSM
	public static final String MIN = "MIN";// CDMA
	public static final String MDN = "MDN";// CDMA

	public static final String CREATED_BY = "CREATED_BY";
	public static final String UPDATED_BY = "UPDATED_BY";

	/**
	 * CAN NOT be null in DB
	 */
	public static final String[] Impose_If_Not_Provide = { BILLING_ID,
			SUBSCRIBER_KEY, SUBSCRIBER_NUM };
	/*
	 * index in
	 * 
	 * EachRowInfo.allValues/DBColumnsInfo.allColumns/DBColumnsInfo.allTypes
	 */
	public static int BILLING_ID_INDEX = -1;
	public static int SUBSCRIBER_KEY_INDEX = -1;
	public static int SUBSCRIBER_NUM_INDEX = -1;
	public static int TECHNOLOGY_TYPE_INDEX = -1;

	public static int IMSI_INDEX = -1;
	public static int MSISDN_INDEX = -1;

	public static int MIN_INDEX = -1;
	public static int MDN_INDEX = -1;

	public static final String CDMA_TECH_TYPE = "CDMA";
	public static final String GSM_TECH_TYPE = "GSM";

	/**
	 * @param conn
	 * @param commaSplitAll_insert
	 *            , value from from ATTRIBUTE_LIST.ATTR_LIST plus
	 *            BILLING_ID,SUBSCRIBER_KEY
	 * @param commaSplitPK
	 *            , returned value of
	 *            DBManipulate#fetchCommaSplitPrimaryKeyColumns()
	 */
	public static void initialize(Connection conn, String commaSplitAllColumn,
			String commaSplitPK) {
		fillColumnName(commaSplitAllColumn, allColumns);
		fillColumnName(commaSplitPK, pkColumns);

		fillColumnDesc(conn, commaSplitAllColumn, allTypes);
		fillColumnDesc(conn, commaSplitPK, pkTypes);

		setColumnIndexByAllColumnName(allColumns);

		SQL_A = createA(allColumns, TABLE_NAME);
		SQL_U = createU(allColumns, TABLE_NAME);
		SQL_D = createD(TABLE_NAME);
		SQL_CHECK_EXISTENCE = createCheckExistence(TABLE_NAME);

		LOGGER.info("SQL_A:" + SQL_A);
		LOGGER.info("SQL_U:" + SQL_U);
		LOGGER.info("SQL_D:" + SQL_D);
		LOGGER.info("SQL_CHECK_EXISTENCE:" + SQL_CHECK_EXISTENCE);
	}

	private static void fillColumnDesc(Connection conn,
			String commaSplitColumnName, List<ColumnDescInfo> listColumnDescInfo) {
		listColumnDescInfo.clear();
		DBManipulate.putColumnDescToList(conn, commaSplitColumnName,
				listColumnDescInfo, TABLE_NAME);
	}

	private static void fillColumnName(String commaSplitColumnName,
			List<String> listColumnName) {
		listColumnName.clear();
		listColumnName.addAll(CommUtil.split(commaSplitColumnName, ","));
	}

	private static String createA(List<String> allInsertColumns, String table) {
		StringBuilder sb = new StringBuilder("insert into " + table
				+ "(CREATED_TIMESTAMP,CREATED_BY,");
		CommUtil.concat(sb, allInsertColumns, ",", ")");
		sb.append(" values(sysdate,'Loader',");

		int paraSize = allInsertColumns.size();
		List<String> listQuestionMark = new ArrayList<String>(paraSize);
		for (int i = 0; i < paraSize; i++) {
			listQuestionMark.add("?");
		}

		CommUtil.concat(sb, listQuestionMark, ",", ")");
		return sb.toString();
	}

	private static String createU(List<String> allUpdateColumns, String table) {
		StringBuilder sb = new StringBuilder("update " + table
				+ " set UPDATED_TIMESTAMP=sysdate,UPDATED_BY='Loader',");
		CommUtil.concat(sb, allUpdateColumns, "=?,", "=? where ");
		CommUtil.concat(sb, pkColumns, "=? and ", "=?");
		return sb.toString();
	}

	private static String createD(String table) {
		StringBuilder sb = new StringBuilder("delete " + table + " where ");
		CommUtil.concat(sb, pkColumns, "=? and ", "=?");
		return sb.toString();
	}

	private static String createCheckExistence(String table) {
		StringBuilder sb = new StringBuilder("select subscriber_key from "
				+ table + " where billing_id=? and subscriber_key in(");
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

	private static void setColumnIndexByAllColumnName(List<String> listColumns) {
		for (int i = 0; i < listColumns.size(); i++) {
			String colName = listColumns.get(i);
			if (TECHNOLOGY_TYPE.equals(colName)) {
				TECHNOLOGY_TYPE_INDEX = i;
				continue;
			}
			if (IMSI.equals(colName)) {
				IMSI_INDEX = i;
				continue;
			}
			if (MIN.equals(colName)) {
				MIN_INDEX = i;
				continue;
			}
			if (MSISDN.equals(colName)) {
				MSISDN_INDEX = i;
				continue;
			}
			if (MDN.equals(colName)) {
				MDN_INDEX = i;
				continue;
			}
			if (SUBSCRIBER_KEY.equals(colName)) {
				SUBSCRIBER_KEY_INDEX = i;
				continue;
			}
			if (SUBSCRIBER_NUM.equals(colName)) {
				SUBSCRIBER_NUM_INDEX = i;
				continue;
			}
			if (BILLING_ID.equals(colName)) {
				BILLING_ID_INDEX = i;
				continue;
			}
		}
	}

	public static void main(String[] args) {
	}
}
