package com.syniverse.info;

import java.io.Serializable;

public class LResultInfo implements Serializable {
	private static final long serialVersionUID = 1L;
	public final String splitfilename;

	/**
	 * 1. NOT means all rows are successfully
	 * 
	 * 2. It indicates whether we encounter exception during execution.
	 * 
	 * No Exception-->true, otherwise-->false
	 * 
	 */
	private boolean finalSuccess;
	private String failReason;

	public LResultInfo(final String splitfilename) {
		this.splitfilename = splitfilename;
		finalSuccess = true;
	}

	public boolean isFinalSuccess() {
		return finalSuccess;
	}

	public void setFinalSuccess(boolean finalSuccess) {
		this.finalSuccess = finalSuccess;
	}

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	@Override
	public String toString() {
		return "LResultInfo [splitfilename=" + splitfilename
				+ ", finalSuccess=" + finalSuccess + ", failReason="
				+ failReason + "]";
	}

}
