package com.skuehnel.dbvisualizer.visualize;

public interface TableVisualizer {
    /**
     * Create a node description for this table
     *
     * @return the dot representation of a table
     */
    String getDotRepresenation();
}
