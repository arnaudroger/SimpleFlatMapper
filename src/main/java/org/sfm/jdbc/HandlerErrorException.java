package org.sfm.jdbc;

import org.sfm.map.MappingException;

@SuppressWarnings("serial")
public class HandlerErrorException extends MappingException {

	public HandlerErrorException(String message, Throwable cause) {
		super(message, cause);
	}

}
