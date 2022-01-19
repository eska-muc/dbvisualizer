package com.skuehnel.dbvisualizer.domain;

import java.util.List;

/**
 * Model container
 */
public class Model {

    // Attributes
    private String schemaName;
    private String catalogName;
    private String databaseName;
    private String jdbcURL;

    /**
     * Getter for attribute databaseType
     *
     * @return current value of field databaseType
     */
    public String getDatabaseType() {
        return databaseType;
    }

    /**
     * Setter for field databaseType
     *
     * @param databaseType new value
     */
    public void setDatabaseType(String databaseType) {
        this.databaseType = databaseType;
    }

    private String databaseType;

    private List<Table> tableList;

    /**
     * Getter for attribute schemaName
     *
     * @return current value of field schemaName
     */
    public String getSchemaName() {
        return schemaName;
    }

    /**
     * Setter for field schemaName
     *
     * @param schemaName new value
     */
    public void setSchemaName(String schemaName) {
        this.schemaName = schemaName;
    }

    /**
     * Getter for attribute catalogName
     *
     * @return current value of field catalogName
     */
    public String getCatalogName() {
        return catalogName;
    }

    /**
     * Setter for field catalogName
     *
     * @param catalogName new value
     */
    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    /**
     * Getter for attribute databaseName
     *
     * @return current value of field databaseName
     */
    public String getDatabaseName() {
        return databaseName;
    }

    /**
     * Setter for field databaseName
     *
     * @param databaseName new value
     */
    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    /**
     * Getter for attribute jdbcURL
     *
     * @return current value of field jdbcURL
     */
    public String getJdbcURL() {
        return jdbcURL;
    }

    /**
     * Setter for field jdbcURL
     *
     * @param jdbcURL new value
     */
    public void setJdbcURL(String jdbcURL) {
        this.jdbcURL = jdbcURL;
    }

    /**
     * Getter for attribute tableList
     *
     * @return current value of field tableList
     */
    public List<Table> getTableList() {
        return tableList;
    }

    /**
     * Setter for field tableList
     *
     * @param tableList new value
     */
    public void setTableList(List<Table> tableList) {
        this.tableList = tableList;
    }
}
