package com.skuehnel.dbvisualizer.retrieve;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skuehnel.dbvisualizer.domain.Column;
import com.skuehnel.dbvisualizer.domain.Table;
import com.skuehnel.dbvisualizer.util.DB_DIALECT;

/**
 * Project DBVisualizer
 *
 * @author Stefan Kuehnel
 */
public class ERModelRetriever {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ERModelRetriever.class);

    private static final String COLUMN_SIZE = "COLUMN_SIZE";
    private static final String TYPE_NAME = "TYPE_NAME";
    private static final String COLUMN_NAME = "COLUMN_NAME";
    private static final String DATA_TYPE = "DATA_TYPE";
    private static final String DECIMAL_DIGITS = "DECIMAL_DIGITS";
    private static final String TABLE_CATALOG = "TABLE_CATALOG";
    private static final String TABLE_SCHEM = "TABLE_SCHEM";
    private static final String TABLE_NAME = "TABLE_NAME";
    private static final String TABLE_TYPE = "TABLE_TYPE";
    private static final String FKCOLUMN_NAME = "FKCOLUMN_NAME";
    private static final String PKCOLUMN_NAME = "PKCOLUMN_NAME";
    private static final String PKTABLE_CAT = "PKTABLE_CAT";
    private static final String PKTABLE_SCHEM = "PKTABLE_SCHEM";
    private static final String PKTABLE_NAME = "PKTABLE_NAME";
    private static final String CHAR_OCTET_LENGTH = "CHAR_OCTET_LENGTH";

    private Connection jdbcConnection;

    private static final String[] TABLE_TYPES = {"TABLE", "VIEW"};

    private Map<String, Table> knownTables;

    private DB_DIALECT dbDialect;

    /**
     * Constructor
     *
     * @param jdbcConnection a {@link java.sql.Connection} object
     */
    public ERModelRetriever(Connection jdbcConnection, DB_DIALECT dbDialect) {
        this.jdbcConnection = jdbcConnection;
        this.dbDialect = dbDialect;
        knownTables = new HashMap<>();
    }

    /**
     * Retrieve a list of {@link com.skuehnel.dbvisualizer.domain.Table} objects
     * from the database
     *
     * @param catalog Catalog in which the schema(s) for which a model should be created are located. May be null
     * @param schema  Schema for which the model shall be created
     * @return list of table objects
     * @throws SQLException if an error occurs
     */
    public List<Table> getModel(String catalog, String schema) throws SQLException {
        List<Table> result = new ArrayList<>();
        DatabaseMetaData databaseMetaData = jdbcConnection.getMetaData();
        if (databaseMetaData != null) {

            if (schema == null) {

                ResultSet schemaResultSet = databaseMetaData.getSchemas();
                if (schemaResultSet != null) {
                    while (schemaResultSet.next()) {

                        String currentCatalog = null;
                        try {
                            currentCatalog = schemaResultSet
                                    .getString(TABLE_CATALOG);
                        } catch (SQLException e) {
                            LOGGER.warn("Problem when trying to read Catalog information. Exception: '{}'", e);
                        }
                        String currentSchema = schemaResultSet
                                .getString(TABLE_SCHEM);
                        LOGGER.debug(
                                "Going to retrieve tables for catalog '{}' and schema '{}'.",
                                catalog, schema);
                        List<Table> tablesForSchema = getTables(
                                databaseMetaData, currentCatalog, currentSchema);
                        result.addAll(tablesForSchema);
                    }
                    schemaResultSet.close();
                }
            } else {
                result.addAll(getTables(databaseMetaData, catalog, schema));
            }
        }
        return result;
    }

    private List<Table> getTables(DatabaseMetaData databaseMetaData,
                                  String catalog, String schema) throws SQLException {
        List<Table> tablesForSchema = new ArrayList<>();
        LOGGER.debug("Retrieving tables for catalog '{}' and schema '{}'",
                catalog, schema);
        ResultSet tablesResultSet = databaseMetaData.getTables(catalog, schema,
                null, TABLE_TYPES);
        if (tablesResultSet != null) {
            while (tablesResultSet.next()) {
                String tableName = tablesResultSet.getString(TABLE_NAME);
                String tableType = tablesResultSet.getString(TABLE_TYPE);
                LOGGER.debug("Table: '{}' Type: '{}'", tableName, tableType);

                Set<String> primaryKeyNames = new HashSet<>();
                Map<String, Table> referencedTables = new HashMap<>();

                ResultSet primaryKeysRS = databaseMetaData.getPrimaryKeys(
                        catalog, schema, tableName);
                if (primaryKeysRS != null) {
                    while (primaryKeysRS.next()) {
                        String pkColumnName = primaryKeysRS
                                .getString(COLUMN_NAME);
                        if (pkColumnName != null) {
                            primaryKeyNames.add(pkColumnName);
                        }
                    }
                    primaryKeysRS.close();
                }

                ResultSet importedKeysRS = databaseMetaData.getImportedKeys(
                        catalog, schema, tableName);
                if (importedKeysRS != null) {
                    while (importedKeysRS.next()) {
                        String fkColumnName = importedKeysRS
                                .getString(FKCOLUMN_NAME);
                        String referencedPkColumnName = importedKeysRS
                                .getString(PKCOLUMN_NAME);

                        if (fkColumnName != null) {
                            String fkTableCat = null;
                            if (dbDialect != DB_DIALECT.MYSQL) {
                                // MySQL returns a cat here, even is no catalog is used overall
                                fkTableCat = importedKeysRS
                                        .getString(PKTABLE_CAT);
                            }
                            String fkTableSchema = importedKeysRS
                                    .getString(PKTABLE_SCHEM);
                            String fkTableName = importedKeysRS
                                    .getString(PKTABLE_NAME);
                            LOGGER.debug(
                                    "FKCOLUMN_NAME: '{}' -> '{}' (PK of referenced table: '{}')",
                                    fkColumnName, fkTableName,
                                    referencedPkColumnName);

                            // If catalog/schema information is null, use current catalog/schema
                            if (fkTableSchema == null) {
                                fkTableSchema = schema;
                            }
                            if (fkTableCat == null) {
                                fkTableCat = catalog;
                            }
                            Table fkTable = getTable(fkTableCat, fkTableSchema,
                                    fkTableName);
                            referencedTables.put(fkColumnName, fkTable);
                        }
                    }
                    importedKeysRS.close();
                }

                // Get the columns for this table
                List<Column> columns = new ArrayList<>();
                ResultSet columnResultSet = databaseMetaData.getColumns(
                        catalog, schema, tableName, null);
                if (columnResultSet != null) {
                    while (columnResultSet.next()) {
                        String columnName = columnResultSet
                                .getString(COLUMN_NAME);
                        String dataType = getTypeDescription(columnResultSet);
                        boolean nullable = checkBoolean(columnResultSet,
                                "IS_NULLABLE", "yes");
                        LOGGER.debug("Column: '{}', Type: '{}'", columnName,
                                dataType);
                        Column column = new Column(columnName, dataType);
                        column.setNotNull(!nullable);
                        if (primaryKeyNames.contains(columnName)) {
                            LOGGER.debug("Column: '{}' is primary key!",
                                    columnName);
                            column.setPrimaryKey(true);
                        }
                        if (referencedTables.containsKey(columnName)) {
                            Table referencedTable = referencedTables
                                    .get(columnName);
                            LOGGER.debug(
                                    "Column: '{}' is foreign key to table '{}'!",
                                    columnName, referencedTable.getName());
                            column.setForeignKeyTable(referencedTable);
                        }

                        columns.add(column);
                    }
                    columnResultSet.close();
                }

                Table t = getTable(catalog, schema, tableName);
                t.setColumns(columns);
                tablesForSchema.add(t);
            }
            tablesResultSet.close();
        }
        return tablesForSchema;
    }

    /**
     * Create a data type description for this column
     *
     * @param columnResultSet result set of java.sql.DatabaseMetaData#getColumns
     * @return String representation of the data type of the column
     * @throws SQLException if an error occurs
     */
    private String getTypeDescription(ResultSet columnResultSet)
            throws SQLException {
        StringBuilder buffer = new StringBuilder();

        // initialize with a proper value, if type cannot be resolved
        JDBCType type = JDBCType.OTHER;
        int typeValue = columnResultSet.getInt(DATA_TYPE);

        try {
            type = JDBCType.valueOf(typeValue);
        } catch (IllegalArgumentException exception) {
            LOGGER.warn(
                    "Integer constant '{}' does not seem to represent a known JDBCType.",
                    typeValue);
        }

        int size = columnResultSet.getInt(COLUMN_SIZE);
        if (type != null) {
            if (columnResultSet.getString(TYPE_NAME) != null) {
                buffer.append(columnResultSet.getString(TYPE_NAME));
            } else {
                buffer.append(type.name());
            }
            if (type.equals(JDBCType.CHAR) || type.equals(JDBCType.VARCHAR)
                    || type.equals(JDBCType.LONGNVARCHAR)
                    || type.equals(JDBCType.LONGVARCHAR)
                    || type.equals(JDBCType.NCHAR)
                    || type.equals(JDBCType.NVARCHAR)) {
                buffer.append("(");
                buffer.append(size);
                buffer.append(")");
            } else if (type.equals(JDBCType.DECIMAL)
                    || type.equals(JDBCType.DOUBLE)
                    || type.equals(JDBCType.FLOAT)
                    || type.equals(JDBCType.REAL)
                    || type.equals(JDBCType.NUMERIC)) {

                buffer.append("(");
                if (dbDialect == DB_DIALECT.ORACLE) {
                    if (size == 0) {
                        if (columnResultSet.getObject(CHAR_OCTET_LENGTH) != null) {
                            size = columnResultSet.getInt(CHAR_OCTET_LENGTH);
                        }
                    }
                    buffer.append(size);
                } else {
                    buffer.append(size);
                }
                if (columnResultSet.getObject(DECIMAL_DIGITS) != null) {
                    int fractionalDigits = columnResultSet.getInt(DECIMAL_DIGITS);
                    if (fractionalDigits > 0) {
                        buffer.append(',');
                        buffer.append(fractionalDigits);
                    }
                }

                buffer.append(")");
            }
        }
        return buffer.toString();
    }

    /**
     * Factory method for tables; each table shall only be created once
     *
     * @param catalog   name of the catalog
     * @param schema    name of the schema
     * @param tableName name of the table
     * @return a Table object
     */
    private Table getTable(String catalog, String schema, String tableName) {
        String key = createKey(catalog, schema, tableName);
        Table t = knownTables.get(key);
        if (t == null) {
            t = new Table(key);
            knownTables.put(key, t);
        }
        return t;
    }

    private String createKey(String catalog, String schema, String tableName) {
        StringBuilder keyBuilder = new StringBuilder();
        if (catalog != null) {
            keyBuilder.append(catalog);
            keyBuilder.append('.');
        }
        if (schema != null) {
            keyBuilder.append(schema);
            keyBuilder.append('.');
        }
        keyBuilder.append(tableName);
        return keyBuilder.toString();
    }

    private boolean checkBoolean(ResultSet rs, String columnName,
                                 String comparison) throws SQLException {
        boolean result = false;
        if (rs != null && columnName != null && comparison != null) {
            String stringValue = rs.getString(columnName);
            if (stringValue != null) {
                result = stringValue.equalsIgnoreCase(comparison);
            }
        }
        return result;
    }

}
