package com.skuehnel.dbvisualizer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

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
import com.skuehnel.dbvisualizer.util.OPTS;
import com.skuehnel.dbvisualizer.visualize.Visualizer;
/**
 * Project DBVisualizer
 * 
 * Main class with command line interface
 * 
 * @author Stefan Kuehnel
 *
 */
public class DBVisualizer {

	private static final Logger LOGGER = LoggerFactory.getLogger(DBVisualizer.class);
	
	private String outputFileName;
	private String jdbcDriver;
	private String jdbcUrl;
	private String databaseUser;
	private String databasePassword;
	private boolean lROption = false;
	
	/**
	 * Default constructor
	 */
	public DBVisualizer() {
	}

	/**
	 * Constructor
	 * @param commandline the commandline 
	 * @throws ConnectionException
	 * @throws SQLException
	 */
	public DBVisualizer(CommandLine commandline) throws ConnectionException, SQLException, IOException {
		if (!assignCommandLineOptions(commandline)) {
			LOGGER.error("Could not assign all command line options. Maybe a mandatory option was not set.");
			System.err.println("One or more of the mandatory options (outputFileName, JDBC Driver class, JDBC URL) is missing.");
			System.exit(1);
		}
		LOGGER.debug("Initializing connection to DB with driver '{}', url '{}' and user '{}'.",jdbcDriver,jdbcUrl,databaseUser);
		JDBCConnection jdbcConnection = new JDBCConnection(jdbcDriver, jdbcUrl, databaseUser, databasePassword);		
		ERModelRetriever retrievER = new ERModelRetriever(jdbcConnection.getConnection());
		List<Table> model = retrievER.getModel();
		Visualizer visualizer = new Visualizer(model);
		OutputWriter writer = new OutputWriter(outputFileName,visualizer.getDotRepresentation());
		writer.write();
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
		}
		return mandatoriesAssigned==0;
	}
	
	/**
	 * Main method
	 * @param args
	 * @throws ConnectionException
	 * @throws SQLException
	 * @throws IOException
	 */
	public static void main(String args[]) throws ConnectionException, SQLException, IOException  {
		CommandLineParser parser = new DefaultParser();
		CommandLine commandLine = null;
		try {
			commandLine = parser.parse(OPTS.getOptions(), args);
		} catch (ParseException p) {
			System.err.println ("Could not parse command line. "+p.getMessage());
			System.exit(1);
		}
		if (commandLine.getOptions().length == 0) {
			OPTS.printHelp(
					"DBVisualizer",
					"Gets all tables from given database connection and generates a .dot file for an ER-Diagram.");

		} else {
			DBVisualizer dbVisualizer = new DBVisualizer(commandLine);			
		}
	}
	
}
