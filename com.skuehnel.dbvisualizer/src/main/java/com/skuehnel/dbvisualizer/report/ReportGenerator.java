package com.skuehnel.dbvisualizer.report;

import com.skuehnel.dbvisualizer.domain.Model;
import com.skuehnel.dbvisualizer.domain.Table;

import java.util.List;

/**
 * Interface of the Report Generator
 */
public interface ReportGenerator {
    /**
     * Generate a report from the model
     *
     * @param outputFile path to the output file
     * @param model      the model
     */
    void generateReport(String outputFile, Model model);
}
