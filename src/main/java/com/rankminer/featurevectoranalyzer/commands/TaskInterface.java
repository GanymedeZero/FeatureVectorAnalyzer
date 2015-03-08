package com.rankminer.featurevectoranalyzer.commands;

import com.rankminer.featurevectoranalyzer.configuration.Configuration;

public interface TaskInterface {
	
	public void executeCommand(String[] args, Configuration configuration);
}
