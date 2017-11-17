package com.syniverse.info;

import java.io.Serializable;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.syniverse.common.CommUtil;
import com.syniverse.rti.csp.validator.ActionType;
import com.syniverse.rti.csp.validator.ValidateResult;

public class EachRowInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public final String originalLine;
	public final String billingID;
	/**
	 * Start from 1
	 */
	public final int lineNumberInSplitFile;

	private boolean passed;
	private ActionType action;
	private String validationMsg;
	private String subscriberKey;
	private ValidateResult validateResult;

	/**
	 * 1. IMSI
	 * <p>
	 * 2. MIN
	 * <p>
	 * 3. MSSDN
	 * <p>
	 * 4. MDN
	 * 
	 */
	private String subkeySource;

	/**
	 * NOT include action column
	 * <p>
	 * Tailing ones are value from (DBColumnsInfo.Impose_If_Not_Provide)
	 * <p>
	 * values of each file row plus values from
	 * (DBColumnsInfo.Impose_If_Not_Provide)
	 */
	public final List<Object> allValue = new ArrayList<Object>();

	/**
	 * value of (BILLING_ID, SUBSCRIBER_KEY)
	 */
	public final List<Object> pkValues = new ArrayList<Object>();

	public EachRowInfo(final String billingID, final String originalLine,
			final int lineNumberInSplitFile, ValidateResult validateResult) {
		this.billingID = billingID;
		this.originalLine = originalLine;
		this.lineNumberInSplitFile = lineNumberInSplitFile;

		this.passed = validateResult.isSuccess();
		this.validationMsg = validateResult.getErrorMessage();
		this.subscriberKey = validateResult.getSubscriberKey();
		this.action = validateResult.getAction();
		this.subkeySource = validateResult.getSubscriberKeySource();
		this.validateResult = validateResult;
		parseLine2GetValue(validateResult);

	}

	private EachRowInfo(final String billingID, final String originalLine,
			final int lineNumberInSplitFile) {
		this.billingID = billingID;
		this.originalLine = originalLine;
		this.lineNumberInSplitFile = lineNumberInSplitFile;
	}

	public static EachRowInfo createDummy(final String billingID,
			final String originalLine, final int lineNumberInSplitFile) {
		EachRowInfo erInfo = new EachRowInfo(billingID, originalLine,
				lineNumberInSplitFile);

		erInfo.passed = false;
		erInfo.validationMsg = "Unknown error. Validator exception";
		return erInfo;
	}

	public static EachRowInfo createFailderInfo(final String billingID,
			final String originalLine, final int lineNumberInSplitFile,
			ValidateResult validateResult) {
		EachRowInfo erInfo = new EachRowInfo(billingID, originalLine,
				lineNumberInSplitFile);

		erInfo.passed = false;
		erInfo.validationMsg = validateResult.getErrorMessage();
		return erInfo;
	}

	private void parseLine2GetValue(ValidateResult validateResult) {
		List<String> listOriginal = CommUtil.split(originalLine, ",");
		// fill the values provided in file
		fillProvided(listOriginal, allValue, DBColumnsInfo.allTypes);
		// fill the values not provided in file(we have to provide these values,
		// because the are NOT nullable in DB.)
		// when update, these values can all be deduced by the provide data row
		int imposedStart = listOriginal.size() - 1;
		fillImposed(allValue, validateResult, imposedStart);

		pkValues.add(billingID);
		pkValues.add(validateResult.getSubscriberKey());

	}

	private void fillImposed(List<Object> allValue,
			ValidateResult validateResult, int imposedStart) {
		for (int i = imposedStart; i < DBColumnsInfo.allColumns.size(); i++) {
			String imposed = DBColumnsInfo.allColumns.get(i);
			if (DBColumnsInfo.BILLING_ID.equals(imposed)) {
				allValue.add(buildObject(validateResult.getBillingID(),
						DBColumnsInfo.allTypes.get(i)));
				continue;
			}
			if (DBColumnsInfo.SUBSCRIBER_KEY.equals(imposed)) {
				allValue.add(buildObject(validateResult.getSubscriberKey(),
						DBColumnsInfo.allTypes.get(i)));
				continue;
			}
			if (DBColumnsInfo.SUBSCRIBER_NUM.equals(imposed)) {
				allValue.add(buildObject(validateResult.getSubscriberNum(),
						DBColumnsInfo.allTypes.get(i)));
				continue;
			}
		}
	}

	private void fillProvided(List<String> listOriginal,
			List<Object> allValues, List<ColumnDescInfo> allTypes) {
		// index 0 is Action, we start from 1
		for (int i = 1; i < listOriginal.size(); i++) {
			String cellValue = listOriginal.get(i);
			ColumnDescInfo cdInfo = allTypes.get(i - 1);
			allValues.add(buildObject(cellValue, cdInfo));
		}
	}

	private Object buildObject(String cellValue, ColumnDescInfo cdInfo) {
		Object finalValue = null;
		switch (cdInfo.sqlType) {
		case Types.VARCHAR:
			finalValue = cellValue;
			break;
		case Types.NUMERIC:
			finalValue = validateResult.convertToInteger(cellValue);
			break;
		case Types.TIMESTAMP:
			finalValue = parseString2Date(cellValue);
			break;
		case Types.CHAR:
			finalValue = cellValue;
			break;

		default:
			finalValue = cellValue;
			break;
		}
		return finalValue;
	}

	private java.sql.Date parseString2Date(String cellValue) {
		return validateResult.convertToDate(cellValue);
	}

	public ActionType getAction() {
		return action;
	}

	public void setAction(ActionType action) {
		this.action = action;
	}

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	public String getValidationMsg() {
		return validationMsg;
	}

	public void setValidationMsg(String validationMsg) {
		this.validationMsg = validationMsg;
	}

	public String getSubscriberKey() {
		return subscriberKey;
	}

	public String getSubkeySource() {
		return subkeySource;
	}

	public void setSubkeySource(String subkeySource) {
		this.subkeySource = subkeySource;
	}

}
