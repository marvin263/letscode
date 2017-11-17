package com.syniverse.db;

import java.io.Serializable;

public class FeedLogTableBean implements Serializable {
	private static final long serialVersionUID = 1L;

	public static final String Rejected = "Failed";
	public static final String Queued = "Queued";
	public static final String Processing = "Processing";
	public static final String Completed = "Completed";
	/**
	 * During execution, unfortunately, we encounter exception
	 */
	public static final String Exception = "Failed";

	private long logID;

	private String billingID;
	/**
	 * file name store in FEED_LOG.NAME
	 * 
	 * user uploaded file name no matter zipped or not
	 * 
	 */
	private String filenameInDB;
	private String feedSource;

	public void setFeedSource(String feedSource) {
		this.feedSource = feedSource;
	}

	public String getFeedSource() {
		return feedSource;
	}

	private String listID;

	public long getLogID() {
		return logID;
	}

	public void setLogID(long logID) {
		this.logID = logID;
	}

	public String getBillingID() {
		return billingID;
	}

	public void setBillingID(String billingID) {
		this.billingID = billingID;
	}

	public String getFilenameInDB() {
		return filenameInDB;
	}

	public void setFilenameInDB(String filename) {
		this.filenameInDB = filename;
	}

	public String getListID() {
		return listID;
	}

	public void setListID(String listID) {
		this.listID = listID;
	}

	public int getVersionNO() {
		return versionNO;
	}

	public void setVersionNO(int versionNO) {
		this.versionNO = versionNO;
	}

	public String getProcessStatus() {
		return processStatus;
	}

	public void setProcessStatus(String processStatus) {
		this.processStatus = processStatus;
	}

	public int getSuccessRow() {
		return successRow;
	}

	public void setSuccessRow(int successRow) {
		this.successRow = successRow;
	}

	public int getFailRow() {
		return failRow;
	}

	public void setFailRow(int failRow) {
		this.failRow = failRow;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getReceiveTime() {
		return receiveTime;
	}

	public void setReceiveTime(long receiveTime) {
		this.receiveTime = receiveTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getUpdatedTime() {
		return updatedTime;
	}

	public void setUpdatedTime(long updatedTime) {
		this.updatedTime = updatedTime;
	}

	private int versionNO;
	private String processStatus;
	private int totalRow;

	public int getTotalRow() {
		return totalRow;
	}

	public void setTotalRow(int totalRow) {
		this.totalRow = totalRow;
	}

	private int successRow;
	private int failRow;

	// GUI/BATCH
	private String type;

	/**
	 * Available only when type is GUI
	 */
	private String uploadedby;
	
	private String failReason;

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	/**
	 * time received this file
	 */
	private long receiveTime;
	private long startTime;
	private long endTime;
	private long updatedTime;

	public String getUploadedby() {
		return uploadedby;
	}

	public void setUploadedby(String uploadedby) {
		this.uploadedby = uploadedby;
	}

	@Override
	public String toString() {
		return "FeedLogTableBean [logID=" + logID + ", billingID=" + billingID
				+ ", filenameInDB=" + filenameInDB + ", feedSource="
				+ feedSource + ", listID=" + listID + ", versionNO="
				+ versionNO + ", processStatus=" + processStatus
				+ ", totalRow=" + totalRow + ", successRow=" + successRow
				+ ", failRow=" + failRow + ", type=" + type + ", uploadedby="
				+ uploadedby + ", failReason=" + failReason + ", receiveTime="
				+ receiveTime + ", startTime=" + startTime + ", endTime="
				+ endTime + ", updatedTime=" + updatedTime + "]";
	}
}
