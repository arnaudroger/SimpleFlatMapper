package org.simpleflatmapper.converter;

import java.net.MalformedURLException;

public class ConversionException extends RuntimeException {

	public ConversionException(MalformedURLException e) {
		super(e.getMessage(), e);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -2395851854732453372L;

}
