package com.skuehnel.dbvisualizer.report;

import com.skuehnel.dbvisualizer.domain.Column;
import com.skuehnel.dbvisualizer.domain.Model;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AbstractReportGenerator {
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
        return constraintList.stream().collect(Collectors.joining(", "));
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
}
