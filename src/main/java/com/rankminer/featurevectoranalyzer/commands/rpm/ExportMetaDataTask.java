package com.rankminer.featurevectoranalyzer.commands.rpm;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.rankminer.featurevectoranalyzer.ApplicationLauncher;
import com.rankminer.featurevectoranalyzer.commands.TaskInterface;
import com.rankminer.featurevectoranalyzer.configuration.Configuration;
import com.rankminer.featurevectoranalyzer.dao.MetaDataDao;

/**
 * 
 * @author achavan
 * For RPM the expectation is export meta data will be called with one file name only.
 */
public class ExportMetaDataTask implements TaskInterface {

	/**
	 * 
	 */
	public void executeCommand(String[] args, Configuration configuration) {
		if(args[2] == null) {
			ApplicationLauncher.logger.severe("File name parameter required");
			return;
		}
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(args[2]));
			String data;
			List<String[]> queryList = new ArrayList<String[]>();
			while (( data = reader.readLine()) != null) {
	              String[] rec = data.split(configuration.getDelimiter());
	              queryList.add(rec);
	        }
			
			MetaDataDao dao = new MetaDataDao(configuration);
			dao.writeBatchRpm(queryList);
		}catch(Exception e) {
			ApplicationLauncher.logger.severe("Problem reading file "+ e.getMessage());
		}finally {
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
