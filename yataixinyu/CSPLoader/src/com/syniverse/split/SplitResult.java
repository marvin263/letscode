package com.syniverse.split;

public class SplitResult {
	private boolean success = false;
	// DO NOT include file header
	private int totalRowCount = 0;
	private String[] smallFileNames = null;
	private int[] smallFileRowCounts = null;

	public void setTotalRowCount(int totalRowNumber) {
		this.totalRowCount = totalRowNumber;
	}

	public int getTotalRowCount() {
		return totalRowCount;
	}

	public void setSmallFileNames(String[] smallFileNames) {
		this.smallFileNames = smallFileNames;
	}

	public String[] getSmallFileNames() {
		return smallFileNames;
	}

	public void setSmallFileRowCounts(int[] smallFileRowNumbers) {
		this.smallFileRowCounts = smallFileRowNumbers;
	}

	public int[] getSmallFileRowCounts() {
		return smallFileRowCounts;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public boolean isSuccess() {
		return success;
	}
}
