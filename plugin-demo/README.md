# Demos for Maven Plugin

## H2 demo

The H2 in memory database is created when the JDBC connection is established as an in-memory database.
The database structure (and some data) is defined in src/main/resources/init.sql

The database does just contain three tables:

* EMPLOYEE
* DEPARTMENT
* EMPLOYEE_DEPARTMENT

Please remove the comments in the pom.xml to run the demo locally.
Currently the H2 JDBC driver is affected by CVE-2022-45868, so the dependency is also commented out.
To execute the demo, you also have to remove the comments around the dependency, as well.

## PostgreSQL Demo

The pom.xml also contains some settings for a typical PostgreSQL database.
To try this out run the script postgres_demo.sql to create the demo schema:

    pgsql -U <user> -d <database> --password -f  src/main/resources/postgresql_demo.sql

Then remove the comments in the pom.xml and change username, password and other seetings according to your local setup.

## MySQL Demo

The pom.xml also contains some settings for a typical MySQL database where a database (schema) "mysql-test" and one
or more tables which names starts with "employee" exists. Please uncomment and adapt this section according to your
set-up.

## Execute maven

When you have made the changes in the pom.xml just run

    mvn install

This will exceutue DBVisualizer and create the files as configured.