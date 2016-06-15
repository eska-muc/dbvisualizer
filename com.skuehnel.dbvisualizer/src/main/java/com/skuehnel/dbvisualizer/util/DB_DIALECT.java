package com.skuehnel.dbvisualizer.util;

/**
 * 
 * @author Stefan Kuehnel
 *
 */
public enum DB_DIALECT {
	OPT_MYSQL("MySQL"),
	OPT_POSTGRESQL("PostgreSQL"),
	OPT_H2("H2"),
	OPT_SQLITE("SQLite");
	
	private String value;
	
	private DB_DIALECT(String value) {
		this.value = value;
	}
	
}
