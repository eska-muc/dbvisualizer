package com.skuehnel.dbvisualizer.util;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Options for DBVisualizer and TestDBgenerator
 * 
 * @author Stefan Kuehnel
 *
 */
public enum OPTS {

	OPT_DIALECT("d", "dialect", false, true,
			"DB dialect. Possible values are PostgreSQL, MySQL, SQLite (not implemented yet)"), 
	OPT_JDBC_URL(
			"url", "jdbc-url", false, true, "JDBC URL."), 
	OPT_JDBC_DRV(
			"driver", "jdbc-driver", false, true,
			"Class name of the JDBC driver. Driver must be in CLASSPATH."), 
	OPT_ENABLE_LR(
			"l", "enable-lr", false, false,
			"Visualizer only: ranking=LR; graph layout from left to right."), 
	OPT_USER(
			"u", "user", false, true, "User name for database connection"), 
	OPT_PASSWORD(
			"p", "password", false, true, "Password for database connection."), 
	OPT_NUM_TABLES(
			"t", "tables", true, true,
			"TestDBGenerator only: number of tables."), 
	OPT_SCHEMA_NAME("s",
			"schema", false, true,
			"Name of the schema to retrvieve tables from. Default: all schemas."),
	OPT_CATALOG_NAME("c",
					"catalog", false, true,
					"Name of the catalog to retrvieve tables from. Default: null."),
	OPT_OUTPUT_FILE(
			"o", "output-file", true, true, "Name of the output file.");

	private String shortOpt;
	private String longOpt;
	private boolean mandatory;
	private boolean hasArg;
	private String description;

	private OPTS(String shortOpt, String longOpt, boolean mandatory,
			boolean hasArg, String description) {
		this.shortOpt = shortOpt;
		this.longOpt = longOpt;
		this.hasArg = hasArg;
		this.description = description;
	}

	/**
	 * Get an {@link org.apache.commons.cli.Option} object for an OPTS
	 * enumeration value.
	 * 
	 * @return an {@link org.apache.commons.cli.Option} object
	 */
	public Option getOption() {
		Option option = new Option(shortOpt, longOpt, hasArg, description);
		option.setRequired(mandatory);
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
	 * Generate a usage message
	 * 
	 * @param application
	 *            name of the application
	 * @param description
	 *            description of the application
	 */
	public static void printHelp(String application, String description) {
		HelpFormatter helpFormatter = new HelpFormatter();
		helpFormatter.printHelp(application, description, getOptions(), "",
				true);
	}
}
