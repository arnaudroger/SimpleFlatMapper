package org.sfm.map;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class LogFieldMapperErrorHandler<K> implements FieldMapperErrorHandler<K> {
	private Logger logger = Logger.getLogger(getClass().getName());
	@Override
	public void errorMappingField(final K key, final Object source, final Object target,
			final Exception error) throws MappingException {
		logger.log(Level.WARNING, "Error getting value from " + source, error);
	}
}
