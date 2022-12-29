package com.skuehnel.dbvisualizer.util;

public class InvalidParamException extends Exception {

	public InvalidParamException(String message) {
		super(message);
	}

	public InvalidParamException(Throwable cause) {
		super(cause);
	}

	public InvalidParamException(String message, Throwable cause) {
		super(message, cause);

	}

	public InvalidParamException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
