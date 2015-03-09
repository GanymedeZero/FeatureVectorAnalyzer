package com.rankminer.featurevectoranalyzer.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "copyConfig", propOrder = {
    "destinationFolder",
    "sourceFolder",
    "status"
})
public class CopyConfig {
	
	@XmlElement(required = true)
	protected String destinationFolder;
	@XmlElement(required = true)
	protected String sourceFolder;
	
	@XmlElement(required=true) 
	protected int status;
	
	public int getStatus() {
		return status;
	}
	
	public void setStatus(int s) {
		status = s;
	}
	
	public String getDestinationFolder() {
		return destinationFolder;
	}
	public void setDestinationFolder(String destinationFolder) {
		this.destinationFolder = destinationFolder;
	}
	public String getSourceFolder() {
		return sourceFolder;
	}
	public void setSourceFolder(String sourceFolder) {
		this.sourceFolder = sourceFolder;
	}
	

}
