package com.skuehnel.dbvisualizer.visualize;

import com.skuehnel.dbvisualizer.domain.Column;
import com.skuehnel.dbvisualizer.domain.Table;

/**
 * Visualizer for a Table
 *
 * @author Stefan Kuehnel
 */
public class DetailedTableVisualizer implements TableVisualizer {

    private Table table;

    /**
     * Constructor
     *
     * @param table the table object for which a dot reprsentation shall be created
     */
    public DetailedTableVisualizer(Table table) {
        this.table = table;
    }

    /**
     * Create a node description for this table
     *
     * @return the detailed representation of the current table in GraphViz' dot language
     */
    @Override
    public String getDotRepresentation() {

        String nodeName = Visualizer.makeDotName(table.getName().toLowerCase());
        StringBuilder builder = new StringBuilder(nodeName);
        builder.append(" [");
        builder.append("label=<");
        builder.append("<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" PORT=\"p0\">\n");
        builder.append("<TR>");
        builder.append("<TD COLSPAN=\"4\">");
        builder.append(table.getName().toUpperCase());
        builder.append("</TD>");
        builder.append("</TR>\n");
        //builder.append("<TR>");
        //builder.append("<TD>pk</TD><TD>name</TD><TD>type</TD><TD>constr</TD>");
        //builder.append("</TR>\n");
        for (Column column : table.getColumns()) {
            builder.append("<TR>");
            builder.append("<TD>");
            if (column.isPrimaryKey()) {
                builder.append("PK");
            } else if (column.getForeignKeyTable() != null) {
                builder.append("FK");
            } else {
                builder.append("-");
            }
            builder.append("</TD><TD>");
            builder.append(column.getName());
            builder.append("</TD><TD>");
            builder.append(column.getType());
            builder.append("</TD><TD>");
            builder.append(getConstraints(column));
            builder.append("</TD></TR>\n");
        }
        builder.append("</TABLE>>];\n");

        return builder.toString();
    }

    /**
     * Entity for Plant UML IE Diagrams
     *
     * @return string in plant uml syntax
     */
    @Override
    public String getPlantRepresentation() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("entity \"%s\" as %s {\n", table.getName(), Visualizer.makeDotName(table.getName().toLowerCase())));
        int pk_count = 0;
        for (Column column : table.getColumns()) {
            if (column.isPrimaryKey()) {
                pk_count++;
                builder.append(String.format("  *%s : %s\n", column.getName(), column.getType()));
            }
        }
        if (pk_count > 0) {
            builder.append("  --\n");
        }
        for (Column column : table.getColumns()) {
            if (!column.isPrimaryKey()) {
                builder.append("  ");
                if (column.isNotNull()) {
                    builder.append("*");
                }
                builder.append(String.format("%s : %s", column.getName(), column.getType()));
                if (column.getForeignKeyTable() != null) {
                    builder.append(" <<FK>>");
                }
                builder.append("\n");
            }
        }
        builder.append("}\n");
        return builder.toString();
    }

    private String getConstraints(Column column) {
        StringBuilder constraints = new StringBuilder();

        if (column.isNotNull()) {
            constraints.append("not null");
        }
        if (column.isUnique()) {
            if (column.isNotNull()) {
                constraints.append(", ");
            }
            constraints.append("unique");
        }
        return constraints.toString();
    }

}
