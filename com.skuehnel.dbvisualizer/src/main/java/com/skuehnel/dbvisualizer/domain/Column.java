package com.skuehnel.dbvisualizer.domain;


/**
 * Describes a database column
 * 
 * @author Stefan Kuehnel
 *
 */
public class Column {

	private String name;
	private String type;
	private boolean primaryKey;
	private boolean unique;
	private boolean notNull;
	private Table foreignKeyTable;
	private Column foreignKeyColumn;
	
	/**
	 * Constructor
	 * @param name name of the column
	 * @param type name of the type
	 * @param primaryKey true, if column is primary key
	 * @param unique true, if "unique" constraint on this column
	 * @param notNull, true if "not null" constraint on this column
	 */
	public Column(String name,String type, boolean primaryKey,boolean unique,boolean notNull) {
		this.name = name;
		this.type = type;
		this.primaryKey = primaryKey;
		this.unique = unique;
		this.notNull = notNull;
	}

	/**
	 * Constructor
	 * @param name name of the column
	 * @param type type of the column
	 */
	public Column(String name,String type) {
		this.name = name;
		this.type = type;		
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the primaryKey
	 */
	public boolean isPrimaryKey() {
		return primaryKey;
	}

	/**
	 * @param primaryKey the primaryKey to set
	 */
	public void setPrimaryKey(boolean primaryKey) {
		this.primaryKey = primaryKey;
	}

	/**
	 * @return the unique
	 */
	public boolean isUnique() {
		return unique;
	}

	/**
	 * @param unique the unique to set
	 */
	public void setUnique(boolean unique) {
		this.unique = unique;
	}

	/**
	 * @return the notNull
	 */
	public boolean isNotNull() {
		return notNull;
	}

	/**
	 * @param notNull the notNull to set
	 */
	public void setNotNull(boolean notNull) {
		this.notNull = notNull;
	}

	/**
	 * @return the foreignKeyTable
	 */
	public Table getForeignKeyTable() {
		return foreignKeyTable;
	}

	/**
	 * @param foreignKeyTable the foreignKeyTo to set
	 */
	public void setForeignKeyTable(Table foreignKeyTable) {
		this.foreignKeyTable = foreignKeyTable;
	}

	/**
	 * @return the foreignKeyColumn
	 */
	public Column getForeignKeyColumn() {
		return foreignKeyColumn;
	}

	/**
	 * @param foreignKeyColumn the foreignKeyColumn to set
	 */
	public void setForeignKeyColumn(Column foreignKeyColumn) {
		this.foreignKeyColumn = foreignKeyColumn;
	}
	
	
	
}
