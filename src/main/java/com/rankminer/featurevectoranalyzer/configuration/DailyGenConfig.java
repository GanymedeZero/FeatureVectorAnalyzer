package com.rankminer.featurevectoranalyzer.configuration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "dgConfig", propOrder = {
    "sourceFolder",
    "fileName",
    "action"
})
public class DailyGenConfig {
	@XmlElement(required=true,name="sourceFolder")
	protected String sourceFolder;

	@XmlElement(required=true, name="fileName")
	protected String fileName;
	
	@XmlElement(required=true, name="action")
	protected String action;
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fn) {
		this.fileName = fn;
	}
	
	public String getSourceFolder() {
		return sourceFolder;
	}
	public void setSourceFolder(String sourceFolder) {
		this.sourceFolder = sourceFolder;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public boolean isAppend() {
		if(action.equalsIgnoreCase("append")) return true;
		else return false;
	}
}
