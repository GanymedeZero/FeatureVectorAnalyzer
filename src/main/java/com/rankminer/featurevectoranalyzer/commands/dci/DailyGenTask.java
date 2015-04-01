package com.rankminer.featurevectoranalyzer.commands.dci;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.rankminer.featurevectoranalyzer.commands.TaskInterface;
import com.rankminer.featurevectoranalyzer.configuration.Configuration;
import com.rankminer.featurevectoranalyzer.dao.DailyListDao;
import com.rankminer.featurevectoranalyzer.model.DailyListModel;

public class DailyGenTask implements TaskInterface {

	@Override
	public void executeCommand(String[] args, Configuration configuration) {
		/**
		 * Format the file name 
		 * Read the file and add to daily_list
		 * 
		 */
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(fileName(configuration)));
			String data;
			List<DailyListModel> dailyListModel = new ArrayList<DailyListModel>();
			int count = 0;
			while (( data = reader.readLine()) != null) {
	              
				  // Skip the header 
				  if(count == 0) {
					  count++;
	            	  continue;
				  }
				  count++;
				  String[] rec = data.split(configuration.getDelimiter());
	              DailyListModel model = new DailyListModel();
	              model.setAudioFileName(rec[0]);
	              model.setAccountNumber(rec[1]);
	              model.setPhoneDialed(rec[2]);
	              model.setAgentId(rec[3]);
	              model.setSkillName(rec[4]);
	              model.setSkillId(rec[5]);
	              dailyListModel.add(model);
			}
			
			reader.close();
			DailyListDao dao = new DailyListDao(configuration);
			dao.updateDailyList(dailyListModel, configuration.getDgConfig().isAppend());
			
		}catch(Exception e) {
			
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
	}
	
	/**
	 * Returns a fileName formaterd using directory/YYYY-MM-DD_results/rankedlist.csv
	 * @param directory
	 * @return
	 */
	public String fileName(Configuration config) {
		StringBuilder fileName = new StringBuilder(config.getDgConfig().getSourceFolder());
		fileName.append(new SimpleDateFormat("YYYY-MM-dd").format(new Date()));
		fileName.append("_results");
		fileName.append("/"+config.getDgConfig().getFileName());
		return fileName.toString();
	}
	

}
