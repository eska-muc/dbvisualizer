package com.skuehnel.dbvisualizer.report;

import com.skuehnel.dbvisualizer.domain.Column;
import com.skuehnel.dbvisualizer.domain.Model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.skuehnel.dbvisualizer.domain.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HTMLReportGenerator extends AbstractReportGenerator implements ReportGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(HTMLReportGenerator.class);
    private static final String HTML_NBSP = "&nbsp;";


    private String header(List<String> headElements) {
        StringBuilder builder = new StringBuilder("<!DOCTYPE HTML>\n");
        builder.append("<html>\n");
        builder.append("  <head>\n");
        for (String headElement : headElements) {
            builder.append("      ");
            builder.append(headElement);
        }
        builder.append("  </head>\n");
        builder.append("  <body>\n");
        return builder.toString();
    }

    private String footer() {
        return "</body></html>\n";
    }

    private String table(Table table) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(String.format("    <h2>Table %s</h2>\n", nvl(table.getName(), "")));
        stringBuilder.append(String.format("    <p>%s</p>\n", nvl(table.getComment(), HTML_NBSP)));
        stringBuilder.append("    <h3>Columns</h3>\n");
        stringBuilder.append("    <table>\n");
        stringBuilder.append("      <thead>\n");
        stringBuilder.append("        <tr>\n");
        stringBuilder.append("          <th>Name</th><th>Constraints</th><th>Type</th><th>Comment</th>\n");
        stringBuilder.append("        </tr>\n");
        stringBuilder.append("      </thead>\n");
        stringBuilder.append("      <tbody>\n");
        for (Column c : table.getColumns()) {
            stringBuilder.append("      <tr>\n");
            stringBuilder.append(String.format("       <td>%s</td><td>%s</td><td>%s</td><td>%s</td>\n",
                    nvl(c.getName(), HTML_NBSP), constraints(c), nvl(c.getType(), HTML_NBSP), nvl(c.getComment(), HTML_NBSP)));
            stringBuilder.append("      </tr>\n");
        }
        stringBuilder.append("      </tbody>\n");
        stringBuilder.append("    </table>\n");
        return stringBuilder.toString();
    }

    @Override
    public void generateReport(String outputFilePath, Model theModel) {
        LOGGER.debug("Creating HTML report in file {}", outputFilePath);
        File outputFile = new File(outputFilePath);
        FileWriter outputWriter;
        try {
            outputWriter = new FileWriter(outputFile);
            outputWriter.write(header(Arrays.asList(
                    String.format("<title>%s</title>\n", getName(theModel)),
                    "<meta charset=\"utf-8\"/>\n",
                    "<style>\n" +
                            "p { font-family: sans-serif ; }\n" +
                            "h1 { font-family: sans-serif ; }\n" +
                            "h2 { font-family: sans-serif ; }\n" +
                            "h3 { font-family: sans-serif ; }\n" +
                            "table, th, td { border: 1px solid black ; border-collapse: collapse ;  }\n" +
                            "th { font-family: sans-serif ; padding: 10px ; text-align: center ; background-color: #f2f2f2 }\n" +
                            "td { font-family: sans-serif ; padding: 10px ; text-align: left ; }\n" +
                            "</style>\n"
            )));
            outputWriter.write(String.format("<h1>Report on \"%s\"</h1>\n", getName(theModel)));
            for (Table t : theModel.getTableList()) {
                outputWriter.write(table(t));
            }
            outputWriter.write(footer());
            outputWriter.close();
        } catch (IOException e) {
            LOGGER.error("Could not open output file.", e);
        }
    }
}
