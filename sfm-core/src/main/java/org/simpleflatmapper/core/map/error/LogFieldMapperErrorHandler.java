package org.simpleflatmapper.core.map.error;

import org.simpleflatmapper.core.map.FieldMapperErrorHandler;
import org.simpleflatmapper.core.map.MappingException;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class LogFieldMapperErrorHandler<K> implements FieldMapperErrorHandler<K> {
	private final Logger logger = Logger.getLogger(getClass().getName());
	@Override
	public void errorMappingField(final K key, final Object source, final Object target,
			final Exception error) throws MappingException {
		logger.log(Level.WARNING, "Error getting value from " + source, error);
	}
}
