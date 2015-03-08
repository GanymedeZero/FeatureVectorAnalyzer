package com.rankminer.featurevectoranalyzer.commands.rpm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.rankminer.featurevectoranalyzer.ApplicationLauncher;
import com.rankminer.featurevectoranalyzer.ProcessErrorReader;
import com.rankminer.featurevectoranalyzer.ProcessOutputReader;
import com.rankminer.featurevectoranalyzer.commands.TaskInterface;
import com.rankminer.featurevectoranalyzer.configuration.Configuration;
import com.rankminer.featurevectoranalyzer.dao.FckResourceDao;
import com.rankminer.featurevectoranalyzer.model.FckResourceModel;

public class ExtractFeatureVectorTask implements TaskInterface {

	public void executeCommand(String[] args, Configuration configuration) {
		FckResourceDao dao;
		dao = new FckResourceDao(configuration);
		List<FckResourceModel> modelList = dao.getFckResourceByStatus("PROCESSED");
		// Extract the feature vector IDs from fck_resource table and write it to a file.
		writeFeatureVectorIds(modelList);
		extractFeatureVectorBlob();	
	}

	/**
	 * Method writes the File_ids to a file. The file name format is fv-todaysdate.txt
	 * e.g fv-11-11-2014.txt
	 * @param modelList
	 */
	private static void writeFeatureVectorIds(List<FckResourceModel> modelList) {
		BufferedWriter writer = null;
		try {
			String dateStr = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
			writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("fv-"+dateStr+".txt")));
			writer.write("FILE_ID");
			writer.newLine();
			for(FckResourceModel resource : modelList) {
				writer.write(""+resource.getFileId());
				writer.newLine();
			}
			
		} catch (Exception e) {
			ApplicationLauncher.logger.severe("Problem writing to the file + "+ e.getMessage());
		} finally{
			try {
				writer.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 
	 */
	private static void extractFeatureVectorBlob() {
		String dateStr = new SimpleDateFormat("MM-dd-yyyy").format(new Date());
		
		ProcessBuilder pb = new ProcessBuilder("java", "-jar","connector-console-1.0-SNAPSHOT.jar");
		Process process;
		try {
			process = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			Thread t = new Thread(new ProcessOutputReader(reader));
			t.start();
			Thread tr = new Thread(new ProcessErrorReader(reader));
			tr.start();
			Thread.sleep(5000);
			System.out.println("Going to call export-features");
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
			writer.write("export-features " + "fv-"+dateStr+".txt"+ " fv-"+dateStr+".xml");
			writer.write("\n");
			writer.flush();
			writer.close();
			process.waitFor();
			process.destroy();
		} catch (Exception e) {
			ApplicationLauncher.logger.severe("Problem calling console-connector jar.Error "+e.getMessage());
		}
	}
}
