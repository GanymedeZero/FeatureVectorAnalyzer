package com.rankminer.featurevectoranalyzer.commands.rpm;

import com.rankminer.featurevectoranalyzer.ApplicationLauncher;
import com.rankminer.featurevectoranalyzer.commands.TaskInterface;
import com.rankminer.featurevectoranalyzer.configuration.Configuration;

public class CopyFileTask implements TaskInterface {

	@Override
	public void executeCommand(String[] args, Configuration configuration) {
		ApplicationLauncher.logger.info("Copy task not implemented for " + configuration.getEnvironment());
	}

}
