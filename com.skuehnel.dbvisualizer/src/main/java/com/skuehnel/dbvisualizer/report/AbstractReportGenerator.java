package com.skuehnel.dbvisualizer.report;

import com.skuehnel.dbvisualizer.domain.Column;
import com.skuehnel.dbvisualizer.domain.Model;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class AbstractReportGenerator implements ReportGenerator {

    private HashMap<String, String> metaInformation = new HashMap<>();

    /**
     * Helper method to get a textual description of the constraints of an column
     *
     * @param c the column object
     * @return a textual representation of the constraints
     */
    protected String constraints(Column c) {
        List<String> constraintList = new ArrayList<>();
        if (c != null) {
            if (c.isPrimaryKey()) {
                constraintList.add("PK");
            }
            if (c.isNotNull()) {
                constraintList.add("Not Null");
            }
            if (c.isUnique()) {
                constraintList.add("Unique");
            }
            if (c.getForeignKeyTable() != null) {
                constraintList.add((String.format("FK (table: %s)", c.getForeignKeyTable().getName())));
            }
        }
        return String.join(", ", constraintList);
    }

    /**
     * Null Value
     *
     * @param in           returned, if not null
     * @param replacement, returned if in param is null
     * @return either in or replacement
     */
    protected String nvl(String in, String replacement) {
        return in != null ? in : replacement;
    }

    /**
     * Null or Empty Value
     *
     * @param in           returned, if not null and not empty
     * @param replacement, returned if in param is null or empty
     * @return either in or replacement
     */
    protected String nevl(String in, String replacement) {
        return StringUtils.isNotEmpty(in) ? in : replacement;
    }

    /**
     * The name of the model to be used in the report
     *
     * @param model the model
     * @return either the database name, the catalog name or the schema name
     */
    protected String getName(Model model) {
        if (model.getDatabaseName() != null) {
            return model.getDatabaseName();
        } else if (model.getCatalogName() != null) {
            return model.getCatalogName();
        } else if (model.getSchemaName() != null) {
            return model.getSchemaName();
        }
        return "Unknown Table/Schema/Catalog";
    }

    @Override
    public void initMetaInformation(Model model) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        metaInformation.putIfAbsent("Report generated at", sdf.format(new Date()));
        metaInformation.putIfAbsent("JDBC URL", nvl(model.getJdbcURL(), ""));
        metaInformation.putIfAbsent("Database Type", model.getDatabaseType());
        if (model.getFilterInfo() != null) {
            metaInformation.putIfAbsent("Filter", model.getFilterInfo());
        }
    }

    /**
     * Getter for attribute metaInformation
     *
     * @return current value of field metaInformation
     */
    @Override
    public HashMap<String, String> getMetaInformation() {
        return metaInformation;
    }

    /**
     * Helper to check if a certain option is set
     *
     * @param options all options
     * @param option  the option to check for
     * @return true, if the option is in the list
     */
    protected boolean checkForOption(REPORT_OPT[] options, REPORT_OPT option) {
        return options != null && Arrays.stream(options).anyMatch(Predicate.isEqual(option));
    }

}
