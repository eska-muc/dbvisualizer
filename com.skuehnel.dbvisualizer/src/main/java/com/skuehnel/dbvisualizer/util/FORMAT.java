package com.skuehnel.dbvisualizer.util;

public enum FORMAT {
	DOT("DOT",".dot","The Language of GraphViz; see http://www.graphviz.org"),
	PNG("PNG",".png","Portable Network Graphics; see http://libpng.org/pub/png"),
	SVG("SVG",".svg","Scalable Vector Graphics; see https://www.w3.org/TR/2003/REC-SVG11-20030114/"),
	PDF("PDF",".pdf","Portable Document Format; see https://en.wikipedia.org/wiki/Portable_Document_Format");
	
	private String name;
	private String extension;
	private String description;
	
	private FORMAT(String name,String extension,String description) {
		this.name = name;
		this.extension = extension;
		this.description = description;
	}
	
	/**
	 * Get description of format
	 * @return
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Get the name of this format
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the filename extension for this format
	 * @return
	 */
	public String getExtension() {
		return extension;
	}
}
