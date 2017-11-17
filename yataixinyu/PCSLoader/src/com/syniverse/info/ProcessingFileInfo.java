package com.syniverse.info;

import java.io.Serializable;

public class ProcessingFileInfo extends AbstractFileInfo implements
		Serializable, IUpload {
	private static final long serialVersionUID = 1L;

	private String datafilename;

	private String datafileFullPath;
	private String datafileParentFolder;

	public ProcessingFileInfo(final String filenameInDB) {
		super(filenameInDB);
	}

	public String getDatafilename() {
		return datafilename;
	}

	public void setDatafilename(String datafilename) {
		this.datafilename = datafilename;
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

}
