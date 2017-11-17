import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchAndReplace {
	private static long THRESHOLD_DO_LOG = 500;

	private static final String REG_EXPRESSION = "((#if)|(#elseif))(\\s*)\\(.*\\)";
	private static final Pattern PTN = Pattern.compile(REG_EXPRESSION,
			Pattern.CASE_INSENSITIVE);
	private static final Pattern PTN_FIND_ROW_NUMBER = Pattern.compile("\n",
			Pattern.LITERAL);

	// All the changed
	public static LogInfo logAllChangedFiles = new LogInfo();
	// Pay special attention
	public static LogInfo logNotStartEndEmpty_Attention = new LogInfo();
	// Changed numbers
	public static LogInfo logChangedNumbers = new LogInfo();
	// Changed Properties
	public static LogInfo logChangedProperty = new LogInfo();
	// Encountered method (Not change it)
	public static LogInfo logMethod = new LogInfo();

	public static String[] arrayAllAttentionNumbersPropertyMethod = new String[5];
	static {
		String parentFolder = GetConfigFileFolder.getExportedFolder() + "/";
		String timestamp = new SimpleDateFormat("yyyy-MM-dd HH mm ss")
				.format(new Date());
		String replaceFullPathPrefix = parentFolder + timestamp + "_";
		arrayAllAttentionNumbersPropertyMethod[0] = replaceFullPathPrefix
				+ "1_All_Changed_Files.txt";
		arrayAllAttentionNumbersPropertyMethod[1] = replaceFullPathPrefix
				+ "2_Pay_Attention.txt";
		arrayAllAttentionNumbersPropertyMethod[2] = replaceFullPathPrefix
				+ "3_Changed_Numbers.txt";
		arrayAllAttentionNumbersPropertyMethod[3] = replaceFullPathPrefix
				+ "4_Changed_Properties.txt";
		arrayAllAttentionNumbersPropertyMethod[4] = replaceFullPathPrefix
				+ "5_Encountered_Method.txt";
	}

	public static String searchAndReplace(String fullPath, String fileContent) {
		Matcher m = PTN.matcher(fileContent);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
			int start_0 = m.start();
			String strIfElseifLine = m.group();
			// the row number in the original file
			int rowNumberInOriginalFile = findRowNumber(fileContent, start_0);
			String newLine = HandleIfElseIfLine.replaceWholeIfElseif(fullPath,
					strIfElseifLine, rowNumberInOriginalFile);
			m.appendReplacement(sb, Matcher.quoteReplacement(newLine));
			// Log it ONLY IF it has been changed
			if (!strIfElseifLine.equals(newLine)) {
				putFullPathAndRowNumber(fullPath, rowNumberInOriginalFile,
						logAllChangedFiles);
			}
		}
		m.appendTail(sb);
		return sb.toString();
	}

	public static void putFullPathAndRowNumber(String fullPath,
			int rowNumberInOriginalFile, LogInfo logInfo) {
		Map<String, TreeSet<Integer>> mapFullPath2TreeSet = logInfo.mapFullPath2TreeSet;
		if (!mapFullPath2TreeSet.containsKey(fullPath)) {
			TreeSet<Integer> setRowNumberInOriginalFile = new TreeSet<Integer>();
			mapFullPath2TreeSet.put(fullPath, setRowNumberInOriginalFile);
		}
		TreeSet<Integer> setRowNumber = mapFullPath2TreeSet.get(fullPath);
		setRowNumber.add(rowNumberInOriginalFile);
	}

	/**
	 * The first \n belongs to the first line
	 * 
	 * @param fileContent
	 * @param start_0
	 * @return
	 */
	private static int findRowNumber(String fileContent, int start_0) {
		Matcher m = PTN_FIND_ROW_NUMBER.matcher(fileContent);
		int currentRow = 0;
		while (m.find()) {
			int nowStart = m.start();
			currentRow++;
			if (nowStart >= start_0) {
				return currentRow;
			}
		}
		return 1;// No line found, then, start_0 belongs to the first line
	}

	private static String createStrAffectedLines(
			TreeSet<Integer> setRowNumberInOriginalFile) {
		StringBuffer sb = new StringBuffer();
		Iterator<Integer> itRowNumber = setRowNumberInOriginalFile.iterator();
		while (itRowNumber.hasNext()) {
			Integer aNumber = (Integer) itRowNumber.next();
			sb.append(aNumber.intValue() + ", ");
		}
		return sb.toString();
	}

	public static void logIfPermitted() {
		logIfPermitted(logAllChangedFiles,
				arrayAllAttentionNumbersPropertyMethod[0]);
		logIfPermitted(logNotStartEndEmpty_Attention,
				arrayAllAttentionNumbersPropertyMethod[1]);
		logIfPermitted(logChangedNumbers,
				arrayAllAttentionNumbersPropertyMethod[2]);
		logIfPermitted(logChangedProperty,
				arrayAllAttentionNumbersPropertyMethod[3]);
		logIfPermitted(logMethod, arrayAllAttentionNumbersPropertyMethod[4]);
	}

	private static void logIfPermitted(LogInfo logInfo, String fullPath) {
		if (logInfo.mapFullPath2TreeSet.size() >= THRESHOLD_DO_LOG) {
			String strContent = createStringByLogInfo(logInfo);
			FileUtil.write2File(strContent, fullPath, FileUtil.ENCODING_cp1252,
					true);
			// clear it
			logInfo.mapFullPath2TreeSet.clear();
		}
	}

	public static void flushLog() {
		flushLog(logAllChangedFiles, arrayAllAttentionNumbersPropertyMethod[0]);
		flushLog(logNotStartEndEmpty_Attention,
				arrayAllAttentionNumbersPropertyMethod[1]);
		flushLog(logChangedNumbers, arrayAllAttentionNumbersPropertyMethod[2]);
		flushLog(logChangedProperty, arrayAllAttentionNumbersPropertyMethod[3]);
		flushLog(logMethod, arrayAllAttentionNumbersPropertyMethod[4]);
	}

	private static void flushLog(LogInfo logInfo, String fullPath) {
		// 1. write the left log into log file
		String strContent = createStringByLogInfo(logInfo);
		FileUtil.write2File(strContent, fullPath, FileUtil.ENCODING_cp1252,
				true);
		// 2. Create summary
		StringBuffer sb = new StringBuffer();
		sb.append(new File(fullPath).getName() + FileUtil.ENTER);
		sb.append("Total Lines affected: " + logInfo.totalAffectedLines
				+ " lines" + FileUtil.ENTER);
		sb.append("Total Files changed: "
				+ logInfo.lastUsedsequenceNumberInLogFile + " files"
				+ FileUtil.ENTER);
		sb.append(FileUtil.ENTER + FileUtil.ENTER);
		// create summary file
		String tempFullPath = fullPath + "_temp";
		FileUtil.write2File(sb.toString(), tempFullPath,
				FileUtil.ENCODING_cp1252, false);
		// 3. append logfile to summary file
		FileUtil.copyFile(fullPath, tempFullPath, true);
		// 4. Delete logfile, and rename to get a new one
		new File(fullPath).delete();
		new File(tempFullPath).renameTo(new File(fullPath));
		// 5. clear the log map
		logInfo.mapFullPath2TreeSet.clear();
	}

	/**
	 * Only the files whose content have been modified would be output
	 * 
	 * @return
	 */
	private static String createStringByLogInfo(LogInfo logInfo) {
		Map<String, TreeSet<Integer>> map = logInfo.mapFullPath2TreeSet;
		long lastSequence = logInfo.lastUsedsequenceNumberInLogFile;
		long affectedLinesThisTimeLogToFile = 0;
		StringBuffer sb = new StringBuffer();
		Iterator<String> itFullPath = map.keySet().iterator();
		while (itFullPath.hasNext()) {
			lastSequence++;
			String fullPath = (String) itFullPath.next();
			TreeSet<Integer> setRowNumberInOriginalFile = map.get(fullPath);
			affectedLinesThisTimeLogToFile = affectedLinesThisTimeLogToFile
					+ setRowNumberInOriginalFile.size();

			sb.append(lastSequence);
			sb.append(", Affected Lines: "
					+ setRowNumberInOriginalFile.size() + ", ");
			sb.append(fullPath + ", ");
			String strAffectedLine = createStrAffectedLines(setRowNumberInOriginalFile);
			sb.append(strAffectedLine);
			sb.append(FileUtil.ENTER);
		}
		// update log file's row sequence number
		logInfo.lastUsedsequenceNumberInLogFile = lastSequence;
		// update total affected Lines
		logInfo.totalAffectedLines = logInfo.totalAffectedLines
				+ affectedLinesThisTimeLogToFile;
		return sb.toString();
	}

	public static void main(String[] args) {
		Matcher m = PTN_FIND_ROW_NUMBER.matcher("\\\\\\\\\\\\wwwwwwabc");
		while (m.find()) {
			System.out.println(m.group());
		}
	}
}
