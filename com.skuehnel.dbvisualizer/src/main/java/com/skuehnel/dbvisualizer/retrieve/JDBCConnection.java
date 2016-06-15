package com.skuehnel.dbvisualizer.retrieve;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * 
 * @author Stefan Kuehnel
 *
 */
public class JDBCConnection {

	private String driver;
	private String url;
	private String user;
	private String password;
	
	private Connection connection;
	
	/**
	 * Constructor  
	 * @param driver name of the driver class
	 * @param url JDBC url 
	 * @param user name of DB user
	 * @param password password of DB user
	 */
	public JDBCConnection(String driver,String url,String user,String password) throws ConnectionException {
		this.driver = driver;
		this.url = url;
		this.user = user;
		this.password = password;
		if (driver == null || url == null) {
			throw new ConnectionException("At least driver and url must be set.");
		}		
		try {
			initialize();
		} catch (Exception e) {
			throw new ConnectionException("Could not initialize database connection.",e);
		}
	}
	
	/**
	 * Get the JDBC connection to the database
	 * @return a {@link java.sql.Connection} object
	 */
	public Connection getConnection() {
		return connection;
	}
	
	protected void initialize() throws ClassNotFoundException, SQLException {
		Class driverClass = Class.forName(driver);
		connection = DriverManager.getConnection(url,user,password);
	}
	
	
}
