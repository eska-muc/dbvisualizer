package com.skuehnel.dbvisualizer.visualize;

import java.util.ArrayList;
import java.util.List;

import com.skuehnel.dbvisualizer.domain.Table;

/**
 * Create a file in dot language 
 * 
 * @author Stefan Kuehnel
 *
 */
public class Visualizer {

	private List<Table> tables;

	private boolean lrEnabled = false;
	
	/**
	 * Constructor
	 */
	public Visualizer(List<Table> tables) {
		this.tables = tables;
	}
	
	/**
	 * Add a table
	 * @param table
	 */
	public void addTable(Table table) {
		tables.add(table);
	}
	
	/**
	 * Set the list of tables to visualize
	 * @param tables
	 */
	public void setTables(List<Table> tables) {
		this.tables = tables;
	}

	/**
	 * Setter 
	 * @param lrEnabled
	 */
	public void setLrEnabled(boolean lrEnabled) {
		this.lrEnabled = lrEnabled;
	}
	
	public boolean isLrEnabled() {
		return lrEnabled;
	}
	
	
	/**
	 * Generate the .dot file
	 * @return
	 */
	public String getDotRepresentation() {
		StringBuilder builder = new StringBuilder();
		builder.append("digraph tables {\n");
		if (lrEnabled) {
			builder.append("  rankdir=LR;\n");
		}
		builder.append("  node [shape=plaintext];\n");
		for (Table table : tables) {
			TableVisualizer tv = new TableVisualizer(table);
			builder.append(tv.getDotRepresenation());
		}
		for (Table table : tables) {
			List<Table> foreignKeyRelations = table.getForeignKeyRelations();
			if (!foreignKeyRelations.isEmpty()) {
				for (Table other : foreignKeyRelations) {
				builder.append(makeDotName(table.getName().toLowerCase()));
				builder.append(":p0");
				builder.append(" -> ");
				builder.append(makeDotName(other.getName().toLowerCase()));
				builder.append(":p0");
				builder.append(" [arrowtail=crow;dir=back];\n");
				}
			}
		}
		builder.append("}\n");		
		return builder.toString();
	}
	
	protected static String makeDotName(String in) {
		return in.replace("[\\.\\$]","_");
	}
	
}
