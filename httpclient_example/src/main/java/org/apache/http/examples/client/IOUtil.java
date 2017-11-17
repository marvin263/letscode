package org.apache.http.examples.client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

public class IOUtil {
	public static final String ENTER = "\n";

	public static BufferedReader createBufferedReader(String fullPath,
			String charsetName) {
		BufferedReader br = null;
		try {
			if (charsetName == null) {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(fullPath)));
			} else {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(fullPath), charsetName));
			}
		} catch (Exception e) {
			// LOGGER.error("Error when createBufferedReader", e);
			throw new RuntimeException("Cannot create BufferedReader", e);
		}
		return br;
	}

	/**
	 * Read first line, and convert it to uppercase
	 * 
	 * @param fullPath
	 * @param charsetName
	 * @return
	 */
	public static String readUppercaseFirstLine(String fullPath,
			String charsetName) {
		BufferedReader br = null;
		try {
			if (charsetName == null) {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(fullPath)));
			} else {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(fullPath), charsetName));
			}
			String line = null;
			if ((line = br.readLine()) != null) {
				return line.toUpperCase();
			}
		} catch (Exception e) {
			// LOGGER.error("Error when firstLine", e);
		} finally {
			closeReader(br);
		}
		return null;
	}

	public static void writeToFile(BufferedWriter bw, String content) {
		try {
			bw.write(content);
			bw.flush();
		} catch (IOException e) {
			// LOGGER.error("Error when writeToFile", e);
		}
	}

	/**
	 * 
	 * 
	 * @param fullPath
	 * @param charsetName
	 * @return
	 */
	public static int getLineCount(String fullPath, String charsetName) {
		int count = 0;
		BufferedReader br = null;
		try {
			if (charsetName == null) {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(fullPath)));
			} else {
				br = new BufferedReader(new InputStreamReader(
						new FileInputStream(fullPath), charsetName));
			}
			while (br.readLine() != null) {
				count++;
			}
		} catch (Exception e) {
			// LOGGER.error("Error when getLineCount", e);
		} finally {
			closeReader(br);
		}
		return count;
	}

	public static boolean close(Closeable c) {
		if (c != null) {
			try {
				c.close();
			} catch (Throwable e) {
				// LOGGER.error("Error when closeIgnoringException", e);
				return false;
			}
		}
		return true;
	}

	public static void closeReader(Reader br) {
		close(br);
	}

	public static void closeWriter(Writer bw) {
		close(bw);
	}

	public static void closeWriter(Writer[] array) {
		if (array != null) {
			for (Writer writer : array) {
				closeWriter(writer);
			}
		}
	}

	public static boolean ensureFolderExist(String folderFullPath) {
		File f = new File(folderFullPath);
		if (f.exists() == false) {
			return f.mkdirs();
		}
		return true;
	}

	public static boolean ensureParentFolderExist(String fullPath) {
		File f = new File(fullPath);
		File parentFolder = f.getParentFile();
		if (parentFolder.exists() == false) {
			return parentFolder.mkdirs();
		}
		return true;
	}

	public static boolean createNewFile(String fullPath) {
		new File(fullPath).delete();
		if (ensureParentFolderExist(fullPath) == true) {
			try {
				return new File(fullPath).createNewFile();
			} catch (Exception e) {
				// LOGGER.error("Create new file fails, datafileFullPath:"
				// + fullPath, e);
				return false;
			}
		} else {
			return false;
		}
	}

	/**
	 * 1. Delete <code>to</code> first if exist
	 * 
	 * 2. Create parent folder of <code>to</code> if not exist
	 * 
	 * 3. Do rename
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	public static boolean renameTo(String from, String to) {
		// source not exist, return false
		if (new File(from).exists() == false) {
			return false;
		}
		new File(to).delete();
		if (ensureParentFolderExist(to) == true) {
			boolean renameOK = new File(from).renameTo(new File(to));
			if (renameOK == false) {
				renameOK = cut2(from, to);
			}
			return renameOK;
		} else {
			return false;
		}
	}

	/**
	 * Precondition to invoke this method is:
	 * 
	 * <code>from</code> definitely exists, <code>to</code>'s parent folder
	 * definitely exists
	 * 
	 * @param from
	 * @param to
	 * @return
	 */
	private static boolean cut2(String from, String to) {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		int length = -1;
		byte[] buf = new byte[8 * 1024];
		try {
			fis = new FileInputStream(from);
			fos = new FileOutputStream(to);
			while ((length = fis.read(buf)) != -1) {
				fos.write(buf, 0, length);
				fos.flush();
			}
			IOUtil.close(fis);
			IOUtil.close(fos);
			new File(from).delete();

		} catch (Exception e) {
			// LOGGER.error(
			// CommUtil.format("Error when cut2: [{0}]-->[{1}]", from, to),
			// e);
			return false;
		} finally {
			IOUtil.close(fis);
			IOUtil.close(fis);
		}
		return true;
	}

	public static boolean copy2(String from, String to) {
		// source not exist, return false
		if (new File(from).exists() == false) {
			return false;
		}
		new File(to).delete();
		if (ensureParentFolderExist(to) == true) {
			FileInputStream fis = null;
			FileOutputStream fos = null;
			int length = -1;
			byte[] buf = new byte[8 * 1024];
			try {
				fis = new FileInputStream(from);
				fos = new FileOutputStream(to);
				while ((length = fis.read(buf)) != -1) {
					fos.write(buf, 0, length);
					fos.flush();
				}
			} catch (Exception e) {
				// LOGGER.error(CommUtil.format("Error when copy2: [{0}]-->[{1}]",
				// from, to), e);
				return false;
			} finally {
				IOUtil.close(fis);
				IOUtil.close(fis);
			}
			return true;
		} else {
			// LOGGER.error(CommUtil.format(
			// "Cannot create parent folder when copy2: [{0}]-->[{1}]",
			// from, to));
			return false;
		}
	}

	public static String getJarStayFolder_nologinfo(Class<?> cls) {
		String jarSelf = cls.getProtectionDomain().getCodeSource()
				.getLocation().getFile();
		try {
			jarSelf = java.net.URLDecoder.decode(jarSelf, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		String jarStayFolder = new File(jarSelf).getParentFile()
				.getAbsolutePath();
		return jarStayFolder;
	}

	public static String readContent(String fullPath) {
		BufferedReader br = null;
		StringBuilder sb = new StringBuilder();
		try {
			br = new BufferedReader(new FileReader(fullPath));
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line + "\r\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(br);
		}
		return sb.toString();
	}

}
