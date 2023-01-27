package com.skuehnel.dbvisualizer.report;

import com.skuehnel.dbvisualizer.domain.Column;
import com.skuehnel.dbvisualizer.domain.Model;
import com.skuehnel.dbvisualizer.domain.Table;
import com.skuehnel.dbvisualizer.util.REPORT_FORMAT;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ReportGeneratorTest {

    private static Model model;

    @BeforeAll
    public static void initModel() {
        model = new Model();
        model.setDatabaseName("TestDatabase");
        model.setSchemaName("TestSchema");

        Table employee_table = new Table("EMP");
        Table department_table = new Table("DEPT");

        List<Column> emp_columns = new ArrayList<>();
        List<Column> dept_columns = new ArrayList<>();

        Column dept_pk = new Column("PK_DEPT_ID", "INT");
        dept_pk.setPrimaryKey(true);
        dept_pk.setNotNull(true);
        dept_columns.add(dept_pk);

        Column dept_name = new Column("NAME", "VARCHAR(80)");
        dept_name.setNotNull(true);
        dept_columns.add(dept_name);

        Column emp_id = new Column("PK_EMP_ID", "INT");
        emp_id.setPrimaryKey(true);
        emp_id.setNotNull(true);
        emp_columns.add(emp_id);

        Column emp_lastname = new Column("LNAME", "VARCHAR(80)");
        emp_lastname.setNotNull(true);
        emp_columns.add(emp_lastname);

        Column emp_fk_dept = new Column("FK_DEPT_ID", "INT");
        emp_fk_dept.setForeignKeyColumn(dept_pk);
        emp_fk_dept.setForeignKeyTable(department_table);
        emp_columns.add(emp_fk_dept);

        employee_table.setColumns(emp_columns);
        department_table.setColumns(dept_columns);
        List<Table> tables = new ArrayList<>();
        tables.add(employee_table);
        tables.add(department_table);
        model.setTableList(tables);
    }


    @Test
    public void markDownGeneratorTest() throws IOException {
        ReportGenerator reportGenerator = ReportGeneratorFactory.createReportGeneratorInstance(REPORT_FORMAT.MARKDOWN.name());
        File f = File.createTempFile("test", ".md");
        f.deleteOnExit();
        reportGenerator.generateReport(f.getAbsolutePath(), model, ReportGenerator.REPORT_OPT.WITH_META_INFORMATION);
        Assertions.assertTrue(f.exists());
        Assertions.assertTrue(f.isFile());
        Assertions.assertTrue(f.length() > 0);
        String content = Files.readString(f.toPath());
        Assertions.assertNotNull(content);
        Assertions.assertTrue(content.contains("## Table EMP"));
        Assertions.assertTrue(content.contains("## Table DEPT"));
        Assertions.assertTrue(content.contains("PK_EMP_ID"));
        Assertions.assertTrue(content.contains("PK_DEPT_ID"));
        Assertions.assertTrue(content.contains("LNAME"));
        Assertions.assertTrue(content.contains("FK_DEPT_ID"));
    }

    @Test
    public void htmlGeneratorTest() throws IOException {
        ReportGenerator reportGenerator = ReportGeneratorFactory.createReportGeneratorInstance(REPORT_FORMAT.HTML.name());
        File f = File.createTempFile("test", ".html");
        f.deleteOnExit();
        reportGenerator.generateReport(f.getAbsolutePath(), model, ReportGenerator.REPORT_OPT.WITH_META_INFORMATION);
        Assertions.assertTrue(f.exists());
        Assertions.assertTrue(f.isFile());
        Assertions.assertTrue(f.length() > 0);
        String content = Files.readString(f.toPath());
        Assertions.assertNotNull(content);
        Assertions.assertTrue(content.contains(">Table EMP<"));
        Assertions.assertTrue(content.contains(">Table DEPT<"));
        Assertions.assertTrue(content.contains("PK_EMP_ID"));
        Assertions.assertTrue(content.contains("PK_DEPT_ID"));
        Assertions.assertTrue(content.contains("LNAME"));
        Assertions.assertTrue(content.contains("FK_DEPT_ID"));
    }


    @Test
    public void pdfGeneratorTest() throws IOException {
        ReportGenerator reportGenerator = ReportGeneratorFactory.createReportGeneratorInstance(REPORT_FORMAT.PDF.name());
        File f = File.createTempFile("test", ".pdf");
        f.deleteOnExit();
        reportGenerator.generateReport(f.getAbsolutePath(), model);
        Assertions.assertTrue(f.exists());
        Assertions.assertTrue(f.isFile());
        Assertions.assertTrue(f.length() > 0);
    }


}
