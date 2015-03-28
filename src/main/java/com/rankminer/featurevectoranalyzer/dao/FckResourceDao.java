package com.rankminer.featurevectoranalyzer.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.rankminer.featurevectoranalyzer.ApplicationLauncher;
import com.rankminer.featurevectoranalyzer.configuration.Configuration;
import com.rankminer.featurevectoranalyzer.model.FckResourceModel;
import com.rankminer.featurevectoranalyzer.utils.EmailHandler;

/**
 * Dao class to handle data access to FCK_RESOURCE table.
 * @author achavan
 *
 */
public class FckResourceDao {
	
	private static final String driver = "com.mysql.jdbc.Driver";

	private static final String url = "jdbc:mysql://%s:3306/";
	private Configuration configuration;
	
	public FckResourceDao(Configuration config) {
		this.configuration = config;
	}
	
	
	/**
	 * Returns a list of fckResourceModel which is prepared by 
	 * querying the fck_resource table by status id.
	 * @return List<FckResourceModel>
	 */
	public List<FckResourceModel> getFckResourceByStatus(String status) {
		List<FckResourceModel> modelList = new ArrayList<FckResourceModel>();
		try {
			Class.forName(driver).newInstance();
			Connection conn = null;
	        conn = DriverManager.getConnection(String.format(url, configuration.getDbConfiguration().getHostName()) + configuration.getDbConfiguration().getDbName(), 
	        		configuration.getDbConfiguration().getUserName(), configuration.getDbConfiguration().getPassword());
	        PreparedStatement preparedStatement = conn.prepareStatement("select FILE_ID from fck_resource where FILE_STATUS=? and DATEDIFF(NOW(),insertion_date) = 0");
	        preparedStatement.setString(1, status);
	       // preparedStatement.setTimestamp(2, new Timestamp(new Date().getTime()));
	        ResultSet rs = preparedStatement.executeQuery();	        
	        while (rs.next()) {
	        	FckResourceModel model = new FckResourceModel();
	        	model.setFileId(rs.getLong("FILE_ID"));
	        	modelList.add(model);
	        }
	        preparedStatement.close();
	        conn.close();
		}catch(Exception e) {
			ApplicationLauncher.logger.severe("Problem retrieving FckResource by File_Status[Processed] environment: "+
					configuration.getEnvironment()+" Error " +e.getMessage());
			EmailHandler.emailEvent("Problem retrieving FckResource by File_Status[Processed] Error " +e.getMessage(),
					"Re["+configuration.getEnvironment()+"]: Problem reading FckResource");
		}
		return modelList;
	}
}
