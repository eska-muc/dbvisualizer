package com.skuehnel.dbvisualizer.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import com.skuehnel.dbvisualizer.domain.Column;
import com.skuehnel.dbvisualizer.domain.Table;
import com.skuehnel.dbvisualizer.visualize.Visualizer;

public class TestDBGenerator {

    private String[] datatypes; // PostgreSQL
    // datatypes

    private int numberOfTables;
    private String outputFileName;
    private TEST_TYPE testType;

    /**
     * Empty constructor
     */
    public TestDBGenerator() {
        datatypes = new String[]{"int", "date", "varchar(255)", "real"};
    }

    /**
     * Constructor
     *
     * @param commandLine commandline
     */
    public TestDBGenerator(CommandLine commandLine) {
        assignOptions(commandLine);
        datatypes = new String[]{"int", "date", "varchar(255)", "real"};
    }

    private void assignOptions(CommandLine commandLine) {
        for (Option option : commandLine.getOptions()) {
            if (option.equals(TEST_OPTS.OPT_FILENAME.getOption())) {
                outputFileName = option.getValue();
            }
            if (option.equals(TEST_OPTS.OPT_NUM.getOption())) {
                numberOfTables = Integer.valueOf(option.getValue());
            }
            if (option.equals(TEST_OPTS.OPT_TYPE.getOption())) {
                testType = TEST_TYPE.valueOf(option.getValue().toUpperCase());
            }
        }
    }

    /**
     * Generate the DDL for the random tables and write it to the configured file
     *
     * @throws InvalidParamException in case a parameter is not valid
     * @throws IOException           if something goes wrong with I/O operations
     */
    private void generateSQL() throws InvalidParamException, IOException {
        List<Table> tables = generateRandomTables(numberOfTables, 10);
        File f = new File(outputFileName);
        Writer writer = new FileWriter(f);
        for (Table t : tables) {
            writer.append(generateCreateTable(t));
            writer.append('\n');
        }
        for (Table t : tables) {
            writer.append(generateAlterTable(t));
            writer.append('\n');
        }
        writer.flush();
        writer.close();
        System.out.println("Generated DDL statements for " + numberOfTables + " tables in file " + outputFileName);
    }

    private void generateDOT() throws InvalidParamException, IOException {
        List<Table> tables = generateRandomTables(numberOfTables, 10);
        Visualizer visualizer = new Visualizer(tables);
        File f = new File(outputFileName);
        Writer writer = new FileWriter(f);
        writer.write(visualizer.getDotRepresentation());
        writer.close();
        System.out.println("Wrote .dot file for " + numberOfTables + " tables in file " + outputFileName);
    }

    private String generateCreateTable(Table t) {
        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE ");
        builder.append(t.getName());
        builder.append(" (");
        boolean first = true;
        for (Column column : t.getColumns()) {
            if (!first) {
                builder.append(", ");
            } else {
                first = false;
            }
            builder.append(column.getName());
            builder.append(' ');
            builder.append(column.getType());
            if (column.isPrimaryKey()) {
                builder.append(" PRIMARY KEY ");
            }
        }
        builder.append(");");
        return builder.toString();
    }

    private String generateAlterTable(Table t) {
        StringBuilder builder = new StringBuilder();
        List<Column> columns = t.getColumns();
        for (Column column : columns) {
            Table fkTable = column.getForeignKeyTable();
            if (fkTable != null) {
                builder.append("ALTER TABLE ");
                builder.append(t.getName());
                builder.append(" ADD FOREIGN KEY (");
                builder.append(column.getName());
                builder.append(") REFERENCES ");
                builder.append(fkTable.getName());
                builder.append(" (");
                builder.append(column.getForeignKeyColumn().getName());
                builder.append(");");
            }
        }
        return builder.toString();
    }

    public static void main(String[] args) throws InvalidParamException,
            IOException {
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(TEST_OPTS.getOptions(), args);
        } catch (ParseException pex) {
            System.err.println("Could not parse commandline. "
                    + pex.getMessage());
            printHelp();
            System.exit(1);
        }
        if (commandLine.getOptions().length == 0) {
            printHelp();
        } else {
            TestDBGenerator testDBGenerator = new TestDBGenerator(commandLine);
            if (testDBGenerator.testType.equals(TEST_TYPE.SQL)) {
                testDBGenerator.generateSQL();
            } else {
                testDBGenerator.generateDOT();
            }
        }
    }

    private static void printHelp() {
        TEST_OPTS.printHelp(
                "TestDBGenerator",
                "Creates a DDL .sql file or a .dot file with some randomly generated tables.");
    }

    /**
     * Generate a bunch of random tables. Each table will have a Primary key
     * column (name "id") and one foreign key relation to a randomly chosen
     * other table. Every table will have a random number of columns (max 10
     *
     * @param number     number of tables (at least 1)
     * @param maxColumns maximal number of columns per table (must be at least 2)
     * @return list of tables.
     * @throws InvalidParamException if an invalid parameter has been used
     */
    public List<Table> generateRandomTables(int number, int maxColumns)
            throws InvalidParamException {
        List<Table> tables = new ArrayList<>();
        if (number < 1 || maxColumns < 2) {
            throw new InvalidParamException(
                    "Number of tables must be at least 1; max number of columns must be at least 2.");
        }
        for (int i = 0; i < number; i++) {
            Table t = new Table(RandomStringUtils.randomAlphabetic(6));
            Column idColumn = new Column("id", "int", true, true, true);
            List<Column> columns = new ArrayList<>();
            columns.add(idColumn);
            // rest of columns
            int numColumns = RandomUtils.nextInt(2, maxColumns);
            for (int j = 1; j < numColumns; j++) {
                Column c = new Column(RandomStringUtils.randomAlphabetic(5),
                        datatypes[RandomUtils.nextInt(0, datatypes.length)],
                        false, false, false);
                columns.add(c);
            }
            t.setColumns(columns);
            tables.add(t);
        }


        // for each table set a FK relationship to one to five other (randomly selected)
        for (Table t : tables) {
            connectTableToOthers(t, tables);
        }
        return tables;
    }

    private void connectTableToOthers(Table t, List<Table> others) {
        int numFKColumns = RandomUtils.nextInt(1, 3);
        for (int i = 0; i < numFKColumns; i++) {
            int tableToConnectTo = RandomUtils.nextInt(0, others.size() - 1);
            Table other = others.get(tableToConnectTo);
            Column pkColumnOfOtherTable = other.getColumns().get(0);
            Column fkColumn = new Column(other.getName().concat("_ID"), "int",
                    false, false, false);
            fkColumn.setForeignKeyTable(other);
            fkColumn.setForeignKeyColumn(pkColumnOfOtherTable);
            t.getColumns().add(fkColumn);
            t.updateForeignKeyRelations();
        }
    }

}
