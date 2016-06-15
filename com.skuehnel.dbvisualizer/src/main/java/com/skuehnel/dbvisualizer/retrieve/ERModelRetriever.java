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

import javax.swing.JScrollBar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skuehnel.dbvisualizer.domain.Column;
import com.skuehnel.dbvisualizer.domain.Table;

/**
 * Project DBVisualizer
 * 
 * @author Stefan Kuehnel
 *
 */
public class ERModelRetriever {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(ERModelRetriever.class);

	private Connection jdbcConnection;

	private static final String TABLE_TYPES[] = {"TABLE","VIEW"}; 
	
	private Map<String,Table> knownTables;
	
	
	/**
	 * Constructor
	 * 
	 * @param jdbcConnection
	 *            a {@link java.sql.Connection} object
	 */
	public ERModelRetriever(Connection jdbcConnection) {
		this.jdbcConnection = jdbcConnection;
		knownTables = new HashMap<>();
	}

	/**
	 * Retrieve a list of {@link com.skuehnel.dbvisualizer.domain.Table} objects
	 * from the database
	 * 
	 * @return
	 * @throws SQLException
	 */
	public List<Table> getModel() throws SQLException {
		List<Table> result = new ArrayList<>();
		DatabaseMetaData databaseMetaData = jdbcConnection.getMetaData();
		if (databaseMetaData != null) {
			ResultSet schemaResultSet = databaseMetaData.getSchemas();
			if (schemaResultSet != null) {
				schemaResultSet.beforeFirst();
				while (schemaResultSet.next()) {
					String catalog = schemaResultSet.getString("TABLE_CATALOG");
					String schema = schemaResultSet.getString("TABLE_SCHEM");
					LOGGER.debug(
							"Going to retrieve tables for catalog '{}' and schema '{}'.",
							catalog, schema);
					List<Table> tablesForSchema = getTables(databaseMetaData,catalog,schema);
					result.addAll(tablesForSchema);
				}
			}

		}
		return result;
	}

	protected List<Table> getTables(DatabaseMetaData databaseMetaData,String catalog,String schema) throws SQLException {
		List<Table> tablesForSchema = new ArrayList<>();
		LOGGER.debug("Retrieving tables for catalog '{}' and schema '{}'",catalog,schema);
		ResultSet tablesResultSet = databaseMetaData.getTables(catalog, schema, null, TABLE_TYPES);
		if (tablesResultSet != null) {
			tablesResultSet.beforeFirst();
			while (tablesResultSet.next()) {
				String tableName = tablesResultSet.getString("TABLE_NAME");
				LOGGER.debug("Table: '{}'",tableName);
				
				
				Set<String> primaryKeyNames = new HashSet<>();
				Map<String,Table> referencedTables = new HashMap<>();
				
				ResultSet primaryKeysRS = databaseMetaData.getPrimaryKeys(catalog, schema, tableName);
				if (primaryKeysRS != null) {
					primaryKeysRS.beforeFirst();
					while (primaryKeysRS.next()) {
						String pkColumnName = primaryKeysRS.getString("COLUMN_NAME");
						if (pkColumnName!=null) {
							primaryKeyNames.add(pkColumnName);
						}
					}
				}			
				
				ResultSet importedKeysRS = databaseMetaData.getImportedKeys(catalog, schema, tableName);
				if (importedKeysRS != null) {
					importedKeysRS.beforeFirst();
					while (importedKeysRS.next()) {
						String fkColumnName = importedKeysRS.getString("FKCOLUMN_NAME");
						String referencedPkColumnName = importedKeysRS.getString("PKCOLUMN_NAME");
						
						if (fkColumnName != null) {							
							String fkTableCat = importedKeysRS.getString("PKTABLE_CAT");
							String fkTableSchema = importedKeysRS.getString("PKTABLE_SCHEM");
							String fkTableName = importedKeysRS.getString("PKTABLE_NAME");
							LOGGER.debug("FKCOLUMN_NAME: '{}' -> '{}' (PK of referenced table: '{}')",fkColumnName,fkTableName,referencedPkColumnName);
							Table fkTable = getTable(fkTableCat, fkTableSchema, fkTableName);							
							referencedTables.put(fkColumnName, fkTable);
						}
						
					}
				}				
				
				// Get the columns for this table
				List<Column> columns = new ArrayList<Column>();
				ResultSet columnResultSet = databaseMetaData.getColumns(catalog, schema, tableName, null);
				if (columnResultSet != null) {
					columnResultSet.beforeFirst();
					while (columnResultSet.next()) {
						String columnName = columnResultSet.getString("COLUMN_NAME");
						String dataType = getTypeDescription(columnResultSet);
						boolean nullable = checkBoolea(columnResultSet, "IS_NULLABLE","yes");
						LOGGER.debug("Column: '{}', Type: '{}'",columnName,dataType);
						Column column = new Column(columnName,dataType);
						column.setNotNull(!nullable);
						if (primaryKeyNames.contains(columnName)) {
							LOGGER.debug("Column: '{}' is primary key!",columnName);
							column.setPrimaryKey(true);
						}
						if (referencedTables.containsKey(columnName)) {
							Table referencedTable = referencedTables.get(columnName);
							LOGGER.debug("Column: '{}' is foreign key to table '{}'!",columnName,referencedTable.getName());
							column.setForeignKeyTable(referencedTable);							
						}
						
						columns.add(column);
					}
				}
				
				Table t = getTable(catalog, schema, tableName);
				t.setColumns(columns);
				tablesForSchema.add(t);
			}
		}		
		return tablesForSchema;
	}
	
	/**
	 * Create a data type description for this column
	 * @param columnResultSet
	 * @return
	 * @throws SQLException 
	 */
	protected String getTypeDescription(ResultSet columnResultSet) throws SQLException {
		StringBuffer buffer = new StringBuffer();
		
		JDBCType type = JDBCType.valueOf(columnResultSet.getInt("DATA_TYPE"));
		
		int size = columnResultSet.getInt("COLUMN_SIZE");
		if (type != null) {
			buffer.append(type.name());			
			if (type.equals(JDBCType.CHAR)
					|| type.equals(JDBCType.VARCHAR) 
					|| type.equals(JDBCType.LONGNVARCHAR) 
					|| type.equals(JDBCType.LONGVARCHAR)
					|| type.equals(JDBCType.NCHAR)
					|| type.equals(JDBCType.NVARCHAR)					
					) {				
				buffer.append("(");								
				int sizeOctets = columnResultSet.getInt("CHAR_OCTET_LENGTH");
				buffer.append(sizeOctets);				
				buffer.append(")");
			} else if (type.equals(JDBCType.DECIMAL) 
					|| type.equals(JDBCType.DOUBLE)
					|| type.equals(JDBCType.FLOAT)
					|| type.equals(JDBCType.REAL)
					|| type.equals(JDBCType.NUMERIC)) {
				buffer.append("(");
				buffer.append(size); 
				int decimalDigits = columnResultSet.getInt("DECIMAL_DIGITS");
				buffer.append(",");
				buffer.append(decimalDigits);
				buffer.append(")");
			} 
		}
		return buffer.toString();
	}
	
	
	/**
	 * Factory method for tables; each table shall only be created once
	 * @param catalog
	 * @param schema
	 * @param tableName
	 * @return
	 */
	private Table getTable(String catalog,String schema,String tableName) {
		String key = createKey(catalog, schema, tableName);
		Table t = knownTables.get(key);
		if (t == null) {
			t = new Table(key);
			knownTables.put(key, t);			
		}
		return t;
	}
	
	private String createKey(String catalog,String schema,String tableName) {
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
	
	private boolean checkBoolea(ResultSet rs,String columnName,String comparison) throws SQLException {
		boolean result = false;
		if (rs!=null && columnName !=null && comparison != null) {
			String stringValue = rs.getString(columnName);
			if (stringValue != null) {
				result = stringValue.equalsIgnoreCase(comparison);
			}
		}
		return result;
	}
	
}
