package com.skuehnel.dbvisualizer.util;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * Options for the TestDBGenerator
 */
public enum TEST_OPTS {
    OPT_FILENAME("f","file","Name of the output file."),
    OPT_TYPE("t","type","Type of test file. Either sql or dot."),
    OPT_NUM("n","num","Number of tables to be generated.");

    String shortName;
    String longName;
    String description;

    TEST_OPTS(String shortName, String longName, String description) {
        this.shortName = shortName;
        this.longName = longName;
        this.description = description;
    }

    Option getOption() {
        Option option = new Option(shortName, longName, true, description);
        option.setRequired(true);
        return option;
    }

    /**
     * Options for apache commons command line parser
     * @return options
     */
    public static Options getOptions() {
        Options options = new Options();
        for (TEST_OPTS opt : TEST_OPTS.values()) {
            options.addOption(opt.getOption());
        }
        return options;
    }

    public static void printHelp(String application, String description) {
        HelpFormatter helpFormatter = new HelpFormatter();
        helpFormatter.printHelp(application, description, getOptions(), "",
                true);
    }
}
