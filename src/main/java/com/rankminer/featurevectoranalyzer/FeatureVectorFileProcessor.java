package com.rankminer.featurevectoranalyzer;

import java.io.BufferedReader;
import java.io.FileReader;
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
			
		}catch(Exception e) {
			
		}
		
		
	}

}
