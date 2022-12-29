package com.skuehnel.dbvisualizer.mavenplugin;

import com.skuehnel.dbvisualizer.DBVisualizer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.Objects;
import java.util.Properties;

@Mojo(name = "dbvisualizer", defaultPhase = LifecyclePhase.COMPILE, requiresDependencyResolution = ResolutionScope.COMPILE)
public class DBVisualizerMavenPluginMojo extends AbstractMojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBVisualizerMavenPluginMojo.class);

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    MavenProject mavenProject;

    @Parameter(property = "user")
    String db_user;

    @Parameter(property = "password")
    String db_password;

    @Parameter(property = "driver")
    String driver;

    @Parameter(property = "jdbc-url")
    String jdbcurl;

    @Parameter(property = "report-file")
    String reportFile;

    @Parameter(property = "report-format")
    String reportFormat;

    @Parameter(property = "diagram-format")
    String diagramFormat;

    @Parameter(property = "diagram-file")
    String diagramFile;

    @Parameter(property = "schema")
    String schema;

    @Parameter(property = "catalog")
    String catalog;

    @Parameter(property = "dialect")
    String dialect;

    @Parameter(property = "reportMetadata")
    boolean reportMetadata = false;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        DBVisualizer dbVisualizer = DBVisualizer.DBVisualizerBuilder.builder()
                .withDatabaseUser(db_user)
                .withDatabasePassword(db_password)
                .withJdbcUrl(jdbcurl)
                .withJdbcDriver(driver)
                .withOutputFileName(diagramFile)
                .withOutputFormat(diagramFormat)
                .withReportFile(reportFile)
                .withReportFormat(reportFormat)
                .withReportMetadata(reportMetadata)
                .withSchema(schema)
                .withCatalog(catalog)
                .withDBDialect(dialect)
                .build();
        try {
            dbVisualizer.execute();
        } catch (Exception e) {
            LOGGER.error("Could not execute DBVisualizer Maven Plugin", e);
        }
    }
}
