package com.skuehnel.dbvisualizer.util;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Options for DBVisualizer and TestDBgenerator
 *
 * @author Stefan Kuehnel
 */
public enum OPTS {

    OPT_DIALECT("d", "dialect", false, true,
            "DB dialect. Possible values are PostgreSQL, MySQL, Oracle", "database.dialect"),
    OPT_JDBC_URL(
            "url", "jdbc-url", true, true, "JDBC URL (mandatory).",
            "database.jdbc.url"),
    OPT_JDBC_DRV(
            "driver", "jdbc-driver", true, true,
            "Class name of the JDBC driver (mandatory).", "database.jdbc.driver.class"),
    OPT_JDBC_DRV_PATH(
            "driverpath", "jdbc-driver-path", false, true,
            "Path to the driver classes. If this option is not specified, the driver is searched in CLASSPATH.",
            "database.jdbc.driver.path"),
    OPT_ENABLE_LR(
            "l", "enable-lr", false, false,
            "Use GraphViz option ranking=LR; Graph layout from left to right.", "output.graphviz.lroption"),
    OPT_ENTITIES_ONLY(
            "e", "entities-only", false, false,
            "Show only entities and relations in output (no attributes/columns).",
            "output.diagram.entitiesonly"),
    OPT_FORMAT("a", "format", false, true, "Format: DOT (default), PLANT, PNG, SVG, PDF",
            "output.diagram.format"),
    OPT_USER(
            "u", "user", false, true, "User name for database connection",
            "database.user"),
    OPT_PASSWORD(
            "p", "password", false, true, "Password for database connection.",
            "database.password"),
    OPT_NUM_TABLES(
            "t", "tables", false, true,
            "TestDBGenerator only: number of tables.", "generator.numberoftables"),
    OPT_SCHEMA_NAME("s",
            "schema", false, true,
            "Name of the schema to retrieve tables from. Default: all schemas.", "database.schema"),
    OPT_CATALOG_NAME("c",
            "catalog", false, true,
            "Name of the catalog to retrieve tables from. Default: null.", "database.catalog"),
    OPT_CONFIG_FILE("C",
            "config", false, true,
            "Name of a configuration file.", null),
    OPT_FILTER("f", "filter", false, true,
            "Regular expression (Java flavor) which is applied on table names", "database.filter"),
    OPT_REPORT_FORMAT("F", "report-format", false, true,
            "Format of the Report file. Supported formats are: html, pdf and markdown", "output.report.format"),
    OPT_REPORT_META("m", "report-metainformation", false, false,
            "Include some meta information (e.g. report generation date) in the generated report.",
            "output.report.metadata"),
    OPT_OUTPUT_FILE(
            "o", "output-file", true, true, "Name of the output file (mandatory).",
            "output.diagram.filename"),
    OPT_REPORT_FILE(
            "r", "report-file", true, true,
            "Name of the report file. If omitted, no report will be generated.", "output.report.filename");

    private final String shortOpt;
    private final String longOpt;
    private final boolean mandatory;
    private final boolean hasArg;
    private final String description;
    private final String property;

    OPTS(String shortOpt, String longOpt, boolean mandatory,
         boolean hasArg, String description, String property) {
        this.shortOpt = shortOpt;
        this.longOpt = longOpt;
        this.hasArg = hasArg;
        this.mandatory = mandatory;
        this.description = description;
        this.property = property;
    }

	/**
	 * Get an {@link org.apache.commons.cli.Option} object for an OPTS
	 * enumeration value.
	 * 
	 * @return an {@link org.apache.commons.cli.Option} object
	 */
	public Option getOption() {
		Option option = new Option(shortOpt, longOpt, hasArg, description);
		return option;
	}

	/**
	 * Get an {@link org.apache.commons.cli.Options} object for all of the
	 * options defined in this enumeration.
	 *
     * @return an {@link org.apache.commons.cli.Options} object
     */
    public static Options getOptions() {
        Options options = new Options();
        for (OPTS opt : OPTS.values()) {
            options.addOption(opt.getOption());
        }
        return options;
    }

    /**
     * Getter for property key
     *
     * @return value for property key
     */
    public String getPropertyKey() {
        return this.property;
    }

    /**
     * Getter for property "mandatory"
     *
     * @return true, if this option is mandatory
     */
    public boolean isMandatory() {
        return this.mandatory;
    }

    /**
     * Generate a usage message
     *
     * @param application name of the application
     * @param description description of the application
     */
    public static void printHelp(String application, String description) {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp(application, description, getOptions(), "",
				true);
	}
}
