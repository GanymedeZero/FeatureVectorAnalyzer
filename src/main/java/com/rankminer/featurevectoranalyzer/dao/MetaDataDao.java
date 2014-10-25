package com.rankminer.featurevectoranalyzer.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.StatementEvent;

import com.rankminer.featurevectoranalyzer.configuration.Configuration;
import com.rankminer.featurevectoranalyzer.configuration.DbConfiguration;
import com.rankminer.featurevectoranalyzer.model.MetaDataModel;
import com.rankminer.featurevectoranalyzer.model.MetaDataModel.MetaDataModelBuilder;

/**
 * DAO class to write to metadata table in the rpm db.
 * @author achavan
 *
 */
public class MetaDataDao {
	private static final String driver = "com.mysql.jdbc.Driver";

	private static final String url = "jdbc:mysql://%s:3306/";
	
	private Configuration configuration;
	
	public MetaDataDao(Configuration config) {
		this.configuration = config;
	}
	
	/**
	 * Query the metadata table and retrive all MetaDataModel which has rec_status='1' or '2' and whose
	 * rec_addi_status is either empty or null.
	 * @return List<MetaDataModel>
	 */
	public List<MetaDataModel> getMetaDataModelByRecStatus() {
		List<MetaDataModel> modelList = new ArrayList<MetaDataModel>();
		try {
			Class.forName(driver).newInstance();
			Connection conn = null;
	        conn = DriverManager.getConnection(String.format(url, configuration.getDbConfiguration().getHostName()) + configuration.getDbConfiguration().getDbName(), 
	        		configuration.getDbConfiguration().getUserName(), configuration.getDbConfiguration().getPassword());
	        PreparedStatement preparedStatement = conn.prepareStatement("select md_id, f_path, office_no, file_num, appl from metadata where rec_status in(?,?) and"
	        		+ "rec_addi_status IS NULL or rec_addi_status=''");
	        preparedStatement.setString(1, configuration.getMetadataConfig().getProcessStatusCode().get(0));
	        preparedStatement.setString(1, configuration.getMetadataConfig().getProcessStatusCode().get(1));
	        ResultSet rs = preparedStatement.executeQuery();	        
	        while (rs.next()) {
	        	MetaDataModel model = new MetaDataModel.MetaDataModelBuilder().setFilePath(rs.getString("f_path")).
	        			setOfficeNo(rs.getString("office_no")).
	        			setFileNumber(rs.getString("file_num")).
	        			setAapl(rs.getString("appl")).
	        			setMdId(rs.getInt("md_id")).
	        			build();
	        	modelList.add(model);
	        }
		}  catch (Exception e) {
			System.out.println("Problem reading MetaData record by rec_status");
		}
		return modelList;
	}
	
	public void updateProcessedMetaDataRecord() {
		//
		try {
			Class.forName(driver).newInstance();
			Connection conn = null;
	        conn = DriverManager.getConnection(String.format(url, configuration.getDbConfiguration().getHostName()) + configuration.getDbConfiguration().getDbName(), 
	        		configuration.getDbConfiguration().getUserName(), configuration.getDbConfiguration().getPassword());
	        Statement statement = conn.createStatement();
	        int records = statement.executeUpdate("UPDATE metadata SET rec_addi_status = 1 WHERE rec_status in ('1','2')");
	       // System.out.println("Update ")
	        
		}catch(Exception e) {
			
		}

		
	}
	
	/**
	 * Update the rec_addi_status field of the metadata table whose mdId is passed into the function.
	 * @param statusCode
	 * @param mdIdList
	 */
	public void updateMetaDataRecordScpCode(String statusCode, List<String> mdIdList) {
		try {
			Class.forName(driver).newInstance();
			Connection conn = null;
	        conn = DriverManager.getConnection(String.format(url, configuration.getDbConfiguration().getHostName()) + configuration.getDbConfiguration().getDbName(), 
	        		configuration.getDbConfiguration().getUserName(), configuration.getDbConfiguration().getPassword());
	        
	        
	        
	        StringBuilder sql = new StringBuilder("Update metadata set rec_addi_status = ? where md_id in (" );
	        conn.setAutoCommit(false);
	        for( String id : mdIdList) {
	        	sql.append("?,");	        		
	        }
	        
	        sql = sql.deleteCharAt( sql.length() -1 );
	        sql.append(")");
	        PreparedStatement statement = conn.prepareStatement(sql.toString());
	        statement.setString(1, statusCode);
	        int parameterIndex =2;
	        for( String id : mdIdList) {
	        	statement.setString(parameterIndex++, id);        		
	        }	        
	        int updateCount = statement.executeUpdate();
	        conn.commit();
	        statement.close();
            conn.close();
	        System.out.println(" "+ updateCount + " rows updated in metadata table");
	        
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	public void writeBatch(List<String[]> queryList) {
		int count = 0;
		try {
			Class.forName(driver).newInstance();
			Connection conn = null;
	        PreparedStatement preparedStatement = null;
	        conn = DriverManager.getConnection(String.format(url, configuration.getDbConfiguration().getHostName()) + configuration.getDbConfiguration().getDbName(), 
	        		configuration.getDbConfiguration().getUserName(), configuration.getDbConfiguration().getPassword());
	        conn.setAutoCommit(false);
	        preparedStatement  = conn.prepareStatement("Insert into metadata (office_no,file_num,appl,filler2,rec_status,"
	        		+ "call_date, TSR,rec_duration,f_path,sample_rate,order_num,rec_addi_status,"
	        		+ "listid,start_time,end_time,station,device_name) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	        long startTime = System.currentTimeMillis();
	        int totalCount = 0;
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
					preparedStatement.setString(12, queryParameter[11].trim().length() >= 1 ? queryParameter[11].trim() : null);
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
		        	totalCount ++;
	        	}catch(Exception e) {
	        		System.out.println("Dropping record no."+ count +" due to "+ e.getMessage());		
	        	}	        		
	        }
	        commitRecords(preparedStatement, conn);
            preparedStatement.close();
            conn.close();
            System.out.println("Time taken to batch update " +totalCount + " records " + (System.currentTimeMillis() - startTime));
		} catch (Exception e) {			
			System.out.println("Problem writing record "+  count +"to the database "+ e.getMessage());
		}
	}
	
	public void commitRecords(PreparedStatement statement, Connection connection) throws SQLException {
		int [] updateCounts = statement.executeBatch();
		connection.commit();
		connection.setAutoCommit(false);
		statement.clearBatch();
		System.out.println("Committed " + updateCounts.length + " objects");
	}
	
	public static String convertToDate(String date) {
		StringBuilder sb = new StringBuilder();
		String tokens[] = date.split("/");
		sb.append(tokens[2] + "-" + tokens[0] + "-" + tokens[1]);
		return sb.toString();
	}
	
	
} 
