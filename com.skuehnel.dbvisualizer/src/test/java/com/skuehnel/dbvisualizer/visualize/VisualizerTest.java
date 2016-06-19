package com.skuehnel.dbvisualizer.visualize;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.skuehnel.dbvisualizer.domain.Column;
import com.skuehnel.dbvisualizer.domain.Table;
import com.skuehnel.dbvisualizer.util.InvalidParamException;
import com.skuehnel.dbvisualizer.util.TestDBGenerator;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

public class VisualizerTest {

	private static Table employees = new Table("EMPLOYEES");
	private static Table departments = new Table("DEPARTMENTS");
	private static TestDBGenerator testDBGenerator = new TestDBGenerator();
	
	@BeforeClass
	public static void setUpTestData() {
		
		Column dept_id   = new Column("ID","NUMBER",true,false,false);
		Column dept_name = new Column("NAME","VARCHAR",false,false,true);
		
		List<Column> deptColumns = new ArrayList<>();
		deptColumns.add(dept_id);
		deptColumns.add(dept_name);
		
		departments.setColumns(deptColumns);
		
		
		Column emp_id      = new Column("ID",   "NUMBER"  ,true ,false,false);
		Column emp_fname   = new Column("FNAME","VARCHAR" ,false,false,true);
		Column emp_lname   = new Column("LNAME","VARCHAR" ,false,false,true);
		Column emp_dept_id = new Column("DEPT_ID","NUMBER" ,false,false,true);
		
		emp_dept_id.setForeignKeyTable(departments);
		emp_dept_id.setForeignKeyColumn(dept_id);
		
		List<Column> empColumns = new ArrayList<>();
		empColumns.add(emp_id);
		empColumns.add(emp_fname);
		empColumns.add(emp_lname);
		empColumns.add(emp_dept_id);
		
		employees.setColumns(empColumns);
	}

	@Test
	public void visualizationEmployeeDepartmentTest() {
		Visualizer v = new Visualizer(new ArrayList<Table>());
		v.addTable(departments);
		v.addTable(employees);		
		String dot = v.getDotRepresentation();		
		assertNotNull(dot);
		System.out.println(dot);
	}
	
	@Test
	public void randomTest20Tables() throws InvalidParamException {
		List<Table> tables = testDBGenerator.generateRandomTables(20,10);
		Visualizer v = new Visualizer(tables);		
		String dot = v.getDotRepresentation();		
		assertNotNull(dot);
		System.out.println(dot);	
	}
	
	@Test
	public void randomTest100Tables() throws InvalidParamException {
		List<Table> tables = testDBGenerator.generateRandomTables(100,20);
		Visualizer v = new Visualizer(tables);		
		String dot = v.getDotRepresentation();		
		assertNotNull(dot);
		System.out.println(dot);	
	}
		 	
}
