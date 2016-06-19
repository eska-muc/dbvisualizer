# dbvisualizer
A tool which creates a GraphViz .dot file for a DB schema via JDBC

## Compilation

Just do an mvn clean install

## Start

java -jar target/dbvisualizer-1-SNAPSHOT-jar-with-dependencies.jar

All command line options are listed, like this:

    usage: DBVisualizer [-d <arg>] [-driver <arg>] [-l] [-o <arg>] [-p <arg>]
           [-t <arg>] [-u <arg>] [-url <arg>]
    Gets all tables from given database connection and generates a .dot file
    for an ER-Diagram.
     -d,--dialect <arg>            DB dialect. Possible values are PostgreSQL,
                                   MySQL, SQLite
     -driver,--jdbc-driver <arg>   Class name of the JDBC driver. Driver must
                                   be in CLASSPATH.
     -l,--enable-lr                Visualizer only: ranking=LR; graph layout
                                   from left to right.
     -o,--output-file <arg>        Name of the output file.
     -p,--password <arg>           Password for database connection.
     -t,--tables <arg>             TestDBGenerator only: number of tables.
     -u,--user <arg>               User name for database connection
     -url,--jdbc-url <arg>         JDBC URL.

## GraphViz

The output file is in the .dot-format of [GraphViz](http://www.graphviz.org). To generate a .pdf-file just type
   
   dot -Tpdf &lt;outputfile of DBVisualizer&gt; &gt; diagram.pdf

You may also try

   neato -Goverlap=false -Gmodel=subset -Tpdf &lt;outputfile of DBVisualizer&gt; &gt; diagram.pdf
