package com.rankminer.featurevectoranalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.rankminer.featurevectoranalyzer.configuration.Configuration;

/**
 * Class reads the feature vector csv file, inserts records in the metadata table and the copies files 
 * to be processed.
 * @author achavan
 *
 */
public class FeatureVectorFileProcessor {
	
	private Configuration configuration;
	
	public FeatureVectorFileProcessor(Configuration config) {
		this.configuration = config; 
	}
	
	public void processFile(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String data;
			List<String[]> queryList = new ArrayList<String[]>();
			while (( data = reader.readLine()) != null) {
	              String[] rec = data.split(configuration.getDelimiter());
	              queryList.add(rec);
	        }
			
			MetaDataDao dao = new MetaDataDao(configuration.getDbConfiguration());
			dao.writeBatch(queryList);
			downloadFiles(queryList);
		}catch(Exception e) {
			System.out.println("Problem reading file "+ e.getMessage());
		}	
	}

	private void downloadFiles(List<String[]> queryList) {
		try {
		List<String> filePaths = new ArrayList<String>();
		for(String[] data : queryList) {
			if(readRecord(data[4].trim())) {
				//f_path + "/"+ office_no + file_num + appl + ".vox"
				filePaths.add(data[8] + File.separator + data[0] + data[1] + data[2] + ".vox" );
			}
		}
		SCPHandler scpHandler = new SCPHandler();
			scpHandler.downloadFiles(filePaths, configuration.getSCPConfig());
	}catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public boolean readRecord(String status) {
		try {
			if(Integer.parseInt(status) == 2 || Integer.parseInt(status) == 1) {
				return true;
			}
		}catch(Exception e) {
			System.out.println("status code :" + status  + " exception " + e.getMessage());
		}		
		return false;
	}
}
