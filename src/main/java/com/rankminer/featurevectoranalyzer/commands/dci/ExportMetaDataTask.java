package com.rankminer.featurevectoranalyzer.commands.dci;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rankminer.featurevectoranalyzer.ApplicationLauncher;
import com.rankminer.featurevectoranalyzer.commands.TaskInterface;
import com.rankminer.featurevectoranalyzer.configuration.Configuration;
import com.rankminer.featurevectoranalyzer.dao.MetaDataDao;

public class ExportMetaDataTask implements TaskInterface {

	@Override
	public void executeCommand(String[] args, Configuration configuration) {
		
		if(args[2] == null) {
			ApplicationLauncher.logger.severe("Directory name parameter required");
			return;
		}
		
		try {
			List<File> files = getFiles(args[2]);
			BufferedReader reader = null;
			for(File f : files) {
				try {
					reader = new BufferedReader(new FileReader(f));
					String data;
					List<String[]> queryList = new ArrayList<String[]>();
					while (( data = reader.readLine()) != null) {
			              String[] rec = data.split(configuration.getDelimiter());
			              queryList.add(rec);
			        }
					MetaDataDao dao = new MetaDataDao(configuration);
					dao.writeBatchDci(queryList);
					reader.close();
				}catch(Exception ee) {
					ApplicationLauncher.logger.severe("Problem occured while reading file " + ee.getMessage());
				}finally {
					reader.close();
				}
			}
		}catch(Exception e) {
			ApplicationLauncher.logger.severe("Problem reading file "+ e.getMessage());
		}	
	}
	
	/**
	 * Get an list of csv files from the given directory.
	 * @param directory
	 * @return
	 */
	public List<File> getFiles(String directory) {
		File fileDir = new File(directory);
		FilenameFilter csvFileFilter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if(dir.isDirectory()) return false;
				String lowercaseName = name.toLowerCase();
				if (lowercaseName.endsWith(".csv")) {
					return true;
				} else {
					return false;
				}
			}
		};
		File [] files = fileDir.listFiles(csvFileFilter);
		return Arrays.asList(files);
	}

}
