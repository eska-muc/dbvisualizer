package com.skuehnel.dbvisualizer.report;

import com.skuehnel.dbvisualizer.domain.Column;
import com.skuehnel.dbvisualizer.domain.Model;
import com.skuehnel.dbvisualizer.domain.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MarkdownReportGenerator extends AbstractReportGenerator implements ReportGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkdownReportGenerator.class);

    private static final String TABLE_ROW_SEPARATOR = "|----|-----------|----|-------|\n";

    @Override
    public void generateReport(String outputFilePath, Model model) {
        LOGGER.debug("Creating Markdown report in file {}", outputFilePath);
        File outputFile = new File(outputFilePath);
        FileWriter outputWriter;
        try {
            outputWriter = new FileWriter(outputFile);
            outputWriter.write(String.format("# Report on %s\n\n", getName(model)));
            for (Table t : model.getTableList()) {
                outputWriter.write(String.format("## Table %s\n", t.getName()));
                outputWriter.write(String.format("%s\n", nvl(t.getComment(), "")));
                outputWriter.write("### Columns\n\n");
                outputWriter.write("|Name|Constraints|Type|Comment|\n");
                outputWriter.write(TABLE_ROW_SEPARATOR);
                for (Column c : t.getColumns()) {
                    outputWriter.write(String.format("|%s|%s|%s|%s|\n", c.getName(), nevl(constraints(c), " "), c.getType(), nvl(c.getComment(), " ")));
                }
            }
            outputWriter.close();
        } catch (IOException e) {
            LOGGER.error("Caught an I/O exception.");
        }
    }
}
