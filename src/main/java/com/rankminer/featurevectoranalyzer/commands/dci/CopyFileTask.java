package com.rankminer.featurevectoranalyzer.commands.dci;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.rankminer.featurevectoranalyzer.ApplicationLauncher;
import com.rankminer.featurevectoranalyzer.commands.TaskInterface;
import com.rankminer.featurevectoranalyzer.configuration.Configuration;
import com.rankminer.featurevectoranalyzer.dao.MetaDataDao;
import com.rankminer.featurevectoranalyzer.utils.EmailHandler;

public class CopyFileTask implements TaskInterface {

	@Override
	public void executeCommand(String[] args, Configuration configuration) {
		MetaDataDao dao = new MetaDataDao(configuration);
		List<String> fileNames = dao.findFilesToCopy(configuration.getCopyConfig().getStatus());
		List<String> errorCode = new ArrayList<String>();
		for(String fileName : fileNames) {
			File sourceFile = new File(configuration.getCopyConfig().getSourceFolder() + fileName);
	    	File destFile = new File(configuration.getCopyConfig().getDestinationFolder() + fileName);
	    	try {
				FileUtils.moveFile(sourceFile, destFile);
			} catch (IOException e) {
				ApplicationLauncher.logger.severe("Environment: " + configuration.getEnvironment()
						+ " failure to copy file " + sourceFile.getName() + ". Error - " + e.getMessage());
				errorCode.add("Environment: " + configuration.getEnvironment()
						+ " failure to copy file " + sourceFile.getName() + ". Error - " + e.getMessage());
			}	
		}
		sendErrorEmails(errorCode);
	}
	
	/**
	 * Send email about any failure that occured.
	 * @param errorCodes
	 */
	private void sendErrorEmails(List<String> errorCodes) {
		StringBuilder sb = new StringBuilder();
		for(String error : errorCodes) {
			sb.append(error + " \n");
		}
		
		EmailHandler.emailEvent(sb.toString());
	}
}
