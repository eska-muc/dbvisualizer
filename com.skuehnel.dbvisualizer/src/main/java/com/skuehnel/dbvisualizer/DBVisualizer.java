package com.skuehnel.dbvisualizer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.skuehnel.dbvisualizer.domain.Model;
import com.skuehnel.dbvisualizer.report.HTMLReportGenerator;
import com.skuehnel.dbvisualizer.report.ReportGenerator;
import com.skuehnel.dbvisualizer.report.ReportGeneratorFactory;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private enum REPORT_FORMAT {HTML, MARKDOWN, PDF}

    private String outputFileName;
    private String jdbcDriver;
    private String jdbcUrl;
    private String jdbcDriverPath;
    private String databaseUser;
    private String databasePassword;
    private String catalog = null;
    private String schema;
    private FORMAT outputFormat = FORMAT.DOT;
    private DB_DIALECT dbDialect = DB_DIALECT.MYSQL;
    private boolean lROption = false;
    private boolean entitiesOnly = false;
    private Pattern filter;
    private String reportFile;
    private REPORT_FORMAT reportFormat = REPORT_FORMAT.HTML;
    private boolean reportMeta = false;

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
        LOGGER.debug("Initializing connection to DB with driver '{}', driver path {}, url '{}' and user '{}'.", jdbcDriver, jdbcDriverPath != null ? jdbcDriverPath : "n/a", jdbcUrl, databaseUser);
        JDBCConnection jdbcConnection = new JDBCConnection(jdbcDriver, jdbcDriverPath, jdbcUrl, databaseUser, databasePassword);
        ERModelRetriever retrievER = new ERModelRetriever(jdbcConnection.getConnection(), dbDialect);
        retrievER.setFilter(filter);
        Model model = retrievER.getModel(catalog, schema);
        model.setJdbcURL(jdbcUrl);
        model.setFilterInfo(filter != null ? filter.toString() : null);
        Visualizer visualizer = new Visualizer(model.getTableList());
        visualizer.setLrEnabled(lROption);
        visualizer.setEntitiesOnly(entitiesOnly);
        // Create an additional report
        if (reportFile != null) {
            ReportGenerator reportGenerator = ReportGeneratorFactory.createReportGeneratorInstance(reportFormat.toString());
            if (reportMeta) {
                reportGenerator.initMetaInformation(model);
            }
            reportGenerator.generateReport(reportFile, model, reportMeta ? ReportGenerator.REPORT_OPT.WITH_META_INFORMATION : null);
        }
        if (outputFormat.equals(FORMAT.DOT)) {
            OutputWriter writer = new OutputWriter(outputFileName, visualizer.getDotRepresentation());
            writer.write();
        } else if (outputFormat.equals(FORMAT.PLANT)) {
            OutputWriter writer = new OutputWriter(outputFileName, visualizer.getPlantRepresentation());
            writer.write();
        }
    }

    private boolean assignCommandLineOptions(CommandLine commandLine) {
        long mandatoriesAssigned = Arrays.stream(OPTS.values()).filter(o -> o.getOption().isRequired()).count();
        LOGGER.debug("Number of mandatory arguments: {}", mandatoriesAssigned);
        for (Option option : commandLine.getOptions()) {
            if (option.isRequired()) {
                mandatoriesAssigned--;
            }
            if (option.equals(OPTS.OPT_OUTPUT_FILE.getOption())) {
                outputFileName = option.getValue();
            }
            if (option.equals(OPTS.OPT_JDBC_DRV.getOption())) {
                jdbcDriver = option.getValue();
            }
            if (option.equals(OPTS.OPT_JDBC_URL.getOption())) {
                jdbcUrl = option.getValue();
            }
            if (option.equals(OPTS.OPT_JDBC_DRV_PATH.getOption())) {
                jdbcDriverPath = option.getValue();
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
                    LOGGER.warn("Could not parse regular expression '{}' for filtering. Will not apply any filter!", filterString);
                }
            }
            if (option.equals(OPTS.OPT_REPORT_FILE.getOption())) {
                reportFile = option.getValue();
            }
            if (option.equals(OPTS.OPT_REPORT_META.getOption())) {
                reportMeta = true;
            }
            if (option.equals(OPTS.OPT_REPORT_FORMAT.getOption())) {
                try {
                    reportFormat = REPORT_FORMAT.valueOf(option.getValue().toUpperCase());
                } catch (IllegalArgumentException illegalArgumentException) {
                    LOGGER.error("Unsupported Format for reports.", illegalArgumentException);
                    System.exit(1);
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
        LOGGER.debug("Number of mandatory arguments left: {}", mandatoriesAssigned);
        return mandatoriesAssigned == 0;
    }

    private static void usage() {
        OPTS.printHelp(
                "DBVisualizer",
                "Gets all (matching) tables from given database connection and generates an outputfile in the specified format (default: .dot)");
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
        if (args.length == 0) {
            usage();
            System.exit(0);
        }
        try {
            commandLine = parser.parse(OPTS.getOptions(), args);
        } catch (ParseException p) {
            System.err.println("Could not parse command line. " + p.getMessage());
            usage();
            System.exit(1);
        }
        new DBVisualizer(commandLine);
    }

}
