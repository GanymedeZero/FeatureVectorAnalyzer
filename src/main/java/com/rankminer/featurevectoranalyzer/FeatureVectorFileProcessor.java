package com.rankminer.featurevectoranalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.rankminer.featurevectoranalyzer.configuration.Configuration;

/**
 * Class reads the feature vector csv file, inserts records in the metadata table and the copies files 
 * to be processed.
 * @author achavan
 *
 */
public class FeatureVectorFileProcessor {
	
	private static final Logger LOGGER = Logger
			.getLogger(FeatureVectorFileProcessor.class);
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
			LOGGER.error("Problem reading file "+ e.getMessage());
		}	
	}

	private void downloadFiles(List<String[]> queryList) {
		List<String> filePaths = new ArrayList<String>();
		for(String[] data : queryList) {
			if(Integer.parseInt(data[4].trim()) == 2 || Integer.parseInt(data[4].trim()) == 1) {
				//f_path + "/"+ office_no + file_num + appl + ".vox"
				filePaths.add(data[8] + File.pathSeparator + data[0] + data[1] + data[2] + ".vox" );
			}
		}
		
		SCPHandler scpHandler = new SCPHandler();
		try {
			scpHandler.downloadFiles(filePaths, configuration.getSCPConfig());
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
