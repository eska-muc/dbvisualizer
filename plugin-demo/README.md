# Demo for Maven Plugin

This project demonstrates how to use the maven plugin with an in-memory h2 database.
The database does just contain three tables:

* EMPLOYEE
* DEPARMENT
* EMPLOYEE_DEPARTMENT

Please remove the comments in the pom.xml to run the demo locally.
Currently the H2 JDBC driver is affected by CVE-2022-45868,
so the dependency is commented out.