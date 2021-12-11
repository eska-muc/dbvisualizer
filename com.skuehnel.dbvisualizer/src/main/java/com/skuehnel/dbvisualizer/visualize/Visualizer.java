package com.skuehnel.dbvisualizer.visualize;

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
	private boolean entitiesOnly = false;
	
	/**
	 * Constructor
	 */
	public Visualizer(List<Table> tables) {
		this.tables = tables;
	}
	
	/**
	 * Add a table
	 * @param table a table object
	 */
	public void addTable(Table table) {
		tables.add(table);
	}
	
	/**
	 * Set the list of tables to visualize
	 * @param tables a list of table objects
	 */
	public void setTables(List<Table> tables) {
		this.tables = tables;
	}

	/**
	 * Setter for LR option
	 * @param lrEnabled current value
	 */
	public void setLrEnabled(boolean lrEnabled) {
		this.lrEnabled = lrEnabled;
	}

	/**
	 * Getter for LR option (GraphViz left to right layout)
	 *
	 * @return value of LR option
	 */
	public boolean isLrEnabled() {
		return lrEnabled;
	}

	/**
	 * Getter for option "Entities only"
	 *
	 * @return value of setting "entities only"
	 */
	public boolean isEntitiesOnly() {
		return entitiesOnly;
	}

	/**
	 * Setter for option "entities only"
	 *
	 * @param entitiesOnly value for option "entities only"
	 */
	public void setEntitiesOnly(boolean entitiesOnly) {
		this.entitiesOnly = entitiesOnly;
	}

	/**
	 * Generate the .dot file
	 * @return the ER-model as GraphViz dot file
	 */
	public String getDotRepresentation() {
		StringBuilder builder = new StringBuilder();
		builder.append("digraph tables {\n");
		if (lrEnabled) {
			builder.append("  rankdir=LR;\n");
		}
		builder.append("  node [shape=plaintext];\n");
		for (Table table : tables) {
			TableVisualizer tv = getTableVisualizer(table);
			builder.append(tv.getDotRepresentation());
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

	/**
	 * Get Output for Plant IE (ER) Diagrams
	 *
	 * @return a string describing the database in PlantUML notation
	 */
	public String getPlantRepresentation() {
		StringBuilder builder = new StringBuilder();
		builder.append("@startuml\n");
		builder.append("'hide the spot\n");
		builder.append("hide circle\n");
		builder.append("' comment\n");
		builder.append("skinparam linetype ortho\n");
		for (Table table : tables) {
			TableVisualizer tv = getTableVisualizer(table);
			builder.append(tv.getPlantRepresentation());
			builder.append("\n");
		}
		builder.append("\n");
		for (Table table : tables) {
			List<Table> foreignKeyRelations = table.getForeignKeyRelations();
			if (!foreignKeyRelations.isEmpty()) {
				for (Table other : foreignKeyRelations) {
					builder.append(Visualizer.makeDotName(table.getName()).toLowerCase());
					builder.append(" }|--|| ");
					builder.append(Visualizer.makeDotName(other.getName()).toLowerCase());
					builder.append("\n");
				}
			}
		}
		builder.append("@enduml\n");
		return builder.toString();
	}

	protected static String makeDotName(String in) {
		return in.replaceAll("([\\.\\$])", "_");
	}

	private TableVisualizer getTableVisualizer(Table table) {
		if (entitiesOnly) {
			return new SimpleTableVisualizer(table);
		}
		return new DetailedTableVisualizer(table);
	}

}
