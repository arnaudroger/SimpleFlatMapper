package org.sfm.map.impl;

import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MappingException;

public final class RethrowFieldMapperErrorHandler<K> implements FieldMapperErrorHandler<K> {

	@Override
	public void errorMappingField(final K name, final Object source, final Object target,
			final Exception error) throws MappingException {
		throw new MappingException("Error mapping field " + name + " : " + error.getMessage(), error);
	}

}
