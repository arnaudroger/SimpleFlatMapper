package org.sfm.map;

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
	public void setterNotFound(final Class<?> target, final String property) {
		logger.log(Level.WARNING, "Setter for " + property + " on  " + target + " not found");
	}
}
