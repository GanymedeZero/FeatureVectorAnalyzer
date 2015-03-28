package com.rankminer.featurevectoranalyzer.commands.rpm;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import com.rankminer.featurevectoranalyzer.ApplicationLauncher;
import com.rankminer.featurevectoranalyzer.commands.TaskInterface;
import com.rankminer.featurevectoranalyzer.configuration.Configuration;
import com.rankminer.featurevectoranalyzer.dao.MetaDataDao;
import com.rankminer.featurevectoranalyzer.translator.Resources;
import com.rankminer.featurevectoranalyzer.translator.Resources.Resource;
import com.rankminer.featurevectoranalyzer.utils.EmailHandler;

public class TranslateFVToCsvTask implements TaskInterface {

	/**
	 * 
	 */
	public void executeCommand(String[] args, Configuration configuration) {
		if(args.length < 3) {
			ApplicationLauncher.logger.severe("Insufficient arguments passed to the TranslateFVToCsv task");
			return;
		}
		convertFeatureVectorXmlToCsv(args[2], args[3], configuration);
	}
	
	/**
	 * Reads the source xml file and converts it to an output format 
	 * in csv
	 * @param sourceFileName
	 * @param destinationFileName
	 */
	public void convertFeatureVectorXmlToCsv(String sourceFileName, String destinationFileName, Configuration configuration) {
		ApplicationLauncher.logger.info("Reading feature vector file "+ sourceFileName);
		File file = new File(sourceFileName);
		JAXBContext jaxbContext;
		try {
			jaxbContext = JAXBContext.newInstance(Resources.class);
			Unmarshaller jaxbMarshaller = jaxbContext.createUnmarshaller();
			Resources resources = (Resources)jaxbMarshaller.unmarshal(file);
			createCsvFile(resources,destinationFileName);
			writeFeatureVectorToDb(resources, configuration);
			filterTopFeatureVectors();
		} catch (Exception e) {
			ApplicationLauncher.logger.severe("Environment["+configuration.getEnvironment()+"]: Problems while translating "+ sourceFileName +" file to "+ destinationFileName + " file. Error "+ e.getMessage());
			EmailHandler.emailEvent("Problems while translating "+ sourceFileName +" file to "+ destinationFileName + " file. Error "+ e.getMessage(), 
					"Re["+configuration.getEnvironment()+"]: Problem translating feature vectors");
		}
	}

	private void filterTopFeatureVectors() {
		System.out.println("Still to implement this");
		
	}
	/**
	 * Write the first five feature vectors to the 
	 * @param resources
	 */
	private void writeFeatureVectorToDb(Resources resources, Configuration configuration) {
		Map<String, List<Double>> featureVectorByFile = new HashMap<String, List<Double>>();
		for(Resource resource : resources.getResource()) {
			List<Double> featureVectors = new ArrayList<Double>();
			for(int fvIndex=0; fvIndex < 5; fvIndex++) {
				featureVectors.add(resource.getFeatures().get(fvIndex));
			}
			featureVectorByFile.put(resource.getFileName(), featureVectors);
		}
		
		MetaDataDao dao = new MetaDataDao(configuration);
		dao.writeFeatureVector(featureVectorByFile);
	}

	/**
	 * Create 
	 * @param resources
	 * @param destinationFileName
	 */
	private void createCsvFile(Resources resources, String destinationFileName) throws Exception {
		BufferedWriter writer;
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(destinationFileName)));
		for(Resource resource : resources.getResource()) {
			StringBuilder builder = new StringBuilder();
			builder.append(resource.getFileName()).append(",");
			for(double d : resource.getFeatures()) {
				builder.append(d).append(",");
			}
			builder.deleteCharAt( builder.length() -1 );
			writer.write(builder.toString());
			writer.newLine();
		}
		writer.close();
	}
	

}
