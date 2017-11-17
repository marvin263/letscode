package com.syniverse.info;

public class OverviewValidateInfo {
	public static final String FAIL_REASON_common = "Process failed. Please contact Syniverse Administrator";
	public static final String DELIMITER_fail_reason = "<br>";

	public static final String FAIL_REASON_datafeed_disabled = "Data feed is disabled";
	public static final String SUGGESTION_datafeed_disabled = "Please turn data feed on first";
	public static final String FAIL_REASON_billing_id_not_exist = "Billing ID does not exist in DB table OPERATOR";
	public static final String SUGGESTION_billing_id_not_exist = "Check billing ID of this file";
	public static final String FAIL_REASON_lack_action_flag = "No action flag in the file header";
	public static final String SUGGESTION_lack_action_flag = "Add action flag into the file";
	public static final String FAIL_REASON_lack_file_header = "File header not exist";
	public static final String SUGGESTION_lack_file_header = "File header not exist";
	public static final String FAIL_REASON_file_header_not_match_dbconfig = "Wrong file format";
	public static final String SUGGESTION_file_header_not_match_dbconfig = "Download the template again";
	private boolean passed;
	private String failReason;
	private String suggestion;

	public OverviewValidateInfo() {
	}

	public boolean isPassed() {
		return passed;
	}

	public void setPassed(boolean passed) {
		this.passed = passed;
	}

	public String getFailReason() {
		return failReason;
	}

	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}

	public String getSuggestion() {
		return suggestion;
	}

	public void setSuggestion(String suggestion) {
		this.suggestion = suggestion;
	}

	@Override
	public String toString() {
		return "OverviewValidateInfo [passed=" + passed + ", failReason="
				+ failReason + ", suggestion=" + suggestion + "]";
	}

}
