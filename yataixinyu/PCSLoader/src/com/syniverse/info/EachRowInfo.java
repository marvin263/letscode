package com.syniverse.info;

import java.io.Serializable;

import com.syniverse.loader.ErrorMsgConst;

public class EachRowInfo implements Serializable, Comparable<EachRowInfo> {
	private static final long serialVersionUID = 1L;
	/**
	 * Start from 1
	 */
	public final long lineNumberInOrgnFile;
	public final String subkey;
	public final boolean yesPerfect;
	/**
	 * Whether subscriberKey format is OK. Not check database, only check
	 * "length < 20, all digit"
	 */
	public final boolean formatError;
	public final boolean notInSubscriber;
	public final boolean tooManyTypes;

	private int subkeyType;

	/**
	 * 1. subscriberKey format is not legal
	 * <p>
	 * 2. rejected because of subkey NOT existing in T_PCS_SUBSCRIBER
	 * <p>
	 * 3. subkey duplicated in T_PCS_XREF_GRP_SUB
	 * <p>
	 * 4. any error msg because of error when inserting into T_PCS_SUBSCRIBER
	 * 
	 */
	private String notSuccessMsg;

	private EachRowInfo(final boolean yesPerfect, final String subkey,
			final boolean formatError, final long lineNumberInOrgnFile,
			final boolean notInSubscriber, final boolean tooManyTypes) {
		this.subkey = subkey;
		this.lineNumberInOrgnFile = lineNumberInOrgnFile;
		this.yesPerfect = yesPerfect;
		this.formatError = formatError;
		this.notInSubscriber = notInSubscriber;
		this.tooManyTypes = tooManyTypes;
	}

	public static EachRowInfo creatFormatError(final String subkey,
			final long lineNumberInOrgnFile) {
		EachRowInfo erInfo = new EachRowInfo(false, subkey, true,
				lineNumberInOrgnFile, false, false);
		return erInfo;
	}

	public static EachRowInfo creatNotInSubscriber(final String subkey,
			final long lineNumberInOrgnFile) {
		EachRowInfo erInfo = new EachRowInfo(false, subkey, false,
				lineNumberInOrgnFile, true, false);
		erInfo.setNotSuccessMsg(ErrorMsgConst.NOT_IN_SUBSCRIBER);
		return erInfo;
	}

	public static EachRowInfo creatTooManyTypes(final String subkey,
			final long lineNumberInOrgnFile) {
		EachRowInfo erInfo = new EachRowInfo(false, subkey, false,
				lineNumberInOrgnFile, false, true);
		erInfo.setNotSuccessMsg(ErrorMsgConst.MORE_THAN_1_TYPE);
		return erInfo;
	}

	public static EachRowInfo creatPerfect(final String subkey,
			final long lineNumberInOrgnFile) {
		EachRowInfo erInfo = new EachRowInfo(true, subkey, false,
				lineNumberInOrgnFile, false, false);
		return erInfo;
	}

	public int getSubkeyType() {
		return subkeyType;
	}

	public void setSubkeyType(int subkeyType) {
		this.subkeyType = subkeyType;
	}

	public String getNotSuccessMsg() {
		return notSuccessMsg;
	}

	public void setNotSuccessMsg(String notSuccessMsg) {
		this.notSuccessMsg = notSuccessMsg;
	}

	@Override
	public int compareTo(EachRowInfo other) {
		return (int) (lineNumberInOrgnFile - other.lineNumberInOrgnFile);
	}

}
