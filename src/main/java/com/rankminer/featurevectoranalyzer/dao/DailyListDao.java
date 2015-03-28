package com.rankminer.featurevectoranalyzer.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import com.rankminer.featurevectoranalyzer.ApplicationLauncher;
import com.rankminer.featurevectoranalyzer.configuration.Configuration;
import com.rankminer.featurevectoranalyzer.model.DailyListModel;
import com.rankminer.featurevectoranalyzer.utils.EmailHandler;

public class DailyListDao {

	private static final String driver = "com.mysql.jdbc.Driver";

	private static final String url = "jdbc:mysql://%s:3306/";
	
	private Configuration configuration;
	
	public DailyListDao(Configuration config) {
		this.configuration = config;
	}
	
	
	
	public void updateDailyList(List<DailyListModel> modelList, boolean append) {
		Connection conn = null;
		PreparedStatement preparedStatement = null;
		Statement statement = null;
		try{
			Class.forName(driver).newInstance();
			
			conn = DriverManager.getConnection(String.format(url, configuration.getDbConfiguration().getHostName()) + configuration.getDbConfiguration().getDbName(), 
	        		configuration.getDbConfiguration().getUserName(), configuration.getDbConfiguration().getPassword());
	     
			if(!append) {
				 conn.setAutoCommit(false);
				 statement = conn.createStatement();
			     ApplicationLauncher.logger.info("Environment: "+ configuration.getEnvironment() + " deleting records from daily_list table");
			     int deleteCount = statement.executeUpdate("delete from daily_list");
			     ApplicationLauncher.logger.info("Environment : " + configuration.getEnvironment() + " deleted " + deleteCount + " entries from daily_list table");
			     statement.close();
			}
			
			if(conn.getAutoCommit() == true){
				conn.setAutoCommit(false);
			}
			
			
	        preparedStatement  = conn.prepareStatement("Insert into daily_list (filename,account_number,phone_dialed,agent_id,skill_name,"
	        		+ "skill_id) values (?,?,?,?,?,?)");
	        int count = 0;
	        long startTime = System.currentTimeMillis();
	        int totalCount = 0;
	        for(DailyListModel  model : modelList) {
	        	preparedStatement.setString(1, model.getAudionFileName());
	        	preparedStatement.setString(2, model.getAccountNumber());
	        	preparedStatement.setString(3, model.getPhoneDialed());
	        	preparedStatement.setString(4, model.getAgentId());
	        	preparedStatement.setString(5, model.getSkillName());
	        	preparedStatement.setString(6, model.getSkillId());
	        	
	        	preparedStatement.addBatch();
	        	count++;
	        	totalCount ++;
	        	if(count %1000 == 0) {
        			count = 0;
        			commitRecords(preparedStatement, conn);
        		}
	        }
	        
	        commitRecords(preparedStatement, conn);
            preparedStatement.close();
            conn.close();
            ApplicationLauncher.logger.info("Environment " + configuration.getEnvironment() + " Time taken to finish adding  " +totalCount + " records to daily_task table " + (System.currentTimeMillis() - startTime) +" ms");
	      
		}catch(Exception e) {
			try {
				conn.rollback();
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			ApplicationLauncher.logger.severe("Environment: "+ configuration.getEnvironment() + " problem updating daily_list table. Error + " + e.getMessage());
    		EmailHandler.emailEvent("Problem writing to the daily_list db  Error: " + e.getMessage(), "Re["+configuration.getEnvironment()+"]: Error writing to daily_list");
		}finally {
			try {
				if(!conn.isClosed()) {
					conn.close();
				}
				if(!statement.isClosed()) {
					statement.close();
				}
				if(!preparedStatement.isClosed()) {
					preparedStatement.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void commitRecords(PreparedStatement statement, Connection connection) throws SQLException {
		int [] updateCounts = statement.executeBatch();
		connection.commit();
		connection.setAutoCommit(false);
		statement.clearBatch();
		ApplicationLauncher.logger.info("Committed " + updateCounts.length + " items for daily_list table for environment: " + configuration.getEnvironment());
	}
	
}
