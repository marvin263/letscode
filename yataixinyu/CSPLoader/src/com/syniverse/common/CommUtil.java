package com.syniverse.common;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CommUtil {
	private static final Log LOGGER = LogFactory.getLog(CommUtil.class);

	public static List<String> split(String original, String delimiter) {
		List<String> list = new ArrayList<String>();
		if (original == null || original.trim().length() == 0) {
			return list;
		}
		int delimiterL = delimiter.length();
		int orgnL = original.length();
		int lastEnd = 0;// delimiter's (endIndex+1)
		while (lastEnd <= (orgnL - 1)) {
			int currentStart = original.indexOf(delimiter, lastEnd);
			if (currentStart == -1) {
				list.add(original.substring(lastEnd));
				break;
			}
			list.add(original.substring(lastEnd, currentStart));
			lastEnd = currentStart + delimiterL;
		}
		if (original.endsWith(delimiter)) {
			list.add("");
		}
		return list;
	}

	public static void concat(StringBuilder sb, String[] array, String normal,
			String lastSpecial) {
		int length = array.length;
		for (int i = 0; i < length; i++) {
			sb.append(array[i]);
			if (i == length - 1) {
				sb.append(lastSpecial);
			} else {
				sb.append(normal);
			}
		}
	}

	public static boolean isEmpty(String str) {
		if (str == null) {
			return true;
		}
		return str.trim().length() == 0;
	}

	public static String trimToEmpty(String str) {
		if (str == null) {
			return "";
		}
		return str.trim();
	}

	public static void concat(StringBuilder sb, List<String> list,
			String normal, String lastSpecial) {
		concat(sb, list.toArray(new String[0]), normal, lastSpecial);
	}

	public static String concat(List<String> list, String normal,
			String lastSpecial) {
		StringBuilder sb = new StringBuilder("");
		concat(sb, list.toArray(new String[0]), normal, lastSpecial);
		return sb.toString();
	}

	public static String appendToTailIfNotExist(String commaSplitAttrInDB,
			String[] listImposedColumn) {
		List<String> listAttrInDB = CommUtil.split(commaSplitAttrInDB, ",");
		List<String> allColumns = new ArrayList<String>(listAttrInDB);
		for (String impose : listImposedColumn) {
			if (listAttrInDB.contains(impose) == false) {
				allColumns.add(impose);
			}
		}
		String commaSplitAll = CommUtil.concat(allColumns, ",", "");
		LOGGER.info("appendToTailIfNotExist: commaSplitAttrInDB="
				+ commaSplitAttrInDB + ", commaSplitAll=" + commaSplitAll);
		return commaSplitAll;
	}

	public static String[] combineNewArray(String[] array1, String[] array2) {

		if (array1 == null || array1.length == 0) {
			if (array2 == null || array2.length == 0) {
				return new String[0];
			} else {
				String[] rst = new String[0];
				System.arraycopy(array2, 0, rst, 0, array2.length);
				return rst;
			}
		}

		if (array2 == null || array2.length == 0) {
			if (array1 == null || array1.length == 0) {
				return new String[0];
			} else {
				String[] rst = new String[0];
				System.arraycopy(array1, 0, rst, 0, array1.length);
				return rst;
			}
		}
		// Since here, both array1 and array2 are not empty
		String[] rst = new String[array1.length + array2.length];
		System.arraycopy(array1, 0, rst, 0, array1.length);
		System.arraycopy(array2, 0, rst, array1.length, array2.length);
		return rst;
	}

	/**
	 * 
	 * Remove "Action," from <code>commaSplitAttrListInDB</code>
	 * 
	 * Action,NOTIFICATION_EMAIL,TECHNOLOGY_TYPE-->NOTIFICATION_EMAIL,
	 * TECHNOLOGY_TYPE
	 * 
	 * 
	 * @param commaSplitStringWithAction
	 * @return
	 */
	public static String eliminateLeadingAction(
			String commaSplitStringWithAction) {
		int firstCommaIndex = commaSplitStringWithAction.indexOf(",");
		if (firstCommaIndex == -1) {
			return commaSplitStringWithAction;
		}

		// We're sure that the first column is always "Action", just remove it
		String commaSplitStringWithoutAction = commaSplitStringWithAction
				.substring(firstCommaIndex + 1);

		LOGGER.debug("Eliminating leading action, commaSplitStringWithAction="
				+ commaSplitStringWithAction
				+ "-->commaSplitAttrListWithoutAction="
				+ commaSplitStringWithoutAction);
		return commaSplitStringWithoutAction;
	}

	/**
	 * Formats a string using {@link MessageFormat#format(String, Object[])}.
	 * <p>
	 * This is a convenience method.
	 * </p>
	 * 
	 * @param pattern
	 * @param longValue1
	 * @return the formated string
	 */
	public static String format(final String pattern, final long longValue1) {
		return MessageFormat.format(pattern,
				new Object[] { new Long(longValue1) });
	}

	/**
	 * Formats a string using {@link MessageFormat#format(String, Object[])}.
	 * <p>
	 * This is a convenience method.
	 * </p>
	 * 
	 * @param pattern
	 * @param longValue1
	 * @param longValue2
	 * @return the formated string
	 */
	public static String format(final String pattern, final long longValue1,
			final long longValue2) {
		return MessageFormat.format(pattern, new Object[] {
				new Long(longValue1), new Long(longValue2) });
	}

	public static String format(final String pattern, final long longValue1,
			final long longValue2, final long longValue3) {
		return MessageFormat.format(pattern, new Object[] {
				new Long(longValue1), new Long(longValue2),
				new Long(longValue3) });
	}

	public static String format(final String pattern, final long longValue1,
			final long longValue2, final long longValue3, final long longValue4) {
		return MessageFormat.format(pattern, new Object[] {
				new Long(longValue1), new Long(longValue2),
				new Long(longValue3), new Long(longValue4) });
	}

	/**
	 * Formats a string using {@link MessageFormat#format(String, Object[])}.
	 * <p>
	 * This is a convenience method.
	 * </p>
	 * 
	 * @param pattern
	 * @param singleArgument
	 * @return the formated string
	 */
	public static String format(final String pattern,
			final Object singleArgument) {
		return MessageFormat.format(pattern, new Object[] { singleArgument });
	}

	/**
	 * Formats a string using {@link MessageFormat#format(String, Object[])}.
	 * <p>
	 * This is a convenience method.
	 * </p>
	 * 
	 * @param pattern
	 * @param argument1
	 * @param argument2
	 * @return the formated string
	 */
	public static String format(final String pattern, final Object argument1,
			final Object argument2) {
		return MessageFormat.format(pattern, new Object[] { argument1,
				argument2 });
	}

	/**
	 * Formats a string using {@link MessageFormat#format(String, Object[])}.
	 * <p>
	 * This is a convenience method.
	 * </p>
	 * 
	 * @param pattern
	 * @param argument1
	 * @param argument2
	 * @param argument3
	 * @return the formated string
	 */
	public static String format(final String pattern, final Object argument1,
			final Object argument2, final Object argument3) {
		return MessageFormat.format(pattern, new Object[] { argument1,
				argument2, argument3 });
	}

	/**
	 * Formats a string using {@link MessageFormat#format(String, Object[])}.
	 * <p>
	 * This is a convenience method.
	 * </p>
	 * 
	 * @param pattern
	 * @param argument1
	 * @param argument2
	 * @param argument3
	 * @param argument4
	 * @return the formated string
	 */
	public static String format(final String pattern, final Object argument1,
			final Object argument2, final Object argument3,
			final Object argument4) {
		return MessageFormat.format(pattern, new Object[] { argument1,
				argument2, argument3, argument4 });
	}

	public static String format(final String pattern, final Object argument1,
			final Object argument2, final Object argument3,
			final Object argument4, final Object argument5) {
		return MessageFormat.format(pattern, new Object[] { argument1,
				argument2, argument3, argument4, argument5 });
	}

	public static void main(String[] args) {
		StringBuilder sb = new StringBuilder(
				"select KEYID, KEYVALUE from APP_CONFIG where CONFIG_TYPE=? and KEYID in('");
		String[] allParaKeys = { "aaaa", "bbbb", "cccc" };
		CommUtil.concat(sb, allParaKeys, "','", "')");
		System.out.println(sb.toString());
	}

	/**
	 * Truncates the specified string if it's larger than the allowed size.
	 * <p>
	 * This method operates on the number of bytes a string contains and not on
	 * the string length.
	 * </p>
	 * 
	 * @param maximumSize
	 *            the allowed length
	 * @param appendDots
	 *            if <code>true</code> and the string exceeds the maximum size
	 *            it will be truncated to maximumSize - 2 and 2 dots (
	 *            <code>..</code>) will be added to the end of the string
	 * @return the string truncated to the allowed size
	 */
	public static String truncateToSize(String string, int maximumSize,
			boolean appendDots) {
		if (null == string)
			return string;

		char[] chars = string.toCharArray();
		StringBuilder result = new StringBuilder();

		// go through the string and watch the size
		int size = 0;
		boolean doAppendDots = false;
		for (int charIndex = 0; charIndex < chars.length; charIndex++) {
			char c = chars[charIndex];

			// calculate size
			if (c <= 0x7f) {
				// one byte character
				size++; // just one byte
			} else if (c <= 0x7ff) {
				// two byte character
				size += 2; // two bytes
			} else if (c <= 0xffff) {
				// three byte character
				size += 3; // three bytes
			} else {
				// four byte character
				size += 4; // four bytes
			}

			// check if the size reached the maximum
			if (size <= maximumSize) {
				result.append(c);
			} else {
				doAppendDots = appendDots;
				break;
			}
		}

		// append dots if necessary
		if (doAppendDots) {
			char lastChar = result.charAt(result.length() - 1);
			if (lastChar > 0x7f) {
				/*
				 * last char is a multi byte character; we replace it with a dot
				 * and simply add the second dot; this is possible because one
				 * dot only takes one byte
				 */
				result.setCharAt(result.length() - 1, '.');
				result.append('.');
			} else {
				result.setCharAt(result.length() - 1, '.');
				result.setCharAt(result.length() - 2, '.');
			}
		}

		return result.toString();
	}

}
