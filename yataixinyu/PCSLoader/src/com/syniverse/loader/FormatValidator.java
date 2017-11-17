package com.syniverse.loader;

import com.syniverse.common.CommUtil;

public class FormatValidator {
	public static final int MAX_LENGTH = 15;

	public static String checkFormat(String subkey) {
		subkey = CommUtil.trimToEmpty(subkey);
		if (subkey.length() <= 0) {
			return ErrorMsgConst.FORMAT_ERROR_empty;
		}
		if (subkey.length() > MAX_LENGTH) {
			return ErrorMsgConst.FORMAT_ERROR_too_long;
		}
		boolean allDigit = CommUtil.allDigit(subkey);
		if (allDigit == false) {
			return ErrorMsgConst.FORMAT_ERROR_not_all_digit;
		}
		return null;
	}

}
