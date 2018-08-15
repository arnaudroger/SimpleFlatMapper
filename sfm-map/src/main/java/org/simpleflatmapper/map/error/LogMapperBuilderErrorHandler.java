package org.simpleflatmapper.map.error;

import org.simpleflatmapper.map.FieldKey;
import org.simpleflatmapper.map.MapperBuilderErrorHandler;
import org.simpleflatmapper.util.ErrorDoc;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LogMapperBuilderErrorHandler implements
        MapperBuilderErrorHandler {
	private final Logger logger = Logger.getLogger(getClass().getName());

	@Override
	public void accessorNotFound(final String msg) {
		logger.log(Level.WARNING, msg);
	}

	@Override
	public void propertyNotFound(final Type target, final String property) {
		logger.log(Level.WARNING, "Setter for " + property + " on  " + target + " not found See " + ErrorDoc.PROPERTY_NOT_FOUND.toUrl());
	}

	@Override
	public void customFieldError(FieldKey<?> key, String message) {
		logger.log(Level.WARNING, message);
	}
}
