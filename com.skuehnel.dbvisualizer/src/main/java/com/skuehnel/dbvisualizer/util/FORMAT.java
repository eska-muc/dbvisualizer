package com.skuehnel.dbvisualizer.util;

public enum FORMAT {
	DOT("DOT",".dot","The Language of GraphViz; see http://www.graphviz.org",true),
    PLANT("PLANT", ".txt", "PlantUML ER Diagrams; see https://plantuml.com/ie-diagram", true),
	PNG("PNG",".png","Portable Network Graphics; see http://libpng.org/pub/png",false),
	SVG("SVG",".svg","Scalable Vector Graphics; see https://www.w3.org/TR/2003/REC-SVG11-20030114/",false),
	PDF("PDF",".pdf","Portable Document Format; see https://en.wikipedia.org/wiki/Portable_Document_Format",false),
	VISJS("VISJS",".html","JavaScript Visualization Framework; see: https://github.com/almende/vis",false);
	
	
	private String name;
	private String extension;
	private String description;
	private boolean implemented;
	
	private FORMAT(String name,String extension,String description,boolean implemented) {
		this.name = name;
		this.extension = extension;
		this.description = description;
		this.implemented = implemented;
	}
	
	/**
	 * Get description of format
     * @return a string
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * Get the name of this format
     * @return a string
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Get the filename extension for this format
     * @return a string
	 */
	public String getExtension() {
		return extension;
	}
	
	/**
	 * Getter for implemented flag
	 * @return value of implemented
	 */
	public boolean hasImplemented() {
		return implemented;
	}
	
}
