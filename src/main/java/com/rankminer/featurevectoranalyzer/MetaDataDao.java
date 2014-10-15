package com.rankminer.featurevectoranalyzer;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;

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
		try {
			Class.forName(driver).newInstance();
			Connection conn = null;
	        PreparedStatement st = null;
	        conn = DriverManager.getConnection(String.format(url, dbConfig.getHostName()) + dbConfig.getDbName(), dbConfig.getUserName(), dbConfig.getPassword());
	        conn.setAutoCommit(false);
	        st  = conn.prepareStatement("Insert into metadata values (office_no,file_num,appl,filler2,rec_status,"
	        		+ "call_date, TSR,rec_duration,f_path,sample_rate,order_num,rec_addi_status,"
	        		+ "listid,start_time,end_time,station,device_name) values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
	        int count = 0;
	        for(String[] queryParameter : queryList) {
	        	if(count > 10) break;
	        	count++;
	        	for(String s : queryParameter) {
	        		st.setString(2,s);	
	        	}
	        		
	        }
	        int [] updateCounts = st.executeBatch();
	        LOGGER.info("Committed " + updateCounts.length + " objects");
            conn.commit();
            st.close();
            conn.close();
            
		} catch (Exception e) {
			LOGGER.error("Problem writing to the database "+ e.getMessage());
		}
	}
	
}
