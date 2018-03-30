# dbvisualizer
A tool which creates a GraphViz .dot file for a DB schema via JDBC

## Compilation

Just do an mvn clean install

## Start

java -jar target/dbvisualizer-1-SNAPSHOT-jar-with-dependencies.jar

All command line options are listed, like this:

    usage: DBVisualizer [-c <arg>] [-d <arg>] [-driver <arg>] [-f <arg>] [-l]
       [-o <arg>] [-p <arg>] [-s <arg>] [-t <arg>] [-u <arg>] [-url <arg>]
       
    Gets all tables from given database connection and generates a .dot file
    for an ER-Diagram.
     -c,--catalog <arg>            Name of the catalog to retrieve tables
                                   from. Default: null.
     -d,--dialect <arg>            DB dialect. Possible values are PostgreSQL,
                                   MySQL, SQLite (not implemented yet)
     -driver,--jdbc-driver <arg>   Class name of the JDBC driver. Driver must
                                   be in CLASSPATH.
     -f,--format <arg>             Output format (NOT SUPPORTED YET!). Possible
                                   values: "png", "pdf", "svg" and "dot"
                                   (default). When "pdf", "png" or "svg" are
                                   selected, the rendering will be done
                                   internally using graphviz-java (may have
                                   some performance impact).
     -l,--enable-lr                Visualizer only: ranking=LR; graph layout
                                   from left to right.
     -o,--output-file <arg>        Name of the output file.
     -p,--password <arg>           Password for database connection.
     -s,--schema <arg>             Name of the schema to retrvieve tables
                                   from. Default: all schemas.
     -t,--tables <arg>             TestDBGenerator only: number of tables.
     -u,--user <arg>               User name for database connection
     -url,--jdbc-url <arg>         JDBC URL.
     
## Supported Databases

Tested mainly with PostgreSQL and MySQL.

## GraphViz

The output file is in the .dot-format of [GraphViz](http://www.graphviz.org). To generate a .pdf-file just type
   
   dot -Tpdf &lt;outputfile of DBVisualizer&gt; &gt; diagram.pdf

You may also try

   neato -Goverlap=false -Gmodel=subset -Tpdf &lt;outputfile of DBVisualizer&gt; &gt; diagram.pdf
