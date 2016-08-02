package org.simpleflatmapper.core.map.error;

import org.simpleflatmapper.core.map.FieldMapperErrorHandler;
import org.simpleflatmapper.core.map.MappingException;
import org.simpleflatmapper.core.utils.ErrorHelper;

public final class RethrowFieldMapperErrorHandler<K> implements FieldMapperErrorHandler<K> {

	@Override
	public void errorMappingField(final K name, final Object source, final Object target,
			final Exception error) throws MappingException {
        ErrorHelper.rethrow(error);
	}

}
