package com.rankminer.featurevectoranalyzer.model;

/**
 * Data mapper class which is used to transfer data
 * to and from FCK_RESOURCE table. This only has subset of the fck_resource table.
 * @author achavan
 *
 */
public class FckResourceModel {
	private long fileId;
	private String fileStatus;
	private String metaDataStatus;
	
	public long getFileId() {
		return fileId;
	}
	
	public String getFileStatus() {
		return fileStatus;
	}
	public void setFileStatus(String fileStatus) {
		this.fileStatus = fileStatus;
	}
	public String getMetaDataStatus() {
		return metaDataStatus;
	}
	public void setMetaDataStatus(String metaDataStatus) {
		this.metaDataStatus = metaDataStatus;
	}
	public void setFileId(long value) {
		this.fileId = value;
	}
}
