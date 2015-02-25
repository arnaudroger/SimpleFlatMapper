package org.sfm.map.impl;

import org.sfm.map.FieldKey;
import org.sfm.map.MapperBuilderErrorHandler;
import org.sfm.map.MapperBuildingException;

import java.lang.reflect.Type;

public final class IgnorePropertyNotFoundMapperBuilderErrorHandler implements
		MapperBuilderErrorHandler {

	@Override
	public void getterNotFound(final String msg) {
		throw new MapperBuildingException(msg);
	}

	@Override
	public void propertyNotFound(final Type target, final String property) {
	}

	@Override
	public void customFieldError(FieldKey<?> key, String message) {
		throw new MapperBuildingException(message);
	}

}
