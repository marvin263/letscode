package com.syniverse.info;

import java.io.Serializable;

public abstract class AbstractFileInfo implements Serializable, IUpload {
	private static final long serialVersionUID = 1L;

	public final String filenameInDB;

	// file upload, begin
	protected long logID;
	protected int curRetryCount;
	// file upload, end
	// range, begin
	protected long rangeID = -1;
	protected String rangeLower = null;
	protected String rangeUpper = null;
	// range, end
	//
	protected long groupID;
	protected int ownerOperatorNum;
	//
	protected long totalRow;
	protected long successRow;
	protected long failRow;
	protected long dupsRow;

	public AbstractFileInfo(final String filenameInDB) {
		this.filenameInDB = filenameInDB;
	}

	public boolean isUpload() {
		return rangeID == -1 && rangeLower == null && rangeUpper == null;
	}

	/**
	 * Start from 1
	 * 
	 * Next time, we begin to process from (lastRowProcessed+1)
	 * 
	 */
	public long getLastRowProcessed() {
		return successRow + failRow + dupsRow;
	}

	public long getLogID() {
		return logID;
	}

	public void setLogID(long logID) {
		this.logID = logID;
	}

	public int getCurRetryCount() {
		return curRetryCount;
	}

	public void setCurRetryCount(int curRetryCount) {
		this.curRetryCount = curRetryCount;
	}

	public long getRangeID() {
		return rangeID;
	}

	public void setRangeID(long rangeID) {
		this.rangeID = rangeID;
	}

	public String getRangeLower() {
		return rangeLower;
	}

	public void setRangeLower(String rangeLower) {
		this.rangeLower = rangeLower;
	}

	public String getRangeUpper() {
		return rangeUpper;
	}

	public void setRangeUpper(String rangeUpper) {
		this.rangeUpper = rangeUpper;
	}

	public long getGroupID() {
		return groupID;
	}

	public void setGroupID(long groupID) {
		this.groupID = groupID;
	}

	public int getOwnerOperatorNum() {
		return ownerOperatorNum;
	}

	public void setOwnerOperatorNum(int ownerOperatorNum) {
		this.ownerOperatorNum = ownerOperatorNum;
	}

	public long getTotalRow() {
		return totalRow;
	}

	public void setTotalRow(long totalRow) {
		this.totalRow = totalRow;
	}

	public long getSuccessRow() {
		return successRow;
	}

	public void setSuccessRow(long successRow) {
		this.successRow = successRow;
	}

	public long getFailRow() {
		return failRow;
	}

	public void setFailRow(long failRow) {
		this.failRow = failRow;
	}

	public long getDupsRow() {
		return dupsRow;
	}

	public void setDupsRow(long dupsRow) {
		this.dupsRow = dupsRow;
	}
}
