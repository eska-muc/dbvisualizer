package com.skuehnel.dbvisualizer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * Project DBVisualizer
 * Writes a String into a file
 * @author Stefan Kuehnel
 *
 */
public class OutputWriter {

	private static final Logger LOGGER = LoggerFactory.getLogger(OutputWriter.class);
	
	String fileName;
	String content;
	
	/**
	 * Constructor
	 */
	public OutputWriter(String fileName,String content) {
		this.fileName = fileName;
		this.content = content;		
	}
	
	/**
	 * Write content to file
	 * @throws IOException
	 */
	public void write() throws IOException {
		if (fileName != null) {
			File f = new File(fileName);
			BufferedWriter writer = new BufferedWriter(new FileWriter(f));
			writer.write(content);
			writer.flush();
			writer.close();			
		} else {
			LOGGER.error("No FileName. Will not write anything.");
		}
	}
	

}
