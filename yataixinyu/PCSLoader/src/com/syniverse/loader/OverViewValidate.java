package com.syniverse.loader;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.syniverse.info.OverviewValidateInfo;
import com.syniverse.info.ProcessingFileInfo;

public class OverViewValidate {
	private static final Log LOGGER = LogFactory.getLog(OverViewValidate.class);
	public static final String FIRST_FILE_COLUMN = "ACTION";
	private final Connection conn;
	private final ProcessingFileInfo pfInfo;

	public OverViewValidate(Connection conn, ProcessingFileInfo orgnfInfo) {
		this.conn = conn;
		this.pfInfo = orgnfInfo;
	}

	public OverviewValidateInfo validate() {
		OverviewValidateInfo ovfi = new OverviewValidateInfo();
		ovfi.setPassed(false);

		ovfi.setPassed(true);
		ovfi.setFailReason(null);
		ovfi.setSuggestion(null);
		return ovfi;
	}

	public static void main(String[] args) {
		List<String> list = new ArrayList<String>();
		list.add("adfdf");
		list.add("b");
		list.add("d");
		System.out.println(list.toString());
	}
}
