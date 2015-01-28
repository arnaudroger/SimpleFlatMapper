package org.sfm.map.impl;

import org.sfm.map.FieldKey;
import org.sfm.map.MapperBuilderErrorHandler;

import java.lang.reflect.Type;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class LogRethrowMapperBuilderErrorHandler implements
		MapperBuilderErrorHandler {
	private Logger logger = Logger.getLogger(getClass().getName());

	@Override
	public void getterNotFound(final String msg) {
		logger.log(Level.WARNING, msg);
	}

	@Override
	public void propertyNotFound(final Type target, final String property) {
		logger.log(Level.WARNING, "Setter for " + property + " on  " + target + " not found");
	}

	@Override
	public void customFieldError(FieldKey<?> key, String message) {
		logger.log(Level.WARNING, message);
	}
}
