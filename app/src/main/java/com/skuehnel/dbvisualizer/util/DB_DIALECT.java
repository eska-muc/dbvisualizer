package com.skuehnel.dbvisualizer.util;

/**
 * 
 * @author Stefan Kuehnel
 *
 */
public enum DB_DIALECT {
	MYSQL("MySQL"),
	POSTGRESQL("PostgreSQL"),
	H2("H2"),
	ORACLE("Oracle"),
	SQLITE("SQLite");
	
	private String value;
	
	DB_DIALECT(String value) {
		this.value = value;
	}
		
	public String getValue() {
		return value;
	}
	
}
