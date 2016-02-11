package org.sfm.map.error;

import org.sfm.map.FieldKey;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.utils.ErrorDoc;

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
		logger.log(Level.WARNING, "Setter for " + property + " on  " + target + " not found See " + ErrorDoc.toUrl("PROPERTY_NOT_FOUND"));
	}

	@Override
	public void customFieldError(FieldKey<?> key, String message) {
		logger.log(Level.WARNING, message);
	}
}
