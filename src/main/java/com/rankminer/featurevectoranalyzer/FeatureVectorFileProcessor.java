package com.rankminer.featurevectoranalyzer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import com.rankminer.featurevectoranalyzer.configuration.Configuration;
import com.rankminer.featurevectoranalyzer.dao.MetaDataDao;

/**
 * Class responsible for reading metadata from csv and add it to 
 * MYSQL db to metadata table.
 * @author achavan
 *
 */
public class FeatureVectorFileProcessor {
	
	private Configuration configuration;

	/**
	 * Set the configuration object
	 * @param config
	 */
	public FeatureVectorFileProcessor(Configuration config) {
		this.configuration = config; 
	}
	
	/**
	 * Process the CSV file and add contents to metadata table.
	 * @param fileName
	 */
	public void processFile(String fileName) {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			String data;
			List<String[]> queryList = new ArrayList<String[]>();
			while (( data = reader.readLine()) != null) {
	              String[] rec = data.split(configuration.getDelimiter());
	              queryList.add(rec);
	        }
			
			MetaDataDao dao = new MetaDataDao(configuration);
			dao.writeBatch(queryList);
		}catch(Exception e) {
			System.out.println("Problem reading file "+ e.getMessage());
		}	
	}
}
