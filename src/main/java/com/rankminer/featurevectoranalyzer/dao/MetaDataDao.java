package com.rankminer.featurevectoranalyzer.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.rankminer.featurevectoranalyzer.configuration.Configuration;
import com.rankminer.featurevectoranalyzer.configuration.MetaDataConfig;
import com.rankminer.featurevectoranalyzer.model.MetaDataModel;

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
	        PreparedStatement preparedStatement = conn.prepareStatement("select md_id, f_path, office_no, file_num, appl from metadata where rec_status in "+ 
	        		prepareResultSet(configuration.getMetadataConfig())+" and"
	        		+ " rank_miner_status IS NULL");
	        setResultSet(preparedStatement, configuration.getMetadataConfig());
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
		};
		return modelList;
	}

	/**
	 * Add processStatusCode to the prepared statement.
	 * @param preparedStatement
	 * @throws SQLException 
	 */
	private void setResultSet(PreparedStatement preparedStatement, MetaDataConfig metaDataConfig) throws SQLException {
		List<String> processCodes = metaDataConfig.getProcessStatusCode();
		int i = 1;
		for(String processCode : processCodes) {
			preparedStatement.setString(i, processCode);
			i++;
		}
	}
	
	
	private String prepareResultSet(MetaDataConfig metaDataConfig) {
		List<String> processCodes = metaDataConfig.getProcessStatusCode();
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		for(String processCode : processCodes) {
			builder.append("?,");
		}
		
		builder.deleteCharAt(builder.toString().length()-1);
		builder.append(")");
		return builder.toString();
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
	        StringBuilder sql = new StringBuilder("Update metadata set rank_miner_status = ? where md_id in (" );
	        conn.setAutoCommit(false);
	        for( String id : mdIdList) {
	        	sql.append("?,");	        		
	        }
	        
	        sql = sql.deleteCharAt( sql.length() -1 );
	        sql.append(")");
	        System.out.println("SQL query : " + sql.toString());
	        PreparedStatement statement = conn.prepareStatement(sql.toString());
	        statement.setString(1, statusCode);
	        int parameterIndex =2;
	        for( String id : mdIdList) {
	        	statement.setInt(parameterIndex++, Integer.parseInt(id));        		
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
	
	/**
	 * Write data from the metadata csv into the metadata table.
	 * @param queryList
	 */
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
	        		+ "listid,start_time,end_time,station,device_name, file_name) values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
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
					preparedStatement.setString(12, queryParameter[11]);
					preparedStatement.setString(13, queryParameter[12]);
					preparedStatement.setTimestamp(14, java.sql.Timestamp.valueOf(convertToDate(queryParameter[5]) + " "+queryParameter[13]));
					preparedStatement.setTimestamp(15, java.sql.Timestamp.valueOf(convertToDate(queryParameter[5]) + " "+ queryParameter[14]));
					preparedStatement.setString(16, queryParameter[15]);
					preparedStatement.setString(17, queryParameter[16]);
					// Prepare file name by using office_no+file_num+appl+".vox"
					if(queryParameter[0] != null && queryParameter[1] != null && queryParameter[3] != null) {
						preparedStatement.setString(18,queryParameter[0]+queryParameter[1]+queryParameter[3]+".vox");	
					}
					
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

	/**
	 * Writing the first five feature vector to the database.
	 * @param featureVectorByFile
	 */
	public void writeFeatureVector(Map<String, List<Double>> featureVectorByFile) {
		try {
			Class.forName(driver).newInstance();
			Connection conn = null;
	        conn = DriverManager.getConnection(String.format(url, configuration.getDbConfiguration().getHostName()) + configuration.getDbConfiguration().getDbName(), 
	        		configuration.getDbConfiguration().getUserName(), configuration.getDbConfiguration().getPassword());
	        conn.setAutoCommit(false);
	        
	        for(Map.Entry<String, List<Double>> entry : featureVectorByFile.entrySet()) {
	       		try {
	       			StringBuilder sql = new StringBuilder("Update metadata set fv1 = ? and fv2 = ? and fv3 = ? and fv4 = ? and fv5 = ? where file_name = ?" );
		       		PreparedStatement statement = conn.prepareStatement(sql.toString());	
		       		int i=1;
		       		for(Double value : entry.getValue()) {
		       			statement.setDouble(i, value);	
		       			i++;
		       		}
		       		statement.setString(6, entry.getKey());
		       		statement.executeUpdate();	
			        statement.close();	
	       		}catch(Exception e) {
	       			System.out.println("Problem writing feature vector for file :" + entry.getKey());
	       		}
	        }
	        conn.commit();	        
	        conn.close();
		}catch(Exception e) {
			System.out.println(e.getMessage());
		}
	}
} 