package org.simpleflatmapper.map.error;

import org.simpleflatmapper.map.FieldMapperErrorHandler;
import org.simpleflatmapper.map.MappingException;
import org.simpleflatmapper.util.ErrorHelper;

public final class RethrowFieldMapperErrorHandler<K> implements FieldMapperErrorHandler<K> {

	@Override
	public void errorMappingField(final K name, final Object source, final Object target,
			final Exception error) throws MappingException {
        ErrorHelper.rethrow(error);
	}

}
