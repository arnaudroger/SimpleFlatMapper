package org.sfm.map;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class LogFieldMapperErrorHandler implements FieldMapperErrorHandler {
	private Logger logger = Logger.getLogger(getClass().getName());
	@Override
	public void errorMappingField(final String name, final Object source, final Object target,
			final Exception error) throws Exception {
		logger.log(Level.WARNING, "Error getting value from " + source, error);
	}
}
