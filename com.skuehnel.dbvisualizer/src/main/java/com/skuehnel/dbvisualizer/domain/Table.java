package com.skuehnel.dbvisualizer.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Describes a database table.
 * 
 * @author Stefan Kuehnel
 *
 */
public class Table {

	private String name;
	private List<Column> columns;
	
	private List<Table> foreignKeyRelations;
	
	
	/**
	 * Cosntructor
	 * @param name the name of the table
	 */
	public Table(String name) {
		this.name = name;
		foreignKeyRelations = new ArrayList<>();
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
	 * @return the columns
	 */
	public List<Column> getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(List<Column> columns) {
		this.columns = columns;
		updateForeignKeyRelations();
	}
	
	/**
	 * Getter for foreign key relations
	 * @return
	 */
	public List<Table> getForeignKeyRelations() {
		return foreignKeyRelations;
	}
	
	/**
	 * Update foreign key relations
	 */
	public void updateForeignKeyRelations() {
		foreignKeyRelations.clear();
		for (Column column : columns) {
			Table t = column.getForeignKeyTable();
			if (t != null) {
				foreignKeyRelations.add(t);
			}
		}
	}

	
	
}
