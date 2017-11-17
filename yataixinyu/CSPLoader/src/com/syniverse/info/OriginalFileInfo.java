package com.syniverse.info;

import java.io.Serializable;

public class OriginalFileInfo implements Serializable {
	public static final String FEED_SOURCE_EXTERNAL = "external";
	public static final String FEED_SOURCE_INTERNAL = "internal";
	private static final long serialVersionUID = 1L;

	private long logID;
	private String billingID;
	/**
	 * FEED_SOURCE_EXTERNAL, or FEED_SOURCE_INTERNAL
	 */
	private String feedSource;

	/**
	 * DO NOT including file header
	 * 
	 * Total row count of this original file.
	 */
	private int originalFileTotalRowCount;

	/**
	 * this value is from FEED_LOG.LIST_ID, which is stored there by
	 * LoopFolderService
	 */
	private String listID;

	/**
	 * this value is from FEED_LOG.VERSION_NUM, which is stored there by
	 * LoopFolderService
	 */
	private int versionNO;

	/**
	 * 1. DO NOT include Action
	 * <p>
	 * 2. Each element is table column name (NOT display name)
	 * 
	 */
	private String commaSplitFileheader;

	/**
	 * Delimiter has nothing to do with listID/versionNo
	 * <p>
	 * At any time, we always only have one delimiter which is stored in
	 * attribute_config.DELIMITER
	 * 
	 */
	private String delimiter;

	/**
	 * datafile full path in working directory
	 */
	private String datafileFullPath;

	/**
	 * the parent folder of datafile which is in working directory
	 */
	private String datafileParentFolder;

	/**
	 * datafilename in working directory
	 */
	private String datafilename;

	/**
	 * zipfilename in working directory. If NOT a zipped file, this is null
	 */
	private String zipfilename;

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

	public String getFeedSource() {
		return feedSource;
	}

	public void setFeedSource(String feedSource) {
		this.feedSource = feedSource;
	}

	public int getOriginalFileTotalRowCount() {
		return originalFileTotalRowCount;
	}

	public void setOriginalFileTotalRowCount(int originalFileTotalRowCount) {
		this.originalFileTotalRowCount = originalFileTotalRowCount;
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

	/**
	 * 1. DO NOT include Action
	 * <p>
	 * 2. Each element is table column name (NOT display name)
	 * 
	 */
	public String getCommaSplitFileheader() {
		return commaSplitFileheader;
	}

	public void setCommaSplitFileheader(String commaSplitFileheader) {
		this.commaSplitFileheader = commaSplitFileheader;
	}

	/**
	 * Delimiter has nothing to do with listID/versionNo
	 * <p>
	 * At any time, we always only have one delimiter which is stored in
	 * attribute_config.DELIMITER
	 * 
	 * @return
	 */
	public String getDelimiter() {
		return delimiter;
	}

	public void setDelimiter(String delimiter) {
		this.delimiter = delimiter;
	}

	public String getDatafileFullPath() {
		return datafileFullPath;
	}

	public void setDatafileFullPath(String datafileFullPath) {
		this.datafileFullPath = datafileFullPath;
	}

	public String getDatafileParentFolder() {
		return datafileParentFolder;
	}

	public void setDatafileParentFolder(String datafileParentFolder) {
		this.datafileParentFolder = datafileParentFolder;
	}

	public String getDatafilename() {
		return datafilename;
	}

	public void setDatafilename(String datafilename) {
		this.datafilename = datafilename;
	}

	public String getZipfilename() {
		return zipfilename;
	}

	public void setZipfilename(String zipfilename) {
		this.zipfilename = zipfilename;
	}

	@Override
	public String toString() {
		return "OriginalFileInfo [logID=" + logID + ", billingID=" + billingID
				+ ", feedSource=" + feedSource + ", originalFileTotalRowCount="
				+ originalFileTotalRowCount + ", listID=" + listID
				+ ", versionNO=" + versionNO + ", commaSplitFileheader="
				+ commaSplitFileheader + ", delimiter=" + delimiter
				+ ", datafileFullPath=" + datafileFullPath
				+ ", datafileParentFolder=" + datafileParentFolder
				+ ", datafilename=" + datafilename + ", zipfilename="
				+ zipfilename + "]";
	}

}
