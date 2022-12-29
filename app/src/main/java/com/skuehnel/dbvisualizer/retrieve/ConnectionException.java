package com.skuehnel.dbvisualizer.retrieve;

/**
 * Project DBVisualizer
 * 
 * @author Stefan Kuehnel
 *
 */
public class ConnectionException extends Exception {

	/**
	 * Default Constructor
	 */
	public ConnectionException() {
		super();
	}

	/**
	 * Constructor
	 * @param message a message
	 */
	public ConnectionException(String message) {
		super(message);
	}

	/**
	 * Constructor
	 * @param cause a root cause
	 */
	public ConnectionException(Throwable cause) {
		super(cause);
	}

	/**
	 * Contructor
	 * @param message a message 
	 * @param cause a cause
	 */
	public ConnectionException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructor
	 * @param message a message
	 * @param cause a cause
	 * @param enableSuppression a boolean flag
	 * @param writableStackTrace another flag
	 */
	public ConnectionException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
