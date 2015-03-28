package com.rankminer.featurevectoranalyzer.model;

/**
 * Data model to handle data from daily_list table.
 * @author achavan
 *
 */
public class DailyListModel {
	
	private String audioFileName;
	private String accountNumber;
	private String phoneDialed;
	private String agentId;
	private String skillName;
	private String skillId;
	
	public String getAudionFileName() {
		return audioFileName;
	}
	public void setAudioFileName(String fileName) {
		this.audioFileName = fileName;
	}
	public String getAccountNumber() {
		return accountNumber;
	}
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	public String getPhoneDialed() {
		return phoneDialed;
	}
	public void setPhoneDialed(String phoneDialed) {
		this.phoneDialed = phoneDialed;
	}
	public String getAgentId() {
		return agentId;
	}
	public void setAgentId(String agentId) {
		this.agentId = agentId;
	}
	public String getSkillName() {
		return skillName;
	}
	public void setSkillName(String skillName) {
		this.skillName = skillName;
	}
	public String getSkillId() {
		return skillId;
	}
	public void setSkillId(String skillId) {
		this.skillId = skillId;
	}
	
	

}
