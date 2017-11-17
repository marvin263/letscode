package com.syniverse.info;

import java.io.Serializable;

public class SplitFileInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	public static OriginalFileInfo orgnfInfo;

	private String processStatus;
	private String splitfilename;
	/**
	 * Start from 1
	 * 
	 * Next time, we begin to process from (lastRowProcessed+1)
	 * 
	 */
	private int lastRowProcessed;
	/**
	 * Total row count of this split file
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

	public static OriginalFileInfo getOrgnfInfo() {
		return orgnfInfo;
	}

	public static void setOrgnfInfo(OriginalFileInfo orfInfo) {
		SplitFileInfo.orgnfInfo = orfInfo;
	}

	public String getSplitfilename() {
		return splitfilename;
	}

	public String getSplitFullPath() {
		return orgnfInfo.getDatafileParentFolder() + "/" + splitfilename;
	}

	public void setSplitfilename(String splitfilename) {
		this.splitfilename = splitfilename;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public void lastProcessRowIncre1() {
		lastRowProcessed++;
	}

}
