import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HandleIfElseIfLine {
	public static final char L = '(';
	public static final char R = ')';
	public static final char SPACE = ' ';
	public static final char TAB = '\t';
	public static final String DOUBLE_QUOTE = "\"";
	public static final String SINGLE_QUOTE = "'";
	public static final String EQUAL = "==";
	public static final String NOT_EQUAL = "!=";
	public static final String DOLLAR = "$";
	public static final String vmutil = "vmutil";

	private static final String REG_AND_OR_DELIMITER = "(&&)|(\\|\\|)";
	private static final Pattern PTN_AND_OR_DELIMITER = Pattern.compile(
			REG_AND_OR_DELIMITER, Pattern.CASE_INSENSITIVE);

	private static final String REG_NOT_METHOD_INVOKE = "\\$(!)?(\\{)?[\\d\\w-]+\\.[\\d\\w-]+?\\((\\})?";
	private static final Pattern PTN_NOT_METHOD_INVOKE = Pattern.compile(
			REG_NOT_METHOD_INVOKE, Pattern.CASE_INSENSITIVE);

	private static final String REG_LET_US_DO_REPLACEMENT = "((\"|')?)(\\$)?(!)?(\\{)?([.\\d\\w-]+)(\\})?(\\1)";
	private static final Pattern PTN_LET_US_DO_REPLACEMENT = Pattern.compile(
			REG_LET_US_DO_REPLACEMENT, Pattern.CASE_INSENSITIVE);

	public static void main(String[] args) throws Exception {
		String abc = replaceASide("", "$!pageID\"", -1);
		System.out.println(abc);
	}

	public static String replaceWholeIfElseif(String fullPath,
			String strIfElseifLine, int rowNumberInOriginalFile) {
		int[] arrayBeginEnd = firstPairParenthesis(strIfElseifLine);
		int begin = arrayBeginEnd[0];
		int end = arrayBeginEnd[1];
		// Change nothing
		if (begin == -1 || end == -1) {
			return strIfElseifLine;
		}
		StringBuffer sbNew = new StringBuffer();
		// Include (
		String bigHeader = strIfElseifLine.substring(0, begin + 1);
		// Include )
		String bigTailer = strIfElseifLine.substring(end);
		sbNew.append(bigHeader);

		// NOT include ( and )
		String contentInParenthesis = strIfElseifLine.substring(begin + 1, end);
		String[] arrayEachExpression = PTN_AND_OR_DELIMITER
				.split(contentInParenthesis);
		if (arrayEachExpression == null || arrayEachExpression.length == 0) {
			sbNew.append(contentInParenthesis);
		} else {
			// currentPos has been added into sbNew
			int currentPos = begin;
			for (int i = 0; i < arrayEachExpression.length; i++) {
				String thisExpression_NOT_TRIM = arrayEachExpression[i];
				currentPos = replaceOneExpression(fullPath, sbNew,
						strIfElseifLine, currentPos, thisExpression_NOT_TRIM,
						rowNumberInOriginalFile);
			}
		}

		sbNew.append(bigTailer);
		return sbNew.toString();
	}

	/**
	 * currentPos, NOT included in thisExpression_NOT_TRIM
	 * 
	 * @param strIfElseifLine
	 * @param currentPos
	 *            , not include
	 * @param thisExpression_NOT_TRIM
	 * @return
	 */
	private static int replaceOneExpression(String fullPath,
			StringBuffer sbNew, String strIfElseifLine, int currentPos,
			String thisExpression_NOT_TRIM, int rowNumberInOriginalFile) {
		// DO NOT need to modify this expression
		if (couldGoFurther(fullPath, thisExpression_NOT_TRIM,
				rowNumberInOriginalFile) == false) {
			// currentPos-------->startPosThisExpression, has never been added
			// into sbNew
			int startPosThisExpression = strIfElseifLine.indexOf(
					thisExpression_NOT_TRIM, currentPos + 1);
			// insert currentPos--->startPosThisExpression(not include
			// startPosThisExpression)
			sbNew.append(strIfElseifLine.substring(currentPos + 1,
					startPosThisExpression));
			sbNew.append(thisExpression_NOT_TRIM);
			// This pos has been added into sbNew
			int newPosInStrIfElseifLine = startPosThisExpression
					+ thisExpression_NOT_TRIM.length() - 1;
			return newPosInStrIfElseifLine;
		}
		// NEED to modify this expression
		int[] arrayFirstEndSecondStart = separateExpression2GetSide(thisExpression_NOT_TRIM);
		// NOT include
		int firstEnd = arrayFirstEndSecondStart[0];
		// Include
		int secondStart = arrayFirstEndSecondStart[1];

		String LHS_not_TRIM = thisExpression_NOT_TRIM.substring(0, firstEnd);
		String RHS_not_TRIM = thisExpression_NOT_TRIM.substring(secondStart);
		// currentPos-------->startPosThisExpression, has never been added into
		// stringBuffer
		int startPosThisExpression = strIfElseifLine.indexOf(
				thisExpression_NOT_TRIM, currentPos + 1);
		// insert currentPos--->startPosThisExpression (not include
		// startPosThisExpression)
		sbNew.append(strIfElseifLine.substring(currentPos + 1,
				startPosThisExpression));
		// OK, let's do really replacement, begin
		String LHS_not_TRIM_After_Replace = replaceASide(fullPath,
				LHS_not_TRIM, rowNumberInOriginalFile);
		String RHS_not_TRIM_After_Replace = replaceASide(fullPath,
				RHS_not_TRIM, rowNumberInOriginalFile);
		// OK, let's do really replacement, end
		sbNew.append(LHS_not_TRIM_After_Replace);
		// == or !=
		sbNew.append(thisExpression_NOT_TRIM.substring(firstEnd, secondStart));
		sbNew.append(RHS_not_TRIM_After_Replace);

		// This pos has been added into sbNew
		int newPosInStrIfElseifLine = startPosThisExpression
				+ thisExpression_NOT_TRIM.length() - 1;
		return newPosInStrIfElseifLine;
	}

	/**
	 * <code>thisExpression_NOT_TRIM</code> is separated by == or !=, this
	 * method will get
	 * 
	 * @param thisExpression_NOT_TRIM
	 * @return
	 */
	private static int[] separateExpression2GetSide(
			String thisExpression_NOT_TRIM) {
		int[] arrayFirstEndSecondStart = separateByStr(thisExpression_NOT_TRIM,
				EQUAL);
		if (arrayFirstEndSecondStart[0] == -1
				|| arrayFirstEndSecondStart[1] == -1) {
			arrayFirstEndSecondStart = separateByStr(thisExpression_NOT_TRIM,
					NOT_EQUAL);
		}
		return arrayFirstEndSecondStart;
	}

	private static boolean couldGoFurther(String fullPath,
			String thisExpression_NOT_TRIM, int rowNumberInOriginalFile) {
		if (thisExpression_NOT_TRIM == null
				|| thisExpression_NOT_TRIM.trim().length() == 0) {
			return false;
		}
		String trimedExpression = thisExpression_NOT_TRIM.trim();
		// 1. Must contain == or !=
		if (trimedExpression.indexOf(EQUAL) == -1
				&& trimedExpression.indexOf(NOT_EQUAL) == -1) {
			return false;
		}
		// 2. At least one $
		if (trimedExpression.indexOf(DOLLAR) == -1) {
			return false;
		}
		// 3. NO vmutil
		if (trimedExpression.toLowerCase().indexOf(vmutil) != -1) {
			return false;
		}

		// 4. Compare as string?
		int[] arrayFirstEndSecondStart = separateExpression2GetSide(thisExpression_NOT_TRIM);
		// NOT include in First part
		int firstEnd = arrayFirstEndSecondStart[0];
		// Include in second part
		int secondStart = arrayFirstEndSecondStart[1];
		String trimed_LHS = thisExpression_NOT_TRIM.substring(0, firstEnd)
				.trim();
		String trimed_RHS = thisExpression_NOT_TRIM.substring(secondStart)
				.trim();
		// If we found "/' either in the end of trimed_LHS or in the start of
		// trimed_RHS
		// ($myVariable) == ("$!another") WON'T be handled
		if (!endsWithQuote(trimed_LHS) && !startsWithQuote(trimed_RHS)) {
			return false;
		}
		// 5. not method invocation
		if (PTN_NOT_METHOD_INVOKE.matcher(thisExpression_NOT_TRIM).find()) {
			// Log encountered method
			SearchAndReplace.putFullPathAndRowNumber(fullPath,
					rowNumberInOriginalFile, SearchAndReplace.logMethod);
			return false;
		}
		return true;
	}

	private static String replaceASide(String fullPath, String HS_not_TRIM,
			int rowNumberInOriginalFile) {
		StringBuffer sb = new StringBuffer();
		Matcher m = PTN_LET_US_DO_REPLACEMENT.matcher(HS_not_TRIM);
		if (m.find()) {
			int start = m.start();
			int end = m.end();
			// add the header
			String header = HS_not_TRIM.substring(0, start);
			addIfNotNull(sb, header);
			String LdoubleOrSingleQuote = m.group(1);
			// dollar may NOT exist (5 == "$another")
			String dollar = m.group(3);
			String exclamatoryPoint = m.group(4);
			String LBrace = m.group(5);
			String variableName = m.group(6);
			String RBrace = m.group(7);
			String RdoubleOrSingleQuote = m.group(8);

			// 1. Single quote, ignore
			if (SINGLE_QUOTE.equals(LdoubleOrSingleQuote)) {
				return HS_not_TRIM;
			}
			// 2. NO dollar, we can handle it more gracefully
			if (!DOLLAR.equals(dollar)) {
				return noDollarWhenReplaceASide(fullPath, HS_not_TRIM,
						rowNumberInOriginalFile);
				// No dollar is also possible, 5 == "$another"
			}
			// Since we are here, LdoubleOrSingleQuote must be empty or double
			// quote
			// couldGoFurther() can ensure that we are comparing string, so,
			// we can insert double quote freely
			addIfNotNull(sb, DOUBLE_QUOTE);
			// 2. Insert Dollar
			addIfNotNull(sb, dollar);
			// 3. Insert Exclamatory
			insertExclamatoryIfNeeded(fullPath, exclamatoryPoint,
					rowNumberInOriginalFile, sb, variableName);
			// 4. LBrace
			addIfNotNull(sb, LBrace);
			// 5. variableName
			addIfNotNull(sb, variableName);
			// 6. RBrace
			addIfNotNull(sb, RBrace);
			// 7. Last double quote(if it's single quote, we cannot reach here,
			// so add double quote freely)
			addIfNotNull(sb, DOUBLE_QUOTE);
			// 8.Tail
			String tail = HS_not_TRIM.substring(end);
			addIfNotNull(sb, tail);
			// Do we need to Log Special attention?
			logAttentionIfNeeded(fullPath, rowNumberInOriginalFile,
					HS_not_TRIM, header, tail, sb.toString());
			return sb.toString();
		}
		// Only "" or '' come here
		return HS_not_TRIM;
	}

	private static void logAttentionIfNeeded(String fullPath,
			int rowNumberInOriginalFile, String HS_not_TRIM, String header,
			String tail, String newHS) {
		if (HS_not_TRIM.equals(newHS)) {
			return;
		}
		// header and tail are NOT all space, log it
		if (mustBeBlankOrParenthesis(header) == false
				|| mustBeBlankOrParenthesis(tail) == false) {
			// Log pay special attention
			SearchAndReplace.putFullPathAndRowNumber(fullPath,
					rowNumberInOriginalFile,
					SearchAndReplace.logNotStartEndEmpty_Attention);
		}
	}

	private static String noDollarWhenReplaceASide(String fullPath,
			String HS_not_TRIM, int rowNumberInOriginalFile) {
		if (HS_not_TRIM == null || HS_not_TRIM.length() == 0) {
			return HS_not_TRIM;
		}
		String strHeadBlank = headBlank(HS_not_TRIM);
		String strTailBlank = tailBlank(HS_not_TRIM);
		String trimed_HS = HS_not_TRIM.trim();
		// 1. Even one quote exists, we consider as OK
		if (startsWithQuote(trimed_HS) || endsWithQuote(trimed_HS)) {
			return HS_not_TRIM;
		}
		// 2. Start with (, [, or {, we consider as OK
		if (trimed_HS.startsWith("(") || trimed_HS.startsWith("[")
				|| trimed_HS.startsWith("{") || trimed_HS.endsWith(")")
				|| trimed_HS.endsWith("]") || trimed_HS.endsWith("}")) {
			// We do not modify it, so no need to pay attention to this!
			// Log Pay Attention
			// SearchAndReplace.putFullPathAndRowNumber(fullPath,
			// rowNumberInOriginalFile,
			// SearchAndReplace.logNotStartEndEmpty_Attention);
			return HS_not_TRIM;
		}
		// OK, it must be numbers
		StringBuffer sb = new StringBuffer();
		sb.append(strHeadBlank);
		sb.append(DOUBLE_QUOTE);
		sb.append(trimed_HS);
		sb.append(DOUBLE_QUOTE);
		sb.append(strTailBlank);
		// Log changed numbers
		SearchAndReplace.putFullPathAndRowNumber(fullPath,
				rowNumberInOriginalFile, SearchAndReplace.logChangedNumbers);
		return sb.toString();
	}

	private static String headBlank(String str) {
		if (str == null || str.length() == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer("");
		for (int i = 0; i < str.length(); i++) {
			char aChar = str.charAt(i);
			if (aChar == SPACE || aChar == TAB) {
				sb.append(aChar + "");
			} else {
				break;
			}
		}
		return sb.toString();
	}

	private static String tailBlank(String str) {
		if (str == null || str.length() == 0) {
			return "";
		}
		StringBuffer sb = new StringBuffer("");
		for (int i = str.length() - 1; i >= 0; i--) {
			char aChar = str.charAt(i);
			if (aChar == SPACE || aChar == TAB) {
				sb.append(aChar + "");
			} else {
				break;
			}
		}
		return sb.toString();
	}

	private static void insertExclamatoryIfNeeded(String fullPath,
			String excalmatoryPoint, int rowNumberInOriginalFile,
			StringBuffer sb, String variableName) {
		// No exclamatory
		if (excalmatoryPoint == null || excalmatoryPoint.length() == 0) {
			addIfNotNull(sb, "!");
			// property (because method has been filtered in
			// couldGoFurther() )
			if (variableName.indexOf(".") != -1) {
				// Log changed properties
				SearchAndReplace.putFullPathAndRowNumber(fullPath,
						rowNumberInOriginalFile,
						SearchAndReplace.logChangedProperty);
			}
		}
		// The exclamatory exists in original file
		else {
			addIfNotNull(sb, excalmatoryPoint);
		}
	}

	private static boolean mustBeBlankOrParenthesis(String headerOrTailer) {
		// consider as space
		if (headerOrTailer == null || headerOrTailer.length() == 0) {
			return true;
		}
		for (int i = 0; i < headerOrTailer.length(); i++) {
			char aChar = headerOrTailer.charAt(i);
			if (aChar != SPACE && aChar != TAB && aChar != L && aChar != R) {
				return false;
			}
		}
		return true;
	}

	private static void addIfNotNull(StringBuffer sb, String str) {
		if (str != null) {
			sb.append(str);
		}
	}

	/**
	 * arrayFirstEndSecondStart[0]: separator's starIndex
	 * <p>
	 * arrayFirstEndSecondStart[1]: separator's endIndex + 1
	 * 
	 * @param wholeString
	 * @param separator
	 * @return
	 */
	private static int[] separateByStr(String wholeString, String separator) {
		// First
		int[] arrayFirstEndSecondStart = { -1, -1 };
		int pos = wholeString.indexOf(separator);
		if (pos != -1) {
			// NOT include in First part
			arrayFirstEndSecondStart[0] = pos;
			// Include in second part
			arrayFirstEndSecondStart[1] = pos + separator.length();
		}
		return arrayFirstEndSecondStart;

	}

	private static boolean startsWithQuote(String trimedOneSide) {
		return trimedOneSide.startsWith(DOUBLE_QUOTE)
				|| trimedOneSide.startsWith(SINGLE_QUOTE);
	}

	private static boolean endsWithQuote(String trimedOneSide) {
		return trimedOneSide.endsWith(DOUBLE_QUOTE)
				|| trimedOneSide.endsWith(SINGLE_QUOTE);
	}

	private static int[] firstPairParenthesis(String str) {
		int[] arrayBeginEnd = { -1, -1 };
		if (str == null || str.length() < 2) {
			return arrayBeginEnd;
		}
		Stack<String> stack = new Stack<String>();
		int strLength = str.length();
		char aChar = 0;
		int pos = -1;
		while ((++pos) < strLength) {
			aChar = str.charAt(pos);
			if (aChar == L) {
				stack.push(L + "");
				// First parenthesis
				if (stack.size() == 1) {
					arrayBeginEnd[0] = pos;
				}
			} else if (aChar == R) {
				// There should be a L left. If not, too many right parenthesis
				if (stack.size() < 1) {
					// Error: parenthesis not match
					System.out.println("Too many RIGHT parenthesis: " + str);
					return arrayBeginEnd;
				}
				// stack.size()>=1
				else {
					stack.pop();
					// No L left, we've found the first pairs
					if (stack.size() == 0) {
						arrayBeginEnd[1] = pos;
						break;
					}
				}
			}
		}
		// No matching right parenthesis for the first left parenthesis
		if (stack.size() > 0) {
			// Error: parenthesis not match
			System.out.println("Too many Left parenthesis: " + str);
			return arrayBeginEnd;
		}
		return arrayBeginEnd;
	}
}
