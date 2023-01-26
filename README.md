![GitHub](https://img.shields.io/github/license/eska-muc/dbvisualizer.svg)
[![GitHub forks](https://img.shields.io/github/forks/eska-muc/dbvisualizer)](https://github.com/eska-muc/dbvisualizer/network)
[![GitHub Org's stars](https://img.shields.io/github/stars/eska-muc/dbvisualizer)](https://github.com/eska-muc/dbvisualizer/stargazers)
![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/eska-muc/dbvisualizer/maven.yml)

# DBVisualizer

A tool which creates ER-Diagrams as a PMG, PDF, SVG file or as .dot input file for [GraphViz](https://graphviz.org/)
or [PlantUML](https://plantuml.com/) and textual reports (in HTML, MARKDOWN or PDF format) about tables and columns from
a database using JDBC
metadata retrieval.

![Example (generated with PlantUML)](https://github.com/eska-muc/dbvisualizer/blob/master/example/postgresql_test.png)

### Latest news

#### 2023-01

* Configuration can be read from a .properties file (new option -C)

#### 2022-12

* Maven Plugin
* Direct integration of PlantUML (as dependency). Diagrams in .png, .pdf and .svg format can be generated directly now
  without the necessity to call PlantUML manually

### Examples

[Examples](./example/README.md)

## Compilation

Just do

    mvn clean install

This creates a "fat" .jar with all dependencies using the maven assembly plugin.

## Start

### Maven Plugin

For an example how to use DBVisualizer as a maven plugin, just switch to the directory "plugin-demo", edit the
configuration section
in the pom.xml and run "mvn install".

[Documentation](./maven-plugin/README.md)

### CLI

For convenience all required options (for the database connection and the output generation) can be configured using a
.properties file, like this:

    database.dialect=PostgreSQL
    database.jdbc.url=jdbc:postgresql://[::1]:5432/test
    database.jdbc.driver.class=org.postgresql.Driver
    database.jdbc.driver.path=c:/Users/_your_name_/.m2/repository/org/postgresql/postgresql/42.3.3/postgresql-42.3.3.jar
    output.graphviz.lroption=false
    output.diagram.entitiesonly=false
    output.diagram.format=PNG
    database.user=_replace_with_real_username_
    database.password=_replace_with_real_password_
    database.schema=test
    #not required for PostgreSQL
    #database.catalog=
    #database.filter=
    output.report.format=MARKDOWN
    output.report.metadata=true
    output.diagram.filename=postgres_test_with_properties.png
    output.report.filename=postgres_test_with_properties_report.md

To use such a configuration file, just call

    java -jar app/target/dbvisualizer-1.0.0-SNAPSHOT-jar-with-dependencies.jar -C <path to configuration.properties>

You can also specify some of the options in the configuration file and some at runtime. The options specified via
command line options will have priority over the values from the properties file.

For example if you specify -a PDF -o test_diagram.pdf when executing DBVisualizer you will get the diagram as .PDF
even if the value in the properties file for property "output.diagram.format" was set to PNG.

To get a list of all options call DBVisualizer like this:

    java -jar app/target/dbvisualizer-1.0.0-SNAPSHOT-jar-with-dependencies.jar

All command line options are listed:

    usage: DBVisualizer [-a <arg>] [-C <arg>] [-c <arg>] [-d <arg>] -driver <arg>
       [-driverpath <arg>] [-e] [-f <arg>] [-F <arg>] [-l] [-m] -o <arg>
       [-p <arg>] -r <arg> [-s <arg>] [-t <arg>] [-u <arg>] -url <arg>
    
    Gets all (matching) tables from given database connection and generates an
    outputfile in the specified format (default: .dot)
    -a,--format <arg>                      Format: DOT (default), PLANT, PNG,
                                           SVG, PDF
    -c,--catalog <arg>                     Name of the catalog to retrieve
                                           tables from. Default: null.
    -C,--config <arg>                      Name of a configuration file.
    -d,--dialect <arg>                     DB dialect. Possible values are
                                           PostgreSQL, MySQL, Oracle
    -driver,--jdbc-driver <arg>            Class name of the JDBC driver
                                           (mandatory).
    -driverpath,--jdbc-driver-path <arg>   Path to the driver classes. If
                                           this option is not specified, the
                                           driver is searched in CLASSPATH.
    -e,--entities-only                     Show only entities and relations
                                           in output (no attributes/columns).
    -f,--filter <arg>                      Regular expression (Java flavor)
                                           which is applied on table names
    -F,--report-format <arg>               Format of the Report file.
                                           Supported formats are: html, pdf
                                           and markdown
    -l,--enable-lr                         Use GraphViz option ranking=LR;
                                           Graph layout from left to right.
    -m,--report-metainformation            Include some meta information
                                           (e.g. report generation date) in
                                           the generated report.
    -o,--output-file <arg>                 Name of the output file 
                                           (mandatory).
    -p,--password <arg>                    Password for database connection.
    -r,--report-file <arg>                 Name of the report file. If
                                           omitted, no report will be
                                           generated.
    -s,--schema <arg>                      Name of the schema to retrieve
                                           tables from. Default: all schemas.
    -t,--tables <arg>                      TestDBGenerator only: number of
                                           tables.
    -u,--user <arg>                        User name for database connection
    -url,--jdbc-url <arg>                  JDBC URL (mandatory).

Example (assuming a PostgreSQL database "test" on localhost, port 5432):

    java -jar target/dbvisualizer-1-SNAPSHOT-jar-with-dependencies.jar -driverpath ./postgresql-42.3.1.jar -driver org.postgresql.Driver -a PNG -o postgresql_test_diagram.png -d PostgreSQL -s test -u test -p "test123" -url jdbc:postgresql://localhost:5432/test 

Tip: For very large schemas it might be useful to apply an filter and generate
several smaller diagrams instead of one large.

## Supported Databases

Tested with PostgreSQL,H2, MySQL and OracleXE 18c.

## PlantUML

[PlantUML](https://plantuml.com/ie-diagram) is used internally when output formats PNG, SVG or PDF for the diagram are
specified.
Since there is a dependency on PlantUML defined in the pom of DBVisualizer, no explicit installation of PlantUML is
required.
If you are interested in the generated input file for PlantUML, the format specification "PLANT" can be used with
(commandline option -a). In this case, a text input file for PlantUML is generated, which can be converted into one of
the target formats of PlantUML like this (name of the .jar file can be different, -tpdf stands for target format PDF):

    java -jar target/plantuml-1.2021.16-SNAPSHOT-jar-with-dependencies.jar -tpdf <path to output of dbvisualizer>

For a full reference of PlantUML Command Line refer to
the [documentation](https://plantuml.com/command-line#458de91d76a8569c)

## GraphViz

By default the output file is in the .dot-format of [GraphViz](http://www.graphviz.org). To generate a .pdf-file just
type

    dot -Tpdf -odiagram.pdf <outputfile of DBVisualizer> 

You may also try

    neato -Goverlap=false -Gmodel=subset -Tpdf -odiagram.pdf <outputfile of DBVisualizer> 

## Reports

Using the option -r with a filename, a tabular report on the tables and columns will be written in the specified file.
Following formats (option -F) are supported:
* HTML (default)
* MARKDOWN
* PDF

For PDF the library [easytable](https://github.com/vandeseer/easytable) is used.

## TestDBGenerator

If you do not have access to a complex schema, you can generate one using TestDBGenerator.
It creates either an SQL DDL (DDL - Data Definition Language) file or directly a .dot file.
I've used the DDL primarily for end to end testing of DBVisualizer.  The examples have been 
generated with TestDBGenerator as well.

Start TestDBGenerator like this:

    java -cp target/dbvisualizer-1-SNAPSHOT-jar-with-dependencies.jar com.skuehnel.dbvisualizer.util.TestDBGenerator

Command line options are:

    usage: TestDBGenerator -f <arg> -n <arg> -t <arg>
    Creates a DDL .sql file or a .dot file with some randomly generated
    tables.
     -f,--file <arg>   Name of the output file.
     -n,--num <arg>    Number of tables to be generated.
     -t,--type <arg>   Type of test file. Either sql or dot.

## Ideas for future enhancements

List of some features, which might be added in the future:

* Add more automated tests for different databases
* run as REST service and provide a web UI, probably using a JS framework like [visjs.org](http://visjs.org/)
  or [D3js](https://d3js.org/) for visualization 

