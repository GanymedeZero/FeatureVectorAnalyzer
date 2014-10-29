package com.rankminer.featurevectoranalyzer.configuration;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "metadataConfig", propOrder = {
    "processStatusCode",
	"scpSuccessCode",
	"scpFailureCode"
})
public class MetaDataConfig {
	@XmlElementWrapper(name="processStatusCodeList")
	@XmlElement(required = true)
	protected List<String> processStatusCode;
	
	@XmlElement(required = true, name="scpSuccessCode")
	protected String scpSuccessCode;
	
	@XmlElement(required = true, name="scpFailureCode")
	protected String scpFailureCode;
	
	
	public String getScpSuccessCode() {
		return scpSuccessCode;
	}

	public void setScpSuccessCode(String scpSuccessCode) {
		this.scpSuccessCode = scpSuccessCode;
	}

	public String getScpFailureCode() {
		return scpFailureCode;
	}

	public void setScpFailureCode(String scpFailureCode) {
		this.scpFailureCode = scpFailureCode;
	}

	

	public List<String> getProcessStatusCode() {
        if (processStatusCode == null) {
            processStatusCode = new ArrayList<String>();
        }
        return this.processStatusCode;
    }
	
	public void addProcessStatusCode(String code) {
		this.processStatusCode.add(code);
	}
}
