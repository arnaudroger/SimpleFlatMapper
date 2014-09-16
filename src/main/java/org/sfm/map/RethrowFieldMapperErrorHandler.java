package org.sfm.map;

public final class RethrowFieldMapperErrorHandler<K> implements FieldMapperErrorHandler<K> {

	@Override
	public void errorMappingField(final K name, final Object source, final Object target,
			final Exception error) throws MappingException {
		throw new MappingException("Error mapping field " + name + " : " + error.getMessage(), error);
	}

}
