package org.sfm.map;

public interface FieldMapperErrorHandler {
	void errorMappingField(String name, Object source, Object target, Exception error) throws MappingException;
}
