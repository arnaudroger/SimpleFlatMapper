package org.sfm.map;

public class RethrowFieldMapperErrorHandler implements FieldMapperErrorHandler {

	@Override
	public void errorMappingField(String name, Object source, Object target,
			Exception error) throws Exception {
		throw error;
	}

}
