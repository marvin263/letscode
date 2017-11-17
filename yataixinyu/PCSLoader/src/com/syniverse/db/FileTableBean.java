package com.syniverse.db;

import java.io.Serializable;

import com.syniverse.info.AbstractFileInfo;
import com.syniverse.info.IUpload;

public class FileTableBean extends AbstractFileInfo implements Serializable,
		IUpload {
	private static final long serialVersionUID = 1L;

	public static final String Rejected = "Failed";
	public static final String Queued = "Queued";
	public static final String Processing = "Processing";
	public static final String Completed = "Completed";
	/**
	 * During execution, unfortunately, we encounter exception
	 */
	public static final String Exception = "Failed";

	private String processStatus;

	public FileTableBean(final String filenameInDB) {
		super(filenameInDB);
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

}
