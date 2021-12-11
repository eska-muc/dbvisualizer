package com.skuehnel.dbvisualizer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skuehnel.dbvisualizer.domain.Table;
import com.skuehnel.dbvisualizer.retrieve.ConnectionException;
import com.skuehnel.dbvisualizer.retrieve.JDBCConnection;
import com.skuehnel.dbvisualizer.retrieve.ERModelRetriever;
import com.skuehnel.dbvisualizer.util.DB_DIALECT;
import com.skuehnel.dbvisualizer.util.FORMAT;
import com.skuehnel.dbvisualizer.util.OPTS;
import com.skuehnel.dbvisualizer.visualize.Visualizer;

/**
 * Project DBVisualizer
 * <p>
 * Main class with command line interface
 *
 * @author Stefan Kuehnel
 */
public class DBVisualizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBVisualizer.class);

    private String outputFileName;
    private String jdbcDriver;
    private String jdbcUrl;
    private String databaseUser;
    private String databasePassword;
    private String catalog = null;
    private String schema;
    private FORMAT outputFormat = FORMAT.DOT;
    private DB_DIALECT dbDialect = DB_DIALECT.MYSQL;
    private boolean lROption = false;
    private boolean entitiesOnly = false;
    private Pattern filter;

    /**
     * Default constructor
     */
    public DBVisualizer() {
    }

    /**
     * Constructor
     *
     * @param commandline the commandline
     * @throws ConnectionException if the connection to the database could not be established
     * @throws SQLException        if the execution of an SQL command failed
     */
    public DBVisualizer(CommandLine commandline) throws ConnectionException, SQLException, IOException {
        if (!assignCommandLineOptions(commandline)) {
            LOGGER.error("Could not assign all command line options. Maybe a mandatory option was not set.");
            System.err.println("One or more of the mandatory options (outputFileName, JDBC Driver class, JDBC URL) is missing.");
            System.exit(1);
        }
        LOGGER.debug("Initializing connection to DB with driver '{}', url '{}' and user '{}'.", jdbcDriver, jdbcUrl, databaseUser);
        JDBCConnection jdbcConnection = new JDBCConnection(jdbcDriver, jdbcUrl, databaseUser, databasePassword);
        ERModelRetriever retrievER = new ERModelRetriever(jdbcConnection.getConnection(), dbDialect);
        retrievER.setFilter(filter);
        List<Table> model = retrievER.getModel(catalog, schema);
        Visualizer visualizer = new Visualizer(model);
        visualizer.setLrEnabled(lROption);
        visualizer.setEntitiesOnly(entitiesOnly);
        if (outputFormat.equals(FORMAT.DOT)) {
            OutputWriter writer = new OutputWriter(outputFileName, visualizer.getDotRepresentation());
            writer.write();
        } else if (outputFormat.equals(FORMAT.PLANT)) {
            OutputWriter writer = new OutputWriter(outputFileName, visualizer.getPlantRepresentation());
            writer.write();
        }
    }

    private boolean assignCommandLineOptions(CommandLine commandLine) {
        int mandatoriesAssigned = 3;
        for (Option option : commandLine.getOptions()) {
            if (option.equals(OPTS.OPT_OUTPUT_FILE.getOption())) {
                outputFileName = option.getValue();
                mandatoriesAssigned--;
            }
            if (option.equals(OPTS.OPT_JDBC_DRV.getOption())) {
                jdbcDriver = option.getValue();
                mandatoriesAssigned--;
            }
            if (option.equals(OPTS.OPT_JDBC_URL.getOption())) {
                jdbcUrl = option.getValue();
                mandatoriesAssigned--;
            }
            if (option.equals(OPTS.OPT_USER.getOption())) {
                databaseUser = option.getValue();
            }
            if (option.equals(OPTS.OPT_PASSWORD.getOption())) {
                databasePassword = option.getValue();
            }
            if (option.equals(OPTS.OPT_ENABLE_LR.getOption())) {
                lROption = true;
            }
            if (option.equals(OPTS.OPT_ENTITIES_ONLY.getOption())) {
                entitiesOnly = true;
            }
            if (option.equals(OPTS.OPT_SCHEMA_NAME.getOption())) {
                schema = option.getValue();
            }
            if (option.equals(OPTS.OPT_CATALOG_NAME.getOption())) {
                catalog = option.getValue();
            }
            if (option.equals(OPTS.OPT_FORMAT.getOption())) {
                String format = option.getValue();
                try {
                    outputFormat = FORMAT.valueOf(format);
                } catch (IllegalArgumentException e) {
                    LOGGER.error("Unknown format {}", format);
                    System.exit(1);
                }
            }
            if (option.equals(OPTS.OPT_FILTER.getOption())) {
                String filterString = option.getValue();
                try {
                    filter = Pattern.compile(filterString);
                    LOGGER.info("Using filter pattern {}", filterString);
                } catch (PatternSyntaxException pse) {
                    LOGGER.warn("Could not parse regular expression '{}' for filtering. Will not apply any filter!");
                }
            }
            if (option.equals(OPTS.OPT_DIALECT.getOption())) {
                try {
                    dbDialect = DB_DIALECT.valueOf(option.getValue().toUpperCase());
                    LOGGER.info("Using {} as DB dialect.", dbDialect.getValue());
                } catch (IllegalArgumentException | NullPointerException e) {
                    LOGGER.error("Could not parse '{}' as DB dialect.", option.getValue());
                    System.exit(1);
                }
            }
        }
        return mandatoriesAssigned == 0;
    }

    /**
     * Main method
     *
     * @param args command line arguments
     * @throws ConnectionException if the connection to the database could not be established
     * @throws SQLException        if the execution of an SQL command failed
     * @throws IOException         if an problem with file or network I/O occured.
     */
    public static void main(String[] args) throws ConnectionException, SQLException, IOException {
        CommandLineParser parser = new DefaultParser();
        CommandLine commandLine = null;
        try {
            commandLine = parser.parse(OPTS.getOptions(), args);
        } catch (ParseException p) {
            System.err.println("Could not parse command line. " + p.getMessage());
            System.exit(1);
        }
        if (commandLine.getOptions().length == 0) {
            OPTS.printHelp(
                    "DBVisualizer",
                    "Gets all (matching) tables from given database connection and generates an outputfile in the specified format (default: .dot)");
        } else {
            new DBVisualizer(commandLine);
        }
    }

}
