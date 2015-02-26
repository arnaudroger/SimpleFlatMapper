package org.sfm.jdbc;

import org.sfm.map.MappingException;

public class SQLMappingException extends MappingException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 9154523492843730658L;

    public SQLMappingException(Throwable cause) {
        super(cause.getMessage(), cause);
    }

    public SQLMappingException(String message, Throwable cause) {
		super(message, cause);
	}

}
