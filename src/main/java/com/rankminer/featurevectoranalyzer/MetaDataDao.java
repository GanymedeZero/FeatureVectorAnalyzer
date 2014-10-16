package com.rankminer.featurevectoranalyzer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import net.schmizz.sshj.SSHClient;

import org.apache.log4j.Logger;

import com.rankminer.featurevectoranalyzer.configuration.DbConfiguration;

/**
 * DAO class to write to metadata table in the rpm db.
 * @author achavan
 *
 */
public class MetaDataDao {
	private static final Logger LOGGER = Logger
			.getLogger(MetaDataDao.class);

	private static final String driver = "com.mysql.jdbc.Driver";

	private static final String url = "jdbc:mysql://%s:3306/";
	
	private DbConfiguration dbConfig;
	
	public MetaDataDao(DbConfiguration config) {
		this.dbConfig = config;
	}
	
	
	public void writeBatch(List<String[]> queryList) {
		int count = 0;
		try {
			Class.forName(driver).newInstance();
			Connection conn = null;
	        PreparedStatement preparedStatement = null;
	        conn = DriverManager.getConnection(String.format(url, dbConfig.getHostName()) + dbConfig.getDbName(), dbConfig.getUserName(), dbConfig.getPassword());
	        conn.setAutoCommit(false);
	        preparedStatement  = conn.prepareStatement("Insert into metadata (office_no,file_num,appl,filler2,rec_status,"
	        		+ "call_date, TSR,rec_duration,f_path,sample_rate,order_num,rec_addi_status,"
	        		+ "listid,start_time,end_time,station,device_name) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	        long startTime = System.currentTimeMillis();
	        
	        for(String[] queryParameter : queryList) {
	        	try {
	        		
		        	preparedStatement.setString(1,queryParameter[0]);
		        	preparedStatement.setString(2,queryParameter[1]);
		        	preparedStatement.setString(3,queryParameter[2]);
		        	preparedStatement.setString(4,queryParameter[3]);
		        	preparedStatement.setString(5,queryParameter[4]);
					preparedStatement.setDate(6, java.sql.Date.valueOf(convertToDate(queryParameter[5])));
					preparedStatement.setString(7, queryParameter[6]);
					preparedStatement.setInt(8, Integer.parseInt(queryParameter[7]));
					preparedStatement.setString(9, queryParameter[8]);
					preparedStatement.setString(10, queryParameter[9]);
					preparedStatement.setString(11, queryParameter[10]);
					preparedStatement.setString(12, queryParameter[11]);
					preparedStatement.setString(13, queryParameter[12]);
					preparedStatement.setTimestamp(14, java.sql.Timestamp.valueOf(convertToDate(queryParameter[5]) + " "+queryParameter[13]));
					preparedStatement.setTimestamp(15, java.sql.Timestamp.valueOf(convertToDate(queryParameter[5]) + " "+ queryParameter[14]));
					preparedStatement.setString(16, queryParameter[15]);
					preparedStatement.setString(17, queryParameter[16]);
		        	preparedStatement.addBatch();
		        	if(count %1000 == 0) {
	        			count = 0;
	        			commitRecords(preparedStatement, conn);
	        		}
		        	count++;
	        	}catch(Exception e) {
	        		LOGGER.error("Dropping record no."+ count +" due to "+ e.getMessage());		
	        	}	        		
	        }
	        
            preparedStatement.close();
            conn.close();
            LOGGER.info("Time taken to batch update " +count + " records " + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {			
			LOGGER.error("Problem writing record "+  count +"to the database "+ e.getMessage());
		}
	}
	
	public void commitRecords(PreparedStatement statement, Connection connection) throws SQLException {
		int [] updateCounts = statement.executeBatch();
		connection.commit();
		connection.setAutoCommit(false);
		statement.clearBatch();
        LOGGER.info("Committed " + updateCounts.length + " objects");
	}
	
	public static String convertToDate(String date) {
		StringBuilder sb = new StringBuilder();
		String tokens[] = date.split("/");
		sb.append(tokens[2] + "-" + tokens[0] + "-" + tokens[1]);
		return sb.toString();
	}
} 
