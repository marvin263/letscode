package com.syniverse.split;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.common.CommUtil;
import com.syniverse.io.IOUtil;

public class FileSplitter {
	private static final Log LOGGER = LogFactory.getLog(FileSplitter.class);
	private List<String> headerList = null;
	private int headerSize = 0;
	private String delimiter = ",";
	private Pattern integerPattern = Pattern.compile("^[0-9]+$");

	public FileSplitter(String attrList, String delimiter) {
		this.delimiter = delimiter;
		this.headerList = CommUtil.split(attrList.toUpperCase(), delimiter);
		this.headerSize = headerList.size();
	}

	private boolean isInteger(String value) {
		Matcher mat = integerPattern.matcher(value);
		return mat.find();
	}

	private String getSubscriberKey(String record) {
		List<String> columnValues = CommUtil.split(record, delimiter);
		if (columnValues.size() != headerSize) {
			return null;
		}

		String value = null;
		String subscriberKey = null;
		String technologyType = "";
		String IMSI = "";
		String MIN = "";
		String MDN = "";

		for (int i = 0; i < headerSize; i++) {
			value = columnValues.get(i);

			if ("TECHNOLOGY_TYPE".equals(headerList.get(i))) {
				if ("GSM".equalsIgnoreCase(value)) {
					technologyType = "GSM";
				} else if ("CDMA".equalsIgnoreCase((value))) {
					technologyType = "CDMA";
				}
			} else if ("IMSI".equals(headerList.get(i))) {
				if (isInteger(value)) {
					IMSI = value;
				}
			} else if ("MIN".equals(headerList.get(i))) {
				if (isInteger(value)) {
					MIN = value;
				}
			} else if ("MDN".equals(headerList.get(i))) {
				if (isInteger(value)) {
					MDN = value;
				}
			}
		}

		if ("GSM".equals(technologyType)) {
			if (IMSI.length() > 0) {
				subscriberKey = IMSI;
			}
		} else if ("CDMA".equals(technologyType)) {
			if (MIN.length() > 0) {
				subscriberKey = MIN;
			} else if (MDN.length() > 0) {
				subscriberKey = MDN;
			}
		}

		return subscriberKey;
	}

	public SplitResult splitFile(String filePath, int count) {
		long start = System.currentTimeMillis();

		SplitResult splitResult = new SplitResult();
		BufferedReader reader = null;
		BufferedWriter[] writerArray = new BufferedWriter[count];
		String line = null;
		String subscriberKey = null;
		int remainder = 0;
		int totalRowCount = 0;
		String[] smallFileNames = new String[count];
		int[] smallFileRowCounts = new int[count];
		String orginalfilename = new File(filePath).getName();
		for (int i = 0; i < count; i++) {
			smallFileNames[i] = orginalfilename + "_part" + i;
		}

		try {
			reader = new BufferedReader(new FileReader(new File(filePath)));

			for (int i = 0; i < count; i++) {
                writerArray[i] =
                        new BufferedWriter(new FileWriter(filePath + "_part" + i));
			}

			// skip header
			reader.readLine();

			while ((line = reader.readLine()) != null) {
				totalRowCount++;
				line = line.trim();
				subscriberKey = getSubscriberKey(line);

				if (subscriberKey == null) {
					smallFileRowCounts[0]++;
					writerArray[0].write(line);
					writerArray[0].newLine();
				} else {
					remainder = (int) (Long.parseLong(subscriberKey) % count);
					smallFileRowCounts[remainder]++;
					writerArray[remainder].write(line);
					writerArray[remainder].newLine();
				}
			}

			splitResult.setTotalRowCount(totalRowCount);
			splitResult.setSmallFileNames(smallFileNames);
			splitResult.setSmallFileRowCounts(smallFileRowCounts);
			splitResult.setSuccess(true);
		} catch (Exception e) {
			LOGGER.error("Error when split file: " + filePath, e);
		} finally {
			IOUtil.closeReader(reader);
			IOUtil.closeWriter(writerArray);
		}
		return splitResult;
	}

	public static void main(String[] args) throws Exception {
		String attrList = "ACTION,MIN,MDN,IMSI,TECHNOLOGY_TYPE,DEVICE_TYPE,DEVICE_CATEGORY";
		String delimiter = ",";

		FileSplitter splitter = new FileSplitter(attrList, delimiter);

		System.out.println("start split");

		SplitResult result = splitter.splitFile(
				"E:\\sample_data\\02075_0818201301010000", 5);

		if (result.isSuccess()) {
			System.out.println("total: " + result.getTotalRowCount());
			for (int i = 0; i < result.getSmallFileNames().length; i++) {
				System.out.println(result.getSmallFileNames()[i]);
				System.out.println(result.getSmallFileRowCounts()[i]);
			}
		}
	}
}
