package org.sfm.map;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogRethrowMapperBuilderErrorHandler implements
		MapperBuilderErrorHandler {
	private Logger logger = Logger.getLogger(getClass().getName());

	@Override
	public void getterNotFound(String msg) {
		logger.log(Level.WARNING, msg);
	}

	@Override
	public void setterNotFound(Class<?> target, String property) {
		logger.log(Level.WARNING, "Setter for " + property + " on  " + target + " not found");
	}
}
