package com.rankminer.featurevectoranalyzer.model;

import java.sql.Date;
import java.sql.Timestamp;

/**
 * Class represents the metadata table
 * @author achavan
 *
 */
public class MetaDataModel {
	
	private int md_id;
	private String officeNo;
	private String fileNumber;
	private String aapl;
	private String filler;
	private String recordStatus;
	private Date callDate;
	private String tsr;
	private int duration;
	private String filePath;
	private String sampleRate;
	private String orderNumber;
	private String recordAddStatus;
	private String listId;
	private Timestamp startTime;
	private Timestamp endTime;
	private String station;
	private String deviceName;
	
	
	
	public String getOfficeNo() {
		return officeNo;
	}
	public void setOfficeNo(String officeNo) {
		this.officeNo = officeNo;
	}
	public String getFileNumber() {
		return fileNumber;
	}
	public void setFileNumber(String fileNumber) {
		this.fileNumber = fileNumber;
	}
	public String getAapl() {
		return aapl;
	}
	public void setAapl(String aapl) {
		this.aapl = aapl;
	}
	public String getFiller() {
		return filler;
	}
	public void setFiller(String filler) {
		this.filler = filler;
	}
	public String getRecordStatus() {
		return recordStatus;
	}
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	public Date getCallDate() {
		return callDate;
	}
	public void setCallDate(Date callDate) {
		this.callDate = callDate;
	}
	public String getTsr() {
		return tsr;
	}
	public void setTsr(String tsr) {
		this.tsr = tsr;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getSampleRate() {
		return sampleRate;
	}
	public void setSampleRate(String sampleRate) {
		this.sampleRate = sampleRate;
	}
	public String getOrderNumber() {
		return orderNumber;
	}
	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}
	public String getRecordAddStatus() {
		return recordAddStatus;
	}
	public void setRecordAddStatus(String recordAddStatus) {
		this.recordAddStatus = recordAddStatus;
	}
	public String getListId() {
		return listId;
	}
	public void setListId(String listId) {
		this.listId = listId;
	}
	public Timestamp getStartTime() {
		return startTime;
	}
	public void setStartTime(Timestamp startTime) {
		this.startTime = startTime;
	}
	public Timestamp getEndTime() {
		return endTime;
	}
	public void setEndTime(Timestamp endTime) {
		this.endTime = endTime;
	}
	public String getStation() {
		return station;
	}
	public void setStation(String station) {
		this.station = station;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	
	public int getMd_id() {
		return md_id;
	}
	public void setMd_id(int md_id) {
		this.md_id = md_id;
	}

	public static class MetaDataModelBuilder {
		private MetaDataModel model;
		
		public MetaDataModelBuilder() {
			model = new MetaDataModel();
		}
		
		public MetaDataModelBuilder setMdId(int mdId) {
			model.setMd_id(mdId);
			return this;
		}
		
		public MetaDataModelBuilder setOfficeNo(String officeNo) {
			model.setOfficeNo(officeNo);
			return this;
		}

		public MetaDataModelBuilder setFileNumber(String fileNumber) {
			model.setFileNumber(fileNumber);
			return this;
		}

		public MetaDataModelBuilder setAapl(String aapl) {
			model.setAapl(aapl);
			return this;
		}

		public MetaDataModelBuilder setFiller(String filler) {
			model.setFiller(filler);
			return this;
		}

		public MetaDataModelBuilder setRecordStatus(String recordStatus) {
			model.setRecordStatus(recordStatus);
			return this;
		}

		public MetaDataModelBuilder setCallDate(Date callDate) {
			model.setCallDate(callDate);
			return this;
		}

		public MetaDataModelBuilder setTsr(String tsr) {
			model.setTsr(tsr);
			return this;
		}

		public MetaDataModelBuilder setDuration(int duration) {
			model.setDuration(duration);
			return this;
		}

		public MetaDataModelBuilder setFilePath(String filePath) {
			model.setFilePath(filePath);
			return this;
		}

		public MetaDataModelBuilder setSampleRate(String sampleRate) {
			model.setSampleRate(sampleRate);
			return this;
		}

		public MetaDataModelBuilder setOrderNumber(String orderNumber) {
			model.setOrderNumber(orderNumber);
			return this;
		}

		public MetaDataModelBuilder setRecordAddStatus(String recordAddStatus) {
			model.setRecordAddStatus(recordAddStatus);
			return this;
		}

		public MetaDataModelBuilder setListId(String listId) {
			model.setListId(listId);
			return this;
		}

		public MetaDataModelBuilder setStartTime(Timestamp startTime) {
			model.setStartTime(startTime);
			return this;
		}
		
		public MetaDataModelBuilder setEndTime(Timestamp endTime) {
			model.setEndTime(endTime);
			return this;
		}

		public MetaDataModelBuilder setStation(String station) {
			model.setStation(station);
			return this;
		}
		
		public MetaDataModelBuilder setDeviceName(String deviceName) {
			model.setDeviceName(deviceName);
			return this;
		}
		
		public MetaDataModel build() {
			return model;
		}
	}
}
