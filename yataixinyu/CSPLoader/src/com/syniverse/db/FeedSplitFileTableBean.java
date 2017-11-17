package com.syniverse.db;

import java.io.Serializable;

public class FeedSplitFileTableBean implements Serializable {
	public static final String Completed = "Completed";
	public static final String Processing = "Processing";
	/**
	 * During executing this split file, unfortunately, we encounter exception
	 */
	public static final String Exception = "Exception";

	private static final long serialVersionUID = 1L;

	private long logID;

	private String processStatus;
	private String filename;

	/**
	 * Next time, we begin to process from (lastRowProcessed+1). Start from 1
	 * 
	 */
	private int lastRowProcessed;

	/**
	 * Next time, we begin to process from (lastRowProcessed+1). Start from 1
	 * 
	 */
	private int totalCount;
	/**
	 * insert/update/delete successfully
	 */
	private int successCount;

	/**
	 * Add to batch, but execute failed. insert/update/delete failed
	 * 
	 * some imsi not exist, delete this imsi (we suppose execution successfully)
	 * 
	 * Validator should tell us this row DOES NOT pass validation, so, actually
	 * we won't execute sql which delete an non-existed imsi
	 * 
	 */
	private int failCount;

	public long getLogID() {
		return logID;
	}

	public void setLogID(long logID) {
		this.logID = logID;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public int getLastRowProcessed() {
		return lastRowProcessed;
	}

	public void setLastRowProcessed(int lastRowProcessed) {
		this.lastRowProcessed = lastRowProcessed;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getSuccessCount() {
		return successCount;
	}

	public void setSuccessCount(int successCount) {
		this.successCount = successCount;
	}

	public int getFailCount() {
		return failCount;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
}
