package com.skuehnel.dbvisualizer.report;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * Factory for different implementations of the report generator
 */
public class ReportGeneratorFactory {
    public static ReportGenerator createReportGeneratorInstance(String format) {
        ReportGenerator reportGenerator;
        switch (format) {
            case "HTML":
                reportGenerator = new HTMLReportGenerator();
                break;
            case "PDF":
                reportGenerator = new PDFReportGenerator();
                break;
            case "MARKDOWN":
                reportGenerator = new MarkdownReportGenerator();
                break;
            default:
                throw new RuntimeException("Unsupported Format");
        }
        return reportGenerator;
    }
}
