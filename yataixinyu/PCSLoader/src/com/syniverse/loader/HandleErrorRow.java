package com.syniverse.loader;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.common.CommUtil;
import com.syniverse.info.EachRowInfo;
import com.syniverse.io.IOUtil;

public class HandleErrorRow {
	private static final Log LOGGER = LogFactory.getLog(HandleErrorRow.class);

	public static void write_dups(BufferedWriter bw, List<EachRowInfo> list) {
		writeToFile(bw, list, "");
	}

	public static void write_errors(BufferedWriter bw, List<EachRowInfo> list) {
		writeToFile(bw, list, "");
	}

	private static void writeToFile(BufferedWriter bw, List<EachRowInfo> list,
			String errorOrWarning) {
		String content = assembleOriginalLine(list, errorOrWarning);
		if (CommUtil.isEmpty(content) == false) {
			try {
				bw.write(content);
				bw.flush();
			} catch (IOException e) {
				LOGGER.error("Error when writeToFile", e);
			}
		}
	}

	private static String assembleOriginalLine(List<EachRowInfo> list,
			String errorOrWarning) {
		StringBuilder sb = new StringBuilder("");
		for (EachRowInfo erInfo : list) {
			sb.append(erInfo.subkey);
			sb.append("----Original line number: "
					+ erInfo.lineNumberInOrgnFile);
			sb.append(", Message: " + erInfo.getNotSuccessMsg() + IOUtil.ENTER);
		}
		return sb.toString();
	}
}
