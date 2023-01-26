package com.skuehnel.dbvisualizer.util;

public class MissingMandatoryException extends RuntimeException {
    public MissingMandatoryException(String message) {
        super(message);
    }
}
