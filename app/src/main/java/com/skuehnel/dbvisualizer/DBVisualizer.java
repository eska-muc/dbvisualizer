package com.skuehnel.dbvisualizer;

import java.io.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.skuehnel.dbvisualizer.domain.Model;
import com.skuehnel.dbvisualizer.report.ReportGenerator;
import com.skuehnel.dbvisualizer.report.ReportGeneratorFactory;
import com.skuehnel.dbvisualizer.util.MissingMandatoryException;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;
import net.sourceforge.plantuml.SourceStringReader;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.StringUtils;
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


    public static class DBVisualizerBuilder {

        private static final Logger LOGGER = LoggerFactory.getLogger(DBVisualizerBuilder.class);

        private DBVisualizer instance;

        public DBVisualizerBuilder() {
            instance = new DBVisualizer();
        }

        public static DBVisualizerBuilder builder() {
            return new DBVisualizerBuilder();
        }

        public DBVisualizerBuilder withOutputFileName(String outputFileName) {
            instance.setOutputFileName(outputFileName);
            return this;
        }

        public DBVisualizerBuilder withJdbcDriver(String jdbcDriver) {
            instance.setJdbcDriver(jdbcDriver);
            return this;
        }

        public DBVisualizerBuilder withJdbcUrl(String jbdcUrl) {
            instance.setJdbcUrl(jbdcUrl);
            return this;
        }

        public DBVisualizerBuilder withCatalog(String catalog) {
            instance.setCatalog(catalog);
            return this;
        }

        public DBVisualizerBuilder withDatabaseUser(String databaseUser) {
            instance.setDatabaseUser(databaseUser);
            return this;
        }

        public DBVisualizerBuilder withDatabasePassword(String databasePassword) {
            instance.setDatabasePassword(databasePassword);
            return this;
        }

        public DBVisualizerBuilder withSchema(String schema) {
            instance.setSchema(schema);
            return this;
        }

        public DBVisualizerBuilder withOutputFormat(String outputFormat) {
            if (StringUtils.isNotEmpty(outputFormat)) {
                FORMAT format = FORMAT.valueOf(outputFormat.toUpperCase());
                instance.setOutputFormat(format);
            } else {
                LOGGER.warn("Ignoring empty output format");
            }
            return this;
        }

        public DBVisualizerBuilder withDBDialect(String dialect) {
            if (StringUtils.isNotEmpty(dialect)) {
                DB_DIALECT db_dialect = DB_DIALECT.valueOf(dialect.toUpperCase());
                instance.setDbDialect(db_dialect);
            } else {
                LOGGER.warn("Ignoring empty db dialect");
            }
            return this;
        }

        public DBVisualizerBuilder withDbDialect(String dbDialect) {
            if (StringUtils.isNotEmpty(dbDialect)) {
                DB_DIALECT db_dialect = DB_DIALECT.valueOf(dbDialect);
                instance.setDbDialect(db_dialect);
            } else {
                LOGGER.warn("Ignoring empty db dialect");
            }
            return this;
        }

        public DBVisualizerBuilder withFilter(String filter) {
            if (StringUtils.isNotEmpty(filter)) {
                Pattern filterPattern = Pattern.compile(filter);
                instance.setFilter(filterPattern);
            } else {
                LOGGER.warn("Ignoring empty filter");
            }
            return this;
        }

        public DBVisualizerBuilder withReportFile(String reportFile) {
            instance.setReportFile(reportFile);
            return this;
        }

        public DBVisualizerBuilder withReportFormat(String reportFormat) {
            if (StringUtils.isNotEmpty(reportFormat)) {
                REPORT_FORMAT report_format = REPORT_FORMAT.valueOf(reportFormat);
                instance.setReportFormat(report_format);
            } else {
                LOGGER.warn("Ignoring empty report format");
            }
            return this;
        }

        public DBVisualizerBuilder withReportMetadata(boolean reportMetadata) {
            instance.setReportMeta(reportMetadata);
            return this;
        }

        public DBVisualizer build() {
            return instance;
        }

    }

    private enum REPORT_FORMAT {HTML, MARKDOWN, PDF}

    private String outputFileName;
    private String jdbcDriver;
    private String jdbcUrl;
    private String jdbcDriverPath;
    private String databaseUser;
    private String databasePassword;
    private String catalog = null;
    private String configFile = null;
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
     * Getter for attribute outputFileName
     *
     * @return current value of field outputFileName
     */
    public String getOutputFileName() {
        return outputFileName;
    }

    /**
     * Setter for field outputFileName
     *
     * @param outputFileName new value
     */
    public void setOutputFileName(String outputFileName) {
        this.outputFileName = outputFileName;
    }

    /**
     * Getter for attribute jdbcDriver
     *
     * @return current value of field jdbcDriver
     */
    public String getJdbcDriver() {
        return jdbcDriver;
    }

    /**
     * Setter for field jdbcDriver
     *
     * @param jdbcDriver new value
     */
    public void setJdbcDriver(String jdbcDriver) {
        this.jdbcDriver = jdbcDriver;
    }

    /**
     * Getter for attribute jdbcUrl
     *
     * @return current value of field jdbcUrl
     */
    public String getJdbcUrl() {
        return jdbcUrl;
    }

    /**
     * Setter for field jdbcUrl
     *
     * @param jdbcUrl new value
     */
    public void setJdbcUrl(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
    }

    /**
     * Getter for attribute jdbcDriverPath
     *
     * @return current value of field jdbcDriverPath
     */
    public String getJdbcDriverPath() {
        return jdbcDriverPath;
    }

    /**
     * Setter for field jdbcDriverPath
     *
     * @param jdbcDriverPath new value
     */
    public void setJdbcDriverPath(String jdbcDriverPath) {
        this.jdbcDriverPath = jdbcDriverPath;
    }

    /**
     * Getter for attribute databaseUser
     *
     * @return current value of field databaseUser
     */
    public String getDatabaseUser() {
        return databaseUser;
    }

    /**
     * Setter for field databaseUser
     *
     * @param databaseUser new value
     */
    public void setDatabaseUser(String databaseUser) {
        this.databaseUser = databaseUser;
    }

    /**
     * Getter for attribute databasePassword
     *
     * @return current value of field databasePassword
     */
    public String getDatabasePassword() {
        return databasePassword;
    }

    /**
     * Setter for field databasePassword
     *
     * @param databasePassword new value
     */
    public void setDatabasePassword(String databasePassword) {
        this.databasePassword = databasePassword;
    }

    /**
     * Getter for attribute catalog
     *
     * @return current value of field catalog
     */
    public String getCatalog() {
        return catalog;
    }

    /**
     * Setter for field catalog
     *
     * @param catalog new value
     */
    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    /**
     * Getter for attribute schema
     *
     * @return current value of field schema
     */
    public String getSchema() {
        return schema;
    }

    /**
     * Setter for field schema
     *
     * @param schema new value
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * Getter for attribute outputFormat
     *
     * @return current value of field outputFormat
     */
    public FORMAT getOutputFormat() {
        return outputFormat;
    }

    /**
     * Setter for field outputFormat
     *
     * @param outputFormat new value
     */
    public void setOutputFormat(FORMAT outputFormat) {
        this.outputFormat = outputFormat;
    }

    /**
     * Getter for attribute dbDialect
     *
     * @return current value of field dbDialect
     */
    public DB_DIALECT getDbDialect() {
        return dbDialect;
    }

    /**
     * Setter for field dbDialect
     *
     * @param dbDialect new value
     */
    public void setDbDialect(DB_DIALECT dbDialect) {
        this.dbDialect = dbDialect;
    }

    /**
     * Getter for attribute lROption
     *
     * @return current value of field lROption
     */
    public boolean islROption() {
        return lROption;
    }

    /**
     * Setter for field lROption
     *
     * @param lROption new value
     */
    public void setlROption(boolean lROption) {
        this.lROption = lROption;
    }

    /**
     * Getter for attribute entitiesOnly
     *
     * @return current value of field entitiesOnly
     */
    public boolean isEntitiesOnly() {
        return entitiesOnly;
    }

    /**
     * Setter for field entitiesOnly
     *
     * @param entitiesOnly new value
     */
    public void setEntitiesOnly(boolean entitiesOnly) {
        this.entitiesOnly = entitiesOnly;
    }

    /**
     * Getter for attribute filter
     *
     * @return current value of field filter
     */
    public Pattern getFilter() {
        return filter;
    }

    /**
     * Setter for field filter
     *
     * @param filter new value
     */
    public void setFilter(Pattern filter) {
        this.filter = filter;
    }

    /**
     * Getter for attribute reportFile
     *
     * @return current value of field reportFile
     */
    public String getReportFile() {
        return reportFile;
    }

    /**
     * Setter for field reportFile
     *
     * @param reportFile new value
     */
    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }

    /**
     * Getter for attribute reportFormat
     *
     * @return current value of field reportFormat
     */
    public REPORT_FORMAT getReportFormat() {
        return reportFormat;
    }

    /**
     * Setter for field reportFormat
     *
     * @param reportFormat new value
     */
    public void setReportFormat(REPORT_FORMAT reportFormat) {
        this.reportFormat = reportFormat;
    }

    /**
     * Getter for attribute reportMeta
     *
     * @return current value of field reportMeta
     */
    public boolean isReportMeta() {
        return reportMeta;
    }

    /**
     * Setter for field reportMeta
     *
     * @param reportMeta new value
     */
    public void setReportMeta(boolean reportMeta) {
        this.reportMeta = reportMeta;
    }

    /**
     * Getter for attribute configFile
     *
     * @return current value of field configFile
     */
    public String getConfigFile() {
        return configFile;
    }

    /**
     * Setter for field configFile
     *
     * @param configFile new value
     */
    public void setConfigFile(String configFile) {
        this.configFile = configFile;
    }

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
    public DBVisualizer(CommandLine commandline) {
        try {
            assignCommandLineOptionsAndConfigurationValues(commandline);
        } catch (MissingMandatoryException missingMandatoryException) {
            LOGGER.error("Mandatory option was not set.", missingMandatoryException);
            System.exit(1);
        }
    }

    public void execute() throws ConnectionException, SQLException, IOException {
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
        } else if (outputFormat.equals(FORMAT.PNG) || outputFormat.equals(FORMAT.SVG) || outputFormat.equals(FORMAT.PDF)) {
            FileFormatOption fileFormatOption = null;
            switch (outputFormat) {
                case PDF:
                    LOGGER.info("Format for diagram: PDF");
                    fileFormatOption = new FileFormatOption(FileFormat.PDF);
                    break;
                case PNG:
                    LOGGER.info("Format for diagram: PNG");
                    fileFormatOption = new FileFormatOption(FileFormat.PNG);
                    break;
                case SVG:
                    LOGGER.info("Format for diagram: SVG");
                    fileFormatOption = new FileFormatOption(FileFormat.SVG);
                    break;
                default:
                    LOGGER.warn("Unsupported format");
                    break;
            }
            if (fileFormatOption != null) {
                String plantString = visualizer.getPlantRepresentation();
                SourceStringReader sourceStringReader = new SourceStringReader(plantString);
                OutputStream outStream = new FileOutputStream(outputFileName);
                String description = sourceStringReader.outputImage(outStream, fileFormatOption).getDescription();
                LOGGER.info("Description of output image: {}", description);
            }
        }
    }

    private void assignCommandLineOptionsAndConfigurationValues(CommandLine commandLine) throws MissingMandatoryException {
        List<Option> optionList = List.of(commandLine.getOptions());

        // Check for a configuration file
        Properties properties = null;
        Option configFileOption = getOptionFromList(OPTS.OPT_CONFIG_FILE, optionList);
        if (configFileOption != null) {
            String configFilename = configFileOption.getValue();
            LOGGER.debug("Config file: {}", configFilename);
            if (StringUtils.isNotEmpty(configFilename)) {
                properties = readConfig(configFilename);
            }
        }

        for (OPTS option : OPTS.values()) {
            switch (option) {
                case OPT_USER:
                    databaseUser = getValueFromPropertiesOrCli(option, properties, optionList);
                    break;
                case OPT_FILTER:
                    String filterString = getValueFromPropertiesOrCli(option, properties, optionList);
                    if (StringUtils.isNotEmpty(filterString)) {
                        try {
                            filter = Pattern.compile(filterString);
                            LOGGER.info("Using filter pattern {}", filterString);
                        } catch (PatternSyntaxException pse) {
                            LOGGER.warn("Could not parse regular expression '{}' for filtering. Will not apply any filter!",
                                    filterString);
                        }
                    }
                    break;
                case OPT_CATALOG_NAME:
                    catalog = getValueFromPropertiesOrCli(option, properties, optionList);
                    break;
                case OPT_ENTITIES_ONLY:
                    entitiesOnly = getBooleanValueFromPropertiesOrCli(option, properties, optionList);
                    break;
                case OPT_FORMAT:
                    String format = getValueFromPropertiesOrCli(option, properties, optionList);
                    if (StringUtils.isNotEmpty(format)) {
                        try {
                            outputFormat = FORMAT.valueOf(format.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            LOGGER.error(String.format("Unknown format %s", format), e);
                            System.exit(1);
                        }
                    }
                    break;
                case OPT_PASSWORD:
                    databasePassword = getValueFromPropertiesOrCli(option, properties, optionList);
                    break;
                case OPT_OUTPUT_FILE:
                    outputFileName = getValueFromPropertiesOrCli(option, properties, optionList);
                    break;
                case OPT_REPORT_FILE:
                    reportFile = getValueFromPropertiesOrCli(option, properties, optionList);
                    break;
                case OPT_REPORT_META:
                    reportMeta = getBooleanValueFromPropertiesOrCli(option, properties, optionList);
                    break;
                case OPT_DIALECT:
                    String dbDialectString = getValueFromPropertiesOrCli(option, properties, optionList);
                    if (StringUtils.isNotEmpty(dbDialectString)) {
                        try {
                            dbDialect = DB_DIALECT.valueOf(dbDialectString.toUpperCase());
                            LOGGER.info("Using {} as DB dialect.", dbDialect.getValue());
                        } catch (IllegalArgumentException | NullPointerException e) {
                            LOGGER.error(String.format("Could not parse '%s' as DB dialect.", dbDialectString), e);
                            System.exit(1);
                        }
                    }
                    break;
                case OPT_JDBC_DRV:
                    jdbcDriver = getValueFromPropertiesOrCli(option, properties, optionList);
                    break;
                case OPT_JDBC_DRV_PATH:
                    jdbcDriverPath = getValueFromPropertiesOrCli(option, properties, optionList);
                    break;
                case OPT_JDBC_URL:
                    jdbcUrl = getValueFromPropertiesOrCli(option, properties, optionList);
                    break;
                case OPT_REPORT_FORMAT:
                    String reportFormatString = getValueFromPropertiesOrCli(option, properties, optionList);
                    if (StringUtils.isNotEmpty(reportFormatString)) {
                        try {
                            reportFormat = REPORT_FORMAT.valueOf(reportFormatString.toUpperCase());
                        } catch (IllegalArgumentException illegalArgumentException) {
                            LOGGER.error(String.format("Unsupported Format '%s' for reports.", reportFormatString),
                                    illegalArgumentException);
                            System.exit(1);
                        }
                    }
                    break;
                case OPT_ENABLE_LR:
                    lROption = getBooleanValueFromPropertiesOrCli(option, properties, optionList);
                    break;
                case OPT_SCHEMA_NAME:
                    schema = getValueFromPropertiesOrCli(option, properties, optionList);
                    break;
            }
        }
    }

    private Option getOptionFromList(OPTS opt, List<Option> optionList) {
        return optionList.stream().filter(o -> o.getLongOpt().equals(opt.getOption().getLongOpt())).findFirst()
                .orElse(null);
    }

    /**
     * CLI has priority over properties file
     *
     * @param opt        OPTS enum value
     * @param properties Properties object
     * @param optList    list of Option objects as parsed by the command line parser
     * @return value from properties file or command line. Values from command line have priority
     */
    private String getValueFromPropertiesOrCli(OPTS opt, Properties properties, List<Option> optList) {
        Option optionFromCli = getOptionFromList(opt, optList);
        boolean mandatory = opt.isMandatory();
        String value = null;
        if (optionFromCli != null) {
            value = optionFromCli.getValue();
        }
        if (properties != null && properties.containsKey(opt.getPropertyKey()) && StringUtils.isEmpty(value)) {
            value = properties.getProperty(opt.getPropertyKey());
        }
        if (mandatory && value == null) {
            throw new MissingMandatoryException(String.format(
                    "Option %s (property %s) is required but neither set in CLI nor in properties",
                    opt.getOption().getLongOpt(), opt.getPropertyKey()));
        }
        return value;
    }

    private boolean getBooleanValueFromPropertiesOrCli(OPTS opt, Properties properties, List<Option> optList) {
        Option option = getOptionFromList(opt, optList);
        if (option != null) {
            // option set at CLI level
            return true;
        } else if (properties != null) {
            String valueFromProperties = properties.getProperty(opt.getPropertyKey());
            if (StringUtils.isNotEmpty(valueFromProperties)) {
                return valueFromProperties.equalsIgnoreCase("true");
            }
        }
        return false;
    }

    private Properties readConfig(String configFile) {
        Properties properties = new Properties();
        if (StringUtils.isNotEmpty(configFile)) {
            File f = new File(configFile);
            try {
                FileInputStream fis = new FileInputStream(f);
                properties.load(fis);
                LOGGER.debug("Read properties from {}", configFile);
                fis.close();
            } catch (IOException ioException) {
                LOGGER.error("I/O Exception caught", ioException);
                System.exit(1);
            }
        }
        return properties;
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
        DBVisualizer dbVisualizer = new DBVisualizer(commandLine);
        dbVisualizer.execute();
    }

}
