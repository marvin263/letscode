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

	public static void writeWarning_info(BufferedWriter bw, List<EachRowInfo> list) {
		writeToFile(bw, list, "Warning");
	}

	public static void writeFailedValidation_error(BufferedWriter bw,
			List<EachRowInfo> list) {
		writeToFile(bw, list, "Error");
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
			sb.append(errorOrWarning + ": " + erInfo.getValidationMsg());
			sb.append("----" + erInfo.originalLine + IOUtil.ENTER);
		}
		return sb.toString();
	}
}
