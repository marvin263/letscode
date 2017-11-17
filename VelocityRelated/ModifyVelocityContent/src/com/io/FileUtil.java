package com.io;

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

	public static void write2File(String strContent, String fullPath,
			String charsetName) {
		File file = new File(fullPath);
		createParentFolder(file);
		FileOutputStream fos = null;
		OutputStreamWriter osw = null;
		BufferedWriter bw = null;
		try {
			fos = new FileOutputStream(file);
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

	public static String readFromFile(String fullPath, String charsetName) {
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
		return sb.toString();
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
}
