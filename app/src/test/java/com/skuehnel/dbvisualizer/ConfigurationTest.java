package com.skuehnel.dbvisualizer;

import com.skuehnel.dbvisualizer.util.*;
import org.apache.commons.cli.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ConfigurationTest {

    @Test
    public void variableAssignmentFromProperties() throws ParseException {
        CommandLineParser parser = new DefaultParser();
        String[] args = {"-C", "src/test/resources/test.properties"};
        CommandLine commandLine = parser.parse(OPTS.getOptions(), args);
        DBVisualizer dbVisualizer = new DBVisualizer();
        dbVisualizer.assignCommandLineOptionsAndConfigurationValues(commandLine);
        // Assertions
        Assertions.assertEquals("test-catalog", dbVisualizer.getCatalog());
        Assertions.assertEquals("test-schema", dbVisualizer.getSchema());
        Assertions.assertEquals("test-password", dbVisualizer.getDatabasePassword());
        Assertions.assertEquals("test-user", dbVisualizer.getDatabaseUser());
        Assertions.assertEquals("test-driver", dbVisualizer.getJdbcDriver());
        Assertions.assertEquals("test-driver-path", dbVisualizer.getJdbcDriverPath());
        Assertions.assertEquals("test-url", dbVisualizer.getJdbcUrl());
        Assertions.assertEquals("test-report.md", dbVisualizer.getReportFile());
        Assertions.assertEquals("test-diagram.png", dbVisualizer.getOutputFileName());
        Assertions.assertEquals(false, dbVisualizer.isEntitiesOnly());
        Assertions.assertEquals(false, dbVisualizer.islROption());
        Assertions.assertEquals(true, dbVisualizer.isReportMeta());
        Assertions.assertTrue(dbVisualizer.getFilter().matcher("DB_PREFIX.SUBSYSTEM.TEST").matches());
        Assertions.assertFalse(dbVisualizer.getFilter().matcher("TEST.DB_PREFIX.SUBSYSTEM").matches());
        Assertions.assertEquals(DB_DIALECT.POSTGRESQL, dbVisualizer.getDbDialect());
        Assertions.assertEquals(FORMAT.PNG, dbVisualizer.getOutputFormat());
        Assertions.assertEquals(REPORT_FORMAT.MARKDOWN, dbVisualizer.getReportFormat());
    }

    @Test
    public void commandLineHasPriorityTest() throws ParseException {
        CommandLineParser parser = new DefaultParser();
        String[] args = {"-C", "src/test/resources/test.properties", "-p", "test-password-overwritten"};
        CommandLine commandLine = parser.parse(OPTS.getOptions(), args);
        DBVisualizer dbVisualizer = new DBVisualizer();
        dbVisualizer.assignCommandLineOptionsAndConfigurationValues(commandLine);
        // Assertions
        Assertions.assertEquals("test-password-overwritten", dbVisualizer.getDatabasePassword());

    }

    @Test
    void checkForMandatoryOptions() throws ParseException {
        CommandLineParser parser = new DefaultParser();
        String[] args = {"-u", "test-user", "-p", "test-password-overwritten"};
        CommandLine commandLine = parser.parse(OPTS.getOptions(), args);
        DBVisualizer dbVisualizer = new DBVisualizer();
        MissingMandatoryException thrown = Assertions.assertThrows(
                MissingMandatoryException.class,
                () -> dbVisualizer.assignCommandLineOptionsAndConfigurationValues(commandLine),
                "Expected exception for missing mandatory parameters");
    }

}
