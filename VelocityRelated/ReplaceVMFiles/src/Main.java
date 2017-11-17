import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class Main {
	private static final String CONFIG_FILE = "config.prop";
	private static final Set<String> setExcludeFolder = new HashSet<String>();
	private static final Set<String> setExcludeFileExtentioin = new HashSet<String>();
	private static String SCAN_THIS_FOLDER;
	private static long SIZE_10M = 10 * 1024 * 1024;

	private static void initEnvironment() {
		Properties prop = new Properties();
		try {
			FileInputStream fis = new FileInputStream(
					GetConfigFileFolder.getConfigFileFolder() + "/"
							+ CONFIG_FILE);
			prop.load(fis);
			fis.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		SCAN_THIS_FOLDER = prop.getProperty("ScanThisFolder");
		putLowCaseArray2Set(prop.getProperty("ExcludeFolder").split(";"),
				setExcludeFolder);
		putLowCaseArray2Set(prop.getProperty("ExcludeFile").split(";"),
				setExcludeFileExtentioin);
	}

	private static void putLowCaseArray2Set(String[] array, Set<String> set) {
		for (int i = 0; i < array.length; i++) {
			set.add(array[i].toLowerCase());
		}
	}

	private static boolean shouldCheckThisFile(File f) {
		String lowCaseFileName = f.getName().toLowerCase();
		if (f.isDirectory()) {
			if (setExcludeFolder.contains(lowCaseFileName)) {
				return false;
			} else {
				return true;
			}
		} else if (f.isFile()) {
			// less than 10M
			if (lessThan10M(f) == false) {
				// bigger than 10M, return false
				return false;
			}
			String extent = null;
			int lastDot = lowCaseFileName.lastIndexOf(".");
			if (lastDot != -1) {
				extent = f.getName().substring(lastDot);
			}
			if (extent != null) {
				if (setExcludeFileExtentioin.contains(extent)) {
					return false;
				} else {
					return true;
				}
			} else {
				return true;
			}
		}
		// never happen
		return false;
	}

	private static boolean lessThan10M(File f) {
		long size = f.length();
		return size <= SIZE_10M;
	}

	private static void startCheckMainFolder() {
		initEnvironment();
		handleFolder(new File(SCAN_THIS_FOLDER));
	}

	private static void log2File() {
		SearchAndReplace.flushLog();
	}

	private static void handleFolder(File fFolder) {
		File[] children = fFolder.listFiles();
		if (children == null) {
			return;
		}
		for (int i = 0; i < children.length; i++) {
			File currentFile = children[i];
			if (shouldCheckThisFile(currentFile) == true) {
				if (currentFile.isFile()) {
					handleFile(currentFile);
				} else if (currentFile.isDirectory()) {
					handleFolder(currentFile);
				}
			}
		}
	}

	private static void handleFile(File f) {
		String fullPath = f.getAbsolutePath();
		System.out.println("Checking " + fullPath + "....");
		String fileContent = FileUtil.readFromFile(fullPath,
				FileUtil.ENCODING_cp1252);
		if (fileContent == null || "".equals(fileContent)) {
			return;
		}
		String newContent = SearchAndReplace.searchAndReplace(fullPath,
				fileContent);
		if (fileContent.equals(newContent)) {
			return;
		}
		FileUtil.write2File(newContent, fullPath, FileUtil.ENCODING_cp1252,
				false);
		SearchAndReplace.logIfPermitted();
	}

	public static void doAllTask() {
		startCheckMainFolder();
		log2File();
	}

	public static void main(String[] args) {
		doAllTask();
	}
}
