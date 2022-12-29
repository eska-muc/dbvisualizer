package com.skuehnel.dbvisualizer.report;

import com.skuehnel.dbvisualizer.domain.Model;

import java.util.HashMap;

/**
 * Interface of the Report Generator
 */
public interface ReportGenerator {

    enum REPORT_OPT {
        WITH_META_INFORMATION
    }

    /**
     * Generate report from the model
     *
     * @param outputFile path to the output file
     * @param model      the model
     * @param options    list of reporting options
     */
    void generateReport(String outputFile, Model model, REPORT_OPT... options);

    HashMap<String, String> getMetaInformation();

    void initMetaInformation(Model model);
}
