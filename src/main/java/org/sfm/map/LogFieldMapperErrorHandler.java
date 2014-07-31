package org.sfm.map;

import java.util.logging.Level;
import java.util.logging.Logger;

public class LogFieldMapperErrorHandler implements FieldMapperErrorHandler {
	private Logger logger = Logger.getLogger(getClass().getName());
	@Override
	public void errorMappingField(String name, Object source, Object target,
			Exception error) throws Exception {
		logger.log(Level.WARNING, "Error getting value from " + source, error);
	}
}
