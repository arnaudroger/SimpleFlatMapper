package org.sfm.map;

public final class RethrowFieldMapperErrorHandler implements FieldMapperErrorHandler {

	@Override
	public void errorMappingField(final String name, final Object source, final Object target,
			final Exception error) throws MappingException {
		throw new MappingException("Error mapping field " + name + " : " + error.getMessage(), error);
	}

}
