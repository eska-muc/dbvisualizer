package com.skuehnel.dbvisualizer.report;

/**
 * Factory for different implementations of the report generator
 */
public class ReportGeneratorFactory {
    public static ReportGenerator createReportGeneratorInstance(String format) {
        return switch (format) {
            case "HTML" -> new HTMLReportGenerator();
            case "PDF" -> new PDFReportGenerator();
            case "MARKDOWN" -> new MarkdownReportGenerator();
            default -> throw new RuntimeException("Unsupported Format");
        };
    }
}
