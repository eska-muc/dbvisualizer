package com.skuehnel.dbvisualizer.visualize;

import com.skuehnel.dbvisualizer.domain.Table;

public class SimpleTableVisualizer implements TableVisualizer {

    private Table table;

    public SimpleTableVisualizer(Table table) {
        this.table = table;
    }

    @Override
    public String getDotRepresentation() {
        String nodeName = Visualizer.makeDotName(table.getName().toLowerCase());
        StringBuilder builder = new StringBuilder(nodeName);
        builder.append("[label=<");
        builder.append("<TABLE BORDER=\"0\" CELLBORDER=\"1\" CELLSPACING=\"0\" PORT=\"p0\">\n");
        builder.append("<TR><TD>");
        builder.append(table.getSimpleName());
        builder.append("</TD></TR></TABLE>");
        builder.append(">];\n");
        return builder.toString();
    }

    @Override
    public String getPlantRepresentation() {
        StringBuilder builder = new StringBuilder();
        return builder.toString();
    }
}
