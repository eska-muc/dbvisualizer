# Maven Plugin

This module contains a maven plugin for DBVisualizer.

If you want to try out an example, please refer to "plugin-demo"

## Usage:

Example for H2:

     <plugin>
       <groupId>com.skuehnel</groupId>
       <artifactId>dbvisualizer-maven-plugin</artifactId>
       <!-- from your local .m2 repository -->
       <version>1.0.0-SNAPSHOT</version>
       <configuration>
         <db_user>test</db_user>
         <db_password>test</db_password>
         <driver>org.h2.Driver</driver>
         <jdbcurl>jdbc:h2:mem:test;MODE=PostgreSQL;INIT=RUNSCRIPT FROM '${baseDirForH2}/src/main/resources/init.sql';</jdbcurl>
         <diagramFile>test-diagram.png</diagramFile>
         <diagramFormat>PNG</diagramFormat>
         <reportFile>test-report.html</reportFile>
         <reportFormat>HTML</reportFormat>
         <catalog>test</catalog>
         <!-- in H2 the default schema is PUBLIC -->
         <schema>PUBLIC</schema>
         <dialect>H2</dialect>
       </configuration>
       <executions>
         <execution>
           <phase>compile</phase>
           <goals>
             <goal>dbvisualizer</goal>
           </goals>
          </execution>
       </executions>
     </plugin>

Example for PostgreSQL:

    <configuration>
      <db_user>_replace_with_your_user_</db_user>
      <db_password>_replace_with_your_password_</db_password>
      <driver>org.postgresql.Driver</driver>
      <jdbcurl>jdbc:postgresql://[::1]:5432/test</jdbcurl>
      <diagramFile>test-diagram.png</diagramFile>
      <diagramFormat>PNG</diagramFormat>
      <reportFile>test-report.html</reportFile>
      <reportFormat>HTML</reportFormat>
      <schema>test</schema>
    <configuration>  