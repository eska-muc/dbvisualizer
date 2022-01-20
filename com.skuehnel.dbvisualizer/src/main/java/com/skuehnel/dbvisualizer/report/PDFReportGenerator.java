package com.skuehnel.dbvisualizer.report;

import com.skuehnel.dbvisualizer.domain.Column;
import com.skuehnel.dbvisualizer.domain.Model;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vandeseer.easytable.RepeatedHeaderTableDrawer;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.settings.VerticalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.cell.TextCell;

import java.io.IOException;
import java.awt.Color;
import java.util.Map;

public class PDFReportGenerator extends AbstractReportGenerator implements ReportGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(PDFReportGenerator.class);

    @Override
    public void generateReport(String outputFile, Model model, REPORT_OPT... options) {
        try (final PDDocument document = new PDDocument()) {

            if (checkForOption(options, REPORT_OPT.WITH_META_INFORMATION)) {
                addMetaPage(document);
            }

            for (com.skuehnel.dbvisualizer.domain.Table databaseTable : model.getTableList()) {

                PDPage page = new PDPage(PDRectangle.A4);
                float startY = 20;

                TableDrawer drawer = TableDrawer.builder()
                        .table(createDatabaseTableDescriptionTable(page, databaseTable))
                        .startX(50)
                        .startY(startY)
                        .endY(200)
                        .build();
                drawer.draw(() -> document, () -> page, startY);

                RepeatedHeaderTableDrawer.builder()
                        .table(createPdfTable(page, databaseTable))
                        .startX(50)
                        .startY(drawer.getFinalY() - 50f)
                        .endY(200)
                        .numberOfRowsToRepeat(1)
                        .build().draw(() -> document, () -> page, 50f);
            }
            document.save(outputFile);
        } catch (IOException ioException) {
            LOGGER.error("I/O Exception caught.", ioException);
        }
    }

    private void addMetaPage(PDDocument document) throws IOException {
        PDPage page = new PDPage(PDRectangle.A4);
        float startY = 20;
        Table.TableBuilder tableBuilder = Table.builder().addColumnsOfWidth(colWidth(page, 40), colWidth(page, 60));
        Map<String, String> metaInfo = getMetaInformation();
        for (Map.Entry<String, String> entry : metaInfo.entrySet()) {
            tableBuilder.addRow(
                    Row.builder().add(createBodyCell(entry.getKey()))
                            .add(createBodyCell(nvl(entry.getValue(), "")))
                            .build()
            );
        }
        TableDrawer drawer = TableDrawer.builder().table(tableBuilder.build())
                .startX(50)
                .startY(startY)
                .endY(200)
                .build();
        drawer.draw(() -> document, () -> page, startY);
    }

    private Table createPdfTable(PDPage page, com.skuehnel.dbvisualizer.domain.Table table) {

        final Table.TableBuilder tableBuilder = Table.builder().addColumnsOfWidth(colWidth(page, 20), colWidth(page, 20), colWidth(page, 20), colWidth(page, 40));
        tableBuilder.addRow(Row.builder()
                .add(createHeaderCell("Name"))
                .add(createHeaderCell("Constraints"))
                .add(createHeaderCell("Type"))
                .add(createHeaderCell("Description"))
                .build());

        for (Column column : table.getColumns()) {
            tableBuilder.addRow(
                    Row.builder()
                            .add(createBodyCell(column.getName()))
                            .add(createBodyCell(nevl(constraints(column), " ")))
                            .add(createBodyCell(column.getType()))
                            .add(createBodyCell(nvl(column.getComment(), " ")))
                            .build());
        }

        return tableBuilder.build();
    }

    private TextCell createHeaderCell(String text) {
        return TextCell.builder()
                .font(PDType1Font.HELVETICA_BOLD)
                .text(text)
                .horizontalAlignment(HorizontalAlignment.CENTER)
                .padding(6f)
                .borderColor(Color.BLACK)
                .borderWidth(1f)
                .build();
    }

    private TextCell createBodyCell(String text) {
        return TextCell.builder()
                .font(PDType1Font.HELVETICA)
                .text(text)
                .horizontalAlignment(HorizontalAlignment.LEFT)
                .verticalAlignment(VerticalAlignment.TOP)
                .padding(6f)
                .borderColor(Color.BLACK)
                .borderWidth(1f)
                .build();
    }

    private float colWidth(PDPage page, int percentage) {
        float pageWidth = page.getMediaBox().getWidth();
        float usable = pageWidth * 0.8f;
        return usable * ((float) percentage / 100f);
    }

    private Table createDatabaseTableDescriptionTable(PDPage pdPage, com.skuehnel.dbvisualizer.domain.Table table) throws IOException {
        return Table.builder()
                .addColumnsOfWidth(colWidth(pdPage, 100))
                .addRow(
                        Row.builder()
                                .add(TextCell.builder()
                                        .font(PDType1Font.HELVETICA_BOLD)
                                        .fontSize(20)
                                        .horizontalAlignment(HorizontalAlignment.CENTER)
                                        .text(table.getName()).build())
                                .build()
                )
                .addRow(
                        Row.builder()
                                .add(TextCell.builder()
                                        .font(PDType1Font.HELVETICA)
                                        .fontSize(12)
                                        .horizontalAlignment(HorizontalAlignment.LEFT)
                                        .text(nvl(table.getComment(), "")).build())
                                .build()
                ).build();
    }
}
