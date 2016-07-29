package org.simpleflatmapper.csv.impl;

public class ParsingException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2966533946322394852L;

	public ParsingException(String msg) {
		super(msg);
	}

	public ParsingException(Throwable e) {
		super(e.getMessage(), e);
	}

}
