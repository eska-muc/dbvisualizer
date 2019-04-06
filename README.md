![GitHub](https://img.shields.io/github/license/eska-muc/dbvisualizer.svg)
# dbvisualizer
A tool which creates a GraphViz .dot file for a DB schema via JDBC

## Compilation

Just do

    mvn clean install
    
This creates a "fat" .jar with all dependencies using the maven assembly plugin.

## Start

Start DBVisualizer like this:

    java -jar target/dbvisualizer-1-SNAPSHOT-jar-with-dependencies.jar

All command line options are listed, like this:

    usage: DBVisualizer [-c <arg>] [-d <arg>] [-driver <arg>] [-f <arg>] [-l]
           [-o <arg>] [-p <arg>] [-s <arg>] [-t <arg>] [-u <arg>] [-url <arg>]
    Gets all (matching) tables from given database connection and generates a
    .dot file for an ER-Diagram.
     -c,--catalog <arg>            Name of the catalog to retrieve tables
                                   from. Default: null.
     -d,--dialect <arg>            DB dialect. Possible values are PostgreSQL,
                                   MySQL, Oracle
     -driver,--jdbc-driver <arg>   Class name of the JDBC driver. Driver must
                                   be in CLASSPATH.
     -f,--filter <arg>             Regular expression (Java flavor) which is
                                   applied on table names
     -l,--enable-lr                Visualizer only: ranking=LR; graph layout
                                   from left to right.
     -o,--output-file <arg>        Name of the output file.
     -p,--password <arg>           Password for database connection.
     -s,--schema <arg>             Name of the schema to retrvieve tables
                                   from. Default: all schemas.
     -t,--tables <arg>             TestDBGenerator only: number of tables.
     -u,--user <arg>               User name for database connection
     -url,--jdbc-url <arg>         JDBC URL.
     
Tip: For very large schemas it might be useful to apply an filter and generate #
several smaller diagrams instead of one large.

## Supported Databases

Tested with PostgreSQL, MySQL and OracleXE 18c.

## GraphViz

The output file is in the .dot-format of [GraphViz](http://www.graphviz.org). To generate a .pdf-file just type
   
    dot -Tpdf &lt;outputfile of DBVisualizer&gt; &gt; diagram.pdf

You may also try

    neato -Goverlap=false -Gmodel=subset -Tpdf &lt;outputfile of DBVisualizer&gt; &gt; diagram.pdf

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

List of some features, which might be added in future:

* run as REST service and provide a web UI, probably using a JS framework like
 [visjs.org](http://visjs.org/) or [D3js](https://d3js.org/) for visualization 
* generate text reports of the database structure (for documentation purposes) 