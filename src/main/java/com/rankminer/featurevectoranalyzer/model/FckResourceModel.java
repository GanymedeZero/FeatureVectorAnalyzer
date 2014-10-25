package com.rankminer.featurevectoranalyzer.model;

/**
 * Data mapper class which is used to transfer data
 * to and from FCK_RESOURCE table. This only has subset of the fck_resource table.
 * @author achavan
 *
 */
public class FckResourceModel {
	private int fileId;
	private String fileStatus;
	private String metaDataStatus;
	
	public int getFileId() {
		return fileId;
	}
	public void setFileId(int fileId) {
		this.fileId = fileId;
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
}
