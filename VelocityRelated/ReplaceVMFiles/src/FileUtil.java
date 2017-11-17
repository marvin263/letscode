import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class FileUtil {
	public static final String ENTER = "\r\n";
	public static final String ENCODING_cp1252 = "cp1252";

	public static void write2File(String strContent, String fullPath,
			String charsetName, boolean bAppend) {
		File file = new File(fullPath);
		createParentFolder(file);
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			fos = new FileOutputStream(file, bAppend);
			if (charsetName == null) {
				osw = new OutputStreamWriter(fos);
			} else {
				osw = new OutputStreamWriter(fos, charsetName);
			}
			bw = new BufferedWriter(osw);
			strContent = strContent == null ? "" : strContent;
			bw.write(strContent);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(bw);
			close(osw);
			close(fos);
		}
	}

	public static void copyFile(String fromFullPath, String toFullPath,
			boolean bAppend) {
		File tofile = new File(toFullPath);
		createParentFolder(tofile);
		FileInputStream fis = null;
		FileOutputStream fos = null;
		int length = -1;
		byte[] arrayBuffer = new byte[1024 * 4];
		try {
			fis = new FileInputStream(fromFullPath);
			fos = new FileOutputStream(tofile, bAppend);
			while ((length = fis.read(arrayBuffer)) != -1) {
				fos.write(arrayBuffer, 0, length);
			}
			fos.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(fos);
			close(fis);
		}
	}

	public static String readFromFile(String fullPath, String charsetName) {
		boolean endsWithWindowsEnter = endsWithEnter(fullPath);
		StringBuffer sb = new StringBuffer();
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null;
		try {
			fis = new FileInputStream(new File(fullPath));
			if (charsetName == null) {
				isr = new InputStreamReader(fis);
			} else {
				isr = new InputStreamReader(fis, charsetName);
			}
			br = new BufferedReader(isr);
			String aLine = null;
			while ((aLine = br.readLine()) != null) {
				sb.append(aLine);
				sb.append(ENTER);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			close(br);
			close(isr);
			close(fis);
		}
		int sbLength = sb.length();
		// The original file does NOT end with enter, so, we should remove the
		// last enter
		if (endsWithWindowsEnter == false
				&& sbLength >= FileUtil.ENTER.length()) {
			for (int i = 0; i < FileUtil.ENTER.length(); i++) {
				sb.deleteCharAt(sbLength - (i + 1));
			}
		}
		return sb.toString();
	}

	public static boolean endsWithEnter(String fullPath) {
		int enterLength = 1;
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(fullPath, "r");
			long length = raf.length();
			if (length < enterLength) {
				return false;
			}
			raf.seek(length - enterLength);
			char LF = (char) raf.read();
			raf.close();
			if (LF == '\n') {
				return true;
			}
			return false;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * Given file <code>f</code>, create <code>f</code>'s parent folder
	 * 
	 * @param f
	 * @return
	 */
	public static boolean createParentFolder(File f) {
		// 1. Have existed, return true
		if (f.exists()) {
			return true;
		}
		File p = f.getParentFile();
		if (p.exists()) {
			return true;
		}
		return p.mkdirs();
	}

	public static void close(Reader r) {
		if (r != null) {
			try {
				r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void close(Writer w) {
		if (w != null) {
			try {
				w.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void close(InputStream is) {
		if (is != null) {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void close(OutputStream os) {
		if (os != null) {
			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static Document getDocument(String fullPath) {
		Document document = null;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			document = db.parse(fullPath);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return document;
	}

	public static void main(String[] args) throws Exception {
		String fullPath = "C:\\abc.txt";
		RandomAccessFile raf = new RandomAccessFile(fullPath, "rw");
		raf.writeBytes("abc");
		raf.close();
	}
}
