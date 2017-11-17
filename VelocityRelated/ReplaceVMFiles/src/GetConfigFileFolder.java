import java.io.File;

public class GetConfigFileFolder {
	public static final String EXPORT_FILES = "Replace_Log";
	private static String exportedFolder = null;
	static {
		exportedFolder = System.getProperty("user.dir") + "/" + EXPORT_FILES;
	}

	public static String getConfigFileFolder() {
		// Something like: java.lang.String
		String quantifiedName = GetConfigFileFolder.class.getName();
		// Something like: java/lang/String.class
		String strClassQuantifiedName = quantifiedName.replaceAll("\\.", "/")
				+ ".class";

		String classFileAbsPath = GetConfigFileFolder.class.getClassLoader()
				.getResource(strClassQuantifiedName).getPath();
		// File object of this class file
		File classFile = new File(classFileAbsPath);
		File classFileParent = classFile.getParentFile();
		String classFileParentAbsPath = classFileParent.getAbsolutePath();
		// System.out.println(classFileParentAbsPath);
		return classFileParentAbsPath;
	}

	public static String getExportedFolder() {
		return exportedFolder;
	}

	public static void main(String[] args) {
		getConfigFileFolder();
	}
}
