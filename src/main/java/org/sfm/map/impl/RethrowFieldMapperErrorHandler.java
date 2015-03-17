package org.sfm.map.impl;

import org.sfm.map.FieldMapperErrorHandler;
import org.sfm.map.MappingException;
import org.sfm.utils.ErrorHelper;

public final class RethrowFieldMapperErrorHandler<K> implements FieldMapperErrorHandler<K> {

	@Override
	public void errorMappingField(final K name, final Object source, final Object target,
			final Exception error) throws MappingException {
        ErrorHelper.rethrow(error);
	}

}
